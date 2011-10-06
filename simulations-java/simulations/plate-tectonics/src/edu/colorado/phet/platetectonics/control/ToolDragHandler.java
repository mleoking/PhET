// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.platetectonics.model.ToolboxState;

import com.jme3.math.Vector2f;

/**
 * Handles tool dragging, so that we don't clutter the module with this information
 */
public class ToolDragHandler {

    private boolean dragging = false;
    private DraggableTool2D tool;
    private Vector2f lastPosition;

    private ToolboxState toolboxState;

    public ToolDragHandler( ToolboxState toolboxState ) {
        this.toolboxState = toolboxState;
    }

    public void mouseDownOnTool( DraggableTool2D tool, Vector2f viewPosition ) {
        // TODO: improve the x/y on this? not sure what coordinate system it will be using!!!
        if ( tool.allowsDrag( viewPosition ) ) {
            startDragging( tool, viewPosition );
        }
    }

    public void startDragging( DraggableTool2D tool, Vector2f viewPosition ) {
        this.tool = tool;
        lastPosition = viewPosition.clone();
        dragging = true;

        /// send an empty drag delta to hopefully synchronize any model
        tool.dragDelta( new Vector2f( 0, 0 ) );
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

    public void mouseMove( Vector2f viewPosition ) {
        if ( dragging ) {
            tool.dragDelta( viewPosition.subtract( lastPosition ) );
            lastPosition = viewPosition;
        }
    }
}
