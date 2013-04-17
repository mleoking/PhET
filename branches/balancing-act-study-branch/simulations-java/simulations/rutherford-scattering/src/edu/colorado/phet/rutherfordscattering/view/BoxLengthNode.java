// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.rutherfordscattering.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/*
 * Used to label the length of the animation box. Looks like this:
 *
 * |<------------- label -------------->|
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxLengthNode extends PComposite {

    private static final double X_SPACING = 10;
    private static final Dimension ARROW_HEAD_SIZE = new Dimension( 12, 15 );
    private static final double ARROW_TAIL_WIDTH = 2;

    public BoxLengthNode( double width, String label, Color color ) {

        // nodes
        PText textNode = new PText( label );
        textNode.setTextPaint( color );
        textNode.setFont( new PhetFont( Font.BOLD, 18 ) );

        double arrowLength = ( width - textNode.getFullBoundsReference().getWidth() - ( 2 * X_SPACING ) ) / 2;

        ArrowNode leftArrowNode = new ArrowNode( new Point2D.Double( arrowLength, 0 ), new Point2D.Double( 0, 0 ),
                                                 ARROW_HEAD_SIZE.height, ARROW_HEAD_SIZE.width, ARROW_TAIL_WIDTH );
        leftArrowNode.setStroke( null );
        leftArrowNode.setPaint( color );

        ArrowNode rightArrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( arrowLength, 0 ),
                                                  ARROW_HEAD_SIZE.height, ARROW_HEAD_SIZE.width, ARROW_TAIL_WIDTH );
        rightArrowNode.setStroke( null );
        rightArrowNode.setPaint( color );

        PPath leftTickNode = new PPath( new Line2D.Double( 0, 0, 0, textNode.getFullBoundsReference().getHeight() ) );
        leftTickNode.setStroke( new BasicStroke( 2f ) );
        leftTickNode.setStrokePaint( color );

        PPath rightTickNode = new PPath( new Line2D.Double( 0, 0, 0, textNode.getFullBoundsReference().getHeight() ) );
        rightTickNode.setStroke( new BasicStroke( 2f ) );
        rightTickNode.setStrokePaint( color );

        // rendering order
        addChild( textNode );
        addChild( leftArrowNode );
        addChild( rightArrowNode );
        addChild( leftTickNode );
        addChild( rightTickNode );

        // layout
        leftArrowNode.setOffset( 0, textNode.getFullBoundsReference().getCenterY() );
        textNode.setOffset( leftArrowNode.getFullBoundsReference().getMaxX() + X_SPACING, 0 );
        rightArrowNode.setOffset( textNode.getFullBoundsReference().getMaxX() + X_SPACING, leftArrowNode.getYOffset() );
        leftTickNode.setOffset( 0, 0 );
        rightTickNode.setOffset( width, 0 );
    }
}
