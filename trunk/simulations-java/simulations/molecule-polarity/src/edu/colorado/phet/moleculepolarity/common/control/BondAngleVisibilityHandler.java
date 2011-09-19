// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.event.InputEvent;

import edu.colorado.phet.moleculepolarity.common.view.BondAngleDragIndicatorNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Makes a bond angle indicator visible only when the cursor is over it or it's being dragged.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleVisibilityHandler extends PDragSequenceEventHandler {

    private final BondAngleDragIndicatorNode indicatorNode;

    public BondAngleVisibilityHandler( BondAngleDragIndicatorNode indicatorNode ) {
        this.indicatorNode = indicatorNode;
    }

    // make node visible only on mouse enter if the button1 isn't pressed
    @Override public void mouseEntered( PInputEvent event ) {
        super.mouseEntered( event );
        if ( ( event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != InputEvent.BUTTON1_DOWN_MASK ) {
            indicatorNode.setVisible( true );
        }
    }

    // make node invisible on mouse exit
    @Override public void mouseExited( PInputEvent event ) {
        indicatorNode.setVisible( false );
    }

    // make node invisible when dragging starts
    @Override public void startDrag( PInputEvent event ) {
        super.startDrag( event );
        indicatorNode.setVisible( false );
    }
}
