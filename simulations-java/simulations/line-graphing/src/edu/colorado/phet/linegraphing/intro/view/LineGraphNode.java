// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph that displays lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends GraphNode implements Resettable {

    public final Property<Boolean> linesVisible = new Property<Boolean>( true );
    public final Property<Boolean> yEqualsXVisible = new Property<Boolean>( false );
    public final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( false );

    private final LineGraph graph;
    private final ModelViewTransform mvt;
    private SlopeInterceptLineNode interactiveLineNode;
    private final SlopeInterceptLineNode yEqualsXLineNode, yEqualsNegativeXLineNode;
    private PNode savedLinesParentNode, standardLinesParentNode, interactiveLineParentNode; // intermediate nodes, for consistent rendering order

    public LineGraphNode( final LineGraph graph, final ModelViewTransform mvt, final Property<SlopeInterceptLine> interactiveLine, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;

        // Interactive line
        interactiveLineParentNode = new PComposite();

        // Standard lines
        standardLinesParentNode = new PComposite();
        yEqualsXLineNode = new SlopeInterceptLineNode( yEqualsXLine, graph, mvt, LGColors.Y_EQUALS_X );
        standardLinesParentNode.addChild( yEqualsXLineNode );
        yEqualsNegativeXLineNode = new SlopeInterceptLineNode( yEqualsNegativeXLine, graph, mvt, LGColors.Y_EQUALS_NEGATIVE_X );
        standardLinesParentNode.addChild( yEqualsNegativeXLineNode );

        // Saved lines
        savedLinesParentNode = new PComposite();

        // Rendering order
        addChild( standardLinesParentNode );
        addChild( savedLinesParentNode );
        addChild( interactiveLineParentNode );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                if ( interactiveLineNode != null ) {
                    interactiveLineParentNode.removeChild( interactiveLineNode );
                }
                interactiveLineNode = new SlopeInterceptLineNode( interactiveLine.get(), graph, mvt, LGColors.INTERACTIVE_LINE );
                interactiveLineParentNode.addChild( interactiveLineNode );
            }
        } );

        // Visibility of lines
        yEqualsXVisible.addObserver( new SimpleObserver() {
            public void update() {
                updateLinesVisibility();
            }
        } );
        yEqualsNegativeXVisible.addObserver( new SimpleObserver() {
            public void update() {
                updateLinesVisibility();
            }
        } );
        linesVisible.addObserver( new SimpleObserver() {
            public void update() {
                updateLinesVisibility();
            }
        } );
    }

    public void reset() {
        yEqualsXVisible.reset();
        yEqualsNegativeXVisible.reset();
        linesVisible.reset();
        eraseLines();
    }

    private void updateLinesVisibility() {
        interactiveLineParentNode.setVisible( linesVisible.get() );
        savedLinesParentNode.setVisible( linesVisible.get() );
        standardLinesParentNode.setVisible( linesVisible.get() );
        yEqualsXLineNode.setVisible( yEqualsXVisible.get() );
        yEqualsNegativeXLineNode.setVisible( yEqualsNegativeXVisible.get() );
    }

    // "Saves" a line, displaying it on the graph.
    public void saveLine( SlopeInterceptLine line, Color color ) {
        savedLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt, color ) );
    }

    // Erases all of the "saved" lines
    public void eraseLines() {
        savedLinesParentNode.removeAllChildren();
    }

}
