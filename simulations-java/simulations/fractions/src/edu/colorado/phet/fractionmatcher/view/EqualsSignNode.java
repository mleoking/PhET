// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import static java.awt.BasicStroke.JOIN_MITER;

/**
 * It was requested for the equals sign to have a specific look instead of relying on the system font, so this is rendered with shapes.
 *
 * @author Sam Reid
 */
class EqualsSignNode extends RichPNode {
    public EqualsSignNode() {
        addChild( createPath( 0 ) );
        addChild( createPath( 22 ) );
    }

    private static PhetPPath createPath( double y ) { return toPPath( new BasicStroke( 11, BasicStroke.CAP_SQUARE, JOIN_MITER ).createStrokedShape( new Line2D.Double( 0, y, 60, y ) ) ); }

    private static PhetPPath toPPath( Shape shape ) { return new PhetPPath( shape, Color.yellow, new BasicStroke( 2 ), Color.black ); }
}