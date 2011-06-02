package edu.colorado.phet.normalmodes {
import edu.colorado.phet.normalmodes.*;

//model of shaker bar with several mass/spring systems attached

import flash.events.*;
import flash.utils.*;

//contains Timer class

public class Model {

    public var view: View;
    //private var resonatorView_arr:Array;
    private var running: Boolean;//true if shaker bar is ON
    private var _paused: Boolean;  //true if sim paused
    private var t: Number;		//time in seconds
    private var lastTime: Number;	//time in previous timeStep
    private var tRate: Number;	//1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	//default time step in seconds
    private var msTimer: Timer;	//millisecond timer

    public function Model(  ) {
        //this.resonator_arr = new Array(nbrResonators);
        this.initialize();
    }

    private function initialize() {
        this.t = 0;
        this.dt = 0.01; //0.0055;
        this.tRate = 1;
        this.msTimer = new Timer( this.dt * 1000 );
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
    }//end initialize()







    public function setTRate(rate:Number):void{
        this.tRate = rate;
    }



    public function getDt(): Number {
        return this.dt;
    }


    public function getRunning(): Boolean {
        return this.running;
    }

    public function get paused() {
        return this._paused;
    }



    public function pauseSim(): void {
        this._paused = true;
        this.msTimer.stop();
        //this.running = false;
    }

    public function unPauseSim(): void {
        this._paused = false;
        if ( this.running ) {

        }
        else {

        }
    }

    public function registerView( view: View ): void {
        this.view = view;
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
            this.dt = this.tRate * realDt;
        }
        else {
            this.dt = this.tRate * 0.04;
        }
        this.t += this.dt;

        updateView();
    }

    public function singleStepWhenPaused(): void {
        this.dt = this.tRate * 0.02;
        this.t += this.dt;
        //trace("ShakerModel.singleStep called. realDt = "+realDt);
        //this.y0 = this.A*Math.sin(2*Math.PI*f*t + this.phase);
        if ( this.running ) {

        }


        updateView();
    }

    public function updateView(): void {
        this.view.update();
    }

}//end of class

}//end of package