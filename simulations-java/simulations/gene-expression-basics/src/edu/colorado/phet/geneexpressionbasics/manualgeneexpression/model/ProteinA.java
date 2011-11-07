// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author John Blanco
 */
public class ProteinA extends Protein {

    private static final Color BASE_COLOR = Color.YELLOW;
    private static final double FULL_GROWN_WIDTH = 300;

    protected ProteinA( GeneExpressionModel model ) {
        super( model, createInitialShape(), BASE_COLOR );
    }

    @Override protected Shape getShape( double growthFactor ) {
        return createShape( growthFactor );
    }

    private static Shape createInitialShape() {
//        return createShape( 0 );
        return createShape( 1 );
    }

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
