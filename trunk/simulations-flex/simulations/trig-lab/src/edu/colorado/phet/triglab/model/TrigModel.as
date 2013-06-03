
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
    //private var _theta: Number;     //angle in radians
    private var _smallAngle: Number;
    private var _totalAngle: Number;
    private var previousAngle: Number;
    private var nbrHalfTurns: Number;
    private var _cos: Number;        //cosine of angle _theta
    private var _sin: Number;
    private var _tan: Number;


    public function TrigModel( myMainView: MainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this.initialize();
    }//end constructor


    private function initialize():void{
        //_theta = 0;
        this._smallAngle = 0;
        this.nbrHalfTurns = 0;
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
        this.updateTotalAngle();
        updateViews();
    }//end set theta();

    private function updateTotalAngle():void{
        if( _smallAngle <= 0 && previousAngle > 0 ){
            if( previousAngle > 2 ){
                nbrHalfTurns += 1;
            }else if( previousAngle < 1 && previousAngle > 0 && nbrHalfTurns != 0){
                nbrHalfTurns -= 1;
            }
            trace( "nbrHalfTurns: "+nbrHalfTurns );

        } else if( _smallAngle >= 0 && previousAngle < 0 ) {
            if( previousAngle < -2 ){
                nbrHalfTurns -= 1;
            }else if( previousAngle < 0 && previousAngle > -1 && nbrHalfTurns != 0){
                nbrHalfTurns += 1;
            }
            trace( "nbrHalfTurns: "+nbrHalfTurns );
        }

        this.previousAngle = this._smallAngle;
        if( nbrHalfTurns == 0 ){
            this._totalAngle = this._smallAngle;
        } else if ( nbrHalfTurns > 0 ){
            if( _smallAngle >= 0 ){
                this._totalAngle = nbrHalfTurns*Math.PI + this.smallAngle;
            }else if ( _smallAngle < 0 ){
                this._totalAngle = nbrHalfTurns*Math.PI + Math.PI + this.smallAngle;
            }
        } else if ( nbrHalfTurns < 0 ){
            if( _smallAngle < 0 ){
                this._totalAngle = nbrHalfTurns*Math.PI + this.smallAngle;
            }else if ( _smallAngle >= 0 ){
                this._totalAngle = nbrHalfTurns*Math.PI - Math.PI + this.smallAngle;
            }
        }
    }//end updateTotalAngle

    public function set totalAngle( totalAngle:Number ):void{
        _totalAngle = totalAngle;
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
