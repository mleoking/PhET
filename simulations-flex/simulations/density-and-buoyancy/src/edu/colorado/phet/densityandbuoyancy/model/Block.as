package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;
import edu.colorado.phet.densityandbuoyancy.view.BlockNode;
import edu.colorado.phet.densityandbuoyancy.view.DensityObjectNode;

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block.
 */
public class Block extends Cuboid {
    private var color:ColorTransform;

    public function Block(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __material:Material):void {
        super(density, size, size, size, x, y, model, __material);

        this.color = color;
        getDensityProperty().addListener(function ():void {
            updateColor();
        });
    }

    public static function newBlockDensitySize(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __material:Material):Block {
        return new Block(density, size, x, y, color, model, __material);
    }

    public static function newBlockDensityMass(density:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __material:Material):Block {
        return new Block(density, Math.pow(mass / density, 1.0 / 3.0), x, y, color, model, __material);
    }

    public static function newBlockSizeMass(size:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __material:Material):Block {
        return new Block(mass / (size * size * size), size, x, y, color, model, __material);
    }

    public static function newBlockVolumeMass(volume:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __material:Material):Block {
        return new Block(mass / volume, Math.pow(volume, 1.0 / 3), x, y, color, model, __material);
    }

    public function updateColor():void {
        var density:Number = getDensity();
        var maxDensity:Number = 3000;//this value corresponds to the maxColorDelta below
        var minDensity:Number = 100;//this value corresponds to the minColorDelta below
        var maxColorDelta:Number = -100;//at the most dense, colors from the custom.jpg offset by this value
        var minColorDelta:Number = 100;//at the least dense, colors offset by this value, this is at the whiteness end of the spectrum 
        var red:Number = (maxColorDelta - minColorDelta) / (maxDensity - minDensity) * density;
        var green:Number = (maxColorDelta - minColorDelta) / (maxDensity - minDensity) * density;
        var blue:Number = (maxColorDelta - minColorDelta) / (maxDensity - minDensity) * density;
        trace("density = " + density + ", rgb = " + red + ", " + green + ", " + blue);
        this.color = new ColorTransform(1, 1, 1, 1, red, green, blue);
        //notify color listeners to remove order dependency
    }

    public function getColor():ColorTransform {
        return color;
    }

    override public function createNode(view:AbstractDensityModule):DensityObjectNode {
        return new BlockNode(this, view, getLabelProperty());
    }
}
}