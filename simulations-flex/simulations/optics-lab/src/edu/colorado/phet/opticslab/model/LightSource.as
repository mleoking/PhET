/**
 * Created by Dubson on 5/24/2014.
 * A LightSource is a collection of Rays, representing source of light.
 * Part of the model.
 */
package edu.colorado.phet.opticslab.model {
public class LightSource {
    private var myOpticsModel: OpticsModel;
    private var _x: Number; //x- and y-location of source
    private var _y: Number;

    public function LightSource( opticsModel: OpticsModel ) {
        this.myOpticsModel = opticsModel;
        _x = 0;
        _y = 0;
    }


    public function setLocation( x: Number, y: Number ):void{
        _x = x;
        _y = y;
        myOpticsModel.updateViews();
    }
}//end class
}//end package
