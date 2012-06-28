// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * This shows the background (ticks and labels) for the bar graph.  The bars are added and animated elsewhere.
 *
 * @author Sam Reid
 */
class EmptyBarGraphNode extends PNode {
    public EmptyBarGraphNode() {
        double majorWidth = 90;
        double minorWidth = 70;
        double innerWidth = 50;
        addLine( 0, majorWidth, 2, "2" );
        addLine( 25, innerWidth, 1 );
        addLine( 50, minorWidth, 1 );
        addLine( 75, innerWidth, 1 );
        addLine( 100, majorWidth, 2, "1" );
        addLine( 125, innerWidth, 1 );
        addLine( 150, minorWidth, 1 );
        addLine( 175, innerWidth, 1 );
        addLine( 200, majorWidth, 2, "0" );
        addChild( new PhetPPath( new Line2D.Double( 0, -15, 0, 200 ), new BasicStroke( 2 ), Color.black ) );
    }

    private void addLine( double y, double width, float strokeWidth ) {
        addChild( createPath( y, width, strokeWidth ) );
    }

    private void addLine( double y, double width, float strokeWidth, String label ) {
        final PhetPPath path = createPath( y, width, strokeWidth );
        addChild( path );
        addChild( new PhetPText( label, new PhetFont( 16 ) ) {{
            setOffset( path.getFullBounds().getX() - getFullWidth() - 2, path.getFullBounds().getCenterY() - getFullHeight() / 2 );
        }} );
        addChild( new PhetPText( label, new PhetFont( 16 ) ) {{
            setOffset( path.getFullBounds().getMaxX() + 2, path.getFullBounds().getCenterY() - getFullHeight() / 2 );
        }} );
    }

    private PhetPPath createPath( double y, double width, float strokeWidth ) {
        return new PhetPPath( new Line2D.Double( -width / 2, y, width / 2, y ), new BasicStroke( strokeWidth ), Color.black );
    }
}