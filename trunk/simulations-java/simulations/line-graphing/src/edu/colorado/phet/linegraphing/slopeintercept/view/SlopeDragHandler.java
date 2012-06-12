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
import edu.colorado.phet.linegraphing.common.view.LineDragHandler;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the slope manipulator of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeDragHandler extends LineDragHandler {

    private final Property<DoubleRange> riseRange, runRange;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    public SlopeDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                             LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line,
                             Property<DoubleRange> riseRange,
                             Property<DoubleRange> runRange ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.riseRange = riseRange;
        this.runRange = runRange;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().run );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().rise + line.get().yIntercept );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        double run = MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ), runRange.get() );
        double rise = MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ) - line.get().yIntercept, riseRange.get() );
        updateLine( rise, run, line.get().yIntercept );
    }
}
