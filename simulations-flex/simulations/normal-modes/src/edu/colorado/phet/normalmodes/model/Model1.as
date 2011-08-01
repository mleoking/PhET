package edu.colorado.phet.normalmodes.model {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.flexcommon.AboutDialog;
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.view.View1;

//model of 1D array of coupled masses and springs

import flash.events.*;
import flash.utils.*;

public class Model1 {

    public var views_arr:Array;     //views associated with this model
    //physical variables
    private var m:Number;           //mass in kg of each mass in array (all masses equal)
    private var k:Number;           //spring constant in N/m of each spring in array (all springs equal)
    private var b:Number;           //damping constant: F_drag = -b*v, currently unused
    private var _L:Number;          //distance between fixed walls in meters
    private var _nMax:int;          //maximum possible number of mobile masses in 1D array
    private var _N:int;             //number of mobile masses in 1D array; does not include the 2 virtual stationary masses at wall positions
    private var _nChanged:Boolean;  //flag to indicate number of mobile masses has changed, so must update view
    private var _modesChanged:Boolean;//flag to indicate that mode amplitudes and phases have been zeroed, so must update Slider positions
    //public var nbrStepsSinceRelease:int; //number of time steps since one of the masses was ungrabbed;
    private var x0_arr:Array;       //array of equilibrium x-positions of masses; array length = N + 2, since 2 stationary masses at x = 0 and x = L
    private var y0_arr:Array;       //array of equilibrium y-positions of masses, all = 0
    private var s_arr:Array;        //array of s-positions of masses, s = distance from equilibrium positions in either x or y-direction
    private var v_arr:Array;        //array of velocities of masses, array length = N+2, elements 0 and N+1 have value zero
    private var a_arr:Array;        //array of accelerations of masses,
    private var aPre_arr:Array;     //array of accelerations in previous time step, needed for velocity verlet
    private var modeOmega_arr:Array; //array of normal mode angular frequencies, omega = 2*pi*f
    public var freqDividedByOmega_arr:Array;  //frequency coefficients
    private var modeAmpli_arr:Array;//array of normal mode amplitudes
    private var modePhase_arr:Array;//array of normal mode phases
    private var _grabbedMassIndex:int;      //index of mass grabbed by mouse
    //private var _longitudinalMode:Boolean;  //true if in longitudinal mode, false if in transverse mode
    private var _xModes:Boolean;    //true if x-motion modes only; false if y-motion modes only

    //time variables
    private var _paused: Boolean;   //true if sim paused
    private var _interrupted:Boolean; //model interrupted when model unPaused and user switches to other tab/model
    private var _t: Number;		    //time in seconds
    //private var tInt: Number;     //time rounded up to nearest second, for testing only
    private var lastTime: Number;	//time in previous timeStep
    private var tRate: Number;	    //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	    //default time step in seconds
    private var msTimer: Timer;	    //millisecond timer

    public function Model1( ) {
        this.views_arr = new Array();
        this.x0_arr = new Array(_nMax + 2);     //_nMax = max nbr of mobile masses, +2 virtual stationary masses at ends
        this.y0_arr = new Array(_nMax + 2);
        this.s_arr = new Array(_nMax + 2);
        this.v_arr = new Array(_nMax + 2);
        this.a_arr = new Array(_nMax + 2);
        this.aPre_arr = new Array(_nMax + 2);
        this.modeOmega_arr = new Array( _nMax );
        this.freqDividedByOmega_arr = new Array( _nMax );
        this.modeAmpli_arr = new Array( _nMax );
        this.modePhase_arr = new Array( _nMax );
        this.initialize();
    }//end of constructor

    private function initialize():void{
        this._nMax = 10;             //maximum of 10 mobile masses in array
        this._N = 5;                 //start with 5 mobile masses
        this._nChanged = false;
        this._modesChanged = false;
        //this.nbrStepsSinceRelease = 10;  //just needs to be larger than 3
        this.m = 0.1;               //100 gram masses
        this.k = this.m*4*Math.PI*Math.PI;  //k set so that period of motion is about 1 sec
        this.b = 0;                 //initial damping = 0, F_drag = -b*v
        this._L = 1;                //1 meter between fixed walls
        this._grabbedMassIndex = 0;      //left mass (index 0) is always stationary
        //this._longitudinalMode = true;
        this._xModes = false;
        this.initializeKinematicArrays();
        this.initializeModeArrays();
        //this.setInitialPositions(); //for testing only
        this._paused = true;
        this._interrupted = false;
        this._t = 0;
        //this.tInt = 1;              //testing only
        this.dt = 0.01;
        this.tRate = 1;
        this.msTimer = new Timer( this.dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        //this.startMotion();
    }//end initialize()

    public function initializeKinematicArrays():void{
        var arrLength:int = this._N + 2;
        for(var i:int = 0; i < arrLength; i++){
            this.x0_arr[i] = i*this._L/(this._N + 1);  //space masses evenly between x = 0 and x = L
            this.y0_arr[i] = 0;
            this.s_arr[i] = 0;
            this.v_arr[i] = 0;                      //initial velocities = 0;
            this.a_arr[i] = 0;                      //initial accelerations = 0
            this.aPre_arr[i] = 0;
        }
        //reset time
        this._t = 0;
        //this.computeModeAmplitudesAndPhases();
    }//end initializeKinematicArrays()

    private function initializeModeArrays():void{
        for(var i:int = 0; i < _nMax; i++){
            modeAmpli_arr[i] = 0;
            modePhase_arr[i] = 0;
        }
        this.setResonantFrequencies();
    }

    private function setResonantFrequencies():void{
        var omega0:Number = Math.sqrt( k/m );
        for(var i:int = 0; i < _N; i++){
            var j:int = i + 1;
            modeOmega_arr[i] = 2*omega0*Math.sin( j*Math.PI/(2*(_N + 1 )));
            freqDividedByOmega_arr[i] = modeOmega_arr[i]/omega0;
        }
    }

    public function zeroModeArrays():void{
        for(var i:int = 0; i < _nMax; i++){
            modeAmpli_arr[i] = 0;
            modePhase_arr[i] = 0;
        }
        this._modesChanged = true;
        updateViews();
        this._modesChanged = false;
    }

    //SETTERS and GETTERS
    public function setN(nbrOfMobileMasses:int):void{
        if(nbrOfMobileMasses > this._nMax){
            this._N = this._nMax;
            trace("ERROR: nbr of masses too high");
        }else if(nbrOfMobileMasses < 1){
            this._N = 1;
            trace("ERROR: nbr of masses too low");
        }else{
           this._N = nbrOfMobileMasses;
        }
        //trace("Model1.setN called N = " + this._N);
        this.initializeKinematicArrays();
        this.zeroModeArrays();
        this.setExactPositions();
        this.setResonantFrequencies();
        this._nChanged = true;
        this.updateViews();
        this._nChanged = false;
    }//end setN

    public function get N():int {
        return this._N;
    }

    public function get L():Number{
      return this._L;
    }

    public function get nMax():int {
        return this._nMax;
    }


    public function get modesChanged():Boolean{
        return this._modesChanged;
    }

    public function set modesChanged( tOrF: Boolean ):void{
        this._modesChanged = tOrF;
    }

    public function get nChanged():Boolean{
        return this._nChanged;
    }

    public function set nChanged( tOrF: Boolean ):void{
        this._nChanged = tOrF;
    }

    //called from MassView dragTarget
    public function setX(i:int, xPos:Number):void{
        if( _xModes ){
            var sPos:Number = xPos - this.x0_arr[i];
            this.s_arr[i] = sPos;
            this.updateViews();
        }
        //if( this._paused ){
        //   this.updateViews();
        //}
    }

    public function getX(i:int):Number{
        var xPos:Number;
        if( _xModes ){
           xPos = this.x0_arr[i] + this.s_arr[i];
        }else{
           xPos = this.x0_arr[i];
        }
        return xPos ;
    }

    //used when in transverse mode
    public function setY(i:int, yPos:Number):void{
        if(!_xModes){
            this.s_arr[i] = yPos;
            this.updateViews();   //needed in case that sim is paused
        }
    }

    public function getY(i:int):Number{
        var yPos:Number;
        if(!_xModes){     //if tranverse mode
            yPos = this.s_arr[i];
        }else{
            yPos = 0;
        }
        return yPos
    }//end getY()

//    public function get longitudinalMode():Boolean{
//        return this._longitudinalMode;
//    }

    //currently unused, because model has no damping
    public function setB(b:Number):void{
        if(b < 0 || b > 2*Math.sqrt(this.m*this.k)){         //if b negative or if b > critical damping value
            trace("ERROR: damping constant out of bounds")
        }
        this.b = b;
    }

    //get/set polarization in x-direction or y-direction
    public function get xModes():Boolean{
        return this._xModes;
    }

    public function set xModes( tOrF:Boolean ):void{
        this._xModes = tOrF;
        this.updateViews();
    }

    public function setModeAmpli( modeNbr:int, A:Number ):void{
        this.modeAmpli_arr[ modeNbr - 1 ] = A;
        this.setExactPositions();
        this._modesChanged = true;
        updateViews();
        this._modesChanged = false;
    }

    public function getModeAmpli( modeNbr:int ):Number{
        return this.modeAmpli_arr[ modeNbr - 1];
    }

    public function setModePhase( modeNbr:int,  phase:Number ):void{
        this.modePhase_arr[ modeNbr - 1 ] = phase;
        this.setExactPositions();
        this._modesChanged = true;
        updateViews();
        this._modesChanged = false;
    }

    public function getModePhase( modeNbr:int ):Number{
        return this.modePhase_arr[ modeNbr - 1 ];
    }

    public function getModeOmega( modeNbr:int ):Number{
        return this.modeOmega_arr[ modeNbr - 1];
    }

    public function setTRate(rate:Number):void{
        this.tRate = rate;
    }

    public function get t(): Number {
        return this._t;
    }

    public function set t( time:Number ):void{
        this._t = time;
        this.setExactPositions();
        this.updateViews();
    }

    public function get paused():Boolean {
        return this._paused;
    }

    public function set interrupted( tOrF:Boolean ):void{
        this._interrupted = tOrF;
    }

    //called form MassView.startTargetDrag();
    public function set grabbedMass(indx:int){
        this._grabbedMassIndex = indx;
    }


    //END SETTERS and GETTERS
    public function pauseSim(): void {
        if( !this._paused ){
            this._paused = true;
            this.msTimer.stop();
            this.updateViews();
        }
    }

    public function unPauseSim(): void {
        if( this._paused ){
            this._paused = false;
            this.msTimer.start();
            this.updateViews();
        }
    }

    //used when switching tabs between 1D and 2D
    public function interruptSim():void{
        if( !_paused ){
            pauseSim();
            _interrupted = true;
        }
    }

    public function resumeSim():void{
        if( this._interrupted ){
            this.unPauseSim();
            this._interrupted = false;
        }
    }

    public function startMotion(): void {
        //trace("Model.startMotion called.");
        if ( !this._paused ) {
            this.msTimer.start();
        }
    }

    public function stopMotion(): void {
        if ( !this._paused ) {
            this.msTimer.stop();
        }
    }

    private function stepForward( evt: TimerEvent ): void {
        //need function without event argument
        this.singleStep();
        evt.updateAfterEvent();
    }

    public function computeModeAmplitudesAndPhases():void{
        this._t = 0;
        var N:int  = this._N;
        var mu:Array = new Array( this._N );
        var nu:Array = new Array( this._N );
        for (var r:int = 1; r <= N; r++ ){
            mu[ r-1 ] = 0;
            nu[ r-1 ] = 0;
            for (var i:int = 1; i <= N; i++ ){
                mu[ r-1 ] += ((2)/(N+1))*s_arr[i]*Math.sin(i*r*Math.PI/(N+1));
                nu[ r-1 ] += (-2/(this.modeOmega_arr[r-1]*(N+1)))*v_arr[i]*Math.sin(i*r*Math.PI/(N+1));
            }
            this.modeAmpli_arr[ r - 1] = Math.sqrt( mu[r-1]*mu[r-1] + nu[r-1]*nu[r-1] );
            this.modePhase_arr[ r - 1 ] = Math.atan2( nu[ r-1 ], mu[ r-1 ]) ;
        }
        this._modesChanged = true;
        this.updateViews();
        this._modesChanged = false;
    }//computeModeAmplitudesAndPhases();

    private function singleStep(): void {
        var currentTime:Number = getTimer() / 1000;              //flash.utils.getTimer()
        var realDt: Number = currentTime - this.lastTime;
        this.lastTime = currentTime;
        //time step must not exceed 0.04 seconds.
        //If time step < 0.04 s, then sim uses time-based animation. Else uses frame-based animation
        if ( realDt < 0.04 ) {
            this.dt = this.tRate * realDt;
        }
        else {
            this.dt = this.tRate * 0.04;
        }
        this._t += this.dt;

        if(this._grabbedMassIndex != 0 ){     //if user has grabbed some mass with mouse
            this.setVerletPositions();
        }else {
           this.setExactPositions();
        }
        this.updateViews();
    } //end singleStep()

    private function setVerletPositions():void{       //velocity verlet algorithm
        for(var i:int = 1; i <= this._N; i++){      //loop thru all mobile masses  (masses on ends always stationary)
            if(i != this._grabbedMassIndex ){            //grabbed mass position determined by mouse, not by this algorithm
                s_arr[i] = s_arr[i] + v_arr[i] * dt + (1 / 2) * a_arr[i] * dt * dt;
                aPre_arr[i] = a_arr[i];   //store current accelerations for next step
            }
            //var vp:Number = v_arr[i] + a_arr[i] * dt;		//post velocity, only needed if computing drag
        }//end 1st for loop

        for( i = 1; i <= this._N; i++){             //loop thru all mobile masses  (masses on ends always stationary)
            if( i != this._grabbedMassIndex ){           //grabbed mass position determined by mouse, not by this algorithm
               this.a_arr[i] = (this.k/this.m)*(s_arr[i+1] + s_arr[i-1] - 2*s_arr[i]);		//post-acceleration
                v_arr[i] = v_arr[i] + 0.5 * (this.aPre_arr[i] + a_arr[i]) * dt;
            }
        }//end 2nd for loop
    }

    private function setExactPositions():void{
        for(var i:int = 1; i <= this._N; i++){          //step thru N mobile masses
            s_arr[i] = 0
            for( var r:int = 1; r <= this._N; r++){     //step thru N normal modes
               var j:int = r - 1;
               this.s_arr[i] +=  modeAmpli_arr[j]*Math.sin(i*r*Math.PI/(_N + 1))*Math.cos(modeOmega_arr[j]*this._t - modePhase_arr[j]);
            }
        }
    }

    public function singleStepWhenPaused():void{
        this.dt = this.tRate * 0.02;
        this._t += this.dt;
        this.singleStep( );
        updateViews();
    }

    public function registerView( view: Object ): void {
        //this.view = view;    //only one view, so far
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
        //this.view.update();
    }//end updateView()

}//end of class
}//end of package