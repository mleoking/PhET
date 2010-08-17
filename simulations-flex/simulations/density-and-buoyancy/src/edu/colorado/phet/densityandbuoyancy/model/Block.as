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

    public function Block(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):void {
        super(density, size, size, size, x, y, model, __substance);

        this.color = color;
        getDensityProperty().addListener(function ():void {
            updateColor();
        });
    }

    public static function newBlockDensitySize(density:Number, size:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        return new Block(density, size, x, y, color, model, __substance);
    }

    public static function newBlockDensityMass(density:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        return new Block(density, Math.pow(mass / density, 1.0 / 3.0), x, y, color, model, __substance);
    }

    public static function newBlockSizeMass(size:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        return new Block(mass / (size * size * size), size, x, y, color, model, __substance);
    }

    public static function newBlockVolumeMass(volume:Number, mass:Number, x:Number, y:Number, color:ColorTransform, model:DensityModel, __substance:Substance):Block {
        return new Block(mass / volume, Math.pow(volume, 1.0 / 3), x, y, color, model, __substance);
    }

    public function updateColor():void {
        var density:Number = getDensity();
        var maxDensity:Number = 20 * 1000;
        var minDensity:Number = -1 * 1000;
        var maxRed:Number = 1;
        var minRed:Number = 0;
        var red:Number = 1 - (maxRed - minRed) / (maxDensity - minDensity) * density * 0.9;
        var green:Number = 1 - (maxRed - minRed) / (maxDensity - minDensity) * density * 0.8;
        var blue:Number = 1 - (maxRed - minRed) / (maxDensity - minDensity) * density * 0.7;
        trace("rgb = " + red + ", " + green + ", " + blue);
        this.color = new ColorTransform(clamp(red), clamp(green), clamp(blue));
        //notify color listeners to remove order dependency
    }

    private function clamp(blue:Number):Number {
        if (blue < 0) return 0;
        if (blue > 1)return 1;
        return blue;
    }

    public function getColor():ColorTransform {
        return color;
    }

    override public function createNode(view:AbstractDensityModule):DensityObjectNode {
        return new BlockNode(this, view, getLabelProperty());
    }

}
}