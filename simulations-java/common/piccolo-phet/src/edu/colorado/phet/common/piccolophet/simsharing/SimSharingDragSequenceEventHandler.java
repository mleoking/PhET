// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * <p/>
 * If a client is not interested in sim-sharing, use the zero-arg constructor;
 * otherwise use one of the other constructors, or setSimSharingEventArgs, to
 * provide information that will be sent with sim-sharing events.
 * <p/>
 * Client overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragSequenceEventHandler extends PDragSequenceEventHandler {

    /*
     * Function implemented by clients who want to send sim-sharing events.
     * The action and input event are provided in the callback.
     * Action should typically be provided to as the action for a sim-sharing event.
     * Input event can be used to create sim-sharing event parameters, depending on what the client is interested in.
     * X and Y coordinates are provided as standardized "convenience" parameters, since they are frequently desired by clients.
     */
    public interface DragFunction {
        public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event );
    }

    private DragFunction startEndDragFunction; // optional function called when drag starts and ends (on startDrag and endDrag)
    private DragFunction draggingFunction; // optional function called while dragging (on drag).

    public SimSharingDragSequenceEventHandler() {
        this( null, null );
    }

    public SimSharingDragSequenceEventHandler( DragFunction startEndDragFunction ) {
        this( startEndDragFunction, null );
    }

    public SimSharingDragSequenceEventHandler( DragFunction startEndDragFunction, DragFunction draggingFunction ) {
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

    @Override protected void startDrag( final PInputEvent event ) {
        if ( startEndDragFunction != null ) {
            startEndDragFunction.apply( Actions.START_DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.startDrag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        if ( startEndDragFunction != null ) {
            startEndDragFunction.apply( Actions.END_DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.endDrag( event );
    }

    @Override protected void drag( PInputEvent event ) {
        if ( draggingFunction != null ) {
            draggingFunction.apply( Actions.DRAG, getXParameter( event ), getYParameter( event ), event );
        }
        super.drag( event );
    }

    private static Parameter getXParameter( PInputEvent event ) {
        return new Parameter( Parameters.CANVAS_POSITION_X, event.getCanvasPosition().getX() );
    }

    private static Parameter getYParameter( PInputEvent event ) {
        return new Parameter( Parameters.CANVAS_POSITION_Y, event.getCanvasPosition().getY() );
    }
}
