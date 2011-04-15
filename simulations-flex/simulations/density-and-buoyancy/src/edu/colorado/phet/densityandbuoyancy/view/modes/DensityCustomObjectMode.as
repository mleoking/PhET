//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

import flash.geom.ColorTransform;


//REVIEW This class is part of the view (at least according to the package organization) but it retains model
// state in that it keeps the customizableObject around so that it can add it back.  The behavior of the sim is
// a bit inconsistent because one mode retains state and others do not.  Seems like it would work better to have a
// separate model associated with each mode.
/**
 * Represents a 'custom object' mode in the density sim, in which the user sees and manipulates a single customizable block.
 */
public class DensityCustomObjectMode extends Mode {
    private var customizableObject: DensityObject;
    private var customObjectPropertiesPanel: CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing: Boolean = false;//REVIEW why is this necessary? Can't you use canvas.getChildByName to see if the panel is showing?

    private var DEFAULT_MATERIAL: Material = Material.WOOD;

    public function DensityCustomObjectMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = DEFAULT_MATERIAL;
        const volume: Number = DensityAndBuoyancyConstants.DEFAULT_BLOCK_MASS / material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        customizableObject = Block.newBlockDensityMass( material.getDensity(), DensityAndBuoyancyConstants.DEFAULT_BLOCK_MASS, -DensityAndBuoyancyConstants.POOL_WIDTH_X / 3, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel( customizableObject, canvas.units );
        customObjectPropertiesPanel.x = DensityAndBuoyancyConstants.CONTROL_INSET;
        customObjectPropertiesPanel.y = DensityAndBuoyancyConstants.CONTROL_INSET;
    }

    override public function teardown(): void {
        super.teardown();
        if ( customObjectPropertiesPanelShowing ) {
            canvas.container.removeChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = false;
        }
    }

    override public function init(): void {
        super.init();
        customizableObject.updateBox2DModel();

        if ( !customObjectPropertiesPanelShowing ) {
            canvas.container.addChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = true;
        }
        canvas.model.addDensityObject( customizableObject );
    }

    public override function reset(): void {
        customizableObject.reset();
        customizableObject.material = DEFAULT_MATERIAL;
        customObjectPropertiesPanel.reset();
    }
}

}