// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
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

    private static final Random RAND = new Random( 1962 );

    public EnergyChunkNode( EnergyChunk energyChunk, final ModelViewTransform mvt ) {

        if ( RAND.nextBoolean() ) {
            PNode image = new PImage( EnergyFormsAndChangesResources.Images.ENERGY_CHUNK_THERMAL );
            image.setOffset( -image.getFullBoundsReference().width / 2, -image.getFullBoundsReference().height / 2 );
            addChild( image );
        }
        else {
            double width = 17;
            PNode outline = new PhetPPath( new RoundRectangle2D.Double( -width / 2, -width / 2, width, width, 3, 3 ), new BasicStroke( 2 ), new Color( 200, 0, 0 ) );
            outline.addChild( new PhetPText( "E", new PhetFont( 12, true ) ) {{
                centerFullBoundsOnPoint( 0, 0 );
            }} );
            addChild( outline );
        }

        // Set this node's position when the corresponding model element moves.
        energyChunk.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }
}
