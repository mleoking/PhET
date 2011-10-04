// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import com.jme3.math.Vector2f;

/**
 * Handles tool dragging, so that we don't clutter the module with this information
 */
public class ToolDragHandler {

    private boolean dragging = false;
    private DraggableTool tool;
    private Vector2f lastPosition;

    public void mouseDownOnTool( DraggableTool tool, Vector2f viewPosition ) {
        // TODO: improve the x/y on this? not sure what coordinate system it will be using!!!
        if ( tool.allowsDrag( viewPosition.x, viewPosition.y ) ) {
            this.tool = tool;
            lastPosition = viewPosition.clone();
            dragging = true;
        }
    }

    public void mouseUp() {
        dragging = false;
    }

    public void mouseMove( Vector2f viewPosition ) {
        if ( dragging ) {
            tool.dragDelta( viewPosition.subtract( lastPosition ) );
            lastPosition = viewPosition;
        }
    }
}
