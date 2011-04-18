//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.MysteryObjectsControlPanel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.MysteryBlock;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;
import edu.colorado.phet.densityandbuoyancy.view.DensityContainer;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import mx.core.UIComponent;
import mx.events.FlexEvent;

/**
 * Density simulation mode which shows mystery blocks for which the users can try to determine the identities of various materials.
 */
public class DensityMysteryObjectsMode extends Mode {
    private var mysteryObjectsControlPanel: MysteryObjectsControlPanel;

    function DensityMysteryObjectsMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super( canvas );
        mysteryObjectsControlPanel = new MysteryObjectsControlPanel();
        mysteryObjectsControlPanel.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );

        // grab the panel above this panel
        var modeControlPanel: UIComponent = (canvas.container as DensityContainer).modeControlPanel;

        // set its initial value
        mysteryObjectsControlPanel.setStyle( "top", modeControlPanel.height + 2 * DensityAndBuoyancyConstants.CONTROL_INSET );

        // if the mode control panel is uninitialized, hide this panel (we have a bad height if that is the case)
        mysteryObjectsControlPanel.visible = modeControlPanel.initialized;

        modeControlPanel.addEventListener( FlexEvent.INITIALIZE, function(): void {
            mysteryObjectsControlPanel.setStyle( "top", modeControlPanel.height + 2 * DensityAndBuoyancyConstants.CONTROL_INSET );
            mysteryObjectsControlPanel.visible = true;
        } );
    }

    override public function teardown(): void {
        super.teardown();
        if ( isControlPanelShowing() ) {
            canvas.container.removeChild( mysteryObjectsControlPanel );
            mysteryObjectsControlPanel.teardown();
        }
    }

    override public function init(): void {
        super.init();
        const block1: MysteryBlock = new MysteryBlock( Material.GOLD.getDensity(), 0.15, DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, 0.15 / 2, DensityAndBuoyancyConstants.YELLOW, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.A", "A" ) );
        canvas.model.addDensityObject( block1 );
        const block2: MysteryBlock = new MysteryBlock( Material.APPLE.getDensity(), 0.1, DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, block1.getHeight() + block1.getY(), DensityAndBuoyancyConstants.BLUE, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.B", "B" ) );
        canvas.model.addDensityObject( block2 );
        const block3: MysteryBlock = new MysteryBlock( Material.GASOLINE.getDensity(), DensityAndBuoyancyConstants.LARGE_BLOCK_WIDTH, -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, 0.18 / 2, DensityAndBuoyancyConstants.GREEN, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.C", "C" ) );
        canvas.model.addDensityObject( block3 );
        const block4: MysteryBlock = new MysteryBlock( Material.ICE.getDensity(), 0.15, -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, block3.getHeight() + block3.getY(), DensityAndBuoyancyConstants.RED, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.D", "D" ) );
        canvas.model.addDensityObject( block4 );
        const block5: MysteryBlock = new MysteryBlock( Material.DIAMOND.getDensity(), 0.1, -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, block4.getHeight() + block4.getY(), DensityAndBuoyancyConstants.PURPLE, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.E", "E" ) );
        canvas.model.addDensityObject( block5 );

        if ( !isControlPanelShowing() ) {
            canvas.container.addChild( mysteryObjectsControlPanel );
        }
        canvas.model.addDensityObject( new Scale( Scale.GROUND_SCALE_X_LEFT, Scale.GROUND_SCALE_Y, canvas.model ) );
    }

    private function isControlPanelShowing(): Boolean {
        return canvas.container.contains( mysteryObjectsControlPanel );
    }
}
}