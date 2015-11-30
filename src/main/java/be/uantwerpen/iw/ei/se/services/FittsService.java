package be.uantwerpen.iw.ei.se.services;

import be.uantwerpen.iw.ei.se.fittsTest.models.FittsResult;
import be.uantwerpen.iw.ei.se.fittsTest.models.FittsTest;
import be.uantwerpen.iw.ei.se.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Thomas on 15/11/2015.
 */
@Service
public class FittsService
{
    @Autowired
    private FittsTestRepository fittsTestRepository;

    @Autowired
    private FittsTestStageRepository fittsTestStageRepository;

    @Autowired
    private FittsResultService fittsResultService;

    public FittsTest findTestById(String testID)
    {
        return this.fittsTestRepository.findByTestID(testID);
    }

    public Iterable<FittsTest> findAllTests()
    {
        return this.fittsTestRepository.findAll();
    }

    public Iterable<FittsResult> findAllResults()
    {
        return this.fittsResultService.findAll();
    }

    public boolean saveTestResult(FittsResult result)
    {
        if(findTestById(result.getTestID()) != null)
        {
            fittsResultService.save(result);

            return true;
        }
        else
        {
            return false;
        }
    }

    public Iterable<FittsResult> findResultsByTestId(String testID)
    {
        return this.fittsResultService.findByTestID(testID);
    }

    public FittsResult findResultById(String resultID)
    {
        return this.fittsResultService.findByResultID(resultID);
    }

    public Iterable<FittsTest> findTestsByCompleteState(boolean completed)
    {
        return this.fittsTestRepository.findByCompleteState(completed);
    }

    public boolean addTest(final FittsTest test)
    {
        if(isDuplicatedTestId(test))
        {
            return false;
        }

        //Save the stages of the test to the database
        this.fittsTestStageRepository.save(test.getTestStages());

        //Save the test to the database
        this.fittsTestRepository.save(test);

        return true;
    }

    public boolean saveTest(FittsTest test)
    {
        for(FittsTest t : findAllTests())
        {
            if(t.getId().equals(test.getId()))
            {
                if(!this.isDuplicatedTestId(test))
                {
                    t.setTestID(test.getTestID());
                    t.setCompleted(test.getCompleted());
                    t.setTestStages(test.getTestStages());

                    //Save the stages of the test to the database
                    this.fittsTestStageRepository.save(test.getTestStages());

                    //Save the test to the database
                    this.fittsTestRepository.save(t);

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

    public boolean testIdAlreadyExists(final String testID)
    {
        List<FittsTest> tests = fittsTestRepository.findAll();

        for(FittsTest testIt : tests)
        {
            if(testIt.getTestID().equals(testID))
            {
                return true;
            }
        }

        return false;
    }

    public boolean resultIdAlreadyExists(final String resultID)
    {
        return this.fittsResultService.resultIdAlreadyExists(resultID);
    }

    private boolean isDuplicatedTestId(final FittsTest test)
    {
        List<FittsTest> tests = fittsTestRepository.findAll();

        for(FittsTest testIt : tests)
        {
            if(testIt.getTestID().equals(test.getTestID()) && !testIt.getId().equals(test.getId()))
            {
                //Two different test objects with the same testID
                return true;
            }
        }

        return false;
    }
/*
    public boolean add(final FittsTest fittstest)
    {
        if(fittstest.getNumberOfDots()<2)
        {
            fittstest.setNumberOfDots(2);
        }
        if(fittstest.getDotSize()>70)
        {
            fittstest.setDotSize(70);
        }
        if(fittstest.getDotDistance()>250)
        {
            fittstest.setDotDistance(250);
        }


        int size=0;
        int amount_zeros =0;
        for( FittsTest u : findAll())
        {
            size+=1;
        }
        if(size>=10)
        {
            String givenID = fittstest.getTestID();
            size+=1;
            fittstest.setTestID(givenID +"0"+Integer.toString(size));
        }
        else if(size>100)
        {
            String givenID = fittstest.getTestID();
            size+=1;
            fittstest.setTestID(givenID +Integer.toString(size));
        }
        else
        {
            String givenID = fittstest.getTestID();
            size += 1;
            fittstest.setTestID(givenID +"00"+ Integer.toString(size));
        }


        this.fittsRepository.save(fittstest);
        return true;
    }
*/
}
