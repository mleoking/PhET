
/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.model {
import edu.colorado.phet.triglab.view.MainView;

import flash.events.TimerEvent;

import flash.utils.Timer;
import flash.utils.getTimer;

import mx.rpc.AbstractInvoker;

//model of trigonometric functions of angle theta in radians, includes sine, cosine, and tangent functions
public class TrigModel {

    public var views_arr:Array;     //views associated with this model
    public var myMainView:MainView; //communications hub for model-view-controller
    private var _smallAngle: Number;   //angle in radians between -pi and + pi, regardless of how many full revolutions around unit circle
    private var _totalAngle: Number;   //total angle in radians between -infinity and +infinity
    private var _x: Number;            //value of x on unit circle: x = cos(angle)
    private var _y: Number;            //value of y on unit circle: y = sin(angle)
    private var previousAngle: Number;
    private var nbrFullTurns: Number;  //number of full turns around unit circle, increments at theta = pi (not theta = 0)
    private var _cos: Number;        //cosine of angle _theta
    private var _sin: Number;
    private var _tan: Number;


    public function TrigModel( myMainView: MainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this.initialize();
    }//end constructor


    private function initialize():void{
        this._smallAngle = 0;
        this.nbrFullTurns = 0;
        this._totalAngle = 0;
        this.updateViews();
    }  //end initialize()

//    public function get theta():Number{
//        return _theta;
//    }

    public function get smallAngle():Number{
        return _smallAngle;
    }

    public function get totalAngle():Number{
        return _totalAngle;
    }

    public function get x():Number{
        return _x;
    }

    public function get y():Number{
        return _y;
    }

    public function get cos():Number{
        return _cos;
    }

    public function get sin():Number{
        return _sin;
    }

    public function get tan():Number{
        return _tan;
    }

    //Set the angle in radians, then update the trig functions, then update views
    public function set smallAngle( angleInRads:Number ):void{
        _smallAngle = angleInRads;
        _cos = Math.cos( _smallAngle );
        _sin = Math.sin( _smallAngle );
        _tan = Math.tan( _smallAngle );
        _x = _cos;
        _y = _sin;
        this.updateTotalAngle();
        updateViews();
    }//end set theta();


    private function updateTotalAngle():void{
        if( _smallAngle <= 0  && previousAngle > 2 ){
             this.nbrFullTurns += 1;
        } else if ( _smallAngle >= 0 && previousAngle < -2) {
            this.nbrFullTurns -= 1;
        }
        this._totalAngle = nbrFullTurns*2*Math.PI + this._smallAngle;
        this.previousAngle = this._smallAngle;

    } //end updateTotalAngle()

    public function set totalAngle( totalAngle:Number ):void{
        _totalAngle = totalAngle;
        this.nbrFullTurns = Math.round( totalAngle/( 2*Math.PI ) );
        //this.myMainView.myReadoutView.diagnosticReadout.setText( "nbrTurns =  " + String( nbrTurns ) ) ;
        _cos = Math.cos( _totalAngle );
        _sin = Math.sin( _totalAngle );
        _tan = Math.tan( _totalAngle );
        _x = _cos;
        _y = _sin;
        var moduloAngleInRads:Number = _totalAngle % ( 2*Math.PI );
        this._smallAngle = moduloAngleInRads;
        //this.myMainView.myReadoutView.diagnosticReadout.setText( String( moduloAngleInRads ) ) ;
        updateViews();
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


    public function updateViews(): void {
        for(var i:int = 0; i < this.views_arr.length; i++){
            this.views_arr[ i ].update();
        }
    }//end updateView()


} //end of class
} //end of package
