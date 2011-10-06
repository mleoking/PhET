// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.platetectonics.model.ToolboxState;

import com.jme3.math.Vector2f;

public interface DraggableTool2D {

    // whether drags should be allowed to start at the specified screen point
    public boolean allowsDrag( Vector2f initialPosition );

    // actually perform a drag movement (view coordinates)
    public void dragDelta( Vector2f delta );

    // pick what part of the toolbox we are!
    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState );

    // when we start the drag, what point (in 3D view coordinates) should the mouse be over?
    public Vector2f getInitialMouseOffset();

    // detach the tool and remove it from use
    public void recycle();
}
