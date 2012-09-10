// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.FunctionHighlightHandler;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.RiseRunBracketNode.Direction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class graph for the "Slope-Intercept" and "Point-Slope" modules.
 * Displays the following lines:
 * <ul>
 * <li>one interactive line with point and slope manipulators, and slope (rise/run) brackets</li>
 * <li>zero or more "saved" lines</li>
 * <li>zero or more "standard" lines</li>
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFormsGraphNode extends GraphNode {

    protected static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units

    private final LineFormsModel model;
    private final LineFormsViewProperties viewProperties;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, bracketsParentNode;
    private final LineManipulatorNode pointManipulator, slopeManipulatorNode;
    private StraightLineNode interactiveLineNode;

    /**
     * Constructor
     *
     * @param model
     * @param viewProperties
     * @param pointManipulatorColor
     * @param slopeManipulatorColor
     */
    public LineFormsGraphNode( LineFormsModel model,
                               final LineFormsViewProperties viewProperties,
                               Color pointManipulatorColor,
                               Color slopeManipulatorColor ) {
        super( model.graph, model.mvt );

        this.model = model;
        this.viewProperties = viewProperties;

        // Parent nodes for each category of line (standard, saved, interactive) to maintain rendering order
        standardLinesParentNode = new PComposite();
        savedLinesParentNode = new PNode();
        interactiveLineParentNode = new PComposite();

        // Rise and run brackets for the interactive line
        bracketsParentNode = new PComposite();

        // Manipulators for the interactive line
        final double manipulatorDiameter = model.mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );

        // interactivity for point (x1,y1) manipulator
        pointManipulator = new LineManipulatorNode( manipulatorDiameter, pointManipulatorColor );
        pointManipulator.addInputEventListener( new PointDragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                      pointManipulator, model.mvt, model.interactiveLine, model.x1Range, model.y1Range ) );
        // interactivity for slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, slopeManipulatorColor );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, model.mvt, model.interactiveLine, model.riseRange, model.runRange ) );

        // Rendering order
        addChild( interactiveLineParentNode );
        addChild( savedLinesParentNode );
        addChild( standardLinesParentNode );
        addChild( bracketsParentNode );
        addChild( pointManipulator );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

        // Add/remove standard lines
        model.standardLines.addElementAddedObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                standardLineAdded( line );
            }
        } );
        model.standardLines.addElementRemovedObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                standardLineRemoved( line );
            }
        } );

        // Add/remove saved lines
        model.savedLines.addElementAddedObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                savedLineAdded( line );
            }
        } );
        model.savedLines.addElementRemovedObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                savedLineRemoved( line );
            }
        } );

        // When the interactive line changes, update the graph.
        model.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                updateInteractiveLine( line );
            }
        } );

        // Visibility of lines
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                updateLinesVisibility( viewProperties.linesVisible.get(), viewProperties.interactiveLineVisible.get(), viewProperties.slopeVisible.get() );
            }
        };
        visibilityObserver.observe( viewProperties.linesVisible, viewProperties.interactiveLineVisible, viewProperties.slopeVisible );

        // Visibility of the equation on the interactive line
        viewProperties.interactiveEquationVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( interactiveLineNode != null ) {
                    interactiveLineNode.setEquationVisible( visible );
                }
            }
        } );

        updateInteractiveLine( model.interactiveLine.get() ); // initial position of manipulators
    }

    // Called when a standard line is added to the model.
    private void standardLineAdded( Line line ) {
        standardLinesParentNode.addChild( createLineNode( line, model.graph, model.mvt ) );
    }

    // Called when a standard line is removed from the model.
    private void standardLineRemoved( Line line ) {
        removeLineNode( line, standardLinesParentNode );
    }

    // Called when a saved line is added to the model.
    private void savedLineAdded( final Line line ) {
        final StraightLineNode lineNode = createLineNode( line, model.graph, model.mvt );
        savedLinesParentNode.addChild( lineNode );
        // highlight on mouseOver
        lineNode.addInputEventListener( new FunctionHighlightHandler( new VoidFunction1<Boolean>() {
            public void apply( Boolean highlighted ) {
                lineNode.updateColor( highlighted ? LGColors.SAVED_LINE_HIGHLIGHT : line.color );
            }
        } ) );
    }

    // Called when a saved line is removed from the model.
    private void savedLineRemoved( Line line ) {
        removeLineNode( line, savedLinesParentNode );
    }

    // Removes the node that corresponds to the specified line.
    private static void removeLineNode( Line line, PNode parent ) {
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
    protected void updateInteractiveLine( final Line line ) {

        final ModelViewTransform mvt = model.mvt;

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineNode = createLineNode( line, model.graph, mvt );
        interactiveLineNode.setEquationVisible( viewProperties.interactiveEquationVisible.get() );
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
            if ( line.rise != 0 ) {
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
    protected abstract StraightLineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt );
}
