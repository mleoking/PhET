package edu.colorado.phet.densityflex.model {
import edu.colorado.phet.densityflex.view.BlockNode;
import edu.colorado.phet.densityflex.view.DensityObjectNode;
import edu.colorado.phet.densityflex.view.AbstractDensityModule;

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block.
 */
public class Block extends Cuboid {
    private var color:ColorTransform;

    public function Block(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):void {
        super(density, size, size, size, x, y, model, __substance);

        this.color = color;
    }

    public static function newBlockDensitySize(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        return new Block(density, size, x, y, color, model, __substance);
    }

    public static function newBlockDensityMass(density:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        var size:Number = Math.pow(mass / density, 1.0 / 3.0);
        return new Block(density, size, x, y, color, model, __substance);
    }

    public static function newBlockSizeMass(size:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        var density:Number = mass / (size * size * size);
        return new Block(density, size, x, y, color, model, __substance);
    }

    public function getColor():ColorTransform {
        return color;
    }

    override public function createNode(view:AbstractDensityModule):DensityObjectNode {
        return new BlockNode(this, view, getLabelProperty());
    }

}
}