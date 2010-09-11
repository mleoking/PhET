package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.MysteryObjectsControlPanel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.MysteryBlock;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class MysteryObjectsMode extends Mode {
    private var mysteryObjectsControlPanel:MysteryObjectsControlPanel;
    private var mysteryObjectsControlPanelShowing:Boolean = false;

    function MysteryObjectsMode( module:AbstractDensityModule ) {
        super( module );
        mysteryObjectsControlPanel = new MysteryObjectsControlPanel();
    }

    override public function teardown():void {
        super.teardown();
        if ( mysteryObjectsControlPanelShowing ) {
            module.canvas.removeChild( mysteryObjectsControlPanel );
            mysteryObjectsControlPanelShowing = false;
        }
    }

    override public function init():void {
        super.init();
        const block1:MysteryBlock = new MysteryBlock( Material.GOLD.getDensity(), 0.15, DensityConstants.POOL_WIDTH_X / 2, 0.15 / 2, DensityConstants.YELLOW, module.model, FlexSimStrings.get( "mode.mysteryObjects.A", "A" ) );
        module.model.addDensityObject( block1 );
        const block2:MysteryBlock = new MysteryBlock( Material.APPLE.getDensity(), 0.1, DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() + block1.getY(), DensityConstants.BLUE, module.model, FlexSimStrings.get( "mode.mysteryObjects.B", "B" ) );
        module.model.addDensityObject( block2 );
        const block3:MysteryBlock = new MysteryBlock( Material.GASOLINE_BALLOON.getDensity(), DensityConstants.LARGE_BLOCK_WIDTH, -DensityConstants.POOL_WIDTH_X / 2, 0.18 / 2, DensityConstants.GREEN, module.model, FlexSimStrings.get( "mode.mysteryObjects.C", "C" ) );
        module.model.addDensityObject( block3 );
        const block4:MysteryBlock = new MysteryBlock( Material.ICE.getDensity(), 0.15, -DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() + block3.getY(), DensityConstants.RED, module.model, FlexSimStrings.get( "mode.mysteryObjects.D", "D" ) );
        module.model.addDensityObject( block4 );
        const block5:MysteryBlock = new MysteryBlock( Material.DIAMOND.getDensity(), 0.1, -DensityConstants.POOL_WIDTH_X / 2, block4.getHeight() + block4.getY(), DensityConstants.PURPLE, module.model, FlexSimStrings.get( "mode.mysteryObjects.E", "E" ) );
        module.model.addDensityObject( block5 );

//        module.model.addDensityObject( new Scale( Scale.SCALE_X, Scale.SCALE_HEIGHT / 2, module.model, 100 ) );

        if ( !mysteryObjectsControlPanelShowing ) {
            module.canvas.addChild( mysteryObjectsControlPanel );
            mysteryObjectsControlPanelShowing = true;
        }
    }

    override public function addScales():void {
        module.model.addDensityObject( new Scale( Scale.GROUND_SCALE_X, Scale.GROUND_SCALE_Y, module.model ) );
        if( module.showScales() ) {
            module.model.addDensityObject( new Scale( Scale.POOL_SCALE_X, Scale.POOL_SCALE_Y, module.model ) );
        }
    }
}
}