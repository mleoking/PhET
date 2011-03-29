// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;

/**
 * Double arrow used for drag handles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class DragHandleArrowNode extends DoubleArrowNode {

    private static final Color NORMAL_COLOR = CLPaints.DRAGGABLE_NORMAL;
    private static final Color HIGHLIGHT_COLOR = CLPaints.DRAGGABLE_HIGHLIGHT;
    private static final Color STOKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );

    public DragHandleArrowNode( Point2D pStart, Point2D pEnd ) {
        this( pStart, pEnd, Math.abs( pStart.distance( pEnd ) ) );

    }

    private DragHandleArrowNode( Point2D pStart, Point2D pEnd, double length ) {
        super( pStart, pEnd, length / 4 /* headHeight */, length / 2 /* headWidth */, length / 6 /* tailWidth */ );
        setPaint( NORMAL_COLOR );
        setStrokePaint( STOKE_COLOR );
        setStroke( STROKE );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PaintHighlightHandler( this, NORMAL_COLOR, HIGHLIGHT_COLOR ) );
    }
}
