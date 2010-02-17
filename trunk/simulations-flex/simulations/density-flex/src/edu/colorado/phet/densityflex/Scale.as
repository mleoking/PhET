package edu.colorado.phet.densityflex {


/**
 * This class represents the model object for a scale.
 */
public class Scale extends Cuboid {
    private static var SCALE_DENSITY : Number = 1.0;

    public function Scale( x:Number, y:Number, model : DensityModel ) : void {
        super(SCALE_DENSITY, 3, 1, 3, x, y, model);
    }


}
}