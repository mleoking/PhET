/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 8/2/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
import flash.events.TimerEvent;
import flash.utils.Timer;
import flash.utils.getTimer;

/**
 * Model of a single trajectory of a projectile.  Multiple trajectories can be running simultaneously.
 */
public class Trajectory {
    private var stageH: Number;
    private var stageW: Number;
    private var mainModel: MainModel;  //model controlling all the trajectories;
    private var g: Number;          //acceleration of gravity, all units are SI
    private var altitude: Number;   //altitude above sea level in meters
    private var rho: Number;        //density of air in kg/m^3.  At sea level, rho = 1.6 m/s^3
    private var _xP: Number;        //current x- and y_coords of position of projectile in meters
    private var _yP: Number;
    private var _xP0: Number;       //x- and y- coordinates of current position of cannon,
    private var _yP0: Number;       //relative to origin, which is at ground level (y = 0)
    private var xP00: Number;       //x- and y-coordinates of initial position of projectile when projectile was fired
    private var yP00: Number
    private var _vX: Number;        //x- and y-coords of velocity of projectile
    private var _vY: Number;
    private var v: Number;          //current speed of projectile
    private var aX: Number;         //x- and y-components of acceleration
    private var aY: Number;
    private var _vX0: Number;       //x- and y-components of initial velocity
    private var _vY0: Number;
    private var _v0: Number;        //initial speed of projectile
    private var _angleInDeg: Number;    //angle of cannon barrel in degrees, measured CCW from horizontal

    private var _ticMarkTime: Number;   //time of tic Mark, which are at 1 second intervals
    private var _drawTicMarkNow: Boolean;      //flag to indicate that 1-sec tic mark should be drawn on trajectory
    private var _updateReadoutsNow: Boolean;   //flag to indicate that readouts should be updated, 1 update per second

    private var _airResistance: Boolean;  //true if air resistance is on
    private var B: Number;                //parameter that measures drag:  acceleration due to drag = -B*v*v

    private var trajectoryTimer: Timer;	//master millisecond timer
    private var _t: Number;             //time in seconds, projectile fired at t = 0
    public var startTime: Number;       //real time in seconds that the projectile was fired, from flash.utils.getTimer()
    public var previousTime: Number;    //previous real time in seconds that getTimer() was called.
    public var elapsedTime: Number;     //real elapsed time in seconds since previous call to getTimer()
    private var _inFlight: Boolean;     //true if projectile is in flight
    private var _paused: Boolean;       //true if sim is paused
    private var stepsPerFrame: int;     //number of algorithm steps between screen refreshes
    private var frameCounter: int;      //counts algorithm steps between view updates
    private var _tRate:Number;          //Normal time rate: tRate = 1;
    private var dt: Number;             //time step for trajectory algorithm, all times in seconds

    //private var projectileIndex: int;           //index of projectile of this trajectory: projectiles[pIndex] = current projectile
    private var _pIndex: int;           //index of currently selected projectile: projectiles[pIndex] = current projectile
    private var mass0: Number;          //mass, diameter, and drag coefficient of user-controlled projectile
    private var diameter0: Number;
    private var dragCoefficient0: Number;

    public function Trajectory( mainModel: MainModel ) {
        this.mainModel = mainModel;
    }

    private function initialize():void{
        mass0 = 2;        //mass, diameter, and dragCoefficient of user-choice projectile
        diameter0 = 0.1;
        dragCoefficient0 = 1;
//        this._pIndex = 0;
//        this._projectiles = new Array(10);  //10 different projectiles
//        this.initializeProjectileStrings();
//        this.initializeProjectiles();
        this.g = 9.8;            //acceleration of gravity in m/s^2; all units in SI
        this._xP = 0;            //origin is near (but not at) lower left corner of stage
        this._yP = 0;
        this._xP0 = 0;
        this._yP0 = 0;
        this.xP00 = _xP0;
        this.yP00 = _yP0;
        this._vX = 0;
        this._vY = 0;
        this.v = Math.sqrt( _vX*_vX + _vY*_vY );
        this._v0 = 18;
        this._angleInDeg = 45;
        this._vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        this._vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
        this._t = 0;
        this._inFlight = false;
        this._airResistance = false;
        this.rho = 1.6;
        this.setDragFactor();
        this.dt = 0.01;
        this._tRate = 1;
        this.stepsPerFrame = 2;
        this.frameCounter = 0;
        this.trajectoryTimer = new Timer( dt * 1000 );   //argument of Timer constructor is time step in ms
        this.trajectoryTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.updateReadoutsNow = true;
        this.mainModel.updateViews();

    }//end initialize

    /*Set drag factor of current projectile*/
    private function setDragFactor():void{
        var diameter: Number = mainModel.projectiles[_pIndex].diameter;
        var mass: Number = mainModel.projectiles[_pIndex].mass;
        var dragCoefficient: Number = mainModel.projectiles[_pIndex].dragCoefficient;
        var area: Number = Math.PI*diameter*diameter/4;
        if(mainModel.airResistance){
            rho = 1.6;
        } else{    //if no air resistance
            rho = 0;
        }
        B = dragCoefficient*rho*area/mass;
    }

