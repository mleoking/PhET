// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class drag handler for line manipulators.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineManipulatorDragHandler extends SimSharingDragHandler {

    protected final LineManipulatorNode manipulatorNode;
    protected final ModelViewTransform mvt;
    protected final Property<StraightLine> line;

    /**
     * Constructor
     * @param userComponent sim-sharing component identifier
     * @param componentType sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt  transform between model and view coordinate frames
     * @param line the line being manipulated
     */
    public LineManipulatorDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                       LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line ) {
        super( userComponent, componentType, true /* sendDragMessages */ );
        this.manipulatorNode = manipulatorNode;
        this.mvt = mvt;
        this.line = line;
    }

    // Update the line using point-slope form, snaps to integer grid.
    protected void updateLine( double rise, double run, double x1, double y1 ) {
        line.set( new StraightLine( MathUtil.roundHalfUp( rise ), MathUtil.roundHalfUp( run ), MathUtil.roundHalfUp( x1 ), MathUtil.roundHalfUp( y1 ), line.get().color, line.get().highlightColor ) );
    }

    // Update the line using slope-intercept form, snaps to integer grid.
    protected void updateLine( double rise, double run, double yIntercept ) {
        line.set( new StraightLine( MathUtil.roundHalfUp( rise ), MathUtil.roundHalfUp( run ), MathUtil.roundHalfUp( yIntercept ), line.get().color, line.get().highlightColor ) );
    }

    @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().
                with( ParameterKeys.rise, line.get().rise ).
                with( ParameterKeys.run, line.get().run ).
                with( ParameterKeys.x1, line.get().x1 ).
                with( ParameterKeys.y1, line.get().y1 ).
                with( ParameterKeys.intercept, line.get().yIntercept ).
                with( super.getParametersForAllEvents( event ) );
    }
}
