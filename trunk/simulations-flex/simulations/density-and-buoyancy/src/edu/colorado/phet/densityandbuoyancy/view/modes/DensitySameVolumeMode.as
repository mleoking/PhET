//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

public class DensitySameVolumeMode extends Mode {

    public function DensitySameVolumeMode( canvas: AbstractDBCanvas ) {
        super( canvas );
    }

    override public function init(): void {
        super.init();
        const model: DensityModel = canvas.model;

        var block1: Block = Block.newBlockVolumeMass( DensityConstants.litersToMetersCubed( 5 ), 8, 0, 0, DensityConstants.YELLOW, model, Material.CUSTOM );
        block1.setPosition( -DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() / 2 );
        model.addDensityObject( block1 );

        var block2: Block = Block.newBlockVolumeMass( DensityConstants.litersToMetersCubed( 5 ), 6, 0, 0, DensityConstants.BLUE, model, Material.CUSTOM );
        block2.setPosition( -DensityConstants.POOL_WIDTH_X / 2 - block1.getWidth(), block2.getHeight() / 2 );
        model.addDensityObject( block2 );

        var block3: Block = Block.newBlockVolumeMass( DensityConstants.litersToMetersCubed( 5 ), 4, 0, 0, DensityConstants.GREEN, model, Material.CUSTOM );
        block3.setPosition( DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() / 2 );
        model.addDensityObject( block3 );

        var block4: Block = Block.newBlockVolumeMass( DensityConstants.litersToMetersCubed( 5 ), 2, 0, 0, DensityConstants.RED, model, Material.CUSTOM );
        block4.setPosition( DensityConstants.POOL_WIDTH_X / 2 + block3.getWidth(), block4.getHeight() / 2 );
        model.addDensityObject( block4 );
    }
}
}