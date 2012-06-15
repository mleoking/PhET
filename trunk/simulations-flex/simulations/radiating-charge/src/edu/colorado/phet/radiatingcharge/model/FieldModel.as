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

//model of radiating E-field from a moving point charge
public class FieldModel {

    public var views_arr:Array;     //views associated with this model
    public var myMainView:MainView;

    private var c:Number;           //speed of light in pixels per second, charge cannot move faster than c
    private var _xC:Number;         //x-position of charge in pixels
    private var _yC:Number;         //y-position of charge in pixels
    private var vX:Number;          //x-component of charge velocity
    private var vY:Number;          //y-component of charge velocity
    private var vXInit:Number;      //x-comp of velocity when starting to brake stopping charge
    private var vYInit:Number;      //y-comp of velocity when starting to brake stopping charge
    private var _v:Number;          //speed of charge
    private var gamma:Number;       //gamma factor
    private var fX:Number;          //x-component of force on charge
    private var fY:Number;          //y-component of force on charge
    private var k:Number;           //spring constant of spring between charge and mouse
    private var m:Number;           //mass of charge
    private var _amplitude:Number;  //amplitude of motion of charge (for sinusoidal or circular motion)
    private var _frequency:Number;  //frequency(Hz) of motion of charge (for sinusoidal or circular motion)
    private var slopeSign:Number;   //+1 or -1, used in sawtooth generator
    private var phi:Number;         //phase of oscillatory motion (sinusoidal or circular)
    private var beta:Number;        //ratio (speed of charge)/c
    private var _nbrLines:int;      //number of field lines coming from charge
    private var _nbrPhotonsPerLine:int //number of photons in a given field line
    private var cos_arr:Array;      //cosines of angles of the rays, CCW from horizontal, angle must be in radians
    private var theta_arr:Array;    //angles of rest frame rays (in rads)
    private var sin_arr:Array;      //sines of angles of the rays,
    private var _fieldLine_arr:Array;  //array of field lines

    private var _paused:Boolean;    //true if sim is paused
    private var motionType_str:String;  //type of motion: user-controlled, linear, sinusoidal, circular, etc
    private var userControlled_str:String;
    private var linear_str:String;
    private var sinusoidal_str:String;
    private var circular_str:String;
    private var sawTooth_str:String;
    private var random_str:String;
    private var stopping_str:String;

    private var _t:Number;          //time in arbitrary units
    private var _tLastPhoton: Number;	    //time of previous Photon emission
    private var tRate: Number;	    //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	    //default time step in seconds
    private var delTPhoton:Number;  //time in sec between photon emission events
    private var tLastRandomStep:Number; //time of previous step in random walk motion
    private var delTRandomWalk:Number; //time between steps in random walk motion
    private var msTimer: Timer;	    //millisecond timer
    private var stageW:int;         //width of stage in pixels
    private var stageH:int;


    public function FieldModel( myMainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.c = this.stageW/5;    //n seconds to cross height of stage
        this.k = 10;
        this.m = 1;
        this.vX = 0;
        this.vY = 0;
        this.fX = 0;
        this.fY = 0;
        this._v = Math.sqrt( vX*vX + vY*vY );
        this.beta = this._v/this.c;
        this.gamma = 1/Math.sqrt( 1 - beta*beta );
        this._nbrLines = 16;
        this._nbrPhotonsPerLine = 100;
        this.theta_arr = new Array( this._nbrLines );
        this.cos_arr = new Array( this._nbrLines );
        this.sin_arr = new Array( this._nbrLines );
        this._fieldLine_arr = new Array ( this._nbrLines );
        this.initialize();
    }//end constructor

