/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/16/12
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
import edu.colorado.phet.projectilemotionflex.view.MainView;

import flash.events.TimerEvent;

import flash.utils.Timer;

public class TrajectoryModel {

    public var views_arr: Array;     //views associated with this model
    public var mainView: MainView;
    private var stageH: Number;
    private var stageW: Number;
    private var g: Number;      //acceleration of gravity
    private var _xP: Number;    //x- and y_coords of position of projectile in meters
    private var _yP: Number;
    private var _xP0: Number;   //x- and y- coordinates of initial position of projectile
    private var _yP0: Number;
    private var vX: Number;     //x- and y-coords of velocity of projectile
    private var vY: Number;
    private var _vX0: Number;   //x- and y-components of initial velocity
    private var _vY0: Number;
    private var _v0: Number;      //speed of projectile
    private var _mP: Number;    //mass of projectile in kg
    private var _angleInDeg: Number;  //angle of cannon barrel in degrees, measured CCW from horizontal
    private var _theta: Number; //initial angle of projectile, in radians, measured CCW from horizontal
    private var _t: Number;     //time in seconds, projectile fired at t = 0
    private var _inFlight: Boolean;  //true if projectile is in fligt
    private var stepsPerFrame: int;      //number of algorithm steps between screen updates
    private var frameCounter: int;
    private var _tRate:Number;   //Normal time rate: tRate = 1;
    private var dt: Number;     //time step for trajectory algorithm, all times in seconds

    private var msTimer: Timer;	        //millisecond timer



    public function TrajectoryModel( mainView: MainView ) {
        this.mainView = mainView;
        this.views_arr = new Array();
        this.stageW = this.mainView.stageW;
        this.stageH = this.mainView.stageH;
        this.initialize();
    }

    private function initialize():void{
        this.g = 9.8;
        this._xP = 0;            //origin is at lower left
        this._yP = 0;
        this._xP0 = 0.2*stageW/mainView.pixPerMeter;
        this._yP0 = 4;
        this.vX = 0;
        this.vY = 0;
        this._v0 = 18;
        this._angleInDeg = 45;
        this._vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        this._vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
        this._t = 0;
        this._inFlight = false;
        this.dt = 0.01;
        this._tRate = 1;
        this.stepsPerFrame = 2;
        this.frameCounter = 0;
        this.msTimer = new Timer( dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.updateViews();
    }

    public function fireCannon():void{
        _xP = xP0;
        _yP = yP0;
        vX = _vX0;
        vY = _vY0;
        this._t = 0;
        msTimer.start();
    }

    private function stepForward( evt: TimerEvent ):void{
        _t += dt;
        frameCounter += 1;
        var aX: Number = 0;
        var aY: Number = -g;
        _xP += vX * dt + (0.5) * aX * dt*dt;
        vX += aX * dt;
        _yP += vY * dt + (0.5) * aX * dt*dt;
        vY += aY * dt;
        if( frameCounter > stepsPerFrame ){
            frameCounter = 0;
            this.updateViews();
            this._inFlight = true;
            //trace( "Trajectory Model:  y = " + this._yP );
        }
        if( _yP <= 0 ){
            _inFlight = false;
            msTimer.stop();
            trace("timer stopped")
        }
    }//stepForward()

    public function registerView( view: Object ): void {
        this.views_arr.push( view );
    }

    public function unregisterView( view: Object ):void{
        var indexLocation:int = -1;
        indexLocation = this.views_arr.indexOf( view );
        if( indexLocation != -1 ){
            this.views_arr.splice( indexLocation, 1 )
        }
    }

    public function updateViews(): void {
        for(var i:int = 0; i < this.views_arr.length; i++){
            this.views_arr[ i ].update();
        }
    }//end updateView()

    //SETTERS and GETTERS
    public function set xP0( xP0: Number ):void{
        this._xP0 = xP0;
        this.updateViews();
    }

    public function get xP0():Number{
        return this._xP0;
    }

    public function set yP0( yP0: Number ):void{
        this._yP0 = yP0;
        this.updateViews();
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

    public function get tRate():Number {
        return _tRate;
    }

    public function set tRate(value:Number):void {
        _tRate = value;
    }

    public function get angleInDeg():Number {
        return _angleInDeg;
    }

    public function set angleInDeg(value:Number):void {
        _angleInDeg = value;
        _vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        _vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
        this.updateViews();
    }

    public function get v0():Number {
        return _v0;
    }

    public function set v0( value:Number ):void {
        _v0 = value;
        _vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        _vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
    }

    public function get inFlight():Boolean {
        return _inFlight;
    }
}//end class
}//end package
