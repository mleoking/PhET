// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingDragPoints;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * To use it, specify the user component in the constructor as well as any args for computing drag information.
 * <p/>
 * Other implementation did not follow the same standard pattern of sim sharing subclasses in which information is required in constructor and the class sending the messages itself.
 * <p/>
 * Unlike other simsharing subclasses, this one allows client code to supply additional information via overriding the get***Parameters methods.
 * TODO: add more summary information and hooks for the sim client to send additional param info.
 *
 * @author Sam Reid
 */
public class SimSharingDragHandler extends PDragSequenceEventHandler {

    protected final IUserComponent userComponent;
    private final SimSharingDragPoints dragPoints; // canvas coordinates, accumulated during a drag sequence

    public SimSharingDragHandler( IUserComponent userComponent ) {
        this.userComponent = userComponent;
        this.dragPoints = new SimSharingDragPoints();
    }

    // Start the drag and send a sim-sharing message.
    @Override protected void startDrag( final PInputEvent event ) {
        clearDragPoints();
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, UserActions.startDrag, getStartDragParameters( event ).add( getXParameter( event ) ).add( getYParameter( event ) ) );
        super.startDrag( event );
    }

    // Finish the drag and send a sim-sharing message.
    @Override protected void endDrag( PInputEvent event ) {
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, UserActions.endDrag, getEndDragParameters( event ).
                add( getXParameter( event ) ).
                add( getYParameter( event ) ).
                addAll( dragPoints.getParameters() ) );
        clearDragPoints();
        super.endDrag( event );
    }

    // Override this is you want to send messages during drags. Be sure to call super.mouseDragged
    @Override protected void drag( PInputEvent event ) {
        addDragPoint( event );
        super.drag( event );
    }

    private void addDragPoint( PInputEvent event ) {
        dragPoints.add( getPosition( event ) );
    }

    private void clearDragPoints() {
        dragPoints.clear();
    }

    //Override to supply any additional parameters to send on start drag message
    public ParameterSet getStartDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event );
    }

    //Override to supply any additional parameters to send on end drag message
    public ParameterSet getEndDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event );
    }

    //Return parameters that are used by default for startDrag and endDrag
    public ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet();
    }

    private static Parameter getXParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionX, getPosition( event ).getX() );
    }

    private static Parameter getYParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionY, getPosition( event ).getY() );
    }

    // Gets the interpretation of the position used throughout this class.
    private static Point2D getPosition( PInputEvent event ) {
        return event.getCanvasPosition();
    }
}