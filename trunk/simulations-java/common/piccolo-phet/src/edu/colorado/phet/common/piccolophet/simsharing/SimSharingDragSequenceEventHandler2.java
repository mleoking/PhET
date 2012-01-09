// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.numberDragEvents;

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
public class SimSharingDragSequenceEventHandler2 extends PDragSequenceEventHandler {

    private final UserComponent userComponent;
    private ArrayList<Point2D> dragPoints = new ArrayList<Point2D>();

    public SimSharingDragSequenceEventHandler2( UserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    @Override protected void startDrag( final PInputEvent event ) {
        dragPoints.clear();
        SimSharingManager.sendUserMessage( userComponent, UserActions.startDrag, new ParameterSet().add( getXParameter( event ) ).add( getYParameter( event ) ).addAll( getStartDragParameters() ) );
        super.startDrag( event );
    }

    //Override to supply any additional parameters to send on start drag message
    public Parameter[] getStartDragParameters() {
        return new Parameter[0];
    }

    @Override protected void drag( PInputEvent event ) {

        //Don't send a message for each event, may require too much bandwidth.
//        SimSharingManager.sendUserEvent( userComponent, UserActions.drag, getXParameter( event ), getYParameter( event ) );
        dragPoints.add( new Point2D.Double( event.getCanvasPosition().getX(), event.getCanvasPosition().getY() ) );
        super.drag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        SimSharingManager.sendUserMessage( userComponent, UserActions.endDrag, new ParameterSet().add( getXParameter( event ) ).add( getYParameter( event ) ).addAll( param( numberDragEvents, dragPoints.size() ) ) );
        dragPoints.clear();
        super.endDrag( event );
    }

    private static Parameter getXParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionX, event.getCanvasPosition().getX() );
    }

    private static Parameter getYParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionY, event.getCanvasPosition().getY() );
    }
}