    private function initialize():void{
        for(var i:int = 0; i < this._nbrLines; i++ ){
           this.theta_arr[i] = i*2*Math.PI/this._nbrLines;
           this.cos_arr[i] = Math.cos( theta_arr[i] ) ;
           this.sin_arr[i] = Math.sin( theta_arr[i] ) ;
        }
        this._xC = 0; //place charge at origin, which is at center of stage
        this._yC = 0;
        //populate rays with photons
        for( var i:int = 0; i < this._nbrLines ; i++ ) {
            this._fieldLine_arr[i] = new Array( this._nbrPhotonsPerLine );
            for (var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){
                this._fieldLine_arr[i][j]= new Photon( _xC,  _yC );     //each photon has a position (x,y) and direction vector (cos, sin)
            }
        } //end for loop

        //set motion-type strings. NOTE: these are not visible on the stage and should NOT be internationalized.
        this.userControlled_str = "userControlled";
        this.linear_str = "linear";
        this.sinusoidal_str = "sinusoidal";
        this.circular_str = "circular";
        this.sawTooth_str = "sawTooth";
        this.random_str = "random";
        this.stopping_str = "stopping";
        this.motionType_str = userControlled_str;

        this._amplitude = 5;
        this._frequency = 2;
        this.phi = 0;
        this.beta = 0.5;
        this._v = beta*this.c;

        this._paused = false;
        this._t = 0;
        this._tLastPhoton = 0;
        this.dt = 0.015;
        this.delTPhoton = 0.015;
        this.tRate = 1;         //not currently used
        this.msTimer = new Timer( this.dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.updateViews();
        this.startRadiation();
    }

    public function get nbrLines():int{
        return this._nbrLines
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
    } //end set Paused

    public function get paused():Boolean{
        return this._paused;
    } 
    
    public function get amplitude():Number{
        return this._amplitude;
    }

    public function setAmplitude( ampli:Number ):void{
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
        this.vX = _v;
        this.vY = 0;
    }

    public function getBeta():Number{
        return this.beta;
    }


    public function stopCharge():void{
        motionType_str = userControlled_str;
        this.myMainView.myControlPanel.myComboBox.selectedIndex = 0;         //is there a more elegant way?
        this.motionType_str = stopping_str;
        this.vXInit = this.vX;
        this.vYInit = this.vY;
    }

    public function centerCharge():void{
        this._xC = 0;
        this._yC = 0;
        motionType_str = userControlled_str;
        this.vX = 0;
        this.vY = 0;
        this._v = 0;
        this.beta = this._v/this.c;
        this.myMainView.myControlPanel.myComboBox.selectedIndex = 0;
        this.initializeFieldLines();
    }

    public function bumpCharge():void{
        trace("FieldModel.bumpCharge called()");
    }

    public function restartCharge():void{
        this.setMotion( 1 );  //restart linear motion of charge at left edge of screen
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
        for( var i:int = 0; i < this._nbrLines; i++ ){
            for( var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){
                this._fieldLine_arr[i][j].xP = this._xC;
                this._fieldLine_arr[i][j].yP = this._yC;
                this._fieldLine_arr[i][j].cos = 0;
                this._fieldLine_arr[i][j].sin = 0;
                this._fieldLine_arr[i][j].emitted = false;
            }
        }
    }

    public function setMotion( choice:int ):void{
        this.stopRadiation();
        trace("FieldModel.setMotion() called. choice = " + choice );
        if( choice == 0 ){  //do nothing
            motionType_str = userControlled_str;
        }else if( choice == 1 ){    //linear
            motionType_str = linear_str;
            this.fX = 0;
            this.fY = 0;
            this.beta = this.myMainView.myControlPanel.speedSlider.getVal();
            this.vX = this.beta*this.c;//0;
            this.vY = 0;//0.95*this.c;
            this._v = beta*this.c;
            this._xC = -stageW/2;//
            this._yC = 0;//-stageH; //0;
        }else if(choice == 2 ){  //sinusoid
            motionType_str = sinusoidal_str;
        }else if( choice == 3 ){  //circular
            //this._t = 0;      //this is FATAL!!  Why??
            motionType_str = circular_str;
        }else if( choice == 4 ){   //sawtooth
            motionType_str = sawTooth_str;
            _xC = 0;
            _yC = this.amplitude;
            vX = 0;
            vY = 0;
            slopeSign = 1;
        }else if( choice == 5 ){
            motionType_str = random_str;
            _xC = 0;
            _yC = 0;
            this.delTRandomWalk = 0.5;
            this.tLastRandomStep = this._t;
            this.vX = this.c*1*(Math.random() - 0.5);   //c * random number between -1 and +1
            this.vY = this.c*1*(Math.random() - 0.5);
        }
        this.initializeFieldLines();
        this.startRadiation();
        this.paused = false;
    }//end setMotion()


    private function moveCharge():void{
        if( motionType_str == userControlled_str ){
            this.beta = this._v/this.c;
            this.gamma = 1/Math.sqrt( 1 - beta*beta );
            var g3:Number = Math.pow( gamma, 3 );
            _v = Math.sqrt( vX*vX + vY*vY );
            var aX:Number = fX/( g3*m ); //- b*_v*vX;  //x-component of acceleration, no drag
            var aY:Number = fY/( g3*m ); //- b*_v*vY;  //y-component of acceleration
            _xC += vX*dt + 0.5*aX*dt*dt;
            _yC += vY*dt + 0.5*aY*dt*dt;
            vX += aX*dt;
            vY += aY*dt;
            _v = Math.sqrt( vX*vX + vY*vY );
            this.beta = this._v/this.c;
            if( _v > c ){
                while( _v >= c ){
                    trace( "error: _v > c, beta = " + this.beta );
                    vX = 0.99*vX*c/_v;
                    vY = 0.99*vY*c/_v;
                    _v = Math.sqrt( vX*vX + vY*vY );
                    beta = this._v/this.c;
                }
            }
        } else if( motionType_str == linear_str ){
            _xC += vX*dt;
            _yC += vY*dt;

        }else if( motionType_str == sinusoidal_str ) {
            var A:Number = this._amplitude;  //_amplitude of sinusoidal motion
            var f:Number = this._frequency;   //_frequency of motion in hertz
            this._xC = 0;
            this._yC = A*Math.sin( 2*Math.PI*f*this._t + phi ) ;
            this.vX = 0;
            this.vY = A*2*Math.PI*f*Math.cos( 2*Math.PI*f*this._t + phi );
            this._v = Math.sqrt( vX*vX + vY*vY );
            this.beta = this._v/this.c;
        }else if( motionType_str == circular_str ) {
            var R:Number = this._amplitude;  //radius of circle
            var omega:Number = 2*Math.PI*_frequency;
            this._xC = R*Math.cos( omega*this._t + phi );
            this._yC = R*Math.sin( omega*this._t + phi );
            this.vX = -R*omega*Math.sin( omega*_t + phi );
            this.vY = R*omega*Math.cos( omega*_t + phi );
            _v = Math.sqrt( vX*vX + vY*vY );
            this.beta = this._v/this.c;
        }else if( motionType_str == sawTooth_str ){
            _xC = 0;
            vX = 0;
            this.fX = 0;

            if( _yC >= 0 ){
                slopeSign = -1;
            } else if(_yC < 0 ){
                slopeSign = 1;
            }
            var extraForceFactor:Number = 100*(Math.abs( yC ))/amplitude;
            var signY:Number = yC/Math.abs( yC );
            var signVY:Number = vY/Math.abs( vY );
            if( signY != signVY  ){
                 extraForceFactor *= 0.7;
            }

            this.fY = slopeSign*(100+extraForceFactor)*amplitude;
            this.beta = this._v/this.c;
            this.gamma = 1/Math.sqrt( 1 - beta*beta );
            var g3:Number = Math.pow( gamma, 3 );
            var aY:Number = fY/( g3*m ); //- b*_v*vY;  //y-component of acceleration
            _yC += vY*dt + 0.5*aY*dt*dt;
            vY += aY*dt;
            _v = Math.sqrt( vX*vX + vY*vY );
            this.beta = this._v/this.c;
        }else if( motionType_str == random_str ){
            if( (this._t - this.tLastRandomStep) > this.delTRandomWalk ){
                this.tLastRandomStep = this._t;
                this.vX = this.c*0.5*(Math.random() - 0.5);   //fMax * random number between -0.25 and +0.25
                this.vY = this.c*0.5*(Math.random() - 0.5);
            }
            this.beta = this._v/this.c;
            _xC += vX*dt;// + 0.5*aX*dt*dt;
            _yC += vY*dt;// + 0.5*aY*dt*dt;
            _v = Math.sqrt( vX*vX + vY*vY );
            this.beta = this._v/this.c;
        }else if( motionType_str == stopping_str ){
            var div:Number = 10;
            this.gamma = 1/Math.sqrt( 1 - beta*beta );
            var g3:Number = Math.pow( gamma, 3 );
            var aX:Number = -vXInit/(g3*m*div*dt);
            var aY:Number = -vYInit/(g3*m*div*dt);

            vX += aX*dt;
            vY += aY*dt;
            this._v = Math.sqrt( vX*vX + vY*vY );
            var signVXInit:Number = vXInit/Math.abs( vXInit );
            var signVYInit:Number = vYInit/Math.abs( vYInit );
            var signVX:Number = vX/Math.abs(vX);
            var signVY:Number = vY/Math.abs(vY);
            var ratioX:Number = signVX/signVXInit;
            var ratioY:Number = signVY/signVYInit;
            if( ratioX < 0  || ratioY < 0 ){
                vX = 0;
                vY = 0;
                aX = 0;
                aY = 0;
                motionType_str = userControlled_str;
            }
            _xC += vX*dt + (0.5)*aX*dt*dt;
            _yC += vY*dt + (0.5)*aY*dt*dt;
            this.beta = this._v/this.c;
        }
    }//end moveCharge()

    private function stepForward( evt: TimerEvent ):void{
        this._t += this.dt;
        if( this._t > this._tLastPhoton + this.delTPhoton ){
            this._tLastPhoton = this._t;
            this.emitPhotons();
        }
        for( var i:int = 0; i < this._nbrLines; i++ ){  //for each ray
            for( var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){    //for each photon in a ray.
                this._fieldLine_arr[i][j].xP += _fieldLine_arr[i][j].cos*this.c*this.dt;
                this._fieldLine_arr[i][j].yP += _fieldLine_arr[i][j].sin*this.c*this.dt;
            }
        }
        this.moveCharge();
        this.updateViews();
        evt.updateAfterEvent();
    }//end stepForward()

    private function emitPhotons():void{
        //add new photon to 1st element of photon array
        for( var i:int = 0; i < this._nbrLines; i++ ){
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
        var thetaC:Number = Math.atan2( this.vY, this.vX );   //angle of velocity vector of charge
        var cosThetaC:Number = Math.cos( thetaC );
        var sinThetaC:Number = Math.sin( thetaC );
        //trace("cos = "+cosThetaC+"    sin = " + sinThetaC )
        for( var i:int = 0; i < this._nbrLines; i++ ){
            //reset positions
            this._fieldLine_arr[i][0].xP = this._xC;
            this._fieldLine_arr[i][0].yP = this._yC;
            this._fieldLine_arr[i][0].emitted = true;
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

} //end of class
} //end of package
