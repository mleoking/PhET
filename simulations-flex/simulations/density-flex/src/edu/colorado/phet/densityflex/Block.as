package edu.colorado.phet.densityflex {

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block.
 */
public class Block extends Cuboid {
    private var color:ColorTransform;

    public function Block( density : Number, size : Number, x:Number, y:Number, color : ColorTransform, model : DensityModel ) : void {
        super(density, size, size, size, x, y, model);

        this.color = color;
    }

    public function getColor():ColorTransform {
        return color;
    }

}
}