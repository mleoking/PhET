//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.away3d.BlockNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityAndBuoyancyObjectNode;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.StringProperty;

import flash.geom.ColorTransform;

/**
 * The MysteryBlock is used in the 'mystery' mode of density, and increases the font of the block name readout so it is more visible.
 */
public class MysteryBlock extends Block {
    private var label: String;

    public function MysteryBlock( density: Number, size: Number, x: Number, y: Number, color: ColorTransform, model: DensityAndBuoyancyModel, label: String ) {
        super( density, size, x, y, color, model, Material.CUSTOM );
        this.label = label;
    }

    override public function createNode( view: AbstractDBCanvas, massReadoutsVisible: BooleanProperty ): DensityAndBuoyancyObjectNode {
        return new BlockNode( this, view, new StringProperty( label ), massReadoutsVisible, 2 );
    }

}
}