// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunkNode extends PNode {

    public EnergyChunkNode( EnergyChunk energyChunk, final ModelViewTransform mvt ) {

        PNode image = new PImage( EnergyFormsAndChangesResources.Images.ENERGY_CHUNK_THERMAL );
        image.setOffset( -image.getFullBoundsReference().width / 2, -image.getFullBoundsReference().height / 2 );
        addChild( image );

        // Set this node's position when the corresponding model element moves.
        energyChunk.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }
}
