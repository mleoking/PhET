/**
 * Created by Dubson on 5/24/2014.
 * A LightSource is a collection of Rays, representing source of light.
 * Part of the model.
 */
package edu.colorado.phet.opticslab.model {
import edu.colorado.phet.opticslab.view.LightSourceView;

public class LightSource {
    private var myOpticsModel: OpticsModel;
    private var _x: Number; //x- and y-location of source, in meters
    private var _y: Number; //comment
    private var _index: uint; //index labeling position of this source in OpticsModel.source_arr
    private var ray_arr: Array; //array holding light rays eminating from this light source
    //private var _view: LightSourceView;  //view of this model of LightSource, probably not necessary that this model hold its own view

    public function LightSource( opticsModel: OpticsModel, idx: uint ) {
        this.myOpticsModel = opticsModel;
        _x = 0;
        _y = 0;
        _index = idx;
        ray_arr = new Array();
        createArrayOfRays();
    }

    private function createArrayOfRays():void{
        var nbrRays = 9;                      //fan of N rays uniformly spread over the fullAngle(in rads), facing right
        var fullAngle = 60*Math.PI/180;
        var delAngle = fullAngle/(nbrRays/9);
        for( var i: int = 0; i < 8; i++ ){
            ray_arr[i] = new Ray( this, -(fullAngle/2)+i*delAngle );
        }
    }

    public function setLocation( xInMeters: Number, yInMeters: Number ):void{
        _x = xInMeters;
        _y = yInMeters;
        myOpticsModel.updateViews();
    }

    public function get x():Number {
        return _x;
    }

    public function get y():Number {
        return _y;
    }

//    public function set view( value:LightSourceView ):void {
//        _view = value;
//    }
}//end class
}//end package
