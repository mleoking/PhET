// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;

/**
 * Base class for drag listeners that perform sim-sharing data collection.
 * <p/>
 * If a client is not interested in sim-sharing, use the zero-arg constructor;
 * otherwise use one of the other constructors or setters.
 * <p/>
 * Client overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragListener extends MouseAdapter {

    /*
     * Function implemented by clients who want to send sim-sharing events.
     * The action and mouse event are provided in the callback.
     * Action should typically be provided to as the action for a sim-sharing event.
     * Mouse event can be used to create sim-sharing event parameters, depending on what the client is interested in.
     * X and Y coordinates are provided as standardized "convenience" parameters, since they are frequently desired by clients.
     */
    public interface DragFunction {
        public void apply( IUserAction action, Parameter xParameter, Parameter yParameter, MouseEvent event );
    }

    private DragFunction startFunction; // optional function called when drag starts (on mousePressed)
    private DragFunction endFunction; // optional function called when drag ends (on mouseReleased)
    private DragFunction dragFunction; // optional function called while dragging (on mouseDragged).

    public SimSharingDragListener() {
    }

    // Same function for drag start, end, drag.
    public SimSharingDragListener( DragFunction startEndDragFunction ) {
        this( startEndDragFunction, startEndDragFunction, startEndDragFunction );
    }

    // Same function for drag start and end, different function for drag.
    public SimSharingDragListener( DragFunction startEndFunction, DragFunction dragFunction ) {
        this( startEndFunction, startEndFunction, dragFunction );
    }

    // Different functions for start, end, drag.
    public SimSharingDragListener( DragFunction startFunction, DragFunction endFunction, DragFunction dragFunction ) {
        this.startFunction = startFunction;
        this.endFunction = endFunction;
        this.dragFunction = dragFunction;
    }

    // Set the function called when dragging starts.
    public void setStartFunction( DragFunction startFunction ) {
        this.startFunction = startFunction;
    }

    // Set the function called when dragging ends.
    public void setEndFunction( DragFunction endFunction ) {
        this.endFunction = endFunction;
    }

    // Sets the function called while dragging.
    public void setDragFunction( DragFunction dragFunction ) {
        this.dragFunction = dragFunction;
    }

    // Convenience method for specifying the same function for start and end.
    public void setStartEndFunction( DragFunction f ) {
        setStartFunction( f );
        setEndFunction( f );
    }

    // Convenience method for specifying the same function for start, end, and drag.
    public void setStartEndDragFunction( DragFunction f ) {
        setStartFunction( f );
        setEndFunction( f );
        setDragFunction( f );
    }

    @Override public void mousePressed( MouseEvent event ) {
        if ( startFunction != null ) {
            startFunction.apply( UserActions.startDrag, getXParameter( event ), getYParameter( event ), event );
        }
        super.mousePressed( event );
    }

    @Override public void mouseReleased( MouseEvent event ) {
        if ( endFunction != null ) {
            endFunction.apply( UserActions.endDrag, getXParameter( event ), getYParameter( event ), event );
        }
        super.mouseReleased( event );
    }

    @Override public void mouseDragged( MouseEvent event ) {
        if ( dragFunction != null ) {
            dragFunction.apply( UserActions.drag, getXParameter( event ), getYParameter( event ), event );
        }
        super.mouseDragged( event );
    }

    private static Parameter getXParameter( MouseEvent event ) {
        return new Parameter( ParameterKeys.x, event.getX() );
    }

    private static Parameter getYParameter( MouseEvent event ) {
        return new Parameter( ParameterKeys.y, event.getY() );
    }
}
