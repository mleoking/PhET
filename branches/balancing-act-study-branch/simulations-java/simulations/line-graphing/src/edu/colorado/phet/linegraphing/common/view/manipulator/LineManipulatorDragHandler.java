// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.manipulator;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class drag handler for line manipulators.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineManipulatorDragHandler extends SimSharingDragHandler {

    protected final LineManipulatorNode manipulatorNode;
    protected final ModelViewTransform mvt;
    protected final Property<Line> line;

    /**
     * Constructor
     *
     * @param userComponent   sim-sharing component identifier
     * @param componentType   sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt             transform between model and view coordinate frames
     * @param line            the line being manipulated
     */
    public LineManipulatorDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                       LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<Line> line ) {
        super( userComponent, componentType, true /* sendDragMessages */ );
        this.manipulatorNode = manipulatorNode;
        this.mvt = mvt;
        this.line = line;
    }

    // Add information about the line to the sim-sharing message.
    @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().
                with( ParameterKeys.x1, line.get().x1 ).
                with( ParameterKeys.y1, line.get().y1 ).
                with( ParameterKeys.x2, line.get().x2 ).
                with( ParameterKeys.y2, line.get().y2 ).
                with( ParameterKeys.rise, line.get().rise ).
                with( ParameterKeys.run, line.get().run ).
                with( super.getParametersForAllEvents( event ) );
    }
}
