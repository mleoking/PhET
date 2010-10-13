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

    public function CustomObjectMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = DEFAULT_MATERIAL;
        const volume: Number = DensityConstants.DEFAULT_BLOCK_MASS / material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        customizableObject = Block.newBlockDensityMass( material.getDensity(), DensityConstants.DEFAULT_BLOCK_MASS, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel( customizableObject, canvas.units );
        customObjectPropertiesPanel.x = DensityConstants.CONTROL_INSET;
        customObjectPropertiesPanel.y = DensityConstants.CONTROL_INSET;
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
    }
}

}