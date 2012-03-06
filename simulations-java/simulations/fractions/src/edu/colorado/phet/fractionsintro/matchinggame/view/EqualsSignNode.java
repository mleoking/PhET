// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import static edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameNode.createSignNode;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;

/**
 * It was requested for the equals sign to have rounded edges, so we won't render with a font.
 *
 * @author Sam Reid
 */
public class EqualsSignNode extends RichPNode {
    public EqualsSignNode() {
        addChild( createPath( 0 ) );
        addChild( createPath( 22 ) );
    }

    private PhetPPath createPath( double y ) { return createSignNode( new BasicStroke( 11, CAP_ROUND, JOIN_MITER ).createStrokedShape( new Line2D.Double( 0, y, 60, y ) ) ); }
}
