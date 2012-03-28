// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;

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
    private final ArrayList<SlopeInterceptLineNode> savedLineNodes;
    private SlopeInterceptLineNode interactiveLineNode;
    private final SlopeInterceptLineNode yEqualsXLineNode, yEqualsNegativeXLineNode;

    public LineGraphNode( final LineGraph graph, final ModelViewTransform mvt, final Property<SlopeInterceptLine> interactiveLine, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;

        savedLineNodes = new ArrayList<SlopeInterceptLineNode>();

        interactiveLineNode = new SlopeInterceptLineNode( interactiveLine.get(), graph, mvt, Color.RED );
        addChild( interactiveLineNode );
        yEqualsXLineNode = new SlopeInterceptLineNode( yEqualsXLine, graph, mvt, Color.BLUE );
        addChild( yEqualsXLineNode );
        yEqualsNegativeXLineNode = new SlopeInterceptLineNode( yEqualsNegativeXLine, graph, mvt, Color.CYAN );
        addChild( yEqualsNegativeXLineNode );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                removeChild( interactiveLineNode );
                interactiveLineNode = new SlopeInterceptLineNode( interactiveLine.get(), graph, mvt, Color.RED );
                addChild( interactiveLineNode );
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

        interactiveLineNode.moveToFront();
    }

    public void reset() {
        yEqualsXVisible.reset();
        yEqualsNegativeXVisible.reset();
        linesVisible.reset();
    }

    private void updateLinesVisibility() {
        interactiveLineNode.setVisible( linesVisible.get() );
        yEqualsXLineNode.setVisible( linesVisible.get() && yEqualsXVisible.get() );
        yEqualsNegativeXLineNode.setVisible( linesVisible.get() && yEqualsNegativeXVisible.get() );
    }

    // "Saves" a line, displaying it on the graph.
    public void saveLine( SlopeInterceptLine line, Color color ) {
        SlopeInterceptLineNode lineNode = new SlopeInterceptLineNode( line, graph, mvt, color );
        savedLineNodes.add( lineNode );
        addChild( lineNode );
        interactiveLineNode.moveToFront(); // interactive line is always in the foreground
    }

    // Erases all of the "saved" lines
    public void eraseLines() {
        for ( SlopeInterceptLineNode node : new ArrayList<SlopeInterceptLineNode>( savedLineNodes ) ) {
            removeChild( node );
            savedLineNodes.remove( node );
        }
    }

}
