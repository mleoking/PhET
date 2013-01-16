// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.manipulator;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the slope manipulator, usable with both slope-intercept and point-slope lines.
 * Note that this manipulator also changes (x2,y2), since changing the slope relative to (x1,y1)
 * effectively changes (x2,y2).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeDragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> riseRange, runRange;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     *
     * @param userComponent   sim-sharing component identifier
     * @param componentType   sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt             transform between model and view coordinate frames
     * @param line            the line being manipulated
     * @param riseRange       range of the rise
     * @param runRange        range of the run
     */
    public SlopeDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                             LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<Line> line,
                             Property<DoubleRange> riseRange,
                             Property<DoubleRange> runRange ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.riseRange = riseRange;
        this.runRange = runRange;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().x2 );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y2 );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        // constrain to range, snap to grid
        double run = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ) - line.get().x1, runRange.get() ) );
        double rise = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ) - line.get().y1, riseRange.get() ) );
        // don't allow slope=0/0, undefined line
        if ( !( run == 0 && rise == 0 ) ) {
            line.set( Line.createPointSlope( line.get().x1, line.get().y1, rise, run, line.get().color ) );
        }
    }
}
