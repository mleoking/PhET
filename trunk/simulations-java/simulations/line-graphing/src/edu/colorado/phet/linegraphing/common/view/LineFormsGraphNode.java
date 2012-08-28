// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.FunctionHighlightHandler;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.LineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.view.RiseRunBracketNode.Direction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class graph for the "Slope-Intercept" and "Point-Slope" modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFormsGraphNode<T extends PointSlopeLine> extends GraphNode {

    protected static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units

    private final Graph graph;
    private final ModelViewTransform mvt;
    private final Property<Boolean> interactiveEquationVisible;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, bracketsParentNode;
    private final LineManipulatorNode pointManipulator, slopeManipulatorNode;
    private StraightLineNode interactiveLineNode;

    /**
     * Constructor
     * @param graph
     * @param mvt transform between model and view coordinate frames
     * @param interactiveLine the line that can be manipulated by the user
     * @param savedLines lines that have been saved by the user
     * @param standardLines standard lines (eg, y=x) that are available for viewing
     * @param linesVisible are lines visible on the graph?
     * @param interactiveLineVisible is the interactive line visible visible on the graph?
     * @param interactiveEquationVisible is the equation visible on the interactive line?
     * @param slopeVisible are the slope (rise/run) brackets visible on the graphed line?
     * @param riseRange
     * @param runRange
     * @param x1Range
     * @param y1Range
     * @param pointManipulatorColor
     * @param slopeManipulatorColor
     */
    public LineFormsGraphNode( final Graph graph, final ModelViewTransform mvt,
                               Property<T> interactiveLine,
                               ObservableList<T> savedLines,
                               ObservableList<T> standardLines,
                               final Property<Boolean> linesVisible,
                               final Property<Boolean> interactiveLineVisible,
                               Property<Boolean> interactiveEquationVisible,
                               final Property<Boolean> slopeVisible,
                               Property<DoubleRange> riseRange,
                               Property<DoubleRange> runRange,
                               Property<DoubleRange> x1Range,
                               Property<DoubleRange> y1Range,
                               Color pointManipulatorColor,
                               Color slopeManipulatorColor,
                               LineFactory<T> lineFactory ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;
        this.interactiveEquationVisible = interactiveEquationVisible;

        // Parent nodes for each category of line (standard, saved, interactive) to maintain rendering order
        standardLinesParentNode = new PComposite();
        savedLinesParentNode = new PNode();
        interactiveLineParentNode = new PComposite();

        // Rise and run brackets for the interactive line
        bracketsParentNode = new PComposite();

        // Manipulators for the interactive line
        final double manipulatorDiameter = mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );

        // interactivity for point (x1,y1) manipulator
        pointManipulator = new LineManipulatorNode( manipulatorDiameter, pointManipulatorColor );
        pointManipulator.addInputEventListener( new CursorHandler() );
        pointManipulator.addInputEventListener( new PointDragHandler<T>( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                         pointManipulator, mvt, interactiveLine, x1Range, y1Range,
                                                                         lineFactory ) );
        // interactivity for slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, slopeManipulatorColor );
        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler<T>( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                             slopeManipulatorNode, mvt, interactiveLine, riseRange, runRange,
                                                                             lineFactory ) );

        // Rendering order
        addChild( interactiveLineParentNode );
        addChild( savedLinesParentNode );
        addChild( standardLinesParentNode );
        addChild( bracketsParentNode );
        addChild( pointManipulator );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

        // Add/remove standard lines
        standardLines.addElementAddedObserver( new VoidFunction1<T>() {
            public void apply( T line ) {
                standardLineAdded( line );
            }
        } );
        standardLines.addElementRemovedObserver( new VoidFunction1<T>() {
            public void apply( T line ) {
                standardLineRemoved( line );
            }
        } );

        // Add/remove saved lines
        savedLines.addElementAddedObserver( new VoidFunction1<T>() {
            public void apply( T line ) {
                savedLineAdded( line );
            }
        } );
        savedLines.addElementRemovedObserver( new VoidFunction1<T>() {
            public void apply( T line ) {
                savedLineRemoved( line );
            }
        } );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<T>() {
            public void apply( T line ) {
                updateInteractiveLine( line, graph, mvt );
            }
        } );

        // Visibility of lines
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                updateLinesVisibility( linesVisible.get(), interactiveLineVisible.get(), slopeVisible.get() );
            }
        };
        visibilityObserver.observe( linesVisible, interactiveLineVisible, slopeVisible );

        // Visibility of the equation on the interactive line
        interactiveEquationVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( interactiveLineNode != null ) {
                    interactiveLineNode.setEquationVisible( visible );
                }
            }
        } );

        updateInteractiveLine( interactiveLine.get(), graph, mvt ); // initial position of manipulators
    }

    // Called when a standard line is added to the model.
    private void standardLineAdded( T line ) {
        standardLinesParentNode.addChild( createLineNode( line, graph, mvt ) );
    }

    // Called when a standard line is removed from the model.
    private void standardLineRemoved( T line ) {
        removeLineNode( line, standardLinesParentNode );
    }

    // Called when a saved line is added to the model.
    private void savedLineAdded( final T line ) {
        final StraightLineNode lineNode = createLineNode( line, graph, mvt );
        savedLinesParentNode.addChild( lineNode );
        // highlight on mouseOver
        lineNode.addInputEventListener( new FunctionHighlightHandler( new VoidFunction1<Boolean>() {
            public void apply( Boolean highlighted ) {
                lineNode.updateColor( highlighted ? LGColors.SAVED_LINE_HIGHLIGHT : line.color );
            }
        } ) );
    }

    // Called when a saved line is removed from the model.
    private void savedLineRemoved( T line ) {
        removeLineNode( line, savedLinesParentNode );
    }

    // Removes the node that corresponds to the specified line.
    private static void removeLineNode( PointSlopeLine line, PNode parent ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode node = parent.getChild( i );
            if ( node instanceof StraightLineNode ) {
                StraightLineNode lineNode = (StraightLineNode) node;
                if ( lineNode.line == line ) {
                    parent.removeChild( node );
                    break;
                }
            }
        }
    }

    // Updates the visibility of lines and associated decorations
    protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {

        // interactive line
        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible && interactiveLineVisible );
        }

        // saved & standard lines
        savedLinesParentNode.setVisible( linesVisible );
        standardLinesParentNode.setVisible( linesVisible );

        // slope brackets
        if ( bracketsParentNode != null ) {
            bracketsParentNode.setVisible( slopeVisible && linesVisible && interactiveLineVisible );
        }

        // Hide the manipulators at appropriate times (when dragging or based on visibility of lines).
        pointManipulator.setVisible( linesVisible && interactiveLineVisible );
        slopeManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
    }

    // Updates the line and its associated decorations
    protected void updateInteractiveLine( final T line, final Graph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineNode = createLineNode( line, graph, mvt );
        interactiveLineNode.setEquationVisible( interactiveEquationVisible.get() );
        interactiveLineParentNode.addChild( interactiveLineNode );

        // replace the rise/run brackets
        bracketsParentNode.removeAllChildren();
        if ( line.run != 0 ) {

            // run bracket
            final Direction runDirection = line.rise >= 0 ? Direction.UP : Direction.DOWN;
            final RiseRunBracketNode runBracketNode = new RiseRunBracketNode( runDirection, mvt.modelToViewDeltaX( line.run ), line.run );
            bracketsParentNode.addChild( runBracketNode );
            runBracketNode.setOffset( mvt.modelToViewX( line.x1 ), mvt.modelToViewY( line.y1 ) );

            // rise bracket
            if (  line.rise != 0  ) {
                final Direction riseDirection = line.run > 0 ? Direction.LEFT : Direction.RIGHT;
                final RiseRunBracketNode riseBracket = new RiseRunBracketNode( riseDirection, mvt.modelToViewDeltaX( line.rise ), line.rise );
                bracketsParentNode.addChild( riseBracket );
                riseBracket.setOffset( mvt.modelToViewX( line.x1 + line.run ), mvt.modelToViewY( line.y1 ) );
            }
        }

        // move the manipulators
        pointManipulator.setOffset( mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
        slopeManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( line.x1 + line.run, line.y1 + line.rise ) ) );
    }

    // Creates a line node of the proper form.
    protected abstract StraightLineNode createLineNode( T line, Graph graph, ModelViewTransform mvt );
}
