package be.uantwerpen.iw.ei.se.services;

import be.uantwerpen.iw.ei.se.fittsTest.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dries on 10/12/2015.
 */
@Service
public class FittsCalculateService
{
    @Autowired
    private FittsService fittsService;

    private Double constant=4.133;

    //calculates the total throughput using the throughput per stage
    public FittsThroughput calculateThroughput(FittsResult result)
    {
        FittsThroughput throughputResults = new FittsThroughput();

        List<Double> stageThroughputs = calculateStageThroughputs(result);

        Double sum = 0.0;
        for(int i=0; i<stageThroughputs.size(); i++)
        {
            sum = sum + stageThroughputs.get(i);
        }

        throughputResults.setTotalThroughput(sum/stageThroughputs.size());
        throughputResults.setStageThroughput(stageThroughputs);

        return throughputResults;
    }

    //calculates the throughput per stage
    private List<Double> calculateStageThroughputs(FittsResult result)
    {
        FittsTest test = fittsService.findTestById(result.getTestID());
        List<Double> stageThroughputs = new ArrayList<Double>();
        List<FittsTrackEvent> clickEvents = new ArrayList<FittsTrackEvent>();
        clickEvents = getAllClickEvents(result);
        int totalDots=0;

        for(int i=0; i<test.getTestStages().size(); i++)
        {
            List<List<Double>> coordinates = new ArrayList<List<Double>>();
            List<List<Double>> lines = new ArrayList<List<Double>>();
            List<List<Double>> projectedClicks = new ArrayList<List<Double>>();
            int dotDistance = test.getTestStages().get(i).getDotDistance();
            int dotNumber = test.getTestStages().get(i).getNumberOfDots();
            //int dotRadius = test.getTestStages().get(currentState).getDotRadius();

            coordinates = calculateCoord(dotNumber, dotDistance);
            lines = calculateLines(dotNumber, coordinates);
            projectedClicks = calculateProjectedPoints(clickEvents, lines, totalDots, dotNumber);
            Double meanDeviation = calculateDeviations(projectedClicks, coordinates);
            Double We = this.constant*meanDeviation;
            //without deviation
            //Double DifficultyIndex = Math.log(((dotDistance*2)+ (dotRadius*2))/(dotRadius*2))/Math.log(2);
            //with  deviation
            Double DifficultyIndex = Math.log(((dotDistance*2) + We) / We) / Math.log(2);
            Double TotalTime = (double)(clickEvents.get(totalDots+dotNumber-1).getTimestamp() - clickEvents.get(totalDots).getTimestamp())/1000;
            Double AverageTime = TotalTime/dotNumber;
            totalDots = totalDots + dotNumber;

            stageThroughputs.add(DifficultyIndex/AverageTime);
        }

        return stageThroughputs;
    }

    //Calculates the coordinates of where should of been clicked, checked
    private List<List<Double>> calculateCoord(int dotNumber, int dotDistance)
    {
        List<List<Double>> coords = new ArrayList<List<Double>>();
        coords.add(new ArrayList<Double>());
        coords.add(new ArrayList<Double>());
        Double angle = 2*Math.PI/dotNumber;
        int nextDot = 0;

        for(int j=0; j<dotNumber; j++)
        {
            coords.get(0).add(-dotDistance * Math.sin(-angle * nextDot));
            coords.get(1).add(-dotDistance * Math.cos(-angle * nextDot));
            nextDot = (int)((nextDot + Math.floor(dotNumber)/2)%dotNumber);
        }

        return coords;
    }

