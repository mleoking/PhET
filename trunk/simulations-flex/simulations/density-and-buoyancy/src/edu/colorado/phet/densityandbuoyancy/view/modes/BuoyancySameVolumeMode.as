//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

import flash.geom.ColorTransform;

public class BuoyancySameVolumeMode extends BuoyancyMode {
    private var woodBlock: DensityObject;
    private var brick: DensityObject;

    public function BuoyancySameVolumeMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = Material.WOOD;
        const volume: Number = DensityAndBuoyancyConstants.litersToMetersCubed( 5 );
        const mass: Number = volume * material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        woodBlock = Block.newBlockDensityMass( material.getDensity(), mass, -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );

        const brickMaterial: Material = Material.BRICK;
        const v2: Number = volume;
        const h2: Number = Math.pow( volume, 1.0 / 3 );
        const mass2: Number = volume * brickMaterial.getDensity();
        brick = Block.newBlockDensityMass( brickMaterial.getDensity(), mass2, DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, h2, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, brickMaterial );
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