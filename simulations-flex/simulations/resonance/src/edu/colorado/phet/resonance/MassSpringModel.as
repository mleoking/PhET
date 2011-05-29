package edu.colorado.phet.resonance {
//model of a mass on a spring, with one end of spring fixed, other end attached to mass

import flash.events.*;
import flash.utils.*;

//contains Timer class

public class MassSpringModel {

    private var view: MassSpringView;	//view associated with this model
    public var shakerModel: ShakerModel;
    private var rNbr: int;		//interger labeling the resonator, 1, 2, 3,
    private var m: Number; 		//mass in kg
    private var k: Number; 		//spring constant in N/m
    private var f: Number;		//resonant frequency when b = 0
    private var L0: Number;		//unstretched length of spring in meters
    private var y0: Number;		//position of fixed end of spring, +direction of y is up(opposite of flash stage coordinate)
    private var y: Number;		//position of mass end of spring
    private var v: Number;		//velocity of mass
    private var b: Number;		//drag constant; f_drag = - b*v
    private var g: Number;		//magnitude accleration due to gravity
    private var t: Number;		//time in seconds
    private var lastTime: Number;	//time of previous timeStep
    private var tRate: Number;	//1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	//time step in seconds
    private var msTimer: Timer;	//millisecond timer
    private var starting: Boolean;	//true if in 1st time step after start

    public function MassSpringModel( shakerModel: ShakerModel, rNbr: int, m: Number, f: Number, L0: Number, b: Number ) {
        this.shakerModel = shakerModel;
        this.rNbr = rNbr;
        this.m = m;
        this.f = f;
        this.L0 = L0;
        this.b = b;
        //this.m = this.k/((2*Math.PI*this.f)*(2*Math.PI*this.f));
        this.k = m * (2 * Math.PI * f) * (2 * Math.PI * f);   //omega^2 = (2*pi*f)^2 = k/m
        var pi: Number = Math.PI;
        var fR: Number = (1 / (2 * pi)) * Math.sqrt( this.k / this.m - this.b * this.b / (2 * this.m * this.m) );
        //trace("MassSpringModel constructor. f = "+f+  "   fR = "+ fR +"   k = "+ this.k + "   this.m = "+this.m);
        this.dt = this.shakerModel.getDt();		//default time step in seconds
        this.lastTime = 0;
        this.tRate = 1;
        this.initialize();
    }//end of constructor

    private function initialize(): void {
        this.g = 0;
        this.y0 = 0;
        this.y = y0 + L0 - (m * g / k);  //start with mass hanging in equilibrium position
        this.v = 0;
        this.t = 0;    //currently unused
        this.msTimer = new Timer( this.dt * 1000 );
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.stopMotion();
    }//end of initialize()

    public function resetToZeroPositionAndSpeed():void{
        this.g = 0;
        this.y0 = 0;
        this.y = y0 + L0 - (m * g / k);  //start with mass hanging in equilibrium position
        this.v = 0;
    }

    public function registerView( view: MassSpringView ): void {
        this.view = view;
    }

    public function getRNbr(): int {
        return this.rNbr;
    }

    public function setG(g:Number):void{
        this.g = g;
    }

    public function getL0(): Number {
        return this.L0;
    }

    public function setL0( newVal: Number ): void {
        this.L0 = newVal;
    }

    public function setB( b: Number ): void {
        this.b = b;
    }

    public function getM(): Number {
        return this.m;
    }

    public function setM( m: Number ): void {
        this.m = m;
        this.view.drawMass();
    }

    public function getY(): Number {
        return this.y;
    }

    public function setY( yInMeters: Number ): void {
        this.y = yInMeters;
        //trace("MassSpringModel.y = "+this.y);
        this.updateView();
    }

    public function getY0(): Number {
        return this.y0;
    }

    public function setY0( yInMeters: Number ): void {
        this.y0 = yInMeters;
        //trace("MassSpringModel.y = "+this.y);
        this.updateView();
    }

    public function getK(): Number {
        return this.k;
    }

    public function setK( k: Number ): void {
        this.k = k;
        this.view.drawSpring();
    }

    public function getF0(): Number {
        var f0: Number = (1 / (2 * Math.PI)) * Math.sqrt( this.k / this.m );
        return f0;
    }

    public function getFRes(): Number {
        var fRes: Number = (1 / (2 * Math.PI)) * Math.sqrt( this.k / this.m - this.b * this.b / (2 * this.m * this.m) );
        //var f0: Number = (1 / (2 * Math.PI)) * Math.sqrt( this.k / this.m );
        return fRes;
    }

    public function setTRate(rate:Number):void{
        this.tRate = rate;
        if(rate > 1 || tRate < 0){
            trace("ERROR: tRate is out-of-bounds.") ;
        }else{
          
        }
    }

    public function stepForward( evt: TimerEvent ): void {
        var currentTime = getTimer() / 1000;
        var realDt: Number = currentTime - this.lastTime;
        this.lastTime = currentTime;
        if ( realDt > 0.04 ) {
            realDt = 0.04;
        }
        var dt:Number = this.tRate*realDt;
        //need function without event argument
        this.singleStep( dt );
        evt.updateAfterEvent();
    }//stepForward

    public function singleStep( dt: Number ): void {
        var dtr: Number = dt; //dt in seconds
        //Verlet algorithm, same as used in Mass Spring Lab

        var a: Number = -g - (k / m) * (y - y0 - L0) - (b / m) * v;
        y = y + v * dtr + (1 / 2) * a * dtr * dtr;

        var vp:Number = v + a * dtr;		//post velocity
        var ap: Number = -g - (k / m) * (y - y0 - L0) - (b / m) * vp;		//post-acceleration
        v = v + 0.5 * (a + ap) * dtr;
        //following code used if block collides inelastically with platform
//        if( y < y0 ){
//            y = y0;
//            v = 0.2*Math.abs(v); //reverse velocity inelastically if block hits shaker platform
//        } else{
//            v = v + 0.5 * (a + ap) * dtr;
//        }


        //this.oldT = this.t;
        //test code
        if(this.rNbr == 5){
            //trace("rNbr = " + rNbr + "   a = " + a + "   v =v" + v + "   y = " + y);
        }
        this.updateView();
    }//end of singleStep()

    public function startMotion(): void {
        //this.v = 0;  //always start from rest
        this.starting = true;
        msTimer.start();
    }

    public function stopMotion(): void {
        msTimer.stop();
        //trace("MassSpringModel.stopMotion() called on resonator "+this.rNbr);
    }

    public function updateView(): void {
        this.view.update();
    }

}//end of class
}//end of package