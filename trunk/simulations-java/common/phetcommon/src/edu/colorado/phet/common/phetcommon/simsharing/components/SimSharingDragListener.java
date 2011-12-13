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

    private DragFunction startEndDragFunction; // optional function called when drag starts and ends (on mousePressed and mouseReleased)
    private DragFunction draggingFunction; // optional function called while dragging (on mouseDragged).

    public SimSharingDragListener() {
        this( null, null );
    }

    public SimSharingDragListener( DragFunction startEndDragFunction ) {
        this( startEndDragFunction, null );
    }

    public SimSharingDragListener( DragFunction startEndDragFunction, DragFunction draggingFunction ) {
        this.startEndDragFunction = startEndDragFunction;
        this.draggingFunction = draggingFunction;
    }

    // Sets the function called when a drag starts and ends.
    public void setStartEndDragFunction( DragFunction startEndDragFunction ) {
        this.startEndDragFunction = startEndDragFunction;
    }

    // Sets the function called while dragging.
    public void setDraggingFunction( DragFunction draggingFunction ) {
        this.draggingFunction = draggingFunction;
    }

    @Override public void mousePressed( MouseEvent event ) {
        if ( startEndDragFunction != null ) {
            startEndDragFunction.apply( Actions.START_DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.mousePressed( event );
    }

    @Override public void mouseReleased( MouseEvent event ) {
        if ( startEndDragFunction != null ) {
            startEndDragFunction.apply( Actions.END_DRAG, getXParameter( event ), getYParameter( event ), event );
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
