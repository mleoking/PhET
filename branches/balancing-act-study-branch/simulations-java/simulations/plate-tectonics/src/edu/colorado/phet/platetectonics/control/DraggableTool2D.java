// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.platetectonics.model.ToolboxState;

/**
 * Interface for a general tool (ruler, density sensor, thermometer)
 */
public interface DraggableTool2D {

    // whether drags should be allowed to start at the specified screen point
    public boolean allowsDrag( Vector2F initialPosition );

    // actually perform a drag movement (view coordinates)
    public void dragDelta( Vector2F delta );

    // pick what part of the toolbox we are!
    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState );

    // when we start the drag, what point (in 3D view coordinates) should the mouse be over?
    public Vector2F getInitialMouseOffset();

    // for sim-sharing messages
    public IUserComponent getUserComponent();

    // where the tool is making a read-out, if applicable
    public Vector3F getSensorModelPosition();

    public Vector3F getSensorViewPosition();

    // for sim-sharing messages
    public ParameterSet getCustomParameters();

    // detach the tool and remove it from use
    public void recycle();
}
