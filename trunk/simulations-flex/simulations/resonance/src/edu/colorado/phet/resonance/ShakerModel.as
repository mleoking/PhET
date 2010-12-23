package edu.colorado.phet.resonance {
//model of shaker bar with several mass/spring systems attached

import flash.events.*;
import flash.utils.*;

//contains Timer class

public class ShakerModel {

    public var view: ShakerView;
    private var maxNbrResonators: int;
    private var nbrResonators: int;
    public var resonatorModel_arr: Array;
    //private var resonatorView_arr:Array;
    private var running: Boolean;//true if shaker bar is ON
    private var y0: Number;		//current height of bar, equil.position is y0 = 0;
    private var f: Number;		//frequency of oscillation of shaker bar
    private var phase: Number;	//phase of oscillation
    private var A: Number;		//amplitude of oscillation of shaker bar
    //private var Aset:Number;	//amplitude of oscillation of shaker bar set by Amplitude slider
    private var maxAmplitude: Number; //
    private var t: Number;		//time in seconds
    private var lastTime: Number;	//time in previous timeStep
    private var tRate: Number;	//1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	//default time step in seconds
    private var msTimer: Timer;	//millisecond timer

    public function ShakerModel( nbrResonators: int ) {
        this.maxNbrResonators = nbrResonators;
        this.nbrResonators = nbrResonators;
        //this.resonator_arr = new Array(nbrResonators);
        this.initialize();
    }

    private function initialize() {
        this.A = 0.00;
        this.f = 0;
        this.phase = 0;
        this.y0 = 0;
        this.t = 0;
        this.dt = 0.01; //0.0055;
        this.msTimer = new Timer( this.dt * 1000 );
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.createResonatorArray();
        //this.stopMotion();
        //this.msTimer.start();
        this.stopShaker();
    }//end initialize()

    private function createResonatorArray(): void {
        this.resonatorModel_arr = new Array( this.nbrResonators );
        for ( var i: int = 0; i < this.maxNbrResonators; i++ ) {
            //var m: Number = 3*(1-0.08*i);//3/(i+1);//4.2 / Math.pow( 1.3, (1 + i) );
            var m: Number = 4.2 / Math.pow( 1.3, (1 + i) );
            //MassSpringModel(shakerModel:ShakerModel, rNbr:int, m:Number, f:Number, L0:Number, b:Number)
            this.resonatorModel_arr[i] = new MassSpringModel( this, i + 1, m, 0.5 + 0.5 * i, 0.8, 1 );
        }//end for
    }//end function

    public function resetInitialResonatorArray(): void {
        for ( var i: int = 0; i < this.maxNbrResonators; i++ ) {
            //var m: Number  = 3*(1-0.08*i);//3/(i+1); //= 4.2 / Math.pow( 1.3, (1 + i) );
            var m: Number  = 4.2 / Math.pow( 1.3, (1 + i) );
            this.resonatorModel_arr[i].setM( m );
            var f: Number = 0.5 + 0.5 * i;
            var k: Number = m * (2 * Math.PI * f) * (2 * Math.PI * f);
            this.resonatorModel_arr[i].setK( k );
        }//end for
    }//end function

    public function setB( b: Number ): void {
        for ( var i: int; i < this.nbrResonators; i++ ) {
            this.resonatorModel_arr[i].setB( b );
        }
    }//end setDampingConstant()

    public function getMaxNbrResonators(): int {
        return this.maxNbrResonators;
    }

    public function getNbrResonators(): int {
        return this.nbrResonators;
    }

    public function setNbrResonators( nbr: int ): void {
        this.nbrResonators = nbr;
        //freeze unused resonators
        for ( var i: int = nbr; i < this.maxNbrResonators; i++ ) {
            this.resonatorModel_arr[i].stopMotion();
        }
        //trace("shakerModel.setNbrResonators called. nbrResonators = " + this.nbrResonators);
    }

    public function getResonatorModel( i: int ): MassSpringModel {
        return resonatorModel_arr[i];
    }

    public function setG(g:Number):void{
        for ( var i: int = 0; i < this.maxNbrResonators; i++ ) {
            this.resonatorModel_arr[i].setG(g);
        }
    }

    public function getDt(): Number {
        return this.dt;
    }

    public function getY0(): Number {
        return this.y0;
    }

    public function setY0( yVal: Number ): void {
        this.y0 = yVal;
        for ( var i: int = 0; i < this.nbrResonators; i++ ) {
            this.resonatorModel_arr[i].setY0( this.y0 );
        }
        this.updateView();
    }

    public function setF( frequency: Number ): void {
        var oldF = this.f;
        var tNow: Number = this.t;
        this.f = frequency;
        //trace("ShakerModel.setF called.  this.f = "+this.f);
        this.phase += 2 * tNow * Math.PI * (oldF - this.f);
    }

    public function setA( amplitude: Number ): void {
        //this.Aset = amplitude;
        this.A = amplitude;
    }

    public function getA(): Number {
        return this.A;
    }

    public function getRunning(): Boolean {
        return this.running;
    }


    public function startShaker(): void {
        this.running = true;
        this.setResonatorsFreeRunning( false );
        this.msTimer.start();
    }

    public function stopShaker(): void {
        this.running = false;
        this.setResonatorsFreeRunning( true );
        this.msTimer.stop();
    }

    public function registerView( view: ShakerView ): void {
        this.view = view;
    }

    public function setResonatorsFreeRunning( On: Boolean ): void {
        if ( On ) {
            for ( var i: int = 0; i < this.nbrResonators; i++ ) {
                this.resonatorModel_arr[i].startMotion();
            }
        } else if ( !On ) {
            for ( var i: int = 0; i < this.nbrResonators; i++ ) {
                this.resonatorModel_arr[i].stopMotion();
            }
        }
        else {
            trace( "ERROR in ShakerModel.setResonatorsFreeRunning: Non-boolean argument." );
        }

    }

    private function stepForward( evt: TimerEvent ): void {
        //need function without event argument
        this.singleStep();
        evt.updateAfterEvent();
    }

    private function singleStep(): void {
        var currentTime = getTimer() / 1000;
        var realDt: Number = currentTime - this.lastTime;
        this.lastTime = currentTime;
        if ( realDt < 0.04 ) {
            this.dt = realDt;
        }
        else {
            this.dt = 0.04;
        }
        this.t += this.dt;
        //trace("ShakerModel.singleStep called. realDt = "+realDt);
        //this.y0 = this.A*Math.sin(2*Math.PI*f*t + this.phase);
        var currentY0 = this.A * Math.sin( 2 * Math.PI * this.f * this.t + this.phase );
        this.setY0( currentY0 );
        //for(var i:int = 0; i < this.nbrResonators; i++){
//				resonatorModel_arr[i].setY0(this.y0);
//			}
        for ( var i: int; i < this.nbrResonators; i++ ) {
            this.resonatorModel_arr[i].singleStep( this.dt );// = this.bar.y;
        }
        updateView();
    }

    public function updateView(): void {
        this.view.update();
    }

}//end of class

}//end of package