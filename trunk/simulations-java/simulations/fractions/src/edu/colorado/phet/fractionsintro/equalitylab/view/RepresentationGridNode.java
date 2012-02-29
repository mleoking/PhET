// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractionsintro.common.model.SingleFractionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the entire grid of 2x2 representations.
 *
 * @author Sam Reid
 */
public class RepresentationGridNode extends PNode {

    //A single cell in the 2x2 grid, showing one shapes-based representation
    public static class RepresentationGridCell extends PhetPNode {
        public RepresentationGridCell() {
            final int width = 450;
            final int height = 320;
            final int curve = 20;
            PNode leftBoxNode = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width, height, curve, curve ), new BasicStroke( 2 ), Color.black );
            addChild( leftBoxNode );
        }
    }

    public RepresentationGridNode( final SingleFractionModel model ) {

        final RepresentationGridCell topLeft = new RepresentationGridCell();
        addChild( topLeft );

        final PhetPText topEqualsSign = new PhetPText( "=", new PhetFont( 80, true ) ) {{
            setOffset( topLeft.getFullWidth() + 20, topLeft.getCenterY() - getFullHeight() / 2 );
        }};
        addChild( topEqualsSign );

        final RepresentationGridCell topRight = new RepresentationGridCell() {{
            setOffset( topEqualsSign.getMaxX() + 20, topLeft.getMinY() + 0 );
        }};
        addChild( topRight );

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
//        final ZeroOffsetNode fractionNode = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator ) ) {{
//            scale( 0.5 );
//            setOffset( topLeft.getCenterX() - getFullWidth() / 2, topLeft.getMaxY() + 5 );
//        }};
//        addChild( fractionNode );

//        final PText bottomEqualsSign = new PhetPText( "=", new PhetFont( 80, true ) ) {{
//            setOffset( topLeft.getFullWidth() + 20, fractionNode.getCenterY() - getFullHeight() / 2 );
//        }};
//        addChild( bottomEqualsSign );
//
//        final ZeroOffsetNode scaledUpVersionNode = new ZeroOffsetNode( new ScaledUpFractionNode( model.numerator, model.denominator ) ) {{
//            scale( 0.5 );
//            setOffset( topRight.getCenterX() - getFullWidth() / 2, topRight.getMaxY() + 5 );
//        }};
//
//        addChild( scaledUpVersionNode );
    }
}