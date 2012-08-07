/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.model {
import edu.colorado.phet.radiatingcharge.view.MainView;

import flash.events.TimerEvent;

import flash.utils.Timer;
import flash.utils.getTimer;

import mx.rpc.AbstractInvoker;

//model of radiating E-field from a moving point charge
public class FieldModel {

    public var views_arr:Array;     //views associated with this model
    public var myMainView:MainView;

    private var c:Number;           //speed of light in pixels per second, charge cannot move faster than c
    private var _xC:Number;         //x-position of charge in pixels
    private var _yC:Number;         //y-position of charge in pixels
    private var _vX:Number;         //x-component of charge velocity
    private var _vY:Number;         //y-component of charge velocity
    private var aX: Number;         //x-component of charge acceleration
    private var aY: Number;         //y-component of charge acceleration
    private var vXInit:Number;      //x-comp of velocity when starting to brake stopping charge
    private var vYInit:Number;      //y-comp of velocity when starting to brake stopping charge
    private var _v:Number;          //speed of charge
    private var _a:Number;          //classical acceleration of charge = delta-v/delta-t
    private var gamma:Number;       //gamma factor
    private var fX:Number;          //x-component of force on charge
    private var fY:Number;          //y-component of force on charge
    private var k:Number;           //spring constant of spring between charge and mouse
    private var m:Number;           //mass of charge
    private var _amplitude:Number;  //amplitude of motion of charge (for sinusoidal or circular motion)
    private var _frequency:Number;  //frequency(Hz) of motion of charge (for sinusoidal or circular motion)
    //private var slopeSign:Number;   //+1 or -1, used in sawtooth generator
    private var phi:Number;         //phase of oscillatory motion (sinusoidal or circular)
    private var beta:Number;        //ratio (speed of charge)/c
    private var _nbrFieldLines:int;      //number of field lines coming from charge
    private var _nbrPhotonsPerLine:int; //number of photons in a given field line
    private var cos_arr:Array;      //cosines of angles of the rays, CCW from horizontal, angle must be in radians
    private var theta_arr:Array;    //angles of rest frame rays (in rads)
    private var sin_arr:Array;      //sines of angles of the rays,
    private var _fieldLine_arr:Array;  //array of field lines

    //types of motion
    private var _motionType: int;
    private const _STOPPING: int = -1;
    private const STARTING: int = -2;    //currently unused
    //types of motion called from radio button group in control panel
    private const _MANUAL_WITH_FRICTION: int = 0;
    private const _MANUAL_NO_FRICTION: int = 1;
    private const LINEAR: int = 2;
    private const SINUSOIDAL: int = 3;
    private const CIRCULAR: int = 4;
    private const BUMP: int = 5;
    private const RANDOM: int = 6;

    private var _paused:Boolean;        //true if sim is paused
    private var _outOfBounds:Boolean;   //true if charge is a ways off-screen

    private var _t:Number;              //time in seconds
    //private var tLastStep:Number;       //time of last screendraw
    //private var elapsedTime:Number;     //elapsed time since previous screen draw
    private var _tLastPhoton: Number;	//time of previous Photon emission
    private var tRate: Number;	        //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	        //current time step in seconds used in time-based animation
    //private var dtDefault: Number;      //default time step in seconds
    private var delTPhoton:Number;      //time in sec between photon emission events, depends on acceleration
    private var delTPhotonDefault: Number;   //default time between photon emission events
    private var stepsPerFrame:int;      //number of algorithm steps between screen draws
    private var tBump:Number;           //time that charge is "bumped" = moved thru one cycle of sine wave
    private var _bumpDuration:Number;    //duration of bump in seconds
    private var tLastRandomStep:Number; //time of previous step in random walk motion
    private var delTRandomWalk:Number;  //time between steps in random walk motion
    private var msTimer: Timer;	        //millisecond timer
    private var stageW:int;             //width of stage in pixels
    private var stageH:int;


    public function FieldModel( myMainView: MainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this._nbrFieldLines = 16;
        this._nbrPhotonsPerLine = 100;
        this.theta_arr = new Array( this._nbrFieldLines );
        this.cos_arr = new Array( this._nbrFieldLines );
        this.sin_arr = new Array( this._nbrFieldLines );
        this._fieldLine_arr = new Array ( this._nbrFieldLines );
        this.initialize();
    }//end constructor


    private function initialize():void{
        for(var i:int = 0; i < this._nbrFieldLines; i++ ){
           this.theta_arr[i] = i*2*Math.PI/this._nbrFieldLines;
           this.cos_arr[i] = Math.cos( theta_arr[i] ) ;
           this.sin_arr[i] = Math.sin( theta_arr[i] ) ;
        }
        this._xC = 0; //place charge at origin, which is at center of stage
        this._yC = 0;
        //populate rays with photons
        for( var i:int = 0; i < this._nbrFieldLines ; i++ ) {
            this._fieldLine_arr[i] = new Array( this._nbrPhotonsPerLine );
            for (var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){
                this._fieldLine_arr[i][j]= new Photon( _xC,  _yC );     //each photon has a position (x,y)
            }                                                           //and direction vector (cos, sin) with default (0,0)
        }

        this._motionType = _MANUAL_WITH_FRICTION;
        this.c = this.stageW/4;     //n seconds to cross height of stage
        this.k = 10;                //spring constant
        this.m = 1;                 //mass

        this.fX = 0;
        this.fY = 0;
        this._vX = 0;
        this._vY = 0;
        this._v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = v/c;
        this.gamma = 1/Math.sqrt( 1 - beta*beta );
//        this._amplitude = 5;
//        this._frequency = 2;
        this.initializeAmplitudeAndFrequency();
        this.phi = 0;
        this._bumpDuration = 0.5;

        this._paused = false;
        this._outOfBounds = false;
        this._t = 0;
        this._tLastPhoton = 0;
        //this.dtDefault = 0.006;
        this.dt = 0.006;
        this.delTPhotonDefault = 0.02;
        this.delTPhoton = delTPhotonDefault;
        this.tRate = 1;         //not currently used
        this.stepsPerFrame = 4;
        this.msTimer = new Timer( stepsPerFrame*dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, reDrawScreen );
        this.updateViews();
        this.startRadiation();
    }

    public function initializeAmplitudeAndFrequency():void{
        this._amplitude = 5;
        this._frequency = 2;
        this.updateViews();
    }

    public function get nbrLines():int{
        return this._nbrFieldLines
    }
    
    public function get nbrPhotonsPerLine():int{
        return this._nbrPhotonsPerLine
    }

    public function getDT():Number{
        return this.dt;
    }

    public function get fieldLine_arr():Array{
        return this._fieldLine_arr;
    }
    
    public function get t():Number{
        return this._t;
    }
    
    public function get v():Number{
        return this._v;
    }

    public function getSpeedOfLight():Number{
        return this.c;
    }

    public function get xC():Number{
        return this._xC;
    }

    public function get yC():Number{
        return this._yC;
    }

    public function set paused( tOrF:Boolean ):void{
        this._paused = tOrF;
        if( tOrF ){
            this.stopRadiation();
        }else{
            this.startRadiation();
        }
    }

    public function get paused():Boolean{
        return this._paused;
    } 
    
    public function get amplitude():Number{
        return this._amplitude;
    }

    public function set amplitude( ampli:Number ):void{
        this._amplitude = ampli;
    }
    
    public function get frequency():Number{
        return this._frequency;
    }

    public function setFrequency( freq:Number ):void{
        var oldF:Number = this._frequency;
        this._frequency = freq;
        this.phi += 2*Math.PI*this._t*( oldF - _frequency );
    }


    public function setBeta( beta:Number ):void{
        this.beta = beta;
        this._v = beta*this.c;
        this._vX = _v;
        this._vY = 0;
    }

    public function getBeta():Number{
        return this.beta;
    }

    public function set bumpDuration( bumpWidth:Number ):void{
        this._bumpDuration = bumpWidth;
    }

    public function get bumpDuration():Number{
        return this._bumpDuration;
    }

    public function stopCharge():void{
        //this.myMainView.myControlPanel.presetMotion_nrbg.selectedIndex = 0;
        //this.myMainView.myControlPanel.myComboBox.selectedIndex = 0;         //is there a more elegant way?
        this._motionType = _STOPPING;
        this.delTPhoton = delTPhotonDefault;
        this.vXInit = this._vX;
        this.vYInit = this._vY;
        //this.updateViews()
    }

    private function setTEqualZero():void{
        this._t = 0;
        this._tLastPhoton = 0;
    }

    //UNUSED
    public function startCharge():void{
        this._motionType = STARTING;
    }

    /*Place charge at origin, center of screen, at rest.*/
    public function centerCharge():void{
        this._xC = 0;
        this._yC = 0;
        _motionType = _MANUAL_WITH_FRICTION;
        this._vX = 0;
        this._vY = 0;
        this._v = 0;
        this.beta = this._v/this.c;
        //this.myMainView.myControlPanel.presetMotion_rgb.selectedValue = 0;
        //this.myMainView.myControlPanel.myComboBox.selectedIndex = 0;
        this.initializeFieldLines();
    }


    public function restartCharge():void{
        this.setTypeOfMotion( 2 );  //restart linear motion of charge at left edge of screen
    }

    public function bumpCharge( bumpDuration:Number ):void{
        this.tBump = this._t;
        this._bumpDuration = bumpDuration;
    }
    
    public function setForce( delX:Number, delY:Number ):void{
        this.fX = this.k*delX;
        this.fY = this.k*delY;
    }

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

    private function initializeFieldLines():void{
        for( var i:int = 0; i < this._nbrFieldLines; i++ ){
            for( var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){
                this._fieldLine_arr[i][j].xP = this._xC;
                this._fieldLine_arr[i][j].yP = this._yC;
                this._fieldLine_arr[i][j].cos = cos_arr[i];
                this._fieldLine_arr[i][j].sin = sin_arr[i];
                this._fieldLine_arr[i][j].emitted = false;
            }
        }
    }

    public function setTypeOfMotion( choice:int ):void{
        this.stopRadiation();
        this._motionType = choice;
        //trace("FieldModel.setTypeOfMotion() called. choice = " + choice );
        this.delTPhoton = delTPhotonDefault;  //default values, changed for linear motion = const velocity motion
        this.stepsPerFrame = 4;
        if( choice == _MANUAL_WITH_FRICTION ){
            //do nothing
        }else if( choice == _MANUAL_NO_FRICTION ){
            //do nothing
        }else if( choice == LINEAR ){    //linear
             this.initializeLinearMotion();
        }else if(choice == SINUSOIDAL ){  //sinusoid
            _xC = 0;
            _yC = 0;
            _vX = 0;
            _vY = 0;
            this.initializeFieldLines();
            this.setTEqualZero();
            this.phi = 0;
        }else if( choice == CIRCULAR ){  //circular
            this.setTEqualZero();
            this.initializeFieldLines();
        }else if( choice == BUMP ){   //bump
            this.initializeFieldLines();
            _xC = 0;
            _yC = 0;
            _vX = 0;
            _vY = 0;
            tBump = this._t;
            this._bumpDuration = this.myMainView.myControlPanel.durationSlider.getVal();
        }else if( choice == 6 ){  //randomWalk, CURRENTLY NOT USED
            _motionType = RANDOM;
            _xC = 0;
            _yC = 0;
            this.delTRandomWalk = 0.5;
            this.tLastRandomStep = this._t;
            this._vX = this.c*(Math.random() - 0.5);   //c * random number between -1 and +1
            this._vY = this.c*(Math.random() - 0.5);
        }
        this.initializeFieldLines();
        this.startRadiation();
        this.paused = false;
    }//end setTypeOfMotion()

    private function initializeLinearMotion():void{
        this.initializeFieldLines();
        this.fX = 0;
        this.fY = 0;
        this.beta = this.myMainView.myControlPanel.speedSlider.getVal();
        //trace("FieldModel.setTypeOfMotion  linear motion, beta = " + this.beta);
        this._vX = this.beta*this.c;
        this._vY = 0;
        this._v = beta*this.c;
        this._xC = -stageW/2;
        this._yC = 0;
        this.delTPhoton = 0.1;
        //this.stepsPerFrame = this.delTPhoton/this.dtDefault;
    }


    private function moveCharge():void{
        var fw:Number = 1.4;     //fudge factors: recenter charge if outside zone = fw*stageW x fh*stageH
        var fh:Number = 1.6;
        if( xC > fw*stageW/2 || xC < -fw*stageW/2 || yC > fh*stageH/2 || yC < -fh*stageH/2 ){   //if charge is out of bounds
            if(_motionType != LINEAR ) {
                outOfBounds = true; //charge is recentered in ChargeView if outOfBounds
            }else{
                initializeLinearMotion();
            }
        }
        if( _motionType == _MANUAL_NO_FRICTION ){
            this.manualNoFrictionStep();
        }else if (_motionType == _MANUAL_WITH_FRICTION){
            this.manualWithFrictionStep();
        } else if( _motionType == LINEAR ){
            _xC += _vX*dt;
            _yC += _vY*dt;
        }else if( _motionType == SINUSOIDAL ) {
            this.sinusiodalStep();
        }else if( _motionType == CIRCULAR ) {
            this.circularStep();
        }else if( _motionType == BUMP ){
            this.bumpStep();
        }else if( _motionType == RANDOM ){
            this.randomWalkStep();
        }else if( _motionType == _STOPPING ){
            this.stoppingStep();
        }else if ( _motionType == STARTING ){
            this.startingStep();
        }
    }//end moveCharge()

    private function manualNoFrictionStep():void{
        this.beta = this._v/this.c;
        this.gamma = 1/Math.sqrt( 1 - beta*beta );
        var g3:Number = Math.pow( gamma, 3 );
        _v = Math.sqrt( _vX*_vX + _vY*_vY );
        var aX:Number = fX/( g3*m ); //- b*_v*vX;  //x-component of acceleration, no drag
        var aY:Number = fY/( g3*m ); //- b*_v*vY;  //y-component of acceleration
        _xC += _vX*dt + 0.5*aX*dt*dt;
        _yC += _vY*dt + 0.5*aY*dt*dt;
        _vX += aX*dt;
        _vY += aY*dt;
        _v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = this._v/this.c;
        if( _v > c ){
            while( _v >= c ){
                trace( "error: _v > c, beta = " + this.beta );
                _vX = 0.99*_vX*c/_v;
                _vY = 0.99*_vY*c/_v;
                _v = Math.sqrt( _vX*_vX + _vY*_vY );
                beta = this._v/this.c;
            }
        }
    }//end manualNoFrictionStep()

    private function manualWithFrictionStep():void{
        this.beta = this._v/this.c;
        this.gamma = 1/Math.sqrt( 1 - beta*beta );
        var g3:Number = Math.pow( gamma, 3 );
        _v = Math.sqrt( _vX*_vX + _vY*_vY );
        var b:Number = 2000/g3;   //friction coefficient
        var dragX: Number = 0;
        var dragY: Number = 0;
        dragX = b*_vX/(c) ; //b*_v*vX/(c*c) ;
        dragY = b*_vY/(c) ; //b*_v*vY/(c*c) ;
        var aX:Number = fX/( g3*m ) - dragX ;  //- b*_v*vX;  //x-component of acceleration, no drag
        var aY:Number = fY/( g3*m ) - dragY ;  //- b*_v*vY;  //y-component of acceleration
        _xC += _vX*dt + 0.5*aX*dt*dt;
        _yC += _vY*dt + 0.5*aY*dt*dt;
        _vX += aX*dt;
        _vY += aY*dt;
        _v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = this._v/this.c;
        if( _v > c ){
            while( _v >= c ){
                trace( "error: _v > c, beta = " + this.beta );
                _vX = 0.99*_vX*c/_v;
                _vY = 0.99*_vY*c/_v;
                _v = Math.sqrt( _vX*_vX + _vY*_vY );
                beta = this._v/this.c;
            }
        }
    }//end manualWithFriction()

    private function sinusiodalStep():void{
        var A:Number = this._amplitude;  //_amplitude of sinusoidal motion
        var f:Number = this._frequency;   //_frequency of motion in hertz
        this._xC = 0;
        this._yC = A*Math.sin( 2*Math.PI*f*this._t + phi ) ;
        this._vX = 0;
        this._vY = A*2*Math.PI*f*Math.cos( 2*Math.PI*f*this._t + phi );
//        this.aX = 0;
//        this.aY =  -A*(2*Math.PI)*f*(2*Math.PI*f)*Math.sin( 2*Math.PI*f*this._t + phi );
//        var fudge: Number = Math.abs(aY)/(c);
//        this.delTPhoton = delTPhotonDefault/fudge;
//        if(delTPhoton > 0.1){delTPhoton = 0.1 };
        //trace("FieldModel.sinusoidalStep()  aY = "+aY/c);
        this._v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = this._v/this.c;
    } //end sinusoidal step

    private function circularStep():void{
        var R:Number = this._amplitude;  //radius of circle
        var omega:Number = 2*Math.PI*_frequency;
        this._xC = R*Math.cos( omega*this._t + phi );
        this._yC = R*Math.sin( omega*this._t + phi );
        this._vX = -R*omega*Math.sin( omega*_t + phi );
        this._vY = R*omega*Math.cos( omega*_t + phi );
        _v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = this._v/this.c;
    }//end cicularStep();

    private function bumpStep():void{
        var A:Number = amplitude;
        var f:Number = 1/bumpDuration;
        var omega:Number = 2*Math.PI*f;
        if( t < tBump + bumpDuration ){
            this._yC = A*Math.cos( omega*(t - tBump) - Math.PI/2);
            this._vY = -A*omega*Math.sin( omega*(t - tBump) );
        }else{
            this._yC = 0;
            this._vY = 0;
        }
        this._v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = this._v/this.c;
    }

    //NOT USED
//    private function sawToothStep():void{
//        _xC = 0;
//        _vX = 0;
//        this.fX = 0;
//        if( _yC >= 0 ){
//            slopeSign = -1;
//        } else if(_yC < 0 ){
//            slopeSign = 1;
//        }
//        var extraForceFactor:Number = 100*(Math.abs( yC ))/amplitude;
//        var signY:Number = yC/Math.abs( yC );
//        var signVY:Number = _vY/Math.abs( _vY );
//        if( signY != signVY  ){
//            extraForceFactor *= 0.7;
//        }
//        this.fY = slopeSign*(100+extraForceFactor)*amplitude;
//        this.beta = this._v/this.c;
//        this.gamma = 1/Math.sqrt( 1 - beta*beta );
//        var g3:Number = Math.pow( gamma, 3 );
//        var aY:Number = fY/( g3*m ); //- b*_v*vY;  //y-component of acceleration
//        _yC += _vY*dt + 0.5*aY*dt*dt;
//        _vY += aY*dt;
//        _v = Math.sqrt( _vX*_vX + _vY*_vY );
//        this.beta = this._v/this.c;
//    }//end sawToothStep();

    private function randomWalkStep():void{
        if( (this._t - this.tLastRandomStep) > this.delTRandomWalk ){
            this.tLastRandomStep = this._t;
            this._vX = this.c*0.5*(Math.random() - 0.5);   //fMax * random number between -0.25 and +0.25
            this._vY = this.c*0.5*(Math.random() - 0.5);
        }
        this.beta = this._v/this.c;
        _xC += _vX*dt;// + 0.5*aX*dt*dt;
        _yC += _vY*dt;// + 0.5*aY*dt*dt;
        _v = Math.sqrt( _vX*_vX + _vY*_vY );
        this.beta = this._v/this.c;
    }//end randomWalkStep()

    private function startingStep():void{
        var startingTime:Number = 0.1;  //time to accelerate from stop to final velocity, in seconds
        var div:Number = startingTime/this.dt;   //number of time steps to accelerate from stop to full velocity
        this.gamma = 1/Math.sqrt( 1 - beta*beta );
        var g3:Number = Math.pow( gamma, 3 );
        var aX:Number = vXInit/(g3*m*div*dt);
        var aY:Number = vYInit/(g3*m*div*dt);
        _vX += aX*dt;
        _vY += aY*dt;
        this._v = Math.sqrt( _vX*_vX + _vY*_vY );
        var signVXInit:Number = vXInit/Math.abs( vXInit );
        var signVYInit:Number = vYInit/Math.abs( vYInit );
        var signVX:Number = _vX/Math.abs(_vX);
        var signVY:Number = _vY/Math.abs(_vY);
        var ratioX:Number = signVX/signVXInit;
        var ratioY:Number = signVY/signVYInit;
        if( ratioX < 0  || ratioY < 0 ){
            _vX = 0;
            _vY = 0;
            aX = 0;
            aY = 0;
            _motionType = _MANUAL_WITH_FRICTION; //manual_str;
        }
        _xC += _vX*dt + (0.5)*aX*dt*dt;
        _yC += _vY*dt + (0.5)*aY*dt*dt;
        this.beta = this._v/this.c;
    }

    private function stoppingStep():void{
        var stoppingTime:Number = 0.1;  //time to stop in seconds
        var div:Number = stoppingTime/this.dt;   //number of time steps to brake to a stop
        this.gamma = 1/Math.sqrt( 1 - beta*beta );
        var g3:Number = Math.pow( gamma, 3 );
        var aX:Number = -vXInit/(g3*m*div*dt);
        var aY:Number = -vYInit/(g3*m*div*dt);
        _vX += aX*dt;
        _vY += aY*dt;
        this._v = Math.sqrt( _vX*_vX + _vY*_vY );
        var signVXInit:Number = vXInit/Math.abs( vXInit );
        var signVYInit:Number = vYInit/Math.abs( vYInit );
        var signVX:Number = _vX/Math.abs(_vX);
        var signVY:Number = _vY/Math.abs(_vY);
        var ratioX:Number = signVX/signVXInit;
        var ratioY:Number = signVY/signVYInit;
        //If charge reverses direction, in either x or y direction, it has overshot full stop and must be reset to full stop
        if( ratioX < 0  || ratioY < 0 ){
            _vX = 0;
            _vY = 0;
            aX = 0;
            aY = 0;
            _motionType = _MANUAL_WITH_FRICTION;
        }
        _xC += _vX*dt + (0.5)*aX*dt*dt;
        _yC += _vY*dt + (0.5)*aY*dt*dt;
        this.beta = this._v/this.c;
    }//end stoppingStep();

    private function reDrawScreen( evt: TimerEvent ):void{
//        var currentTime:Number = this._t;
//        this._t = getTimer();
//        dt = _t - tLastStep;
//        if(dt > 0.05 ){
//            dt = dtDefault;
//        }
        //dt = elapsedTime;
        //this.tLastStep = currentTime;
        //this.dt = dtDefault;

        for(var s:int = 0; s < stepsPerFrame; s++ ) {
            this._t += this.dt;
            if( this._t > this._tLastPhoton + this.delTPhoton ){
                this._tLastPhoton = this._t;
                this.emitPhotons();
            }
            this.moveCharge();
        }
        for( var i:int = 0; i < this._nbrFieldLines; i++ ){  //for each ray
            for( var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){    //for each photon in a ray.
                var t0:Number = this._fieldLine_arr[i][j].tEmitted;
                var x0:Number = this._fieldLine_arr[i][j].x0;
                var y0:Number = this._fieldLine_arr[i][j].y0;
                this._fieldLine_arr[i][j].xP = x0 + _fieldLine_arr[i][j].cos*this.c*( t - t0 );
                this._fieldLine_arr[i][j].yP = y0 + _fieldLine_arr[i][j].sin*this.c*( t - t0 );
            }
        }
        this.updateViews();
        //evt.updateAfterEvent();
    }//end reDrawScreen()

    private function emitPhotons():void{
        //add new photon to 1st element of photon array
        for( var i:int = 0; i < this._nbrFieldLines; i++ ){
            //recycle photon at end of ray and place at front end of ray
            this._fieldLine_arr[i].unshift( _fieldLine_arr[i][nbrPhotonsPerLine - 1])
            // remove last element of ray
            this._fieldLine_arr[i].splice( this.nbrPhotonsPerLine );
        }
        //initialize photons at front end of rays
        this.initializeEmittedPhotons();
    }//end emitPhotons()

    private function initializeEmittedPhotons():void{
        this.beta = this._v/this.c;
        var inverseGamma = Math.sqrt( 1 - beta*beta );
        var thetaC:Number = Math.atan2( this._vY, this._vX );   //angle of velocity vector of charge
        var cosThetaC:Number = Math.cos( thetaC );
        var sinThetaC:Number = Math.sin( thetaC );
        //trace("cos = "+cosThetaC+"    sin = " + sinThetaC )
        for( var i:int = 0; i < this._nbrFieldLines; i++ ){
            //reset positions
            this._fieldLine_arr[i][0].xP = this._xC;
            this._fieldLine_arr[i][0].yP = this._yC;
            this._fieldLine_arr[i][0].x0 = this._xC;
            this._fieldLine_arr[i][0].y0 = this._yC;
            this._fieldLine_arr[i][0].emitted = true;
            this._fieldLine_arr[i][0].tEmitted = this._t;
            var thetaU:Number = this.theta_arr[i] - thetaC;
            var cosThetaU:Number = Math.cos( thetaU );
            var sinThetaU:Number = Math.sin( thetaU );
            var cosThetaFinal:Number = ( (cosThetaU + beta)*cosThetaC - (inverseGamma)*sinThetaU*sinThetaC ) /( 1 + beta*cosThetaU );
            var sinThetaFinal:Number = ( (cosThetaU + beta)*sinThetaC + (inverseGamma)*sinThetaU*cosThetaC ) /( 1 + beta*cosThetaU );
            this._fieldLine_arr[i][0].cos = cosThetaFinal;
            this._fieldLine_arr[i][0].sin = sinThetaFinal;
        }
    }//end initializeEmittedPhotons

    public function startRadiation():void{
        this.msTimer.start();
    }

    public function stopRadiation():void{
        updateViews();
        this.msTimer.stop();
    }

    public function updateViews(): void {
        for(var i:int = 0; i < this.views_arr.length; i++){
            this.views_arr[ i ].update();
        }
    }//end updateView()

    public function get outOfBounds():Boolean {
        return _outOfBounds;
    }

    public function set outOfBounds( value:Boolean ):void {
        _outOfBounds = value;
    }

//    public function get motionType_str():String {
//        return motionType;
//    }
//
//    public function set motionType_str( value:String ):void {
//        motionType = value;
//    }

    public function get STOPPING():int {
        return _STOPPING;
    }

    public function get MANUAL_NO_FRICTION():int {
        return _MANUAL_NO_FRICTION;
    }
//
//    public function set noFriction_str( value:String ):void {
//        MANUAL_NO_FRICTION = value;
//    }
//
    public function get MANUAL_WITH_FRICTION():int {
        return _MANUAL_WITH_FRICTION;
    }
//
//    public function set manual_str( value:String ):void {
//        MANUAL_WITH_FRICTION = value;
//    }

    public function get vX(): Number {
        return _vX;
    }

    public function get vY(): Number {
        return _vY;
    }

    public function get motionType(): int {
        return _motionType;
    }
} //end of class
} //end of package
