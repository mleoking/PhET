package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

import flash.geom.ColorTransform;

public class BuoyancySameDensityMode extends Mode {
    private var woodBlock: DensityObject;
    private var brick: DensityObject;

    public function BuoyancySameDensityMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = Material.WOOD;
        const volume: Number = DensityConstants.litersToMetersCubed( 5 );
        const mass: Number = volume * material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        woodBlock = Block.newBlockDensityMass( material.getDensity(), mass, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );

        const v2: Number = DensityConstants.litersToMetersCubed( 8 );
        const h2: Number = Math.pow( v2, 1.0 / 3 );
        const mass2: Number = v2 * material.getDensity();
        brick = Block.newBlockDensityMass( material.getDensity(), mass2, DensityConstants.POOL_WIDTH_X / 2, h2, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
    }

    override public function init(): void {
        super.init();
        woodBlock.updateBox2DModel();
        brick.updateBox2DModel();

        canvas.model.addDensityObject( woodBlock );
        canvas.model.addDensityObject( brick );
    }

    public override function reset(): void {
        woodBlock.reset();
        brick.reset();
    }
}
}