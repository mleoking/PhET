// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author John Blanco
 */
public class ProteinB extends Protein {

    private static final Color BASE_COLOR = new Color( 255, 99, 71 );

    private static final double FULL_GROWN_WIDTH = 450;

    public ProteinB() {
        this( new StubGeneExpressionModel() );
    }

    protected ProteinB( GeneExpressionModel model ) {
        super( model, createInitialShape(), BASE_COLOR );
    }

    @Override protected Shape getUntranslatedShape( double growthFactor ) {
        return createShape( growthFactor );
    }

    @Override public Protein createInstance( Ribosome parentRibosome ) {
        return new ProteinB( this.model );
    }

    @Override public void setAttachmentPointPosition( Point2D attachmentPointLocation ) {
        // Note: This is specific to this protein's shape, and will need to be
        // adjusted if the protein's shape algorithm changes.
        setPosition( attachmentPointLocation.getX() + FULL_GROWN_WIDTH * 0.12 * getFullSizeProportion(),
                     attachmentPointLocation.getY() + FULL_GROWN_WIDTH * 0.45 * getFullSizeProportion() );
    }

    private static Shape createInitialShape() {
        return createShape( 0 );
    }

    private static Shape createShape( double growthFactor ) {
        final double currentWidth = MathUtil.clamp( 0.01, growthFactor, 1 ) * FULL_GROWN_WIDTH;
        DoubleGeneralPath path = new DoubleGeneralPath( 0, 0 ) {{
            Vector2D vector = new Vector2D( -currentWidth / 2, 0 );
            moveTo( vector.getX(), vector.getY() );
            for ( int i = 0; i < 6; i++ ) {
                vector.rotate( Math.PI / 3 );
                lineTo( vector.getX(), vector.getY() );
            }
            closePath();
        }};

        return path.getGeneralPath();
    }
}
