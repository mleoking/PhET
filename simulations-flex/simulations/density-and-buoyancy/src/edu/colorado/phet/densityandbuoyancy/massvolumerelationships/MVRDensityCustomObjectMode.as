//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.massvolumerelationships {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.MaterialComboBox;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;
import edu.colorado.phet.densityandbuoyancy.view.modes.*;

import flash.geom.ColorTransform;

import mx.core.UIComponent;
import mx.events.FlexEvent;

/**
 * Requested modifications for Abraham, Gelder and Greenbowe, made by copying and modifying the version from the density sim.
 * Represents a 'custom object' mode in the density sim, in which the user sees and manipulates a single customizable block.
 */
public class MVRDensityCustomObjectMode extends Mode {
    private var customizableObject: DensityAndBuoyancyObject;

    private var DEFAULT_MATERIAL: Material = Material.WOOD;
    private var comboBox: MaterialComboBox;

    public function MVRDensityCustomObjectMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = DEFAULT_MATERIAL;
        const volume: Number = DensityAndBuoyancyConstants.DEFAULT_BLOCK_MASS / material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        customizableObject = Block.newBlockDensityMass( material.getDensity(), DensityAndBuoyancyConstants.DEFAULT_BLOCK_MASS, -DensityAndBuoyancyConstants.POOL_WIDTH_X / 3, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );

        comboBox = new MaterialComboBox( customizableObject );
        comboBox.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );

        // grab the panel above this panel
        var modeControlPanel: UIComponent = (canvas.container as MassVolumeRelationshipsCanvas).modeControlPanel;

        // set its initial value
        comboBox.setStyle( "top", modeControlPanel.height + 2 * DensityAndBuoyancyConstants.CONTROL_INSET );

        // if the mode control panel is uninitialized, hide this panel (we have a bad height if that is the case)
        comboBox.visible = modeControlPanel.initialized;

        modeControlPanel.addEventListener( FlexEvent.INITIALIZE, function(): void {
            comboBox.setStyle( "top", modeControlPanel.height + 2 * DensityAndBuoyancyConstants.CONTROL_INSET );
            comboBox.visible = true;
        } );
    }

    override public function teardown(): void {
        super.teardown();
        if ( customObjectPropertiesPanelShowing() ) {
            canvas.container.removeChild( comboBox );
        }
    }

    override public function init(): void {
        super.init();
        customizableObject.updateBox2DModel();

        if ( !customObjectPropertiesPanelShowing() ) {
            canvas.container.addChild( comboBox );
        }
        canvas.model.addDensityObject( customizableObject );
    }

    private function customObjectPropertiesPanelShowing(): Boolean {
        return canvas.container.contains( comboBox );
    }

    public override function reset(): void {
        customizableObject.reset();
        customizableObject.material = DEFAULT_MATERIAL;
    }
}
}