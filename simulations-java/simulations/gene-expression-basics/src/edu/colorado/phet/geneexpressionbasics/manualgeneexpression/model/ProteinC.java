// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.multiplecells.view.ColorChangingCellNode;

/**
 * @author John Blanco
 */
public class ProteinC extends Protein {

    // Make the color look like the fluorescent green used in "multiple cells" tab.
    private static final Color BASE_COLOR = ColorChangingCellNode.FLORESCENT_FILL_COLOR;

    private static final double FULL_GROWN_WIDTH = 320;

    public ProteinC() {
        this( new StubGeneExpressionModel() );
    }

    protected ProteinC( GeneExpressionModel model ) {
        super( model, createInitialShape(), BASE_COLOR );
    }

    @Override protected Shape getUntranslatedShape( double growthFactor ) {
        return createShape( growthFactor );
    }

    @Override public Protein createInstance( GeneExpressionModel model, Ribosome parentRibosome ) {
        return new ProteinC( this.model );
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
        double currentHeight = currentWidth * 1.4;
        DoubleGeneralPath path = new DoubleGeneralPath();
        double topAndBottomCurveMultiplier = 0.55;
        double sideCurvesMultiplier = 0.40;
        // Start in the upper left and proceed clockwise in adding segments.
        path.moveTo( -currentWidth * 0.45, currentHeight * 0.45 );
        path.curveTo( -currentWidth * 0.33, currentHeight * topAndBottomCurveMultiplier, currentWidth * 0.3, currentHeight * topAndBottomCurveMultiplier, currentWidth * 0.45, currentHeight * 0.45 );
        path.curveTo( currentWidth * sideCurvesMultiplier, currentHeight * 0.33, currentWidth * sideCurvesMultiplier, -currentHeight * 0.33, currentWidth * 0.45, -currentHeight * 0.45 );
        path.curveTo( currentWidth * 0.33, -currentHeight * topAndBottomCurveMultiplier, -currentWidth * 0.3, -currentHeight * topAndBottomCurveMultiplier, -currentWidth * 0.45, -currentHeight * 0.45 );
        path.curveTo( -currentWidth * sideCurvesMultiplier, -currentHeight * 0.33, -currentWidth * sideCurvesMultiplier, currentHeight * 0.33, -currentWidth * 0.45, currentHeight * 0.45 );
        return path.getGeneralPath();
    }
}
