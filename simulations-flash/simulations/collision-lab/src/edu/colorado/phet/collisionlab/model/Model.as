//The Model for Collision Lab
package edu.colorado.phet.collisionlab.model {

import edu.colorado.phet.collisionlab.CollisionLab;
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.util.TwoVector;

import edu.colorado.phet.flashcommon.MathUtil;

import flash.events.*;
import flash.geom.*;
import flash.utils.*;

public class Model {
    public var nbrBalls: int;  		//current nbr of interacting balls
    public var ball_arr: Array;		//array of balls
    var startingPos: Array;	//array of initial positions of balls, ResetAll Button
    var startingVel: Array;	//array of initial velocities of balls, ResetAll Button
    public var initPos: Array;		//array of initial positions of balls, after reset, to repeat expt
    public var initVel: Array;		//array of initial velocities of balls, after reset, to repeat expt
    public var CM: Point;			//center-of-mass of system
    var massBall1: Number;	//initial mass of ball 1 (0 index in ball_arr)
    var massBall2: Number;	//initial mass of ball 2 (1 index in ball_arr)
    public var borderOn: Boolean;	//if true, balls elastically reflect from border
    public var borderWidth: Number;	//length of horizontal border in meters
    public var borderHeight: Number;	//current length of vertical border in meters, depends on oneDMode
    public var e: Number;			//elasticity = 0 to 1: 0 = perfectly inelastic, 1 = perfectly elastic
    public var time: Number;		//simulation time in seconds = real time
    var lastTime: Number;	//time of previous step
    var timeStep: Number;	//time step in seconds
    var msTimer: Timer;		//millisecond timer
    var colliding: Boolean;	//true if wall-ball or ball-ball collision has occured in current timestep
    public var playing: Boolean;	//true if motion is playing, false if paused
    public var singleStepping: Boolean; //true if singleStepping forward or backward
    public var soundOn: Boolean;	//true if sound enabled
    public var sounding: Boolean;	//true if click sound is to be played, click sound during collision
    public var nbrBallsChanged: Boolean;  //true if number of balls is changed
    public var atInitialConfig: Boolean;  //true if t = 0;
    var starting: Boolean;	//true if playing and 1st step not yet taken, used to step time-based vs. frame-based animation;
    var reversing: Boolean;	//false if going forward in time, true if going backward
    public var resetting: Boolean;	//true if in process of resetting all (Reset All button on Control Panel);
    public var oneDMode: Boolean;	//true if motions restricted to 1D
    //var realTimer:Timer;	//real timer, used to maintain time-based updates
    var timeHolder: int;		//scatch for hold results of getTimer
    public var timeRate: Number;	//0 to 1: to slow down or speed up action, 1 = realtime, 0 = paused
    var updateRate: int;		//number of time steps between graphics updates
    var frameCount: int;		//when frameCount reaches frameRate, update graphics
    //var colliders:Array;	//2D array of ij pairs: value = 1 if pair colliding, 0 if not colliding.
    var nbrCollisionsInThisTimeStep: int;   //used to check if more than one collision in any single time step
    var view_arr: Array;		//views of this model
    var nbrViews: int;		//number of views

    private var hasAnimationStarted: Boolean = false;

    public var isIntro: Boolean;

    public function Model( isIntro: Boolean ) {
        this.isIntro = isIntro;
        this.borderOn = true;
        this.borderWidth = CLConstants.BORDER_WIDTH;	//units are meters
        this.borderHeight = CLConstants.BORDER_HEIGHT_2D; // TODO: better behavior through setup
        this.e = 1;				//set elasticity of collisions, 1 = perfectly elastic
        if ( CollisionLab.isStudyVersion ) {
            this.e = 0;
        }
        this.massBall1 = 0.5;
        this.massBall2 = 1.5;
        this.oneDMode = false;
        this.CM = new Point();
        this.ball_arr = new Array( CLConstants.MAX_BALLS );  //only first nbrBalls elements of array are used
        this.createInitialBallData();
        this.initializeBalls();
        //this.setCenterOfMass();
        this.time = 0;
        this.timeStep = 0.01;  //time step in seconds
        this.timeRate = 0.5;
        this.updateRate = 1;
        this.frameCount = 0;

        this.msTimer = new Timer( this.timeStep * 1000 );
        msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.stopMotion();
        this.e = 1;				//set elasticity of collisions, 1 = perfectly elastic
        if ( CollisionLab.isStudyVersion ) {
            this.e = 0;
        }
        //this.realTimer = new Timer(1000);  //argument 1000 is irrelevant
        this.view_arr = new Array( 0 );
        this.nbrViews = 0;
        //this.resetAll();

    }//end of constructor

    public function addBall(): void {
        //trace("Model.addBall() called");
        if ( !this.playing ) {
            this.atInitialConfig = true;
        }
        if ( this.nbrBalls < CLConstants.MAX_BALLS ) {
            this.nbrBalls += 1;
            if ( this.oneDMode ) {
                for ( var i: int = 0; i < this.nbrBalls; i++ ) {
                    this.setY( i, 0 ); //this.setY(i, 1);
                    this.setVY( i, 0 );
                }
            }
            this.setTimeToZero();
            //trace("Model.nbrBalls: "+this.nbrBalls);
            this.nbrBallsChanged = true;
            this.separateAllBalls();
            this.setCenterOfMass();
            this.updateViews();
            this.nbrBallsChanged = false;
            //this.ball_arr = new Array(nbrBalls);
            //this.initializePositions();
        }
    }//end of addBall()

    public function removeBall(): void {
        //trace("Model.removeBall()");
        if ( !this.playing ) {
            this.atInitialConfig = true;
        }
        if ( this.nbrBalls > 0 ) {
            this.nbrBalls -= 1;
            this.setTimeToZero();
            this.nbrBallsChanged = true;
            this.setCenterOfMass();
            this.updateViews();
            this.nbrBallsChanged = false;
            //this.ball_arr = new Array(nbrBalls);
            //this.initializePositions();
        }
    }

    public function setReflectingBorder( tOrF: Boolean ): void {
        this.borderOn = tOrF;
    }

    public function setMass( ballNbr: int, mass: Number ): void {
        //trace("Model.setMass() called. ballNbr is "+ballNbr+"   mass is "+mass);
        if ( !this.playing ) {
            this.atInitialConfig = true;
            this.time = 0;
        }
        this.ball_arr[ballNbr].setMass( mass );
        this.separateAllBalls();
        this.setCenterOfMass();
        this.updateViews();
        //this.updateViews();
    }

    //called once, at startup
    public function createInitialBallData(): void {
        this.startingPos = new Array( CLConstants.MAX_BALLS );
        this.startingVel = new Array( CLConstants.MAX_BALLS );
        if ( isIntro ) {
            startingPos[0] = new TwoVector( 1.0, 0 );
            startingPos[1] = new TwoVector( 2.0, 0 );
        }
        else {
            startingPos[0] = new TwoVector( 1.0, 0 );
            startingPos[1] = new TwoVector( 2.0, +0.5 );
        }
        startingPos[2] = new TwoVector( 1, -0.5 );
        startingPos[3] = new TwoVector( 2.2, -1.2 );
        startingPos[4] = new TwoVector( 1.2, +0.8 );
        startingVel[0] = new TwoVector( 0.8, 0 );
        startingVel[1] = new TwoVector( -0.8, 0 );
        startingVel[2] = new TwoVector( -0.5, -0.25 );
        startingVel[3] = new TwoVector( 1.1, 0.2 );
        startingVel[4] = new TwoVector( -1.1, 0 );
    }

    public function initializeBalls(): void {
        this.nbrBalls = 2;		//adjustable by user
        this.atInitialConfig = true;
        this.starting = true;
        this.resetting = false;
        this.soundOn = false;
        this.sounding = false;
        this.initPos = new Array( CLConstants.MAX_BALLS );
        this.initVel = new Array( CLConstants.MAX_BALLS );
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            initPos[i] = startingPos[i].clone();
            initVel[i] = startingVel[i].clone();
        }
        for ( i = 0; i < CLConstants.MAX_BALLS; i++ ) {
            //new Ball(mass, position, velocity);
            this.ball_arr[i] = new Ball( 1.0, initPos[i].clone(), initVel[i].clone() );
        }
        //want initial balls on screen to have different masses.
        this.ball_arr[0].setMass( this.massBall1 );
        this.ball_arr[1].setMass( this.massBall2 );
        //this.nbrBallsChanged = true;
        //No point in updating views, since views not created yet
        this.separateAllBalls(); //should not be necessary, but just in case
        this.setCenterOfMass();
    }//end of initializeBalls()

    public function setTimeToZero(): void {
        this.time = 0;
        //this.initializePositions();
    }

    public function resetAll(): void {
        hasAnimationStarted = false;
        this.resetting = true;
        this.setOneDMode( false );
        this.setReflectingBorder( true );
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            this.ball_arr[i].setBall( 1.0, startingPos[i].clone(), startingVel[i].clone() );
            this.setMass( i, 1.0 );
        }
        this.ball_arr[0].setMass( this.massBall1 );
        this.ball_arr[1].setMass( this.massBall2 );
        this.nbrBalls = 2;
        this.e = 1;			//set elasticity of collisions, 1 = perfectly elastic
        if ( CollisionLab.isStudyVersion ) {
            this.e = 0;
        }
        this.timeRate = 0.5;
        this.nbrBallsChanged = true;
        this.separateAllBalls();
        this.setCenterOfMass();
        this.updateViews();
        this.nbrBallsChanged = false;
        this.resetting = false;
    }

    public function returnBalls(): void {
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            ball_arr[i].setBall( ball_arr[i].mass, startingPos[i].clone(), startingVel[i].clone() );
        }
        stopMotion();
        separateAllBalls();
        setCenterOfMass();
        updateViews();
    }

    public function setOneDMode( tOrF: Boolean ): void {
        this.oneDMode = tOrF;
        if ( this.oneDMode ) {
            this.borderHeight = CLConstants.BORDER_HEIGHT_1D;  // border heigh in meters; reflecting border is resized to give visual clue that 1D mode is ON.
            for ( var i: int = 0; i < this.nbrBalls; i++ ) {
                this.setY( i, 0 );
                this.setVY( i, 0 );
            }
            this.separateAllBalls();
        }
        else { //if 2D mode
            this.borderHeight = CLConstants.BORDER_HEIGHT_2D; //border height in meters
        }
    }

    //called whenever reset button pushed by user or when nbrBalls changes
    public function initializePositions(): void {
        hasAnimationStarted = false;
        this.atInitialConfig = true;
        this.starting = true;
        for ( var i: int = 0; i < this.nbrBalls; i++ ) {
            this.ball_arr[i].position = initPos[i].clone();
            this.ball_arr[i].velocity = initVel[i].clone();
            if ( this.oneDMode ) {
                this.setY( i, 0 );
                this.setVY( i, 0 );
            }
        }
        this.time = 0;
        this.setCenterOfMass();
        this.updateViews();
    }//end of initializePositions()

    public function startMotion(): void {
        this.starting = true;
        this.playing = true;
        this.singleStepping = false;
        this.reversing = false;
        //this.separateAllBalls();  //check if any balls overlapping before starting
        msTimer.start();
        this.atInitialConfig = false;
        updateViews();
    }//startMotion()

    public function stopMotion(): void {
        msTimer.stop();
        this.playing = false;
        this.starting = false;
        updateViews();
    }

    public function setTimeRate( rate: Number ): void {
        this.timeRate = rate;
    }

    public function setElasticity( e: Number ): void {
        this.e = e;
    }

    public function setX( indx: int, xPos: Number ): void {
        if ( isNaN( xPos ) ) {
            var ballNbr: int = indx + 1;
            trace( "ERROR: ball number " + ballNbr + ": xPos is NaN." );
            this.setX( indx, initPos[indx].getX() );
        }
        if ( !this.playing && !this.singleStepping ) {
            this.atInitialConfig = true;
        }
        this.ball_arr[indx].position.setX( xPos );
        if ( this.atInitialConfig ) {
            this.initPos[indx].setX( xPos );
            this.setTimeToZero();
        }
        if ( !hasAnimationStarted ) {
            this.ball_arr[indx].setAsInitialState();
        }
        this.setCenterOfMass();
        if ( !playing ) {this.updateViews();}  //when playing, singleStep() controls updateVeiws
    }

    public function setY( indx: int, yPos: Number ): void {
        if ( isNaN( yPos ) ) {
            var ballNbr: int = indx + 1;
            trace( "ERROR: ball number " + ballNbr + ": yPos is NaN." );
            this.setX( indx, initPos[indx].getY() );
        }
        if ( !this.playing && !this.singleStepping ) {
            this.atInitialConfig = true;
        }
        this.ball_arr[indx].position.setY( yPos );
        if ( this.atInitialConfig ) {
            this.initPos[indx].setY( yPos );
            this.setTimeToZero();
        }
        this.setCenterOfMass();
        if ( !playing ) {this.updateViews();}
    }

    public function setXY( indx: int, xPos: Number, yPos: Number ): void {
        this.setX( indx, xPos );
        this.setY( indx, yPos )
    }

    public function setVX( indx: int, xVel: Number ): void {
        if ( isNaN( xVel ) ) {
            var ballNbr: int = indx + 1;
            trace( "ERROR: ball number " + ballNbr + ": xVel is NaN." );
        }
        xVel = Math.round( xVel * 10 ) / 10;
        if ( !this.playing && !this.singleStepping ) {
            this.atInitialConfig = true;
        }
        this.ball_arr[indx].velocity.setX( xVel );
        if ( this.atInitialConfig ) {
            this.initVel[indx].setX( xVel );
            this.setTimeToZero();
        }
        if ( !hasAnimationStarted ) {
            this.ball_arr[indx].setAsInitialState();
        }
        if ( !playing ) {this.updateViews();}
    }

    public function setVY( indx: int, yVel: Number ): void {
        yVel = Math.round( yVel * 10 ) / 10;
        if ( !this.playing && !this.singleStepping ) {
            this.atInitialConfig = true;
        }
        this.ball_arr[indx].velocity.setY( yVel );
        if ( this.atInitialConfig ) {
            this.initVel[indx].setY( yVel );
            this.setTimeToZero();
        }
        if ( !playing ) {
            this.updateViews();
        }
    }

    public function setVXVY( indx: int, xVel: Number, yVel: Number ): void {
        if ( isNaN( xVel ) ) {
            var ballNbr: int = indx + 1;
            trace( "ERROR: ball number " + ballNbr + ": xVel is NaN." );
        }
        if ( isNaN( yVel ) ) {
            var tempBallNbr: int = indx + 1;
            trace( "ERROR: ball number " + tempBallNbr + ": yVel is NaN." );
        }
        xVel = Math.round( xVel * 10 ) / 10;
        yVel = Math.round( yVel * 10 ) / 10;
        this.ball_arr[indx].velocity.setX( xVel );
        this.ball_arr[indx].velocity.setY( yVel );
        if ( this.atInitialConfig ) {
            this.initVel[indx].setX( xVel );
            this.initVel[indx].setY( yVel );
        }
        if ( !playing ) {this.updateViews();}
    }

    public function stepForward( evt: TimerEvent ): void {
        //need function without event argument
        this.singleStep();
        evt.updateAfterEvent();
    }

    public function singleStep(): void {
        hasAnimationStarted = true;
        this.nbrCollisionsInThisTimeStep = 0;
        if ( this.atInitialConfig ) {this.atInitialConfig = false;}
        var dt: Number;
        if ( playing && !starting && !colliding ) {
            //time-based aminimation
            var realDt: int = getTimer() - timeHolder;
            dt = realDt / 1000;
            //dt = this.timeStep;
        }
        else {
            if ( this.colliding ) {
                //frame-based animation for collision, for stability of algorithm
                dt = this.timeStep;
                //trace("Model collision occured at time = "+this.time);
                this.colliding = false;
            }
            else {
                dt = this.timeStep;
            }
        }
        if ( starting ) {
            this.starting = false;
        }
        dt *= this.timeRate;
        if ( reversing ) {
            dt *= -1;
        }
        if ( !reversing ) {
            this.lastTime = this.time; //should this be here or at end of method?
        }
        this.time += dt;
        for ( var i: int = 0; i < this.nbrBalls; i++ ) {
            var x: Number = this.ball_arr[i].position.getX();
            var y: Number = this.ball_arr[i].position.getY();
            var vX: Number = this.ball_arr[i].velocity.getX();
            var vY: Number = this.ball_arr[i].velocity.getY();
            x += vX * dt;
            y += vY * dt;
            this.setXY( i, x, y );
            //reflect at borders
            checkAndProcessWallCollision( i, x, y, vX, vY );
        }
        this.timeHolder = getTimer();

        this.detectCollision();
        this.frameCount += 1;
        if ( this.frameCount == this.updateRate ) {
            this.frameCount = 0;
            this.updateViews();
        }
        if ( reversing ) {
            this.lastTime = this.time;
        }
        if ( this.nbrCollisionsInThisTimeStep > 1 ) {
            trace( "Model.nbrCollisionsInThisTimeStep = " + this.nbrCollisionsInThisTimeStep )
        }
    }

    //move forward one Frame = several steps
    public function singleFrame(): void {
        for ( var i: int = 0; i < this.updateRate; i++ ) {
            this.singleStep();
        }
    }

    //move backward in time one frame = several steps
    public function backupOneFrame(): void {
        this.reversing = true;
        this.singleFrame();
        this.reversing = false;
    }

    //if ball beyond reflecting border, then translate to back to edge and reflect
    public function checkAndProcessWallCollision( index: int, x: Number, y: Number, vX: Number, vY: Number ): void {
        var wallHit: Boolean = false;
        if ( this.borderOn ) {
            var radius: Number = this.ball_arr[index].getRadius();
            var onePlusDelta: Number = 1.000001;
            if ( (x + radius) > this.borderWidth ) {
                this.setX( index, this.borderWidth - onePlusDelta * radius );
                this.setVX( index, -e * vX );   //ball_arr[i].velocity.setX(-e*vX);
                wallHit = true;
            }
            else {
                if ( (x - radius) < 0 ) {
                    this.setX( index, onePlusDelta * radius );
                    this.setVX( index, -e * vX ); //ball_arr[i].velocity.setX(-e*vX);
                    wallHit = true;
                }
                else {
                    if ( (y + radius) > this.borderHeight / 2 ) {
                        this.setY( index, this.borderHeight / 2 - onePlusDelta * radius );
                        this.setVY( index, -e * vY ); //ball_arr[i].velocity.setY(-e*vY);
                        wallHit = true;
                    }
                    else {
                        if ( (y - radius) < -this.borderHeight / 2 ) {
                            this.setY( index, -this.borderHeight / 2 + onePlusDelta * radius );
                            this.setVY( index, -e * vY ); //ball_arr[i].velocity.setY(-e*vY);
                            wallHit = true;
                        }
                    }
                }
            }
            if ( wallHit ) {
                //trace("wall hit at time t = " + this.time);
                this.nbrCollisionsInThisTimeStep += 1;
                this.playClickSound();
                this.colliding = true;
            }
        }//end if(borderOn)
    }//end of checkWallAndProcessCollision()

    public function detectCollision(): void {
        var N: int = this.nbrBalls;
        //        var nbrBallBallCollisions: int = 0;	//number of collisions during this one time step
        for ( var i: int = 0; i < N; i++ ) {
            for ( var j: int = i + 1; j < N; j++ ) {
                var xi: Number = ball_arr[i].position.getX();
                var yi: Number = ball_arr[i].position.getY();
                var xj: Number = ball_arr[j].position.getX();
                var yj: Number = ball_arr[j].position.getY();
                var dist: Number = Math.sqrt( (xj - xi) * (xj - xi) + (yj - yi) * (yj - yi) );
                var distMin: Number = ball_arr[i].getRadius() + ball_arr[j].getRadius();
                if ( dist < distMin ) {
                    this.nbrCollisionsInThisTimeStep += 1;
                    //trace("elasticity before collision: "+this.e);
                    //trace("collision detected. i = "+i+"   j = "+j+"   at time: "+this.time+"   dist: "+dist+"   distMin: "+distMin);
                    this.collideBalls( i, j );
                    this.colliding = true;
                    //this.colliders[i][j] = 1;  //ball have collided
                }
            }//for(j=..)
        }//for(i=..)
    }//detectCollision

    public function separateAllBalls(): void {
        //trace("separateAllBalls() called at time = "+this.time);
        //loop through all balls repeatedly until no overlap between any pair
        var cntr: int = 0;
        while ( cntr <= 20 ) {
            //allBallsSeparated = true;
            var N: int = this.nbrBalls;
            for ( var i: int = 0; i < N; i++ ) {
                var xi: Number = ball_arr[i].position.getX();
                var yi: Number = ball_arr[i].position.getY();
                this.checkWallCollisionAndSeparate( i, xi, yi );
                //overlappingBalls = false;
                for ( var j: int = i + 1; j < N; j++ ) {
                    var xj: Number = ball_arr[j].position.getX();
                    var yj: Number = ball_arr[j].position.getY();
                    var dist: Number = Math.sqrt( (xj - xi) * (xj - xi) + (yj - yi) * (yj - yi) );
                    var distMin: Number = ball_arr[i].getRadius() + ball_arr[j].getRadius();
                    if ( dist <= distMin ) {
                        trace( "Model: Ball " + i + " and " + j + " overlap at round " + cntr );
                        separateBalls( i, j );
                    }
                }//for(j=..)
            }//for(i=..)
            cntr += 1;
        }//end while()
        //if atInitialConfig, reset initial positions
        if ( atInitialConfig ) {
            for ( i = 0; i < this.nbrBalls; i++ ) {
                this.initPos[i] = this.ball_arr[i].position.clone();
            }
        }
    }//end separateAllBalls();

    //If ball is outside border, translate back in fully, and return wallCollision boolean.
    //Used in separateAllBalls().
    public function checkWallCollisionAndSeparate( index: int, x: Number, y: Number ): void {
        if ( this.borderOn ) {
            var radius: Number = this.ball_arr[index].getRadius();
            if ( (x + radius) > this.borderWidth ) {
                this.setX( index, this.borderWidth - 2 * radius );
            }
            else {
                if ( (x - radius) < 0 ) {
                    this.setX( index, 2 * radius );
                }
                else {
                    if ( (y + radius) > this.borderHeight / 2 ) {
                        this.setY( index, this.borderHeight / 2 - 2 * radius );
                    }
                    else {
                        if ( (y - radius) < -this.borderHeight / 2 ) {
                            this.setY( index, -this.borderHeight / 2 + 2 * radius );
                        }
                    }
                }
            }
        }//end if(borderOn)
    }//end checkWallCollisionAndSeparate()

    public function checkWallCollision( i: int, x: Number, y: Number ): String {
        var collidedWithWall: String = "No";
        if ( this.borderOn ) {
            var radius: Number = this.ball_arr[i].getRadius();
            if ( (x + radius) > this.borderWidth ) {
                collidedWithWall = "R";  //collided with Right Wall
            }
            else {
                if ( (x - radius) < 0 ) {
                    collidedWithWall = "L";  //collided with Left Wall
                }
                else {
                    if ( (y + radius) > this.borderHeight / 2 ) {
                        collidedWithWall = "T";  //collided with Top Wall
                    }
                    else {
                        if ( (y - radius) < -this.borderHeight / 2 ) {
                            collidedWithWall = "B";  //collided with Bottom Wall
                        }
                    }
                }
            }
        }//end if(borderOn)
        return collidedWithWall;
    }//end checkWallCollision

    public function collideBalls( i: int, j: int ): void {
        this.playClickSound();
        //        var balliNbr: String = String( i + 1 );
        //        var balljNbr: String = String( j + 1 );
        //if(colliders[i][j] == 0 && !starting){ //if balls overlapped, but not collided yet, and not first step

        //trace("Model.collideBalls(), between i: " + balliNbr + " and j: " + balljNbr + "  at time "+this.time);
        //Balls have already overlapped, so currently have incorrect positions
        var tC: Number = this.getContactTime( i, j );
        var delTBefore: Number = tC - this.lastTime;
        var delTAfter: Number = this.time - tC;
        if ( isNaN( tC ) ) {trace( "tC is NaN" );}
        //trace("contact time is "+tC);
        var v1x: Number = ball_arr[i].velocity.getX();
        var v2x: Number = ball_arr[j].velocity.getX();
        var v1y: Number = ball_arr[i].velocity.getY();
        var v2y: Number = ball_arr[j].velocity.getY();
        //get positions at tC:
        var x1: Number = ball_arr[i].position.getXLast() + v1x * delTBefore;
        var x2: Number = ball_arr[j].position.getXLast() + v2x * delTBefore;
        var y1: Number = ball_arr[i].position.getYLast() + v1y * delTBefore;
        var y2: Number = ball_arr[j].position.getYLast() + v2y * delTBefore;
        var delX: Number = x2 - x1;
        var delY: Number = y2 - y1;
        var d: Number = Math.sqrt( delX * delX + delY * delY );
        //        var Ri: Number = this.ball_arr[i].getRadius();
        //        var Rj: Number = this.ball_arr[j].getRadius();
        //        var S: Number = Ri + Rj;
        //trace("sum of radii = "+S+"   separation at contact = "+d);
        //normal and tangential components of initial velocities
        var v1n: Number = (1 / d) * (v1x * delX + v1y * delY);
        var v2n: Number = (1 / d) * (v2x * delX + v2y * delY);
        var v1t: Number = (1 / d) * (-v1x * delY + v1y * delX);
        var v2t: Number = (1 / d) * (-v2x * delY + v2y * delX);
        var m1: Number = ball_arr[i].getMass();
        var m2: Number = ball_arr[j].getMass();
        //normal components of velocities after collision (P for prime = after)
        //trace("Model.e: "+this.e);
        var v1nP: Number = ((m1 - m2 * this.e) * v1n + m2 * (1 + this.e) * v2n) / (m1 + m2);
        var v2nP: Number = (this.e + 0.00001) * (v1n - v2n) + v1nP;
        var v1xP: Number = (1 / d) * (v1nP * delX - v1t * delY);
        var v1yP: Number = (1 / d) * (v1nP * delY + v1t * delX);
        var v2xP: Number = (1 / d) * (v2nP * delX - v2t * delY);
        var v2yP: Number = (1 / d) * (v2nP * delY + v2t * delX);
        this.setVXVY( i, v1xP, v1yP );
        this.setVXVY( j, v2xP, v2yP );
        //this.ball_arr[i].velocity.setXY(v1xP, v1yP);
        //this.ball_arr[j].velocity.setXY(v2xP, v2yP);

        //Don't allow balls to rebound after collision during timestep of collision
        //this seems to improve stability
        var newXi: Number = x1 + v1xP * delTAfter;
        var newYi: Number = y1 + v1yP * delTAfter;
        var newXj: Number = x2 + v2xP * delTAfter;
        var newYj: Number = y2 + v2yP * delTAfter;

        this.setXY( i, newXi, newYi );
        this.setXY( j, newXj, newYj );

        //}else{ //end if(colliders[i][j] == 0)
        //if balls already collided, but still not separated, or just starting then pull apart keeping C.M. fixed
        //trace("Model.collideBalls calling separateBalls(): " + balliNbr + " and " + balljNbr+"at t = "+this.time);
        //this.separateBalls(i,j);
        //}//end else
    }//collideBalls

    //Check if balls i, j overlap. If they do, separate them, keeping C.M. fixed.
    public function separateBalls( i: int, j: int ): void {
        var x1: Number = ball_arr[i].position.getX();
        var x2: Number = ball_arr[j].position.getX();
        var y1: Number = ball_arr[i].position.getY();
        var y2: Number = ball_arr[j].position.getY();
        var delX: Number = x2 - x1;
        var delY: Number = y2 - y1;
        var delR: Number = Math.sqrt( delX * delX + delY * delY );
        var R1: Number = this.ball_arr[i].getRadius();
        var R2: Number = this.ball_arr[j].getRadius();
        var S: Number = R1 + R2;
        var OL: Number = (R1 + R2) - delR;  //overlap distance
        if ( OL > 0 ) {
            //            var balliNbr: int = i + 1;
            //            var balljNbr: int = j + 1;
            //trace("Model.separateBalls: ball numbers "+balliNbr+" and "+balljNbr+" overlap: "+OL+"  delX: "+delX+"  delY: "+delY);
            //trace("Model: ball numbers "+balliNbr+" and "+balljNbr+" overlapped at time "+this.time);
            var m1: Number = ball_arr[i].getMass();
            var m2: Number = ball_arr[j].getMass();
            //trace("overlap is "+OL);
            var extraBit: Number = 0.04 * S;
            OL = OL + extraBit;
            if ( delR == 0 ) {//to prevent delXBall or delYBall from becoming NaN
                delX = 1;
                delR = 1;
            }
            var delXBall1: Number = -m2 * OL * delX / (delR * (m1 + m2));
            var delYBall1: Number = -m2 * OL * delY / (delR * (m1 + m2));
            var delXBall2: Number = m1 * OL * delX / (delR * (m1 + m2));
            var delYBall2: Number = m1 * OL * delY / (delR * (m1 + m2));
            var iHitWall: String = this.checkWallCollision( i, x1 + delXBall1, y1 + delYBall1 );
            var jHitWall: String = this.checkWallCollision( j, x2 + delXBall2, y2 + delYBall2 );
            //trace("Ball "+ balliNbr+ " hit wall "+ iHitWall + ".  Ball " + balljNbr +" hit wall " + jHitWall);
            var wallXOffset: Number = 0;  //translate both balls away from colliding wall
            var wallYOffset: Number = 0;
            if ( iHitWall == "T" || jHitWall == "T" ) {
                wallYOffset = -(R1 + R2);
            }
            else {
                if ( iHitWall == "B" || jHitWall == "B" ) {
                    wallYOffset = R1 + R2;
                }
            }
            if ( iHitWall == "L" || jHitWall == "L" ) {
                wallXOffset = R1 + R2;
            }
            else {
                if ( iHitWall == "R" || jHitWall == "R" ) {
                    wallXOffset = -(R1 + R2);
                }
            }
            //trace("calling setXY() from separateBalls(i,j)");
            //trace("ball "+i+"  x1 = "+x1+"  delXBall1 = "+delXBall1+"  wallXOffset= "+wallXOffset);
            this.setXY( i, x1 + delXBall1 + wallXOffset, y1 + delYBall1 + wallYOffset );
            this.setXY( j, x2 + delXBall2 + wallXOffset, y2 + delYBall2 + wallYOffset );
            this.ball_arr[i].position.initializeXLastYLast();
            this.ball_arr[j].position.initializeXLastYLast();
            this.updateViews();
        }
    }//end separateBalls(i, j);

    //get contact time between balls i and j
    public function getContactTime( i: int, j: int ): Number {
        var tC: Number;  //contact time
        //tC = this.lastTime;
        var x1: Number = this.ball_arr[i].position.getXLast();
        var y1: Number = this.ball_arr[i].position.getYLast();
        var x2: Number = this.ball_arr[j].position.getXLast();
        var y2: Number = this.ball_arr[j].position.getYLast();
        var v1x: Number = this.ball_arr[i].velocity.getX();
        var v1y: Number = this.ball_arr[i].velocity.getY();
        var v2x: Number = this.ball_arr[j].velocity.getX();
        var v2y: Number = this.ball_arr[j].velocity.getY();
        var delX: Number = x2 - x1;
        var delY: Number = y2 - y1;
        var delVx: Number = v2x - v1x;
        var delVy: Number = v2y - v1y;
        var delVSq: Number = delVx * delVx + delVy * delVy;
        var R1: Number = this.ball_arr[i].getRadius();
        var R2: Number = this.ball_arr[j].getRadius();
        var SSq: Number = (R1 + R2) * (R1 + R2);		//square of center-to-center separation of balls at contact
        var delRDotDelV: Number = delX * delVx + delY * delVy;
        var delRSq: Number = delX * delX + delY * delY;
        //trace("Model.getContactTime(). DelVsq = "+ delVSq);
        //trace("Model.getContactTime(). delRDotDelV = "+ delRDotDelV);
        //trace("Model.getContactTime(). delRSq = "+ delRSq);

        var underSqRoot: Number = delRDotDelV * delRDotDelV - delVSq * (delRSq - SSq);
        //if collision is superslow, then set collision time = half-way point since last time step
        //of if tiny number precision causes number under square root to be negative
        if ( delVSq < 0.000000001 || underSqRoot < 0 ) {
            tC = this.lastTime + 0.5 * (this.time - this.lastTime);
            //
            trace( "delVSq<0.000000001 or underSqRoot < 0, tC is " + tC );
        }
        else { //if collision is normal
            if ( reversing ) {
                var delT: Number = (-delRDotDelV + Math.sqrt( delRDotDelV * delRDotDelV - delVSq * (delRSq - SSq) )) / delVSq;
            }
            else {
                delT = (-delRDotDelV - Math.sqrt( delRDotDelV * delRDotDelV - delVSq * (delRSq - SSq) )) / delVSq;
            }
            tC = this.lastTime + delT;
        }
        //trace("getContactTime: this.lastTime = "+this.lastTime+"  this.time: "+this.time+"   delT:"+delT+"   tC: "+tC);
        //trace("delRDotDelV: "+delRDotDelV+"  delVSq: "+delVSq+"   delRSq: "+delRSq+"   SSq: "+SSq+"   delVSq: "+delVSq);
        if ( isNaN( this.lastTime ) ) {
            trace( "this.lastTime = " + this.lastTime )
        }

        return tC;
    }//end getContactTime()

    public function getTotalKE(): Number {
        var KETot: Number = 0;
        for ( var i: Number = 0; i < this.nbrBalls; i++ ) {
            KETot += this.ball_arr[i].getKE();
        }
        return KETot;
    }//getKETotal

    public function getTotalMomentum(): TwoVector {
        var pX: Number = 0;	//x-component of momentum
        var pY: Number = 0;	//y-component
        for ( var i: Number = 0; i < this.nbrBalls; i++ ) {
            pX += this.ball_arr[i].getMomentum().getX();
            pY += this.ball_arr[i].getMomentum().getY();
        }
        return new TwoVector( pX, pY );
    }

    public function setCenterOfMass(): void {
        var totMass: Number = 0;
        var sumXiMi: Number = 0;
        var sumYiMi: Number = 0;
        for ( var i: int = 0; i < this.nbrBalls; i++ ) {
            var m: Number = this.ball_arr[i].getMass();
            var x: Number = this.ball_arr[i].position.getX();
            var y: Number = this.ball_arr[i].position.getY();
            totMass += m;
            sumXiMi += m * x;
            sumYiMi += m * y;
        }
        this.CM.x = sumXiMi / totMass;
        this.CM.y = sumYiMi / totMass;
    }//end setCenterOfMass();

    private function playClickSound(): void {
        if ( soundOn ) {
            this.sounding = true;
            this.updateViews();
            this.sounding = false;
        }
    }

    public function registerView( aView: Object ): void {
        this.nbrViews += 1;
        this.view_arr.push( aView );
    }

    public function updateViews(): void {
        //trace("Model.updateViews called at time = "+this.time);
        for ( var i: int = 0; i < nbrViews; i++ ) {
            this.view_arr[i].update();
        }
    }

}//end of class
}//end of package