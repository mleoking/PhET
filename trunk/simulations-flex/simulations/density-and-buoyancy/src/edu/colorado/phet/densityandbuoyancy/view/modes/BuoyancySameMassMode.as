//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

import flash.geom.ColorTransform;

public class BuoyancySameMassMode extends BuoyancyMode {
    private var woodBlock: DensityObject;
    private var brick: DensityObject;

    public function BuoyancySameMassMode( canvas: AbstractDBCanvas ) {
        super( canvas );
        //Showing the blocks as partially floating allows easier visualization of densities
        const material: Material = Material.WOOD;
        const mass: Number = DensityConstants.DEFAULT_BLOCK_MASS * 2.5;
        const volume: Number = mass / material.getDensity();
        const height: Number = Math.pow( volume, 1.0 / 3 );
        woodBlock = Block.newBlockDensityMass( material.getDensity(), mass, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, material );
        woodBlock.name = "woodBlock";

        const brickMaterial: Material = Material.BRICK;
        const v2: Number = mass / material.getDensity();
        const h2: Number = Math.pow( volume, 1.0 / 3 );
        brick = Block.newBlockDensityMass( brickMaterial.getDensity(), mass, DensityConstants.POOL_WIDTH_X / 2, h2, new ColorTransform( 0.5, 0.5, 0 ), canvas.model, brickMaterial );
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