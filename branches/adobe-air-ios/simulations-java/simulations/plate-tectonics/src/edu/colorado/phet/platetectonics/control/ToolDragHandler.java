// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.ToolboxState;

/**
 * Handles tool dragging, so that we don't clutter the module with this information
 */
public class ToolDragHandler {

    private boolean dragging = false;
    private DraggableTool2D tool;
    private ImmutableVector2F lastPosition;

    private ToolboxState toolboxState;

    public ToolDragHandler( ToolboxState toolboxState ) {
        this.toolboxState = toolboxState;
    }

    public void mouseDownOnTool( DraggableTool2D tool, ImmutableVector2F viewPosition ) {
        if ( tool.allowsDrag( viewPosition ) ) {
            startDragging( tool, viewPosition );
        }
    }

    public void startDragging( DraggableTool2D tool, ImmutableVector2F viewPosition ) {
        this.tool = tool;
        lastPosition = new ImmutableVector2F( viewPosition );
        dragging = true;

        /// send an empty drag delta to hopefully synchronize any model
        tool.dragDelta( new ImmutableVector2F( 0, 0 ) );
    }

    public void mouseUp( boolean overToolbox ) {
        if ( dragging && overToolbox ) {
            // get rid of the tool
            tool.recycle();

            // mark the toolbox as having the tool again
            tool.getInsideToolboxProperty( toolboxState ).set( true );
        }
        dragging = false;
    }

    public void mouseMove( ImmutableVector2F viewPosition ) {
        if ( dragging ) {
            tool.dragDelta( viewPosition.minus( lastPosition ) );
            lastPosition = viewPosition;
        }
    }

    public boolean isDragging() {
        return dragging;
    }
}
