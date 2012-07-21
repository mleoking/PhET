// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserActions;
import edu.colorado.phet.platetectonics.model.ToolboxState;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.drag;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.endDrag;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.startDrag;

/**
 * Handles tool dragging, so that we don't clutter the module/tab with this state information
 */
public class ToolDragHandler {

    private boolean dragging = false;
    private DraggableTool2D tool;
    private Vector2F lastPosition;

    private ToolboxState toolboxState;

    public ToolDragHandler( ToolboxState toolboxState ) {
        this.toolboxState = toolboxState;
    }

    public void mouseDownOnTool( DraggableTool2D tool, Vector2F viewPosition ) {
        if ( tool.allowsDrag( viewPosition ) ) {
            startDragging( tool, viewPosition );
        }
    }

    public void startDragging( DraggableTool2D tool, Vector2F viewPosition ) {
        this.tool = tool;
        lastPosition = new Vector2F( viewPosition );
        dragging = true;

        /// send an empty drag delta to hopefully synchronize any model
        tool.dragDelta( new Vector2F( 0, 0 ) );

        SimSharingManager.sendUserMessage( tool.getUserComponent(), UserComponentTypes.sprite, startDrag, getToolLocationParameterSet( tool ) );
    }

    public void mouseUp( boolean overToolbox ) {
        if ( dragging ) {
            SimSharingManager.sendUserMessage( tool.getUserComponent(), UserComponentTypes.sprite, endDrag, getToolLocationParameterSet( tool ) );
        }
        if ( dragging && overToolbox ) {
            putToolBackInToolbox( tool );
        }
        dragging = false;
    }

    public void mouseMove( Vector2F viewPosition ) {
        if ( dragging ) {
            tool.dragDelta( viewPosition.minus( lastPosition ) );
            lastPosition = viewPosition;

            SimSharingManager.sendUserMessage( tool.getUserComponent(), UserComponentTypes.sprite, drag, getToolLocationParameterSet( tool ) );
        }
    }

    public void putToolBackInToolbox( DraggableTool2D tool ) {
        // get rid of the tool
        tool.recycle();

        // mark the toolbox as having the tool again
        tool.getInsideToolboxProperty( toolboxState ).set( true );

        SimSharingManager.sendUserMessage( tool.getUserComponent(), UserComponentTypes.sprite, UserActions.putBackInToolbox );
    }

    public boolean isDragging() {
        return dragging;
    }

    private static ParameterSet getToolLocationParameterSet( DraggableTool2D tool ) {
        return new ParameterSet( new Parameter[]{
                new Parameter( ParameterKeys.x, tool.getSensorModelPosition().x ),
                new Parameter( ParameterKeys.y, tool.getSensorModelPosition().y ),
                new Parameter( ParameterKeys.z, tool.getSensorModelPosition().z )
        } ).with( tool.getCustomParameters() );
    }
}
