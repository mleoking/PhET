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

    private var _c:Number;          //speed of light in pixels per second, charge cannot moving faster than c
    private var _xC:Number;         //x-position of charge in pixels
    private var _yC:Number;         //y-position of charge in pixels
    private var nbrLines:int;       //number of field lines coming from charge
    private var nbrPhotonsPerLine:int //number of photons in a given field line
    private var cos_arr:Array;      //cosines of angles of the rays, CCW from horizontal, angle must be in radians
    private var sin_arr:Array;      //sines of angles of the rays,
    private var fieldLine_arr:Array;  //array of field lines

    private var _t:Number;          //time in arbitrary units
    private var lastTime: Number;	//time in previous timeStep
    private var tRate: Number;	    //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	    //default time step in seconds
    private var msTimer: Timer;	    //millisecond timer


    public function FieldModel( myMainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this._c = this.myMainView.stageH/4;    //4 seconds to cross height of stage
        this.nbrLines = 8;
        this.nbrPhotonsPerLine = 30;
        this.cos_arr = new Array( this.nbrLines );
        this.sin_arr = new Array( this.nbrLines );
        this.fieldLine_arr = new Array ( this.nbrLines );
        for( var i:int = 0; i < this.nbrLines ; i++ ) {
            this.fieldLine_arr[i] = new Array( this.nbrPhotonsPerLine );
        }

        this.initialize();
    }//end constructor

    private function initialize():void{
        for(var i:int = 0; i < this.nbrLines; i++ ){
           this.cos_arr[i] = Math.cos( i*2*Math.PI/this.nbrLines ) ;
           this.sin_arr[i] = Math.sin( i*2*Math.PI/this.nbrLines ) ;
        }
        this._t = 0;
        //this.tInt = 1;              //testing only
        this.dt = 0.2;
        this.tRate = 1;
        this.msTimer = new Timer( this.dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.startRadiation();
    }

    public function get t():Number{
        return this._t;
    }

    public function get xC():Number{
        return this._xC;
    }

    public function get yC():Number{
        return this._yC;
    }

    public function set xC(xPos:Number):void{
        this._xC = xPos;
    }

    public function set yC(yPos:Number):void{
        this._yC = yPos;
    }

    public function setXY( xPos:Number, yPos:Number ):void{
        this._xC = xPos;
        this._yC = yPos;
        this.updateViews();
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

    private function stepForward( evt: TimerEvent ):void{
        this._t += this.dt;
        //trace("time = "+this._t) ;
        //evt.updateAfterEvent();
    }

    public function startRadiation():void{
        this.msTimer.start();
    }

    public function stopRadiation():void{
        this.msTimer.stop();
    }

    public function updateViews(): void {
        for(var i:int = 0; i < this.views_arr.length; i++){
            this.views_arr[ i ].update();
        }
        //this.view.update();
    }//end updateView()

} //end of class
} //end of package
