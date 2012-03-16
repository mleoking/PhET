// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;

/**
 * A fancy arrow node, points to the right, for use in equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RightArrowNode extends ArrowNode {

    // using constants here because super constructor is hard to grok
    private static final Point2D TAIL_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D TIP_LOCATION = new Point2D.Double( 60, 0 );
    private static final int HEAD_HEIGHT = 23;
    private static final int HEAD_WIDTH = 27;
    private static final int TAIL_WIDTH = 11;

    public RightArrowNode( boolean highlighted ) {
        super( TAIL_LOCATION, TIP_LOCATION, HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH );
        setStroke( new BasicStroke( 1.5f ) );
        setStrokePaint( Color.BLACK );
        setHighlighted( highlighted );
    }

    public void setHighlighted( boolean highlighted ) {
        setPaint( highlighted ? BCEConstants.BALANCED_HIGHLIGHT_COLOR : BCEConstants.UNBALANCED_COLOR );
    }
}
