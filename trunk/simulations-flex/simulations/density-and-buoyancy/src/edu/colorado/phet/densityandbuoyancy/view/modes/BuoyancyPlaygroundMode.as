package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.Event;
import flash.geom.ColorTransform;

public class BuoyancyPlaygroundMode extends Mode {
    private var woodBlock: DensityObject;
    private var brick: DensityObject;
    private var customObjectPropertiesPanelWrapper1: CustomObjectPropertiesPanelWrapper;
    private var customObjectPropertiesPanelWrapper2: CustomObjectPropertiesPanelWrapper;
    private var oneObject: Boolean = true;

    public function BuoyancyPlaygroundMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = Material.WOOD;
        const volume: Number = DensityConstants.litersToMetersCubed( 5 );
        const mass: Number = volume * material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        woodBlock = Block.newBlockDensityMass( material.getDensity(), mass, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
        woodBlock.name = FlexSimStrings.get( "blockName.a", "A" );

        const v2: Number = DensityConstants.litersToMetersCubed( 8 );
        const h2: Number = Math.pow( v2, 1.0 / 3 );
        const mass2: Number = v2 * material.getDensity();
        brick = Block.newBlockDensityMass( material.getDensity(), mass2, DensityConstants.POOL_WIDTH_X / 2, h2, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
        brick.name = FlexSimStrings.get( "blockName.b", "B" );
        customObjectPropertiesPanelWrapper1 = new CustomObjectPropertiesPanelWrapper( woodBlock, canvas, DensityConstants.CONTROL_INSET, DensityConstants.CONTROL_INSET );
        customObjectPropertiesPanelWrapper2 = new CustomObjectPropertiesPanelWrapper( brick, canvas, customObjectPropertiesPanelWrapper1.x + customObjectPropertiesPanelWrapper1.width, DensityConstants.CONTROL_INSET );
        customObjectPropertiesPanelWrapper1.customObjectPropertiesPanel.addEventListener( Event.RESIZE, function(): void {
            customObjectPropertiesPanelWrapper2.customObjectPropertiesPanel.x = customObjectPropertiesPanelWrapper1.x + customObjectPropertiesPanelWrapper1.width + DensityConstants.CONTROL_INSET;
        } );
    }

    override public function init(): void {
        super.init();
        woodBlock.updateBox2DModel();
        brick.updateBox2DModel();
        customObjectPropertiesPanelWrapper1.init();
        canvas.model.addDensityObject( woodBlock );
    }

    override public function teardown(): void {
        super.teardown();
        customObjectPropertiesPanelWrapper1.teardown();
        customObjectPropertiesPanelWrapper2.teardown();
    }

    public override function reset(): void {
        woodBlock.reset();
        woodBlock.material = Material.WOOD;
        brick.reset();
        brick.material = Material.BRICK;
        setOneObject();
    }

    public function setOneObject(): void {
        if ( !oneObject ) {
            customObjectPropertiesPanelWrapper2.teardown();
            canvas.model.removeDensityObject( brick );
            woodBlock.updateBox2DModel();
            woodBlock.nameVisible = false;
            brick.nameVisible = true;

            oneObject = true;
        }
    }

    public function setTwoObjects(): void {
        if ( oneObject ) {
            customObjectPropertiesPanelWrapper2.init();
            canvas.model.addDensityObject( brick );
            woodBlock.updateBox2DModel();
            brick.updateBox2DModel();
            woodBlock.nameVisible = true;
            brick.nameVisible = true;

            oneObject = false;
        }
    }
}
}