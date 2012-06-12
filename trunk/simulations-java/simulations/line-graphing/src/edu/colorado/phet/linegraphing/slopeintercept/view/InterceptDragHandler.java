// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorDragHandler;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the intercept manipulator of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class InterceptDragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> yInterceptRange;
    private double clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     * @param userComponent sim-sharing component identifier
     * @param componentType sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt  transform between model and view coordinate frames
     * @param line the line being manipulated
     * @param yInterceptRange
     */
    public InterceptDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                 LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line,
                                 Property<DoubleRange> yInterceptRange ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.yInterceptRange = yInterceptRange;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().yIntercept );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        double yIntercept = MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), yInterceptRange.get() );
        updateLine( line.get().rise, line.get().run, yIntercept );
    }
}
