// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.RiseRunBracketNode;
import edu.colorado.phet.linegraphing.common.view.RiseRunBracketNode.Direction;
import edu.colorado.phet.linegraphing.common.view.StraightLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for graph that displays static lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineGraphNode extends GraphNode {

    protected static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units

    private final Graph graph;
    private final ModelViewTransform mvt;
    private final Property<Boolean> interactiveEquationVisible;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, bracketsParentNode;
    private StraightLineNode interactiveLineNode;

    public LineGraphNode( final Graph graph, final ModelViewTransform mvt,
                          Property<StraightLine> interactiveLine,
                          ObservableList<StraightLine> savedLines,
                          ObservableList<StraightLine> standardLines,
                          Property<Boolean> interactiveEquationVisible,
                          final Property<Boolean> linesVisible,
                          final Property<Boolean> interactiveLineVisible,
                          final Property<Boolean> slopeVisible ) {
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

        // Rendering order
        addChild( interactiveLineParentNode );
        addChild( savedLinesParentNode );
        addChild( standardLinesParentNode );
        addChild( bracketsParentNode );

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
    }

    private void addStandardLine( StraightLine line ) {
        standardLinesParentNode.addChild( createLineNode( line, graph, mvt ) );
    }

    private void removeStandardLine( StraightLine line ) {
        removeLine( line, standardLinesParentNode );
    }

    private void addSavedLine( StraightLine line ) {
        savedLinesParentNode.addChild( createLineNode( line, graph, mvt ) );
    }

    private void removeSavedLine( StraightLine line ) {
        removeLine( line, savedLinesParentNode );
    }

    private static void removeLine( StraightLine line, PNode parent ) {
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

    protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {

        savedLinesParentNode.setVisible( linesVisible );
        standardLinesParentNode.setVisible( linesVisible );

        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible && interactiveLineVisible );
        }

        if ( bracketsParentNode != null ) {
            bracketsParentNode.setVisible( slopeVisible && linesVisible && interactiveLineVisible );
        }
    }

    // Updates the line and its associated decorations
    protected void updateInteractiveLine( final StraightLine line, final Graph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineNode = createLineNode( line, graph, mvt );
        interactiveLineNode.setEquationVisible( interactiveEquationVisible.get() && !isInteracting() );
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
    }

    // Is the user interacting with the graph?
    protected abstract boolean isInteracting();

    // Creates a line node of the proper form.
    protected abstract StraightLineNode createLineNode( StraightLine line, Graph graph, ModelViewTransform mvt );
}
