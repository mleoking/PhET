// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingDragPoints;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * Sends messages on startDrag, endDrag, and (optionally) on drag.
 * <p/>
 * Can be customized in 3 ways:
 * 1. Override getParametersForAllEvents to augment or replace the standard parameters for all events.
 * 2. Override the get*Parameters methods to augment or replace the standard parameters for specific.
 * 3. Call set*Function methods to replace the functions invoked for specific events.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragHandler extends PDragSequenceEventHandler {

    public interface DragFunction {
        public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameters, PInputEvent event );
    }

    protected final IUserComponent userComponent;
    protected final IUserComponentType componentType;
    private final SimSharingDragPoints dragPoints; // canvas coordinates, accumulated during a drag sequence
    private DragFunction startDragFunction, dragFunction, endDragFunction; // functions called for various events

    // Sends a message on startDrag and endDrag, but not drag
    public SimSharingDragHandler( IUserComponent userComponent, IUserComponentType componentType ) {
        this( userComponent, componentType, false );
    }

    // Sends a message on drag if reportDrag=true
    public SimSharingDragHandler( IUserComponent userComponent, IUserComponentType componentType, final boolean sendDragMessages ) {

        this.userComponent = userComponent;
        this.componentType = componentType;
        this.dragPoints = new SimSharingDragPoints();

        // default functions
        this.startDragFunction = new DragFunction() {
            public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameterSet, PInputEvent event ) {
                SimSharingManager.sendUserMessage( userComponent, componentType, action, parameterSet );
            }
        };
        this.dragFunction = new DragFunction() {
            public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameterSet, PInputEvent event ) {
                if ( sendDragMessages ) {
                    SimSharingManager.sendUserMessage( userComponent, componentType, action, parameterSet );
                }
            }
        };
        this.endDragFunction = new DragFunction() {
            public void apply( IUserComponent userComponent, IUserComponentType componentType, IUserAction action, ParameterSet parameterSet, PInputEvent event ) {
                SimSharingManager.sendUserMessage( userComponent, componentType, action, parameterSet );
            }
        };
    }

    @Override protected void startDrag( final PInputEvent event ) {
        clearDragPoints();
        addDragPoint( event );
        startDragFunction.apply( userComponent, componentType, UserActions.startDrag, getStartDragParameters( event ), event );
        super.startDrag( event );
    }

    @Override protected void drag( PInputEvent event ) {
        addDragPoint( event );
        dragFunction.apply( userComponent, componentType, UserActions.drag, getDragParameters( event ), event );
        super.drag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        addDragPoint( event );
        endDragFunction.apply( userComponent, componentType, UserActions.endDrag, getEndDragParameters( event ), event );
        clearDragPoints();
        super.endDrag( event );
    }

    // Call this to replace the sim-sharing function that is called on startDrag.
    public void setStartDragFunction( DragFunction f ) {
        startDragFunction = f;
    }

    // Call this to replace the sim-sharing function that is called on drag.
    public void setDragFunction( DragFunction f ) {
        dragFunction = f;
    }

    // Call this to replace the sim-sharing function that is called on endDrag.
    public void setEndDragFunction( DragFunction f ) {
        endDragFunction = f;
    }

    // Gets parameters for startDrag. Override to provide different parameters, chain with super to add parameters.
    protected ParameterSet getStartDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Gets parameters for drag. Override to provide different parameters, chain with super to add parameters.
    protected ParameterSet getDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Gets parameters for endDrag. Override to provide different parameters, chain with super to add parameters.
    protected ParameterSet getEndDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event ).with( dragPoints.getParameters() ); // includes summary of drag points
    }

    // Return parameters that are used by default for startDrag, endDrag, and drag
    protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().with( getXParameter( event ) ).with( getYParameter( event ) );
    }

    private void addDragPoint( PInputEvent event ) {
        dragPoints.add( getPosition( event ) );
    }

    private void clearDragPoints() {
        dragPoints.clear();
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