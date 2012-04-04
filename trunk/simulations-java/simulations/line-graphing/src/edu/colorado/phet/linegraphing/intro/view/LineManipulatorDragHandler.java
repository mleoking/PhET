// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class drag handler for line manipulators.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class LineManipulatorDragHandler extends SimSharingDragHandler {

    protected final PNode dragNode;
    protected final ModelViewTransform mvt;
    protected final Property<SlopeInterceptLine> line;

    public LineManipulatorDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                       PNode dragNode, ModelViewTransform mvt, Property<SlopeInterceptLine> line ) {
        super( userComponent, componentType, true /* sendDragMessages */ );
        this.dragNode = dragNode;
        this.mvt = mvt;
        this.line = line;
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        // snap to grid
        line.set( new SlopeInterceptLine( MathUtil.round( line.get().rise ), MathUtil.round( line.get().run ), MathUtil.round( line.get().intercept ) ) );
    }

    @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().
                with( ParameterKeys.rise, line.get().rise ).
                with( ParameterKeys.run, line.get().run ).
                with( ParameterKeys.intercept, line.get().intercept ).
                with( super.getParametersForAllEvents( event ) );
    }

    // Drag handler for intercept manipulator
    public static class InterceptDragHandler extends LineManipulatorDragHandler {

        private double clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public InterceptDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                     PNode dragNode, ModelViewTransform mvt, Property<SlopeInterceptLine> line ) {
            super( userComponent, componentType, dragNode, mvt, line );
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().intercept );
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double intercept = mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset );
            line.set( new SlopeInterceptLine( line.get().rise, line.get().run, intercept ) );
        }
    }

    // Drag handler for slope manipulator
    public static class SlopeDragHandler extends LineManipulatorDragHandler {

        private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public SlopeDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                 PNode dragNode, ModelViewTransform mvt, Property<SlopeInterceptLine> line ) {
            super( userComponent, componentType, dragNode, mvt, line );
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().run );
            clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().rise + line.get().intercept );
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double run = mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset );
            double rise = mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ) - line.get().intercept;
            line.set( new SlopeInterceptLine( rise, run, line.get().intercept ) );
        }
    }

}
