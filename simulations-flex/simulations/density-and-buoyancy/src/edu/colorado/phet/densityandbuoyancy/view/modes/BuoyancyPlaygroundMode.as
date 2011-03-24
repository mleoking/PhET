package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.events.Event;
import flash.geom.ColorTransform;

public class BuoyancyPlaygroundMode extends BuoyancyMode {
    private var block1: DensityObject;
    private var block2: DensityObject;
    private var customObjectPropertiesPanelWrapper1: CustomObjectPropertiesPanelWrapper;
    private var customObjectPropertiesPanelWrapper2: CustomObjectPropertiesPanelWrapper;
    public var oneObject: BooleanProperty = new BooleanProperty( true );

    private const defaultMaterial: Material = Material.WOOD;

    public function BuoyancyPlaygroundMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const volume: Number = DensityConstants.litersToMetersCubed( 5 );
        const mass: Number = volume * defaultMaterial.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        block1 = Block.newBlockDensityMass( defaultMaterial.getDensity(), mass, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, defaultMaterial );
        block1.name = FlexSimStrings.get( "blockName.a", "A" );

        const v2: Number = DensityConstants.litersToMetersCubed( 8 );
        const h2: Number = Math.pow( v2, 1.0 / 3 );
        const mass2: Number = v2 * defaultMaterial.getDensity();
        block2 = Block.newBlockDensityMass( defaultMaterial.getDensity(), mass2, DensityConstants.POOL_WIDTH_X / 2, h2, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, defaultMaterial );
        block2.name = FlexSimStrings.get( "blockName.b", "B" );
        customObjectPropertiesPanelWrapper1 = new CustomObjectPropertiesPanelWrapper( block1, canvas, DensityConstants.CONTROL_INSET, DensityConstants.CONTROL_INSET );
        customObjectPropertiesPanelWrapper2 = new CustomObjectPropertiesPanelWrapper( block2, canvas, customObjectPropertiesPanelWrapper1.x + customObjectPropertiesPanelWrapper1.width, DensityConstants.CONTROL_INSET );
        customObjectPropertiesPanelWrapper1.customObjectPropertiesPanel.addEventListener( Event.RESIZE, function(): void {
            customObjectPropertiesPanelWrapper2.customObjectPropertiesPanel.x = customObjectPropertiesPanelWrapper1.x + customObjectPropertiesPanelWrapper1.width + DensityConstants.CONTROL_INSET;
        } );
    }

    override public function init(): void {
        super.init();
        block1.updateBox2DModel();
        block2.updateBox2DModel();
        customObjectPropertiesPanelWrapper1.init();
        canvas.model.addDensityObject( block1 );
    }

    override public function teardown(): void {
        super.teardown();
        customObjectPropertiesPanelWrapper1.teardown();
        customObjectPropertiesPanelWrapper2.teardown();
    }

    public override function reset(): void {
        block1.reset();
        block1.material = defaultMaterial;
        block2.reset();
        block2.material = defaultMaterial;
        setOneObject();
        customObjectPropertiesPanelWrapper1.reset();
        customObjectPropertiesPanelWrapper2.reset();
    }

    public function setOneObject(): void {
        if ( !oneObject.value ) {
            customObjectPropertiesPanelWrapper2.teardown();
            canvas.model.removeDensityObject( block2 );
            block1.updateBox2DModel();
            block1.nameVisible = false;
            block2.nameVisible = true;

            oneObject.value = true;
        }
    }

    public function setTwoObjects(): void {
        if ( oneObject ) {
            customObjectPropertiesPanelWrapper2.init();
            canvas.model.addDensityObject( block2 );
            block1.updateBox2DModel();
            block2.updateBox2DModel();
            block1.nameVisible = true;
            block2.nameVisible = true;

            oneObject.value = false;
        }
    }
}
}