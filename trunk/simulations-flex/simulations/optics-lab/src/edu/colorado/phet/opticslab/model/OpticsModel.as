
/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 5/21/2014
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.model {
import edu.colorado.phet.opticslab.view.LightSourceView;
import edu.colorado.phet.opticslab.view.MainView;

import flash.geom.Point;


//model of Optics Lab sim: sources, lenses, mirrors, masks, etc
public class OpticsModel {

    public var views_arr:Array;     //views associated with this model
    public var myMainView:MainView; //communications hub for model-view-controller
    public var source_arr: Array; //light sources
    private var nbrSources: int;    //number of light sources on stage
    private var opticalComponents_arr: Array;   //lenses, mirrors, masks
    public var testSource: LightSource;         //for testing only

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
        this.source_arr = new Array();
        this.nbrSources = 0;
        this.opticalComponents_arr = new Array();
        //this.testSource = new LightSource( this );

        this.initialize();
    }//end constructor


    private function initialize():void{
        //trace("OpticsModel.initialize called.")
        this.updateViews();
    }  //end initialize()

    public function testRayTracing():void{
        this.testSource = new LightSource( this, nbrSources );
        nbrSources += 1;
    }

    public function createNewLightSource():void{
        var index: uint = nbrSources;
        var newSource: LightSource = new LightSource( this, index );
        newSource.setLocation( Math.random()*0.5, Math.random()*0.5 );  //for testing only
        source_arr[ index ] = newSource;
        myMainView.myLayoutView.createNewLightSourceView( index );
        this.updateViews();
        nbrSources += 1;
    }

    public function getXYOfSource( idx: uint ):Point{
        var sourceLocation:Point = new Point();
        sourceLocation.x = source_arr[ idx ].x;
        sourceLocation.y = source_arr[ idx ].y;
        return sourceLocation;
    }


    public function unregisterLightSource( lightSource: LightSource ): void{
        var indexLocation:int = -1;
        indexLocation = this.source_arr.indexOf( lightSource );
        if( indexLocation != -1 ){
            this.source_arr.splice( indexLocation, 1 );
        }
        this.nbrSources -= 1;
        if( nbrSources < 0 ){trace("ERROR: OpticsModel.unregisterLightSource.  Nbr of light souces is negative.")}
    }

    public function registerView( view: Object ): void {
        this.views_arr.push( view );
        //if( ){};
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
