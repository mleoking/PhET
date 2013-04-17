// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * FineCrosshairNode draws a common (aka "fine") crosshair, which looks like a '+' shape.
 * The center of the node is at the center of the '+'.
 * <p/>
 * There are many other types of crosshairs, documented at
 * http://en.wikipedia.org/wiki/Crosshair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FineCrosshairNode extends PhetPNode {

    /**
     * Constructor for a crosshair where horizontal and vertical dimensions are the same size.
     *
     * @param size
     * @param stroke
     * @param strokePaint
     */
    public FineCrosshairNode( double size, Stroke stroke, Paint strokePaint ) {
        this( new PDimension( size, size ), stroke, strokePaint );
    }

    /**
     * Constructor for a crosshair where horizontal and vertical dimensions can be different.
     *
     * @param size
     * @param stroke
     * @param strokePaint
     */
    public FineCrosshairNode( Dimension2D size, Stroke stroke, Paint strokePaint ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        PPath hPath = new PPath( new Line2D.Double( -size.getWidth() / 2, 0, size.getWidth() / 2, 0 ) );
        hPath.setStrokePaint( strokePaint );
        hPath.setStroke( stroke );
        addChild( hPath );

        PPath vPath = new PPath( new Line2D.Double( 0, -size.getHeight() / 2, 0, size.getHeight() / 2 ) );
        vPath.setStrokePaint( strokePaint );
        vPath.setStroke( stroke );
        addChild( vPath );
    }
}