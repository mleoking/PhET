// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import com.jme3.math.Vector2f;

public interface DraggableTool {

    // whether drags should be allowed to start at the specified point
    public boolean allowsDrag( float x, float y );

    // actually perform a drag movement
    public void dragDelta( Vector2f delta );
}
