//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.away3d.BlockNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.geom.ColorTransform;

/**
 * This class represents the model object for a block, implemented as a Cuboid with a ColorTransform
 */
public class Block extends Cuboid {
    private var _colorTransform: ColorTransform;//Used to map colors on textures, e.g., to make the brick texture look blue or red

    public function Block( density: Number, size: Number, x: Number, y: Number, colorTransform: ColorTransform, model: DensityAndBuoyancyModel, __material: Material ): void {
        super( density, size, size, size, x, y, model, __material );
        this._colorTransform = colorTransform;
        getDensityProperty().addListener( updateColorTransform );
        addMaterialListener( updateColorTransform );
    }

    //Updates the color of the block based on its density
    public function updateColorTransform(): void {
        var maxDensity: Number = 3000;//this value corresponds to the maxColorDelta below
        var minDensity: Number = 100;//this value corresponds to the minColorDelta below
        var maxColorDelta: Number = -100;//at the most dense, colors from the custom.jpg offset by this value
        var minColorDelta: Number = 100;//at the least dense, colors offset by this value, this is at the whiteness end of the spectrum
        var red: Number = (maxColorDelta - minColorDelta) / (maxDensity - minDensity) * density;
        var green: Number = (maxColorDelta - minColorDelta) / (maxDensity - minDensity) * density;
        var blue: Number = (maxColorDelta - minColorDelta) / (maxDensity - minDensity) * density;
        trace( "density = " + density + ", rgb = " + red + ", " + green + ", " + blue );
        this._colorTransform = new ColorTransform( 1, 1, 1, 1, red, green, blue );
        //notify color listeners to remove order dependency
        notifyColorTransformListeners();
    }

    public function get colorTransform(): ColorTransform {
        return _colorTransform;
    }

    override public function createNode( view: AbstractDBCanvas, massReadoutsVisible: BooleanProperty ): DensityObjectNode {
        return new BlockNode( this, view, getLabelProperty(), massReadoutsVisible );
    }

    override public function toString(): String {
        return "Block: " + super.toString();
    }

    public static function newBlockDensityMass( density: Number, mass: Number, x: Number, y: Number, color: ColorTransform, model: DensityAndBuoyancyModel, __material: Material ): Block {
        return new Block( density, Math.pow( mass / density, 1.0 / 3.0 ), x, y, color, model, __material );
    }

    public static function newBlockVolumeMass( volume: Number, mass: Number, x: Number, y: Number, color: ColorTransform, model: DensityAndBuoyancyModel, __material: Material ): Block {
        return new Block( mass / volume, Math.pow( volume, 1.0 / 3 ), x, y, color, model, __material );
    }
}
}