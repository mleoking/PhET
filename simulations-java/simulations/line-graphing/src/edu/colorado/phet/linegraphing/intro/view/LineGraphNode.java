// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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
    private final SlopeInterceptLineNode yEqualsXLineNode, yEqualsNegativeXLineNode;

    public LineGraphNode( LineGraph graph, ModelViewTransform mvt, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;

        savedLineNodes = new ArrayList<SlopeInterceptLineNode>();

        yEqualsXLineNode = new SlopeInterceptLineNode( yEqualsXLine, graph, mvt, Color.BLUE );
        addChild( yEqualsXLineNode );
        yEqualsNegativeXLineNode = new SlopeInterceptLineNode( yEqualsNegativeXLine, graph, mvt, Color.CYAN );
        addChild( yEqualsNegativeXLineNode );

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
    }

    private void updateLinesVisibility() {
        yEqualsXLineNode.setVisible( linesVisible.get() && yEqualsXVisible.get() );
        yEqualsNegativeXLineNode.setVisible( linesVisible.get() && yEqualsNegativeXVisible.get() );
    }

    // "Saves" a line, displaying it on the graph.
    public void saveLine( SlopeInterceptLine line, Color color ) {
        SlopeInterceptLineNode lineNode = new SlopeInterceptLineNode( line, graph, mvt, color );
        savedLineNodes.add( lineNode );
        addChild( lineNode );
    }

    // Erases all of the "saved" lines
    public void eraseLines() {
        for ( SlopeInterceptLineNode node : new ArrayList<SlopeInterceptLineNode>( savedLineNodes ) ) {
            removeChild( node );
            savedLineNodes.remove( node );
        }
    }

}
