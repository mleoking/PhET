// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.event;

import java.awt.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for event handlers that highlight a node.
 * Highlighting is typically used to as a visual cue to indicate that a node is interactive.
 * <p/>
 * A node is highlighted if:
 * (a) the mouse cursor is moved inside the node's bounding rectangle, or
 * (b) the mouse was been pressed while inside the node's bounding rectangle and not yet released.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class HighlightHandler extends PBasicInputEventHandler {

    private boolean isMousePressed, isMouseInside;

    /**
     * Implemented by subclasses, handles the node highlighting.
     *
     * @param b true=show highlight, false=show normal
     */
    protected abstract void setHighlighted( boolean b );

    @Override
    public void mouseEntered( PInputEvent event ) {
        isMouseInside = true;
        setHighlighted( true );
    }

    @Override
    public void mouseExited( PInputEvent event ) {
        isMouseInside = false;
        if ( !isMousePressed ) {
            setHighlighted( false );
        }
    }

    @Override
    public void mousePressed( PInputEvent event ) {
        isMousePressed = true;
        setHighlighted( true );
    }

    @Override
    public void mouseReleased( PInputEvent event ) {
        isMousePressed = false;
        if ( !isMouseInside ) {
            setHighlighted( false );
        }
    }

    /**
     * Highlights a PNode by changing its paint.
     */
    public static class PaintHighlightHandler extends HighlightHandler {

        private final PNode node;
        private Paint normal, highlight;

        public PaintHighlightHandler( PNode node, Paint normal, Paint highlight ) {
            this.node = node;
            this.normal = normal;
            this.highlight = highlight;
        }

        protected void setHighlighted( boolean b ) {
            node.setPaint( b ? highlight : normal );
        }

        public void setNormal( Paint normal ) {
            this.normal = normal;
        }

        public void setHighlight( Paint highlight ) {
            this.highlight = highlight;
        }
    }

    /**
     * Highlights a PImage by changing its image.
     */
    public static class ImageHighlightHandler extends HighlightHandler {

        private final PImage node;
        private Image normal, highlight;

        public ImageHighlightHandler( PImage node, Image normal, Image highlight ) {
            this.node = node;
            this.normal = normal;
            this.highlight = highlight;
        }

        protected void setHighlighted( boolean b ) {
            node.setImage( b ? highlight : normal );
        }

        public void setNormal( Image normal ) {
            this.normal = normal;
        }

        public void setHighlight( Image highlight ) {
            this.highlight = highlight;
        }
    }
}
