// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.GeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.common.model.Protein;

/**
 * @author John Blanco
 */
public class ProteinA extends Protein {

    private static final Color BASE_COLOR = new Color( 255, 99, 71 );
    private static final double FULL_GROWN_WIDTH = 450;

    public ProteinA() {
        this( new StubGeneExpressionModel() );
    }

    protected ProteinA( GeneExpressionModel model ) {
        super( model, createInitialShape(), BASE_COLOR );
    }

    @Override protected Shape getUntranslatedShape( double growthFactor ) {
        return createShape( growthFactor );
    }

    @Override public Protein createInstance() {
        return new ProteinA( this.model );
    }

    @Override public void setAttachmentPointPosition( Point2D attachmentPointLocation ) {
        // Note: This is specific to this protein's shape, and will need to be
        // adjusted if the protein's shape algorithm changes.
        setPosition( attachmentPointLocation.getX(), attachmentPointLocation.getY() + ( FULL_GROWN_WIDTH / 2 * getFullSizeProportion() ) );
    }

    private static Shape createInitialShape() {
        return createShape( 0 );
    }

    // REVIEW: Shape instances should be immutable, so I have a slight preference to have a private
    // static final Shape instance instead of a function creating a shape.
    // Same for the other proteins
    private static Shape createShape( double growthFactor ) {
        final double currentWidth = MathUtil.clamp( 0.01, growthFactor, 1 ) * FULL_GROWN_WIDTH;
        DoubleGeneralPath path = new DoubleGeneralPath( 0, 0 ) {{
            moveTo( -currentWidth / 2, 0 );
            lineTo( 0, -currentWidth / 2 );
            lineTo( currentWidth / 2, 0 );
            lineTo( 0, currentWidth / 2 );
            lineTo( -currentWidth / 2, 0 );
            closePath();
        }};

        return path.getGeneralPath();
    }
}
