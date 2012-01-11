// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
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
 * @deprecated
 */
public class SimSharingDragHandlerOld extends PDragSequenceEventHandler {

    /*
     * Function implemented by clients who want to send sim-sharing events.
     * The action and input event are provided in the callback.
     * Action should typically be provided to as the action for a sim-sharing event.
     * Input event can be used to create sim-sharing event parameters, depending on what the client is interested in.
     * X and Y coordinates are provided as standardized "convenience" parameters, since they are frequently desired by clients.
     */
    public interface DragFunction {
        public void apply( UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event );
    }

    private DragFunction startFunction; // optional function called when drag starts (on startDrag)
    private DragFunction endFunction; // optional function called when drag ends (on endDrag)
    private DragFunction dragFunction; // optional function called while dragging (on drag).

    public SimSharingDragHandlerOld() {
        this( null, null, null );
    }

    // Same function for start, end, drag.
    public SimSharingDragHandlerOld( DragFunction startEndDragFunction ) {
        this( startEndDragFunction, startEndDragFunction, startEndDragFunction );
    }

    // Same function for start and end, different function for drag.
    public SimSharingDragHandlerOld( DragFunction startEndFunction, DragFunction dragFunction ) {
        this( startEndFunction, startEndFunction, dragFunction );
    }

    // Different functions for start, end, drag.
    public SimSharingDragHandlerOld( DragFunction startFunction, DragFunction endFunction, DragFunction dragFunction ) {
        this.startFunction = startFunction;
        this.endFunction = endFunction;
        this.dragFunction = dragFunction;
    }

    // Set the function called when dragging starts.
    public void setStartFunction( DragFunction startFunction ) {
        this.startFunction = startFunction;
    }

    // Set the function called when dragging ends.
    public void setEndFunction( DragFunction startDragFunction ) {
        this.endFunction = startDragFunction;
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

    @Override protected void startDrag( final PInputEvent event ) {
        if ( startFunction != null ) {
            startFunction.apply( UserActions.startDrag, getXParameter( event ), getYParameter( event ), event );
        }
        super.startDrag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        if ( endFunction != null ) {
            endFunction.apply( UserActions.endDrag, getXParameter( event ), getYParameter( event ), event );
        }
        super.endDrag( event );
    }

    @Override protected void drag( PInputEvent event ) {
        if ( dragFunction != null ) {
            dragFunction.apply( UserActions.drag, getXParameter( event ), getYParameter( event ), event );
        }
        super.drag( event );
    }

    private static Parameter getXParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionX, event.getCanvasPosition().getX() );
    }

    private static Parameter getYParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionY, event.getCanvasPosition().getY() );
    }
}
