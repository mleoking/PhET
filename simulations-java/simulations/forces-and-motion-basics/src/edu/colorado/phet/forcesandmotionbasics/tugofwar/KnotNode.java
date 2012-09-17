// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class KnotNode extends PNode {
    public KnotNode( final Double knotLocation, final Color color, final Rectangle2D ropeBounds ) {
        double w = 10;
        addChild( new PhetPPath( new Rectangle2D.Double( -w / 2, -w / 2, w, w ), new BasicStroke( 2 ), color ) {{
            setOffset( ropeBounds.getX() + knotLocation, ropeBounds.getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}