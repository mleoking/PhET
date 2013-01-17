// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.FunctionHighlightHandler;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class graph for the "Slope", "Slope-Intercept" and "Point-Slope" modules.
 * Displays the following:
 * <ul>
 * <li>one interactive line</li>
 * <li>slope tool for interactive line</li>
 * <li>zero or more "saved" lines</li>
 * <li>zero or more "standard" lines</li>
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFormsGraphNode extends GraphNode {

    private static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units

    private final LineFormsModel model;
    private final LineFormsViewProperties viewProperties;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, slopeToolNode;
    private LineNode interactiveLineNode;

    /**
     * Constructor
     *
     * @param model the model
     * @param viewProperties properties that are specific to the view
     */
    protected LineFormsGraphNode( LineFormsModel model, final LineFormsViewProperties viewProperties ) {
        super( model.graph, model.mvt );

        this.model = model;
        this.viewProperties = viewProperties;

        // Parent nodes for each category of line (standard, saved, interactive) to maintain rendering order
        standardLinesParentNode = new PComposite();
        savedLinesParentNode = new PNode();
        interactiveLineParentNode = new PComposite();

        // Slope tool
        slopeToolNode = new SlopeToolNode( model.interactiveLine, model.mvt );

        // Rendering order
        addChild( interactiveLineParentNode );
        addChild( savedLinesParentNode );
        addChild( standardLinesParentNode );
        addChild( slopeToolNode );

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
    }

    // Gets the diameter of manipulators, in view coordinate frame.
    protected double getManipulatorDiameter() {
        return model.mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );
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
        final LineNode lineNode = createLineNode( line, model.graph, model.mvt );
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
            if ( node instanceof LineNode ) {
                LineNode lineNode = (LineNode) node;
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

        // slope tool
        slopeToolNode.setVisible( slopeVisible && linesVisible && interactiveLineVisible );
    }

    // Updates the line and its associated decorations
    protected void updateInteractiveLine( final Line line ) {
        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineNode = createLineNode( line, model.graph, model.mvt );
        interactiveLineNode.setEquationVisible( viewProperties.interactiveEquationVisible.get() );
        interactiveLineParentNode.addChild( interactiveLineNode );
    }

    // Creates a line node of the proper form.
    protected abstract LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt );
}
