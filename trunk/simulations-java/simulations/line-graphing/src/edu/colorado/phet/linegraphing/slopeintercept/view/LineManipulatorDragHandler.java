// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class drag handler for line manipulators.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class LineManipulatorDragHandler extends SimSharingDragHandler {

    protected final LineManipulatorNode manipulatorNode;
    protected final ModelViewTransform mvt;
    protected final Property<StraightLine> line;

    public LineManipulatorDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                       LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line ) {
        super( userComponent, componentType, true /* sendDragMessages */ );
        this.manipulatorNode = manipulatorNode;
        this.mvt = mvt;
        this.line = line;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        manipulatorNode.setDragging( true );
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        manipulatorNode.setDragging( false );
        // snap to grid
        line.set( new StraightLine( MathUtil.round( line.get().rise ), MathUtil.round( line.get().run ), MathUtil.round( line.get().yIntercept ),
                                    line.get().color, line.get().highlightColor ) );
    }

    @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().
                with( ParameterKeys.rise, line.get().rise ).
                with( ParameterKeys.run, line.get().run ).
                with( ParameterKeys.intercept, line.get().yIntercept ).
                with( super.getParametersForAllEvents( event ) );
    }

    // Drag handler for intercept manipulator
    public static class InterceptDragHandler extends LineManipulatorDragHandler {

        private final Property<DoubleRange> interceptRange;
        private double clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public InterceptDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                     LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line,
                                     Property<DoubleRange> interceptRange ) {
            super( userComponent, componentType, manipulatorNode, mvt, line );
            this.interceptRange = interceptRange;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
            clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().yIntercept );
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
            double intercept = MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), interceptRange.get() );
            line.set( new StraightLine( line.get().rise, line.get().run, intercept, line.get().color, line.get().highlightColor ) );
        }
    }

    // Drag handler for slope (rise/run) manipulator
    public static class SlopeDragHandler extends LineManipulatorDragHandler {

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
            line.set( new StraightLine( rise, run, line.get().yIntercept, line.get().color, line.get().highlightColor ) );
        }
    }
}
