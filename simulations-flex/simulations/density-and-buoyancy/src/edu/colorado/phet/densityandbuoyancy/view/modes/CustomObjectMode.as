package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

import flash.geom.ColorTransform;

public class CustomObjectMode extends Mode {
    private var customizableObject: DensityObject;
    private var customObjectPropertiesPanel: CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing: Boolean = false;

    private var DEFAULT_MATERIAL: Material = Material.WOOD;

    public function CustomObjectMode( module: AbstractDBCanvas ) {
        super( module );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = DEFAULT_MATERIAL;
        const volume: Number = DensityConstants.DEFAULT_BLOCK_MASS / material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        customizableObject = Block.newBlockDensityMass( material.getDensity(), DensityConstants.DEFAULT_BLOCK_MASS, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), module.model, material );
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel( customizableObject, module.units );
        customObjectPropertiesPanel.x = DensityConstants.CONTROL_INSET;
        customObjectPropertiesPanel.y = DensityConstants.CONTROL_INSET;
    }

    override public function teardown(): void {
        super.teardown();
        if ( customObjectPropertiesPanelShowing ) {
            module.canvas.removeChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = false;
        }
    }

    override public function init(): void {
        super.init();
        customizableObject.updateBox2DModel();

        if ( !customObjectPropertiesPanelShowing ) {
            module.canvas.addChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = true;
        }
        module.model.addDensityObject( customizableObject );

        //        module.getModel().addDensityObject(new Scale(-DensityConstants.POOL_WIDTH_X/2-Scale.SCALE_WIDTH/2, 0.05, module.getModel(), 100));//For debugging the scale
    }

    public function reset(): void {
        customizableObject.reset();
        customizableObject.material = DEFAULT_MATERIAL;
    }
}

}