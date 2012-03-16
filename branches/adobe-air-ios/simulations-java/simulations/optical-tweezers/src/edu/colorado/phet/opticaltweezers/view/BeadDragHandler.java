// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;


/**
 * BeadDragHandler is the drag handler for the bead.
 * <p>
 * This drag handler does the following:
 * 1. constrains dragging to the provided bounds (typically the microscope slide),
 * 2. if the bead is attached to a DNA strand, prevents over-stretching of the strand,
 * 3. disables the influence of Brownian and other forces while the bead is dragged.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeadDragHandler extends BoundedDragHandler {

    private Bead _bead;
    private CursorHandler _cursorHandler;
    private boolean _ignoreDrag;

    /**
     * Constructor.
     * 
     * @param beadNode
     * @param boundingNode
     */
    public BeadDragHandler( BeadNode beadNode, PNode boundingNode, CursorHandler cursorHandler ) {
        super( beadNode, boundingNode );
        _bead = beadNode.getBead();
        _cursorHandler = cursorHandler;
        _ignoreDrag = false;
    }

    /**
     * Disables the bead's motion model when the drag starts.
     * 
     * @param event
     */
    public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        _bead.setMotionEnabled( false );
    }

    /**
     * After dragging, check the DNA strand's extension length.
     * If it exceeds the contour length, then the strand is over-stretched,
     * and we negate the drag operation by moving the bead back to where it
     * was at the start of the drag.
     * 
     * @param event
     */
    public void mouseDragged( PInputEvent event ) {
        DNAStrand dnaStrand = _bead.getDNAStrand();
        if ( dnaStrand == null ) {
            super.mouseDragged( event );
        }
        else if ( !_ignoreDrag ) {
            // Save the bead's position before the drag
            double beadX = _bead.getX();
            double beadY = _bead.getY();
            // Do the drag
            super.mouseDragged( event );
            if ( dnaStrand.getExtension() > dnaStrand.getMaxExtension() ) {
                // Release the bead when the DNA strand becomes fully stretched.
                _bead.setPosition( beadX, beadY );
                endDrag( event );
            }
        }
    }
    
    /**
     * End a drag before the user has released the mouse button.
     * All subsequent drag events will be ignored until the user does
     * release the mouse button.
     * 
     * @param event
     */
    private void endDrag( PInputEvent event ) {
        super.mouseReleased( event );
        _ignoreDrag = true;
        _bead.setMotionEnabled( true );
        _cursorHandler.mouseReleased( event ); // makes the cursor change back to an arrow
    }

    /**
     * Enables the bead's motion model when the drag ends.
     * Sets the flag indicating that it's OK to process drag events.
     * 
     * @param event
     */
    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        _ignoreDrag = false;
        _bead.setMotionEnabled( true );
    }
}
