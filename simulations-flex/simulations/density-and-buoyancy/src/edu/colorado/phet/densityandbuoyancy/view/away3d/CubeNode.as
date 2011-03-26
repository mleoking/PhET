//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.components.BlockLabel;
import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.view.*;

/**
 * The CubeNode renders graphics and provides interactivity for Cuboids.
 */
public class CubeNode extends CuboidNode {
    private var cube: PickableCube;
    private var _blockLabelNode: BlockLabel;

    public function CubeNode( cuboid: Cuboid, canvas: AbstractDBCanvas ) {
        super( cuboid, canvas );
        cube = new PickableCube( this );
        cube.segmentsH = 2;
        cube.segmentsW = 2;
        _blockLabelNode = new Away3DBlockLabelNode( getDensityObject().name, this, getDensityObject().nameVisibleProperty, canvas, canvas.mainCamera, canvas.mainViewport );
    }

    public function getCube(): PickableCube {
        return cube;
    }

    public override function updateGeometry(): void {
        super.updateGeometry();
        cube.width = getCuboid().getWidth() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        cube.height = getCuboid().getHeight() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        cube.depth = getCuboid().getDepth() * DensityAndBuoyancyModel.DISPLAY_SCALE;
    }


    override public function addOverlayObjects(): void {
        canvas.addChild( _blockLabelNode );
        super.addOverlayObjects();
    }


    override public function removeOverlayObjects(): void {
        super.removeOverlayObjects();
        try {
            canvas.removeChild( _blockLabelNode );
        }
        catch ( e: * ) {
            trace( "got exception on blocklabelnode remove: " + e )
        }
    }
}
}