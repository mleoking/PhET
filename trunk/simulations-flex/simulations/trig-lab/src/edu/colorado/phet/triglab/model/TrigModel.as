
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
    private var _theta: Number;     //angle in radians
    private var _cos:Number;        //cosine of angle _theta
    private var _sin:Number;
    private var _tan:Number;


    public function TrigModel( myMainView: MainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();
        this.initialize();
    }//end constructor


    private function initialize():void{
        _theta = 0;
        this.updateViews();
    }  //end initialize()

    public function get theta():Number{
        return _theta;
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
    public function set theta( angleInRads:Number ):void{
        _theta = angleInRads;
        _cos =  Math.cos( _theta );
        _sin = Math.sin( _theta );
        _tan = Math.tan( _theta );
        updateViews();
    }//end set theta();


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
