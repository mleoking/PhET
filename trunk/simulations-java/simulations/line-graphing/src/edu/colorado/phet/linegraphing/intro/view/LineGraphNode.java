// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.util.PDimension;
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

    private final StaticLineNode yEqualsXLineNode, yEqualsNegativeXLineNode;

    public LineGraphNode( LineGraph graph, ModelViewTransform mvt, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt );

        yEqualsXLineNode = new StaticLineNode( yEqualsXLine, graph, mvt, Color.BLUE );
        addChild( yEqualsXLineNode );
        yEqualsNegativeXLineNode = new StaticLineNode( yEqualsNegativeXLine, graph, mvt, Color.CYAN );
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

    private void updateLinesVisibility() {
        yEqualsXLineNode.setVisible( linesVisible.get() && yEqualsXVisible.get() );
        yEqualsNegativeXLineNode.setVisible( linesVisible.get() && yEqualsNegativeXVisible.get() );
    }

    public void reset() {
        yEqualsXVisible.reset();
        yEqualsNegativeXVisible.reset();
        linesVisible.reset();
    }

    // A static line that does not change
    public static class StaticLineNode extends PComposite {

        private static final PDimension ARROW_HEAD_SIZE = new PDimension( 8, 8 );
        private static final double LINE_THICKNESS = 1;
        private static final double LINE_EXTENT = 0.5; // how far line extends past edges of graph, in model coordinates

        public StaticLineNode( SlopeInterceptLine line, LineGraph graph, ModelViewTransform mvt, Color color ) {
            final double tipX = graph.minX - LINE_EXTENT;
            final double tailX = graph.maxX + LINE_EXTENT;
            Point2D tipLocation = new Point2D.Double( mvt.modelToViewDeltaX( tipX ),
                                                      mvt.modelToViewDeltaY( line.solve( tipX ) ) );
            Point2D tailLocation = new Point2D.Double( mvt.modelToViewDeltaX( tailX ),
                                                      mvt.modelToViewDeltaY( line.solve( tailX ) ) );
            DoubleArrowNode arrowNode = new DoubleArrowNode( tailLocation, tipLocation, ARROW_HEAD_SIZE.getHeight(), ARROW_HEAD_SIZE.getWidth(), LINE_THICKNESS );
            arrowNode.setPaint( color );
            arrowNode.setStroke( null ); // DoubleArrowNode is a shape that we fill, no need to stroke
            addChild( arrowNode );
        }
    }
}