    public function startTrajectory():void{
        this.trajectoryTimer = new Timer( dt * 1000 );   //argument of Timer constructor is time step in ms
        this.trajectoryTimer.addEventListener( TimerEvent.TIMER, stepForward );
    }

    //single-step in time-based animation
    private function stepForward( evt: TimerEvent ):void{
        var currentTime:Number = tRate*getTimer()/1000;
        elapsedTime = currentTime - previousTime;
        previousTime = currentTime;
        if(elapsedTime > 2*tRate*dt){    //if cpu can't keep up, revert to frame-based animation
            elapsedTime = tRate*dt;
        }
        singleStep( elapsedTime );
    }

    private function singleStep( timeStep: Number ):void{
        _t += timeStep;
        frameCounter += 1;
        aX = - B*_vX*v;
        aY = -g - B*_vY*v;
        var dtr: Number = dt*tRate;        //adjusted time increment
        if( B*v*dtr > 0.25 ){
            dtr = dtr/( B*v*dtr/0.25 )
        }
        _xP += _vX * dtr + (0.5) * aX * dtr*dtr;
        _yP += _vY * dtr + (0.5) * aY * dtr*dtr;
        _vX += aX * dtr;
        _vY += aY * dtr;

//        if( !_airResistance ){
//            aX = 0;
//            aY = -g;
//            _xP = xP00 + _vX0*t;
//            _yP = yP00 + _vY0*t + 0.5*aY*t*t;
//            _vX = _vX0;
//            _vY = _vY0 +aY*t;
//        }else{       //if air resistance on
//            aX = - B*_vX*v;
//            aY = -g - B*_vY*v;
//            var dtr: Number = dt*tRate;        //adjusted time increment
//            //if air resistance so large that results are unphysical, then reduce time step
//            if( B*v*dtr > 0.25 ){
//                dtr = dtr/( B*v*dtr/0.25 )
//            }
//            _xP += _vX * dtr + (0.5) * aX * dtr*dtr;
//            _yP += _vY * dtr + (0.5) * aY * dtr*dtr;
//            _vX += aX * dtr;
//            _vY += aY * dtr;
//        }

        v = Math.sqrt( _vX*_vX + _vY*_vY );
        //stop when projectile hits the ground (y = 0)
        if( _yP <= 0 ){
            //must first backtrack from yFinal to exact moment when y = 0
            var vYground: Number = -Math.sqrt( _vY*_vY - 2*aY*_yP );   //vY at y = 0, assuming aY = constant
            var delT: Number;  //time elapsed from y = 0 to y = yFinal
            if( aY != 0 ){
                delT = ( _vY - vYground ) / aY;
            }else{
                delT = -_yP / _vY ;
            }
            _t -= delT;    //exact time when y = 0 (ground)
            _yP = 0;
            var vXground: Number = _vX - aX*delT;    //vX at y = 0, assuming aX = constant
            _xP = _xP - vXground*delT - (0.5)*aX*delT*delT;     //exact value of x when y = 0
            updateReadoutsNow = true;
            mainModel.updateViews();
            //mainView.backgroundView.projectileView.drawProjectileOnGround();
            _inFlight = false;
            trajectoryTimer.stop();
        }//end if (_yP < 0 )

        if( frameCounter > stepsPerFrame ){
            frameCounter = 0;
            mainModel.updateViews();
            //trace( "Trajectory Model:  y = " + this._yP );
        }

        if( t >= ticMarkTime ){
            drawTicMarkNow = true;
            updateReadoutsNow = true;
            mainModel.updateViews();
            ticMarkTime += 1

        }

    }//stepForward()



    public function pause():void{
        this._paused = true;
        this.trajectoryTimer.stop();
    }

    public function unPause():void{
        this._paused = false;
        this.trajectoryTimer.start()
    }

    public function singleStepWhenPaused():void{
        this.singleStep( dt*tRate )
    }




    public function get drawTicMarkNow():Boolean {
        return _drawTicMarkNow;
    }

    //SETTERS and GETTERS
    public function set xP0( xP0: Number ):void{
        this._xP0 = xP0;
        mainModel.updateViews();
    }

    public function get xP0():Number{
        return this._xP0;
    }

    public function set yP0( yP0: Number ):void{
        this._yP0 = yP0;
        mainModel.updateViews();
    }

    public function get yP0():Number{
        return this._yP0;
    }

    public function get xP(): Number{
        return this._xP;
    }

    public function get yP(): Number{
        return this._yP;
    }

    public function get t(): Number{
        return this._t;
    }




    public function set drawTicMarkNow(value:Boolean):void {
        _drawTicMarkNow = value;
    }

    public function get ticMarkTime():Number {
        return _ticMarkTime;
    }

    public function set ticMarkTime(value:Number):void {
        _ticMarkTime = value;
    }

    public function get tRate():Number {
        return _tRate;
    }

    public function set tRate(value:Number):void {
        _tRate = value;
    }

    public function get vX():Number {
        return _vX;
    }

    public function set vX(value:Number):void {
        _vX = value;
    }

    public function get vY():Number {
        return _vY;
    }

    public function set vY(value:Number):void {
        _vY = value;
    }

    public function get updateReadoutsNow():Boolean {
        return _updateReadoutsNow;
    }

    public function set updateReadoutsNow(value:Boolean):void {
        _updateReadoutsNow = value;
    }

    } //end class
} //end package
