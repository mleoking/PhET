// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.umd.cs.piccolo.event.PInputEvent;

//TODO duplication with SlopeInterceptDragHandler, base class and slope handler can probably be the same
/**
 * Base class drag handler for point-slope line manipulators.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class PointSlopeDragHandler extends SimSharingDragHandler {

    protected final LineManipulatorNode manipulatorNode;
    protected final ModelViewTransform mvt;
    protected final Property<StraightLine> line;

    public PointSlopeDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                  LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line ) {
        super( userComponent, componentType, true /* sendDragMessages */ );
        this.manipulatorNode = manipulatorNode;
        this.mvt = mvt;
        this.line = line;
    }

    protected void updateLine( double rise, double run, double x1, double y1 ) {
        if ( LGConstants.SNAP_TO_GRID_WHILE_DRAGGING ) {
            line.set( new StraightLine( MathUtil.round( rise ), MathUtil.round( run ), MathUtil.round( x1 ), MathUtil.round( y1 ), line.get().color, line.get().highlightColor ) );
        }
        else {
            line.set( new StraightLine( rise, run, x1, y1, line.get().color, line.get().highlightColor ) );
        }
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        manipulatorNode.setDragging( true );
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        manipulatorNode.setDragging( false );
        updateLine( MathUtil.round( line.get().rise ), MathUtil.round( line.get().run ), MathUtil.round( line.get().x1 ), MathUtil.round( line.get().y1 ) );
    }

    @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().
                with( ParameterKeys.rise, line.get().rise ).
                with( ParameterKeys.run, line.get().run ).
                with( ParameterKeys.intercept, line.get().yIntercept ).
                with( super.getParametersForAllEvents( event ) );
    }

    // Drag handler for point (x1,y1) manipulator
    public static class X1Y1DragHandler extends PointSlopeDragHandler {

        private final Property<DoubleRange> x1Range, y1Range;
        private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public X1Y1DragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                     LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line,
                                     Property<DoubleRange> x1Range, Property<DoubleRange> y1Range ) {
            super( userComponent, componentType, manipulatorNode, mvt, line );
            this.x1Range = x1Range;
            this.y1Range = y1Range;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
            clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().x1 );
            clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y1 );
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
            double x1 = MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ), x1Range.get() );
            double y1 = MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), y1Range.get() );
            updateLine( line.get().rise, line.get().run, x1, y1 );
        }
    }

    // Drag handler for slope (rise/run) manipulator
    public static class SlopeDragHandler extends PointSlopeDragHandler {

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
            clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().x1 + line.get().run );
            clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y1 + line.get().rise );
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
            double run = MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ) - line.get().x1, runRange.get() );
            double rise = MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ) - line.get().y1, riseRange.get() );
            updateLine( rise, run, line.get().x1, line.get().y1 );
        }
    }
}
