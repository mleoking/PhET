//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

public class DensitySameDensityMode extends Mode {

    public function DensitySameDensityMode( canvas: AbstractDBCanvas ) {
        super( canvas );
    }

    override public function init(): void {
        super.init();
        const model: DensityModel = canvas.model;
        var density: Number = 800; //Showing the blocks as partially floating allows easier visualization of densities

        //The masses below were selected so that calculations with 2 decimal points come up exactly equal
        var block1: Block = Block.newBlockDensityMass( density, 4, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.CUSTOM );
        block1.setPosition( -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, block1.getHeight() / 2 );
        model.addDensityObject( block1 );

        var block2: Block = Block.newBlockDensityMass( density, 3, 0, 0, DensityAndBuoyancyConstants.BLUE, model, Material.CUSTOM );
        block2.setPosition( -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 - block1.getWidth(), block2.getHeight() / 2 );
        model.addDensityObject( block2 );

        var block3: Block = Block.newBlockDensityMass( density, 2, 0, 0, DensityAndBuoyancyConstants.GREEN, model, Material.CUSTOM );
        block3.setPosition( DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, block3.getHeight() / 2 );
        model.addDensityObject( block3 );

        var block4: Block = Block.newBlockDensityMass( density, 1, 0, 0, DensityAndBuoyancyConstants.RED, model, Material.CUSTOM );
        block4.setPosition( DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 + block3.getWidth(), block4.getHeight() / 2 );
        model.addDensityObject( block4 );
    }
}
}