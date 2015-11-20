/**
 * Created by Thomas on 03/11/2015.
 */
function FittsTestStage(numberOfDots, dotRadius, dotDistance)
{
    this.numberOfDots = numberOfDots;
    this.testStageFinished = false;
    this.testProgress = 0;
    this.dotRadius = dotRadius;
    this.dotDistance = dotDistance; // Dit is de straal van de cirkel
    this.dotHColor = "red";
    this.dotLColor = "gray";
    this.previousTarget = -1;
    this.nextTarget = 0;
    this.pathTracker = {};
    this.backCircleColor = "blue";
    this.dotsList = [];
    this.backCircle = {};
    this.cursorState = {x: 0, y: 0, leftPressed: false};

    this.initialize = function(canvas)
    {
        this.initializeDots(canvas);

        this.pathTracker = new FittsTracking();
    }

    this.initializeDots = function(canvas)
    {
        for(var i = 0; i < this.numberOfDots; i++)
        {
            this.dotsList[i] = new FittsDot(i, this.dotRadius, this.dotHColor, this.dotLColor);
        }

        this.dotsList[0].setTarget(true);

        this.repositionTest(canvas);
    }

    this.getFinished = function()
    {
        return this.testStageFinished;
    }

    this.repositionTest = function(canvas)
    {
        var angle  = (2*Math.PI)/(this.numberOfDots);     // aan de hand van de hoek worden de cirkels in een cirkel gezet. Deze veranderd aan de hand van het aantal bolletjes
        var centerX = (canvas.width)/2;                   // middelpunt blijft centraal
        var centerY = (canvas.height)/3;

        for(var i = 0; i < this.numberOfDots; i++)
        {
            this.dotsList[i].setPosition((-this.dotDistance * Math.sin((-angle*i)) + centerX), (-this.dotDistance*Math.cos(-angle*i) + centerY));
        }
    }

    this.setDotsRadius = function(dotsRadius)
    {
        this.dotsRadius = dotsRadius;
    }

    this.setNextTarget = function()
    {
        if(this.testProgress < this.numberOfDots)
        {
            this.previousTarget = this.nextTarget;

            this.nextTarget = (this.previousTarget + Math.floor(this.numberOfDots / 2)) % this.numberOfDots;

            this.dotsList[this.previousTarget].setTarget(false);
            this.dotsList[this.nextTarget].setTarget(true);

            this.testProgress++;
        }
        else
        {
            this.dotsList[this.nextTarget].setTarget(false);
            this.testStageFinished = true;
        }
    }

    this.setDotColor = function(dotHColor, dotLColor)
    {
        this.dotHColor = dotHColor;
        this.dotLColor = dotLColor;
    }

    this.setBackCircleColor = function(backCircleColor)
    {
        this.backCircleColor = backCircleColor;
    }

    this.setDistance = function(dotDistance)
    {
        this.dotDistance = dotDistance;
    }

    this.drawBackCircle = function(context)
    {

    }

    this.drawDots = function(context)
    {
        var target = -1;

        for(var i = 0; i < this.numberOfDots; i++)
        {
            if(this.dotsList[i].isTarget())
            {
                //Set target dot (will be drawn as last)
                target = i;
            }
            else
            {
                //Draw dot on screen
                this.dotsList[i].drawDot(context);
            }
        }

        //Draw target dot
        if(target != -1)
        {
            this.dotsList[target].drawDot(context);
        }
    }

    this.triggeredCursorEvent = function(cursorEvent)
    {
        //Calculate cursor position relative to the the center of the test
        this.cursorState.x = cursorEvent.x - (canvas.width)/2;
        this.cursorState.y = cursorEvent.y - (canvas.height)/3;

        this.cursorState.leftPressed = cursorEvent.leftPressed;

        //Add cursor event to current trackpath
        if(this.previousTarget != -1)
        {
            this.logNewCursorEvent();
        }

        //Check if cursor has clicked on target (after releasing the left mouse button)
        if(cursorEvent.leftReleased)
        {
            if(this.checkTargetClicked())
            {
                if(!this.testStageFinished)
                {
                    this.createNewTracePath();
                }
            }
        }
    }

    this.checkTargetClicked = function()
    {
        //Temporary conversion from cursor position (relative to test center) relative to the upper left corner of the canvas (until coordinates are standardized to test center
        var tempPosX = this.cursorState.x + (canvas.width)/2;
        var tempPosY = this.cursorState.y + (canvas.height)/2;

        if(this.dotsList[this.nextTarget].cursorOver(tempPosX, tempPosY))
        {
            this.setNextTarget();

            return true;
        }

        return false;
    }

    this.createNewTracePath = function()
    {
        this.pathTracker.continueWithNextTrackPath();
    }

    this.logNewCursorEvent = function()
    {
        var cursorEvent = new FittsTrackEvent(this.cursorState.x + (canvas.width)/2, this.cursorState.y + (canvas.height)/2, this.cursorState.leftPressed)
        this.pathTracker.addCursorEvent(cursorEvent);
    }

    this.drawStatus = function(context)
    {
        //Draw mouse position coordinates
        var message = "Cursor x: " + this.cursorState.x + " y: " + this.cursorState.y + " - clicked: " + this.cursorState.leftPressed  + " | Current path: " + this.pathTracker.getTrackPaths().length + " - timer: " + this.pathTracker.getCurrentTrackPath().getPathTime();

        context.font = "16px Arial";
        context.fillStyle = "black";
        context.fillText(message, 10, 25);
    }

    this.getTrackPaths = function()
    {
        return this.pathTracker.getTrackPaths();
    }
}