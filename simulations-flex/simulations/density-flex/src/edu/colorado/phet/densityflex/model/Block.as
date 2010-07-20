package edu.colorado.phet.densityflex.model {
import away3d.core.base.Object3D;

import edu.colorado.phet.densityflex.view.ArrowNode;
import edu.colorado.phet.densityflex.view.BlockNode;
import edu.colorado.phet.densityflex.view.DensityObjectNode;
import edu.colorado.phet.densityflex.view.DensityView;

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block.
 */
public class Block extends Cuboid {
    private var color:ColorTransform;

    public function Block(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel):void {
        super(density, size, size, size, x, y, model);

        this.color = color;
    }

    public static function newBlockDensitySize(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel):Block {
        return new Block(density, size, x, y, color, model);
    }

    public static function newBlockDensityMass(density:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel):Block {
        var size:Number = Math.pow(mass / density, 1.0 / 3.0);
        return new Block(density, size, x, y, color, model);
    }

    public static function newBlockSizeMass(size:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel):Block {
        var density:Number = mass / (size * size * size);
        return new Block(density, size, x, y, color, model);
    }

    public function getColor():ColorTransform {
        return color;
    }

    override public function createNode(view:DensityView):DensityObjectNode{
        var blockNode:BlockNode = new BlockNode(this, view);
        return blockNode;
    }
}
}