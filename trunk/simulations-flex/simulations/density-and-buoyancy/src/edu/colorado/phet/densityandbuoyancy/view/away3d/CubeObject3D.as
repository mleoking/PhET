//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.components.BlockLabelSprite;
import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.view.*;

/**
 * The CubeNode renders graphics and provides interactivity for Cuboids.
 */
public class CubeObject3D extends CuboidObject3D {
    private var cube: PickableCube;
    private var _blockLabelNode: BlockLabelSprite;

    public function CubeObject3D( cuboid: Cuboid, canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super( cuboid, canvas );
        cube = new PickableCube( this );
        cube.segmentsH = 2;
        cube.segmentsW = 2;
        _blockLabelNode = new Away3DBlockLabelSprite( getDensityObject().name, this, getDensityObject().nameVisibleProperty, canvas, canvas.mainCamera, canvas.mainViewport );
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

    //Add labels and other graphics that should be in the front layer.
    override public function addOverlayObjects(): void {
        var added: Boolean = false;
        // find the index of another block label, and insert it there (if applicable)
        for ( var i: int = 0; i < canvas.numChildren; i++ ) {
            if ( canvas.getChildAt( i ) instanceof BlockLabelSprite ) {
                canvas.addChildAt( _blockLabelNode, i );
                added = true;
                break;
            }
        }
        if ( !added ) {
            canvas.addChild( _blockLabelNode );
        }
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