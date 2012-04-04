// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph that displays static lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends GraphNode implements Resettable {

    public final Property<Boolean> linesVisible = new Property<Boolean>( true );
    public final Property<Boolean> yEqualsXVisible = new Property<Boolean>( false );
    public final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( false );

    private final LineGraph graph;
    private final ModelViewTransform mvt;
    private final SlopeInterceptLineNode yEqualsXLineNode, yEqualsNegativeXLineNode;
    private PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order

    public LineGraphNode( final LineGraph graph, final ModelViewTransform mvt, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;

        // Standard lines
        standardLinesParentNode = new PComposite();
        yEqualsXLineNode = new SlopeInterceptLineNode( yEqualsXLine, graph, mvt, LGColors.Y_EQUALS_X );
        standardLinesParentNode.addChild( yEqualsXLineNode );
        yEqualsNegativeXLineNode = new SlopeInterceptLineNode( yEqualsNegativeXLine, graph, mvt, LGColors.Y_EQUALS_NEGATIVE_X );
        standardLinesParentNode.addChild( yEqualsNegativeXLineNode );

        // Saved lines
        savedLinesParentNode = new PNode();

        // Rendering order
        addChild( standardLinesParentNode );
        addChild( savedLinesParentNode );

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
        linesVisible.reset();
        yEqualsXVisible.reset();
        yEqualsNegativeXVisible.reset();
        eraseLines();
    }

    protected void updateLinesVisibility() {
        savedLinesParentNode.setVisible( linesVisible.get() );
        standardLinesParentNode.setVisible( linesVisible.get() );
        yEqualsXLineNode.setVisible( yEqualsXVisible.get() );
        yEqualsNegativeXLineNode.setVisible( yEqualsNegativeXVisible.get() );
    }

    // "Saves" a line, displaying it on the graph.
    public void saveLine( SlopeInterceptLine line ) {
        savedLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt, LGColors.SAVED_LINE_NORMAL, LGColors.SAVED_LINE_HIGHLIGHT ) );
    }

    // Erases all of the "saved" lines
    public void eraseLines() {
        savedLinesParentNode.removeAllChildren();
    }

}
