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
    private var _view: LightSourceView;  //view of this model of LightSource

    public function LightSource( opticsModel: OpticsModel, idx: uint ) {
        this.myOpticsModel = opticsModel;
        _x = 0;
        _y = 0;
        _index = idx;
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
}//end class
}//end package