    //Calculates the lines between previous target and current target, checked
    private List<List<Double>> calculateLines(int dotNumber, List<List<Double>> coords)
    {
        List<List<Double>> lines = new ArrayList<List<Double>>();
        lines.add(new ArrayList<Double>());
        lines.add(new ArrayList<Double>());

        for(int j=1; j<=dotNumber; j++)
        {
            if(j==dotNumber)
            {
                if(coords.get(0).get(0) - coords.get(0).get(j - 1) == 0)
                {
                    lines.get(0).add(0.0);
                }
                else
                {
                    lines.get(0).add((coords.get(1).get(0) - coords.get(1).get(j - 1)) / (coords.get(0).get(0) - coords.get(0).get(j - 1)));
                }

                lines.get(1).add(coords.get(1).get(0)-(lines.get(0).get(j-1)*coords.get(0).get(0)));
            }
            else
            {
                if(coords.get(0).get(j) - coords.get(0).get(j - 1) == 0)
                {
                    lines.get(0).add(0.0);
                }
                else
                {
                    lines.get(0).add((coords.get(1).get(j) - coords.get(1).get(j - 1)) / (coords.get(0).get(j) - coords.get(0).get(j - 1)));
                }

                lines.get(1).add(coords.get(1).get(j)-(lines.get(0).get(j-1)*coords.get(0).get(j)));
            }
        }

        return lines;
    }

    //Get the positions where a click occurred, checked
    //clickEvents.get(0) is X-coordinates
    //clickEvents.get(1) is Y-coordinates
    private List<FittsTrackEvent> getAllClickEvents(FittsResult result)
    {
        List<FittsTrackEvent> clickEvents = new ArrayList<FittsTrackEvent>();

        for(int i=0; i < result.getStageResults().size(); i++)
        {
            FittsStageResult stageResult = result.getStageResults().get(i);

            for(int k=0; k < stageResult.getFittsTrackPaths().size(); k++)
            {
                FittsTrackPath trackpath = stageResult.getFittsTrackPaths().get(k);

                clickEvents.add(trackpath.getPath().get(trackpath.getPath().size() - 1));
            }
        }

        return clickEvents;
    }

    private List<List<Double>> calculateProjectedPoints(List<FittsTrackEvent> clickEvents, List<List<Double>> lines, int totalDots, int dotNumber)
    {
        List<List<Double>> projectedClicks = new ArrayList<List<Double>>();
        projectedClicks.add(new ArrayList<Double>());
        projectedClicks.add(new ArrayList<Double>());

        for(int i=totalDots; i < totalDots+dotNumber; i++)
        {
            double clickX = clickEvents.get(i).getCursorPosX();
            double clickY = clickEvents.get(i).getCursorPosY();
            double slope = lines.get(0).get(i-totalDots);
            double offset = lines.get(1).get(i-totalDots);
            double projectedSlope = 0;
            double projectedOffset;

            if(slope != 0)
            {
                //slope*projectedSlope = -1 (rico A * rico B = -1)
                projectedSlope = -1 / slope;
            }

            //clickY = projectedSlope*ClickX + projectedOffset (y = ax+b)
            projectedOffset = clickY - projectedSlope * clickX;

            if(slope == 0)
            {
                projectedClicks.get(0).add(clickX);
                projectedClicks.get(1).add(offset);
            }
            else
            {
                projectedClicks.get(0).add((projectedOffset - offset) / (slope - projectedSlope));
                projectedClicks.get(1).add(slope * projectedClicks.get(0).get(projectedClicks.get(0).size()-1) + offset);
            }
        }

        return projectedClicks;
    }

    private Double calculateDeviations(List<List<Double>> projectedClicks, List<List<Double>> coords)
    {
        Double meanDeviation = 0.0;
        int i;

        for(i = 1; i < projectedClicks.get(0).size(); i++)
        {
            Double deviation = Math.sqrt(Math.pow((projectedClicks.get(0).get(i-1) - coords.get(0).get(i)), 2.0) + Math.pow((projectedClicks.get(1).get(i-1) - coords.get(1).get(i)), 2.0));

            meanDeviation = meanDeviation + deviation;
        }

        Double deviation = Math.sqrt(Math.pow((projectedClicks.get(0).get(projectedClicks.get(0).size()-1) - coords.get(0).get(0)), 2.0) + Math.pow((projectedClicks.get(1).get(projectedClicks.get(0).size()-1) - coords.get(1).get(0)), 2.0));
        meanDeviation = meanDeviation + deviation;

        if(i != 0)
        {
            return meanDeviation / i;
        }
        else
        {
            return 0.0;
        }
    }
}
