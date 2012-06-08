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
    private var _nbrLines:int;       //number of field lines coming from charge
    private var _nbrPhotonsPerLine:int //number of photons in a given field line
    private var cos_arr:Array;      //cosines of angles of the rays, CCW from horizontal, angle must be in radians
    private var sin_arr:Array;      //sines of angles of the rays,
    private var _fieldLine_arr:Array;  //array of field lines

    private var _t:Number;          //time in arbitrary units
    private var lastTime: Number;	//time in previous timeStep
    private var tRate: Number;	    //1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt: Number;  	    //default time step in seconds
    private var f:Number;           //frequency (nbr per second) of photons on a given field line emitted by charge
    private var msTimer: Timer;	    //millisecond timer
    private var stageW:int;
    private var stageH:int;


    public function FieldModel( myMainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this._c = this.stageW/4;    //4 seconds to cross height of stage
        this._nbrLines = 8;
        this._nbrPhotonsPerLine = 30;
        this.cos_arr = new Array( this._nbrLines );
        this.sin_arr = new Array( this._nbrLines );
        this._fieldLine_arr = new Array ( this._nbrLines );
        for( var i:int = 0; i < this._nbrLines ; i++ ) {
            this._fieldLine_arr[i] = new Array( this._nbrPhotonsPerLine );
            for (var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){
               this._fieldLine_arr[i][j]= new Array( 2 );     //each photon has x and y positions
            }
        }

        this.initialize();
    }//end constructor

    private function initialize():void{
        for(var i:int = 0; i < this._nbrLines; i++ ){
           this.cos_arr[i] = Math.cos( i*2*Math.PI/this._nbrLines ) ;
           this.sin_arr[i] = Math.sin( i*2*Math.PI/this._nbrLines ) ;
        }
        this._t = 0;
        this.dt = 0.02;
        this.f = 0.2*1/dt ;           //f should be less than 1/dt
        this.tRate = 1;
        this.msTimer = new Timer( this.dt * 1000 );   //argument of Timer constructor is time step in ms
        this.msTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.startRadiation();
    }

    public function get nbrLines():int{
        return this._nbrLines
    }
    
    public function get nbrPhotonsPerLine():int{
        return this._nbrPhotonsPerLine
    }

    public function get fieldLine_arr():Array{
        return this._fieldLine_arr;
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
        for( var i:int = 0; i < this._nbrLines; i++ ){
            for( var j:int = 0; j < this._nbrPhotonsPerLine; j++ ){
                this._fieldLine_arr[i][j][0] = this._xC + this.cos_arr[i]*j*this.stageW/(2*this._nbrPhotonsPerLine );
                this._fieldLine_arr[i][j][1] = this._yC + this.sin_arr[i]*j*this.stageW/(2*this._nbrPhotonsPerLine );
            }
        }
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
