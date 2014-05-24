
/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 5/21/2014
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.model {
import edu.colorado.phet.opticslab.view.MainView;


//model of Optics Lab sim: sources, lenses, mirrors, masks, etc
public class OpticsModel {

    public var views_arr:Array;     //views associated with this model
    public var myMainView:MainView; //communications hub for model-view-controller
    private var sources_arr: Array; //light sources
    private var opticalComponents: Array;   //lenses, mirrors, masks

    //private var _smallAngle: Number;   //angle in radians between -pi and + pi, regardless of how many full revolutions around unit circle
    //private var _totalAngle: Number;   //total angle in radians between -infinity and +infinity
    //private var _x: Number;            //value of x on unit circle: x = cos(angle)
    //private var _y: Number;            //value of y on unit circle: y = sin(angle)
    //private var previousAngle: Number;
    //private var nbrFullTurns: Number;  //number of full turns around unit circle, increments at theta = pi (not theta = 0)
    //private var _cos: Number;        //cosine of angle _theta
    //private var _sin: Number;
    //private var _tan: Number;
    //private var _specialAnglesMode: Boolean;  //true if in special angle mode = only allowed anlges are 0, 30, 45, 60, 90, etc.


    public function OpticsModel( myMainView: MainView ) {
        this.myMainView = myMainView;
        this.views_arr = new Array();

        this.initialize();
    }//end constructor


    private function initialize():void{
        trace("OpticsModel.initialize called.")
        this.updateViews();
    }  //end initialize()



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
