/**
 * Created by Thomas on 03/11/2015.
 */
function FittsTest(numberOfDots, dotsSize, dotDistance)
{
    this.numberOfDots = numberOfDots;
    this.dotsSize = dotsSize;
    this.dotDistance = dotDistance; // Dit is de straal van de cirkel
    this.dotHColor = "red";
    this.dotLColor = "gray";
    this.previousTarget = -1;
    this.nextTarget = 0;
    this.pathTracker = [];
    this.backCircleColor = "blue";
    this.dotsList = [];
    this.backCircle = {};
    this.cursorState = {x: 0, y: 0, leftPressed: false};
    this.AmountOfClicks = 0;
    var startTime = new Date();
    var ElapsedTime = new Date();

    this.initialize = function(canvas)
    {
        this.initializeDots(canvas);

        //this.pathTracker = new FittsTracking();
    }

    this.initializeDots = function(canvas)
    {
        for(var i = 0; i < this.numberOfDots; i++)
        {
            this.dotsList[i] = new FittsDot(i, this.dotsSize, this.dotHColor, this.dotLColor);
        }

        this.dotsList[0].setTarget(true);
        var startTime = new Date();

        this.repositionTest(canvas);
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

        console.log("dots reposition center x: " + centerX + " center y: " + centerY);
    }

    this.setDotsSize = function(dotsSize)
    {
        this.dotsSize = dotsSize;
    }

    this.setNextTarget = function()
    {
        this.previousTarget = this.nextTarget;

        this.nextTarget = (this.previousTarget + Math.floor(this.numberOfDots/2)) % this.numberOfDots;


        this.dotsList[this.previousTarget].setTarget(false);
        this.dotsList[this.nextTarget].setTarget(true);
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
        this.cursorState.y = cursorEvent.y - (canvas.height)/2;

        this.cursorState.leftPressed = cursorEvent.leftPressed;

        //Check if cursor has clicked on target (after releasing the left mouse button)
        if(cursorEvent.leftReleased)
        {
            this.AmountOfClicks = this.AmountOfClicks+1
            if(this.checkTargetClicked())
            {
                this.createNewTracePath();

                return;
            }
        }

        if(this.previousTarget != -1)   //Test is started
        {
            this.logNewCursorEvent();
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

    }

    function addZero(x,n) {
        while (x.toString().length < n) {
            x = "0" + x;
        }
        return x;
    }

    this.logNewCursorEvent = function()
    {

    }

    this.drawStatus = function(context)
    {
        //Draw mouse position coordinates
        var message = "Cursor x: " + this.cursorState.x + " y: " + this.cursorState.y + " - clicked: " + this.cursorState.leftPressed;
        var clickAmount = "Clicks: " + this.AmountOfClicks;
        var testRound = "Round: " ;
        //var timer = "Time: " ;
        this.ElapsedTime = (new Date() - this.startTime);
        this.seconds = addZero(ElapsedTime.getSeconds(), 2);
        this.minutes = addZero(ElapsedTime.getMinutes(), 2);
        this.hours = addZero(ElapsedTime.getHours(), 2);
        var time = "Time: " + this.hours + ":" + this.minutes + ":" + this.seconds;

        context.font = "16px Arial";
        context.fillStyle = "black";
        context.fillText(message, 10, 25);
        context.fillText(time, 10, 45);
        context.fillText(clickAmount,10,65);
        context.fillText(testRound, 10, 85);
    }
}