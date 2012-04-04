// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.intro.view.BracketValueNode.Direction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Specialization of line graph that adds the ability to directly manipulate a line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InteractiveLineGraphNode extends LineGraphNode {

    public final Property<Boolean> riseOverRunVisible = new Property<Boolean>( true );
    public final Property<Boolean> pointToolVisible = new Property<Boolean>( false );

    private final Property<Boolean> linesVisible;
    private PNode interactiveLineParentNode, bracketsParentNode;
    private PNode slopeManipulatorNode, interceptManipulatorNode;

    public InteractiveLineGraphNode( final LineGraph graph, final ModelViewTransform mvt,
                                     Property<SlopeInterceptLine> interactiveLine, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine,
                                     Property<Boolean> linesVisible, Property<ImmutableVector2D> pointToolLocation ) {
        super( graph, mvt, yEqualsXLine, yEqualsNegativeXLine, linesVisible );

        this.linesVisible = linesVisible;

        // Interactive line
        interactiveLineParentNode = new PComposite();
        interactiveLineParentNode.setVisible( linesVisible.get() );

        // Manipulators
        final double manipulatorDiameter = mvt.modelToViewDeltaX( 0.65 );
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE_COLOR );
        interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT_COLOR );

        // Rise and run brackets
        bracketsParentNode = new PComposite();

        // Point tool
        PNode pointTool = new PointToolNode( pointToolLocation, pointToolVisible, mvt, graph );

        // rendering order
        addChild( interactiveLineParentNode );
        addChild( bracketsParentNode );
        addChild( slopeManipulatorNode );
        addChild( interceptManipulatorNode );
        addChild( pointTool );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                updateInteractiveLine( line, graph, mvt );
            }
        } );

        // Visibility of rise and run brackets
        riseOverRunVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                updateLinesVisibility();
            }
        } );

        // Hide the rise and run brackets when lines aren't visible
        linesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                updateLinesVisibility();
            }
        } );

        // interactivity for slope manipulator
        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite, slopeManipulatorNode, mvt, interactiveLine ) );

        // interactivity for intercept manipulator
        interceptManipulatorNode.addInputEventListener( new CursorHandler() );
        interceptManipulatorNode.addInputEventListener( new InterceptDragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite, interceptManipulatorNode, mvt, interactiveLine ) );
    }

    @Override public void reset() {
        super.reset();
        riseOverRunVisible.reset();
        pointToolVisible.reset();
    }

    // Updates the line and its associated decorations
    private void updateInteractiveLine( final SlopeInterceptLine line, final LineGraph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt, LGColors.INTERACTIVE_LINE ) );

        // replace the rise/run brackets
        bracketsParentNode.removeAllChildren();
        if ( line.isDefined() ) {

            // run bracket
            final Direction runDirection = line.rise >= 0 ? Direction.UP : Direction.DOWN;
            final BracketValueNode runBracketNode = new BracketValueNode( runDirection, mvt.modelToViewDeltaX( line.run ), line.run );
            bracketsParentNode.addChild( runBracketNode );
            runBracketNode.setOffset( mvt.modelToViewDeltaX( 0 ), mvt.modelToViewDeltaY( line.intercept ) );

            // rise bracket
            if ( line.rise != 0 ) {
                final Direction riseDirection = line.run > 0 ? Direction.LEFT : Direction.RIGHT;
                final BracketValueNode riseBracket = new BracketValueNode( riseDirection, mvt.modelToViewDeltaX( line.rise ), line.rise );
                bracketsParentNode.addChild( riseBracket );
                riseBracket.setOffset( mvt.modelToViewDeltaX( line.run ), mvt.modelToViewDeltaY( line.intercept ) );
            }
        }

        // move the manipulators
        final double y = line.rise + line.intercept;
        double x;
        if ( line.run == 0 ) {
            x = 0;
        }
        else if ( line.rise == 0 ) {
            x = line.run;
        }
        else {
            x = line.solveX( y );
        }
        slopeManipulatorNode.setOffset( mvt.modelToView( x, y ) );
        interceptManipulatorNode.setOffset( mvt.modelToView( 0, line.intercept ) );
    }

    // Updates the visibility of lines in the graph
    @Override protected void updateLinesVisibility() {
        super.updateLinesVisibility();

        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible.get() );
            slopeManipulatorNode.setVisible( linesVisible.get() );
            interceptManipulatorNode.setVisible( linesVisible.get() );
        }

        if ( bracketsParentNode != null ) {
            bracketsParentNode.setVisible( riseOverRunVisible.get() && linesVisible.get() );
        }
    }

    // Drag handler for the slope manipulator
    private static class SlopeDragHandler extends SimSharingDragHandler {

        private final PNode dragNode;
        private final ModelViewTransform mvt;
        private final Property<SlopeInterceptLine> line;
        private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public SlopeDragHandler( IUserComponent userComponent, IUserComponentType componentType, PNode dragNode, ModelViewTransform mvt, Property<SlopeInterceptLine> line ) {
            super( userComponent, componentType, true /* sendDragMessage */ );
            this.dragNode = dragNode;
            this.mvt = mvt;
            this.line = line;
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

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            // snap to grid
            line.set( new SlopeInterceptLine( MathUtil.round( line.get().rise ), MathUtil.round( line.get().run ), line.get().intercept ) );
        }

        @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return new ParameterSet().
                    with( ParameterKeys.rise, line.get().rise ).
                    with( ParameterKeys.run, line.get().run ).
                    with( ParameterKeys.intercept, line.get().intercept ).
                    with( super.getParametersForAllEvents( event ) );
        }
    }

    // Drag handler for the intercept manipulator
    private static class InterceptDragHandler extends SimSharingDragHandler {

        private final PNode dragNode;
        private final ModelViewTransform mvt;
        private final Property<SlopeInterceptLine> line;
        private double clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public InterceptDragHandler( IUserComponent userComponent, IUserComponentType componentType, PNode dragNode, ModelViewTransform mvt, Property<SlopeInterceptLine> line ) {
            super( userComponent, componentType, true /* sendDragMessage */ );
            this.dragNode = dragNode;
            this.mvt = mvt;
            this.line = line;
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

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            // snap to grid
            line.set( new SlopeInterceptLine( line.get().rise, line.get().run, MathUtil.round( line.get().intercept ) ) );
        }

        @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return new ParameterSet().
                    with( ParameterKeys.rise, line.get().rise ).
                    with( ParameterKeys.run, line.get().run ).
                    with( ParameterKeys.intercept, line.get().intercept ).
                    with( super.getParametersForAllEvents( event ) );
        }
    }
}
