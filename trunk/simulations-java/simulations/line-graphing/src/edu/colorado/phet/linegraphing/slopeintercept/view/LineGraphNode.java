// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.RiseRunBracketNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.LineManipulatorDragHandler.InterceptDragHandler;
import edu.colorado.phet.linegraphing.slopeintercept.view.LineManipulatorDragHandler.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.RiseRunBracketNode.Direction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph that displays static lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends GraphNode {

    private static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units

    private final Graph graph;
    private final ModelViewTransform mvt;
    private final Property<Boolean> interactiveEquationVisible;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, bracketsParentNode;
    private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;
    private SlopeInterceptLineNode interactiveLineNode;
    private final Property<Boolean> linesVisible, interactiveLineVisible, slopeVisible;

    public LineGraphNode( final Graph graph, final ModelViewTransform mvt,
                          Property<StraightLine> interactiveLine,
                          ObservableList<StraightLine> savedLines,
                          ObservableList<StraightLine> standardLines,
                          Property<DoubleRange> riseRange,
                          Property<DoubleRange> runRange,
                          Property<DoubleRange> interceptRange,
                          Property<Boolean> interactiveEquationVisible,
                          Property<Boolean> linesVisible,
                          Property<Boolean> interactiveLineVisible,
                          Property<Boolean> slopeVisible ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;
        this.interactiveEquationVisible = interactiveEquationVisible;
        this.linesVisible = linesVisible;
        this.interactiveLineVisible = interactiveLineVisible;
        this.slopeVisible = slopeVisible;

        // Parent nodes for each category of line (standard, saved, interactive) to maintain rendering order
        standardLinesParentNode = new PComposite();
        savedLinesParentNode = new PNode();
        interactiveLineParentNode = new PComposite();

        // Manipulators for the interactive line
        final double manipulatorDiameter = mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
        interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );

        // Rise and run brackets for the interactive line
        bracketsParentNode = new PComposite();

        // Rendering order
        addChild( interactiveLineParentNode );
        addChild( savedLinesParentNode );
        addChild( standardLinesParentNode );
        addChild( bracketsParentNode );
        addChild( interceptManipulatorNode );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

        // Add/remove standard lines
        standardLines.addElementAddedObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                addStandardLine( line );
            }
        } );
        standardLines.addElementRemovedObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                removeStandardLine( line );
            }
        } );

        // Add/remove saved lines
        savedLines.addElementAddedObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                addSavedLine( line );
            }
        } );
        savedLines.addElementRemovedObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                removeSavedLine( line );
            }
        } );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                updateInteractiveLine( line, graph, mvt );
            }
        } );

        // Visibility of lines
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                updateLinesVisibility();
            }
        };
        visibilityObserver.observe( interactiveLineVisible, slopeVisible, linesVisible );

        // Visibility of the equation on the interactive line
        interactiveEquationVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( interactiveLineNode != null ) {
                    interactiveLineNode.setEquationVisible( visible );
                }
            }
        } );

        // interactivity for slope manipulator
        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, mvt, interactiveLine, riseRange, runRange ) );

        // interactivity for intercept manipulator
        interceptManipulatorNode.addInputEventListener( new CursorHandler() );
        interceptManipulatorNode.addInputEventListener( new InterceptDragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite,
                                                                                  interceptManipulatorNode, mvt, interactiveLine, interceptRange ) );
    }

    private void addStandardLine( StraightLine line ) {
        standardLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt ) );
    }

    private void removeStandardLine( StraightLine line ) {
        removeLine( line, standardLinesParentNode );
    }

    private void addSavedLine( StraightLine line ) {
        savedLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt ) );
    }

    private void removeSavedLine( StraightLine line ) {
        removeLine( line, savedLinesParentNode );
    }

    private static void removeLine( StraightLine line, PNode parent ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode node = parent.getChild( i );
            if ( node instanceof SlopeInterceptLineNode ) {
                SlopeInterceptLineNode lineNode = (SlopeInterceptLineNode)node;
                if ( lineNode.line == line ) {
                    parent.removeChild( node );
                    break;
                }
            }
        }
    }

    protected void updateLinesVisibility() {

        savedLinesParentNode.setVisible( linesVisible.get() );
        standardLinesParentNode.setVisible( linesVisible.get() );

        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
            slopeManipulatorNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
            interceptManipulatorNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
        }

        if ( bracketsParentNode != null ) {
            bracketsParentNode.setVisible( slopeVisible.get() && linesVisible.get() && interactiveLineVisible.get() );
        }
    }

    // Updates the line and its associated decorations
    private void updateInteractiveLine( final StraightLine line, final Graph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineNode = new SlopeInterceptLineNode( line, graph, mvt );
        interactiveLineNode.setEquationVisible( interactiveEquationVisible.get() && !slopeManipulatorNode.isDragging() && !interceptManipulatorNode.isDragging() );
        interactiveLineParentNode.addChild( interactiveLineNode );

        // replace the rise/run brackets
        bracketsParentNode.removeAllChildren();
        if ( MathUtil.round( line.run ) != 0 ) {

            // run bracket
            final Direction runDirection = line.rise >= 0 ? Direction.UP : Direction.DOWN;
            final RiseRunBracketNode runBracketNode = new RiseRunBracketNode( runDirection, mvt.modelToViewDeltaX( line.run ), line.run );
            bracketsParentNode.addChild( runBracketNode );
            runBracketNode.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( line.yIntercept ) );

            // rise bracket
            if (  MathUtil.round( line.rise ) != 0  ) {
                final Direction riseDirection = line.run > 0 ? Direction.LEFT : Direction.RIGHT;
                final RiseRunBracketNode riseBracket = new RiseRunBracketNode( riseDirection, mvt.modelToViewDeltaX( line.rise ), line.rise );
                bracketsParentNode.addChild( riseBracket );
                riseBracket.setOffset( mvt.modelToViewX( line.run ), mvt.modelToViewY( line.yIntercept ) );
            }
        }

        // move the manipulators
        final double y = line.rise + line.yIntercept;
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
        slopeManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( x, y ) ) );
        interceptManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( 0, line.yIntercept ) ) );
    }
}
