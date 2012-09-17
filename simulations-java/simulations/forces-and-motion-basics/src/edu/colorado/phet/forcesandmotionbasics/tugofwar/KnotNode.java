// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class KnotNode extends PNode {
    public static F<KnotNode, Boolean> _free = new F<KnotNode, Boolean>() {
        @Override public Boolean f( final KnotNode knotNode ) {
            return knotNode.pullerNode == null;
        }
    };
    private PullerNode pullerNode;
    private final PhetPPath path;
    private final Color color;

    public KnotNode( final Double knotLocation, final Color color, final Rectangle2D ropeBounds ) {
        this.color = color;
        double w = 10;
        path = new PhetPPath( new Rectangle2D.Double( -w / 2, -w / 2, w, w ), new BasicStroke( 2 ), color ) {{
            setOffset( ropeBounds.getX() + knotLocation, ropeBounds.getCenterY() - getFullBounds().getHeight() / 2 );
        }};
        addChild( path );
    }

    public void setHighlighted( final boolean highlighted ) { path.setStrokePaint( highlighted ? Color.yellow : color ); }
}