// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Dashed line used to connect a drag handle to the thing that its dragging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class DragHandleLineNode extends PPath {

    public static final Stroke STROKE = new BasicStroke( 3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 ); // dashed
    public static final Color COLOR = Color.BLACK;
    
    public DragHandleLineNode( Point2D pStart, Point2D pEnd ) {
        super( new Line2D.Double( pStart, pEnd ) );
        setStroke( STROKE );
        setStrokePaint( COLOR );
    }
}
