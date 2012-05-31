// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Image;

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

    private static final double WIDTH = 24; // In screen coords, which is close to pixels.

    public EnergyChunkNode( EnergyChunk energyChunk, final ModelViewTransform mvt ) {

        // Control the overall visibility of this node.
        energyChunk.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Control the transparency of this node.
        energyChunk.getExistenceStrength().addObserver( new VoidFunction1<Double>() {
            public void apply( Double existenceStrength ) {
                setTransparency( existenceStrength.floatValue() );
            }
        } );

        // Add the image that represents this energy chunk.
        Image imageSource;
        imageSource = EnergyFormsAndChangesResources.Images.ENERGY_CHUNKS_WHITE;
        PImage image = new PImage( imageSource );
        image.setScale( WIDTH / image.getFullBoundsReference().width );
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
