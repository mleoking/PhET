//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.massvolumerelationships {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.MaterialComboBox;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;
import edu.colorado.phet.densityandbuoyancy.view.modes.*;

import mx.core.UIComponent;
import mx.events.FlexEvent;

/**
 * Requested modifications for Abraham, Gelder and Greenbowe, made by copying and modifying the version from the density sim.
 * Density simulation mode which shows 4 blocks of the same density (though different colors)
 */
public class SameSubstanceMode extends Mode {

    public function SameSubstanceMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super( canvas );
    }

    private var _block1: Block;
    private var _block2: Block;
    private var _block3: Block;
    private var _block4: Block;

    private var comboBox: MaterialComboBox;

    override public function init(): void {
        super.init();
        const model: DensityAndBuoyancyModel = canvas.model;
        var density: Number = 800; //Showing the blocks as partially floating allows easier visualization of densities

        //The masses below were selected so that calculations with 2 decimal points come up exactly equal
        _block1 = Block.newBlockDensityMass( density, 4, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.WOOD );
        model.addDensityObject( _block1 );

        _block2 = Block.newBlockDensityMass( density, 3, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.WOOD );
        model.addDensityObject( _block2 );

        _block3 = Block.newBlockDensityMass( density, 2, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.WOOD );
        model.addDensityObject( _block3 );

        _block4 = Block.newBlockDensityMass( density, 1, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.WOOD );
        model.addDensityObject( _block4 );

        canvas.model.addDensityObject( new Scale( Scale.GROUND_SCALE_X_LEFT, Scale.GROUND_SCALE_Y, canvas.model ) );

        resetBlockPositions();

        comboBox = new MaterialComboBox( _block1 );
        _block1.addMaterialListener( function f(): void {
            _block2.material = _block1.material;
            _block3.material = _block1.material;
            _block4.material = _block1.material;
        } );
        comboBox.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );

        // grab the panel above this panel
        var modeControlPanel: UIComponent = (canvas.container as MVRCanvas).modeControlPanel;

        // set its initial value
        comboBox.setStyle( "top", modeControlPanel.height + 2 * DensityAndBuoyancyConstants.CONTROL_INSET );

        // if the mode control panel is uninitialized, hide this panel (we have a bad height if that is the case)
        comboBox.visible = modeControlPanel.initialized;

        modeControlPanel.addEventListener( FlexEvent.INITIALIZE, function(): void {
            comboBox.setStyle( "top", modeControlPanel.height + 2 * DensityAndBuoyancyConstants.CONTROL_INSET );
            comboBox.visible = true;
        } );

        if ( !customObjectPropertiesPanelShowing() ) {
            canvas.container.addChild( comboBox );
        }
    }

    override public function reset(): void {
        super.reset();
        _block1.reset();
        _block1.material = Material.WOOD;
        resetBlockPositions();
    }

    private function resetBlockPositions(): void {
        _block1.setPosition( -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, _block1.getHeight() / 2 );
        _block2.setPosition( -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 - _block1.getWidth(), _block2.getHeight() / 2 );
        _block3.setPosition( DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, _block3.getHeight() / 2 );
        _block4.setPosition( DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 + _block3.getWidth(), _block4.getHeight() / 2 );
    }

    override public function teardown(): void {
        super.teardown();
        if ( customObjectPropertiesPanelShowing() ) {
            canvas.container.removeChild( comboBox );
        }
    }

    private function customObjectPropertiesPanelShowing(): Boolean {
        return canvas.container.contains( comboBox );
    }
}
}