// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

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
        public void apply( String action, Parameter xParameter, Parameter yParameter, MouseEvent event );
    }

    private DragFunction startDragFunction; // optional function called when drag starts (on mousePressed)
    private DragFunction endDragFunction; // optional function called when drag ends (on mouseReleased)
    private DragFunction draggingFunction; // optional function called while dragging (on mouseDragged).

    public SimSharingDragListener() {
    }

    // Convenience constructor, for specifying the same function for drag start and end.
    public SimSharingDragListener( DragFunction startEndDragFunction ) {
        this( startEndDragFunction, startEndDragFunction, null );
    }

    // Convenience constructor, for specifying the same function for drag start and end.
    public SimSharingDragListener( DragFunction startEndDragFunction, DragFunction draggingFunction ) {
        this( startEndDragFunction, startEndDragFunction, draggingFunction );
    }

    public SimSharingDragListener( DragFunction startDragFunction, DragFunction endDragFunction, DragFunction draggingFunction ) {
        this.startDragFunction = startDragFunction;
        this.endDragFunction = endDragFunction;
        this.draggingFunction = draggingFunction;
    }

    // Set the function called when dragging starts.
    public void setStartDragFunction( DragFunction startDragFunction ) {
        this.startDragFunction = startDragFunction;
    }

    // Set the function called when dragging ends.
    public void setEndDragFunction( DragFunction endDragFunction ) {
        this.endDragFunction = endDragFunction;
    }

    // Convenience method for specifying the same function for drag start and end, since this is usually what we want.
    public void setStartEndDragFunction( DragFunction f ) {
        setStartDragFunction( f );
        setEndDragFunction( f );
    }

    // Sets the function called while dragging.
    public void setDraggingFunction( DragFunction draggingFunction ) {
        this.draggingFunction = draggingFunction;
    }

    @Override public void mousePressed( MouseEvent event ) {
        if ( startDragFunction != null ) {
            startDragFunction.apply( Actions.START_DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.mousePressed( event );
    }

    @Override public void mouseReleased( MouseEvent event ) {
        if ( endDragFunction != null ) {
            endDragFunction.apply( Actions.END_DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.mouseReleased( event );
    }

    @Override public void mouseDragged( MouseEvent event ) {
        if ( draggingFunction != null ) {
            draggingFunction.apply( Actions.DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.mouseDragged( event );
    }

    private static Parameter getXParameter( MouseEvent event ) {
        return new Parameter( Parameters.X, event.getX() );
    }

    private static Parameter getYParameter( MouseEvent event ) {
        return new Parameter( Parameters.Y, event.getY() );
    }
}
