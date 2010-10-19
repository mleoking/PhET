package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

import flash.geom.ColorTransform;

public class OneObjectMode extends Mode {
    private var woodBlock: DensityObject;
    private var customObjectPropertiesPanelWrapper: CustomObjectPropertiesPanelWrapper;

    public function OneObjectMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = Material.WOOD;
        const volume: Number = DensityConstants.litersToMetersCubed( 5 );
        const mass: Number = volume * material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        woodBlock = Block.newBlockDensityMass( material.getDensity(), mass, -DensityConstants.POOL_WIDTH_X / 3, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
        customObjectPropertiesPanelWrapper = new CustomObjectPropertiesPanelWrapper( woodBlock, canvas, DensityConstants.CONTROL_INSET, DensityConstants.CONTROL_INSET );
    }

    override public function teardown(): void {
        super.teardown();
        customObjectPropertiesPanelWrapper.teardown();
    }

    override public function init(): void {
        super.init();
        woodBlock.updateBox2DModel();

        customObjectPropertiesPanelWrapper.init();
        canvas.model.addDensityObject( woodBlock );
    }

    public override function reset(): void {
        woodBlock.reset();
        woodBlock.material = Material.WOOD;
    }
}
}