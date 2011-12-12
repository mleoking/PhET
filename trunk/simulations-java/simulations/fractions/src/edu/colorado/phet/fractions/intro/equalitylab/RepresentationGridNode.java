// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.equalitylab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.common.model.SingleFractionModel;
import edu.colorado.phet.fractions.intro.intro.view.FractionControlNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the entire grid of 2x2 representations.
 *
 * @author Sam Reid
 */
public class RepresentationGridNode extends PNode {

    public static class RepresentationGridCell extends PNode {
        public RepresentationGridCell() {
            final int width = 320;
            final int curve = 20;
            PNode leftBoxNode = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width, width, curve, curve ), new BasicStroke( 2 ), Color.black );
            addChild( leftBoxNode );
        }
    }

    public RepresentationGridNode( final SingleFractionModel model ) {

        final RepresentationGridCell topLeft = new RepresentationGridCell();
        addChild( topLeft );
        final RepresentationGridCell topRight = new RepresentationGridCell();
        topRight.setOffset( topLeft.getFullBounds().getWidth() + 20, topLeft.getFullBounds().getY() + 0 );
        addChild( topRight );

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
        final PNode fractionNode = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator ) ) {{
            scale( 0.5 );
            setOffset( topLeft.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, topLeft.getFullBounds().getMaxY() + 5 );

        }};
        addChild( fractionNode );
    }
}