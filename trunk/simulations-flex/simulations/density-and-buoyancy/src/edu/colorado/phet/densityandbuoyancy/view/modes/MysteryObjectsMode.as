//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.MysteryObjectsControlPanel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.MysteryBlock;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.DensityContainer;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import mx.core.UIComponent;
import mx.events.FlexEvent;

public class MysteryObjectsMode extends Mode {
    private var mysteryObjectsControlPanel: MysteryObjectsControlPanel;
    private var mysteryObjectsControlPanelShowing: Boolean = false;

    function MysteryObjectsMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        mysteryObjectsControlPanel = new MysteryObjectsControlPanel();
        mysteryObjectsControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );

        // grab the panel above this panel
        var modeControlPanel: UIComponent = (canvas.container as DensityContainer).modeControlPanel;

        // set its initial value
        mysteryObjectsControlPanel.setStyle( "top", modeControlPanel.height + 2 * DensityConstants.CONTROL_INSET );

        // if the mode control panel is uninitialized, hide this panel (we have a bad height if that is the case)
        mysteryObjectsControlPanel.visible = modeControlPanel.initialized;

        modeControlPanel.addEventListener( FlexEvent.INITIALIZE, function(): void {
            mysteryObjectsControlPanel.setStyle( "top", modeControlPanel.height + 2 * DensityConstants.CONTROL_INSET );
            mysteryObjectsControlPanel.visible = true;
        } );
    }

    override public function teardown(): void {
        super.teardown();
        if ( mysteryObjectsControlPanelShowing ) {
            canvas.container.removeChild( mysteryObjectsControlPanel );
            mysteryObjectsControlPanelShowing = false;

            mysteryObjectsControlPanel.teardown();
        }
    }

    override public function init(): void {
        super.init();
        const block1: MysteryBlock = new MysteryBlock( Material.GOLD.getDensity(), 0.15, DensityConstants.POOL_WIDTH_X / 2, 0.15 / 2, DensityConstants.YELLOW, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.A", "A" ) );
        canvas.model.addDensityObject( block1 );
        const block2: MysteryBlock = new MysteryBlock( Material.APPLE.getDensity(), 0.1, DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() + block1.getY(), DensityConstants.BLUE, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.B", "B" ) );
        canvas.model.addDensityObject( block2 );
        const block3: MysteryBlock = new MysteryBlock( Material.GASOLINE.getDensity(), DensityConstants.LARGE_BLOCK_WIDTH, -DensityConstants.POOL_WIDTH_X / 2, 0.18 / 2, DensityConstants.GREEN, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.C", "C" ) );
        canvas.model.addDensityObject( block3 );
        const block4: MysteryBlock = new MysteryBlock( Material.ICE.getDensity(), 0.15, -DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() + block3.getY(), DensityConstants.RED, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.D", "D" ) );
        canvas.model.addDensityObject( block4 );
        const block5: MysteryBlock = new MysteryBlock( Material.DIAMOND.getDensity(), 0.1, -DensityConstants.POOL_WIDTH_X / 2, block4.getHeight() + block4.getY(), DensityConstants.PURPLE, canvas.model, FlexSimStrings.get( "mode.mysteryObjects.E", "E" ) );
        canvas.model.addDensityObject( block5 );

        if ( !mysteryObjectsControlPanelShowing ) {
            canvas.container.addChild( mysteryObjectsControlPanel );
            mysteryObjectsControlPanelShowing = true;
        }
        canvas.model.addDensityObject( new Scale( Scale.GROUND_SCALE_X, Scale.GROUND_SCALE_Y, canvas.model ) );
    }
}
}