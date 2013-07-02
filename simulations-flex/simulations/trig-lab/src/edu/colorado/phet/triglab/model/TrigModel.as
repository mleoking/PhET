
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
    private var _specialAnglesMode: Boolean;  //true if in special angle mode = only allowed anlges are 0, 30, 45, 60, 90, etc.


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
        if( _specialAnglesMode ){
            _smallAngle = this.roundToSpecialAngle( _smallAngle );
        }
        _cos = Math.cos( _smallAngle );
        _sin = Math.sin( _smallAngle );
        _tan = Math.tan( _smallAngle );
        _x = _cos;
        _y = _sin;
        this.updateTotalAngle();
        updateViews();
    }//end set theta();


    private function updateTotalAngle():void{
        if( _smallAngle <= 0  && previousAngle > 2.60 ){
             this.nbrFullTurns += 1;
        } else if ( _smallAngle >= 0 && previousAngle < -2.60) {
            this.nbrFullTurns -= 1;
        }
        this._totalAngle = nbrFullTurns*2*Math.PI + this._smallAngle;
        this.previousAngle = this._smallAngle;

    } //end updateTotalAngle()

    public function set totalAngle( totalAngle:Number ):void{
        _totalAngle = totalAngle;
        this.nbrFullTurns = Math.round( totalAngle/( 2*Math.PI ) );
        //this.myMainView.myReadoutView.diagnosticReadout.setText( "nbrTurns =  " + String( nbrFullTurns ) ) ;
        _cos = Math.cos( _totalAngle );
        _sin = Math.sin( _totalAngle );
        _tan = Math.tan( _totalAngle );
        _x = _cos;
        _y = _sin;
        var moduloAngleInRads:Number = _totalAngle - nbrFullTurns*2*Math.PI;
        smallAngle = moduloAngleInRads;   //Note this.smallAngle, NOT this._smallAngle, so that roundToNearestSpecialAngle called, if necessary
        var moduloAngleInDegs: Number = moduloAngleInRads*180/Math.PI;
        //this.myMainView.myReadoutView.diagnosticReadout.setText( String( moduloAngleInDegs ) ) ;
        updateViews();
    }

    /*Take input small angle in rads (between -pi and +pi) and convert to nearest "special" angle in rads.
     *The special angles (in degrees) are 0, 30, 45, 60, 90, etc.
     */
    private function roundToSpecialAngle( anyAngleInRads: Number ): Number{
        trace("TrigModel.roundToSpecialAngle called");
        var angleInDegs: Number = anyAngleInRads*180/Math.PI;
        var nearestSpecialAngleInRads: Number = 0;
        var angles: Array = [-150, -135, -120, -90, -60, -45, -30, 0, 30, 45, 60, 90, 120, 135, 150, 180 ];
        var border: Array = [-165, -142.5, -127.5, -105, -75, -52.5, -37.5, -15, 15, 37.5, 52.5, 75, 105, 127.5, 142.5, 165 ] ;
        for ( var i:int = 0; i < angles.length; i++ ){
            if( angleInDegs > border[i] && angleInDegs < border[i + 1] ){
                nearestSpecialAngleInRads = angles[i]*Math.PI/180;
            }
            //Must deal with 180 deg angle as a special case.
            if( angleInDegs > 165 || angleInDegs < -165 ){
                nearestSpecialAngleInRads = 180*Math.PI/180;
            }
        }
        return nearestSpecialAngleInRads;
    }//end roundToSpecialAngle()


    public function set specialAnglesMode( toOrF:Boolean ):void{
        this._specialAnglesMode = toOrF;
        this.smallAngle = _smallAngle;
    }

    public function get specialAnglesMode():Boolean{
        return this._specialAnglesMode;
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
