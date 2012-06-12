// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.*;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for graphs, displays grid and axes.
 * The node's origin is at model coordinate (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphNode extends PhetPNode {

    // grid
    private static final Color GRID_BACKGROUND = Color.WHITE;
    private static final Stroke MINOR_GRID_LINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRID_LINE_COLOR = Color.LIGHT_GRAY;
    private static final Stroke MAJOR_GRID_LINE_STROKE = new BasicStroke( 0.5f );
    private static final Color MAJOR_GRID_LINE_COLOR = Color.LIGHT_GRAY;

    // axes                                                                                                                                          n
    private static final PDimension AXIS_ARROW_SIZE = new PDimension( 10, 10 );
    private static final double AXIS_THICKNESS = 1;
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final double AXIS_EXTENT = 1.0; // how far the arrow extends past the min/max ticks, in model coordinates
    private static final PhetFont AXIS_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final double AXIS_LABEL_SPACING = 2; // space between end of axis and label

    // ticks
    private static final int MAJOR_TICK_SPACING = 5; // model units
    private static final double MINOR_TICK_LENGTH = 3; // how far a minor tick extends from the axis
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 0.5f );
    private static final Color MINOR_TICK_COLOR = Color.BLACK;
    private static final double MAJOR_TICK_LENGTH = 6; // how far a major tick extends from the axis
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final PhetFont MAJOR_TICK_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final double TICK_LABEL_SPACING = 2;
    private static final double MINUS_SIGN_WIDTH = new PhetPText( "-", MAJOR_TICK_FONT ).getFullBoundsReference().getWidth();

    /**
     * Constructor
     * @param graph model element that this node displays
     * @param mvt transform between model and view coordinate frames
     */
    public GraphNode( Graph graph, ModelViewTransform mvt ) {

        assert ( graph.contains( new ImmutableVector2D( 0, 0 ) ) && graph.contains( new ImmutableVector2D( 1, 1 ) ) ); // (0,0) and quadrant 1 is visible

        addChild( new GridNode( graph, mvt ) );
        addChild( new XAxisNode( graph, mvt ) );
        addChild( new YAxisNode( graph, mvt ) );
    }

    // 2D grid
    private static class GridNode extends PComposite {

        public GridNode( Graph graph, ModelViewTransform mvt ) {

            // Background
            PPath backgroundNode = new PPath( new Rectangle2D.Double( mvt.modelToViewX( graph.xRange.getMin() ), mvt.modelToViewY( graph.yRange.getMax() ),
                                                                      mvt.modelToViewDeltaX( graph.getWidth() ), mvt.modelToViewDeltaY( -graph.getHeight() ) ) );
            backgroundNode.setPaint( GRID_BACKGROUND );
            backgroundNode.setStroke( null );
            addChild( backgroundNode );

            // Horizontal grid lines
            PNode horizontalGridLinesNode = new PNode();
            addChild( horizontalGridLinesNode );
            final int numberOfHorizontalGridLines = graph.getHeight() + 1;
            {
                final double minX = mvt.modelToViewX( graph.xRange.getMin() );
                final double maxX = mvt.modelToViewX( graph.xRange.getMax() );
                // add one line for each unit of grid spacing
                for ( int i = 0; i < numberOfHorizontalGridLines; i++ ) {
                    final int modelY = graph.yRange.getMin() + i;
                    if ( modelY != 0 ) { // skip origin, x axis will live here
                        final double yOffset = mvt.modelToViewY( modelY );
                        PPath gridLineNode = new PPath( new Line2D.Double( minX, yOffset, maxX, yOffset ) );
                        horizontalGridLinesNode.addChild( gridLineNode );
                        if ( Math.abs( modelY ) % MAJOR_TICK_SPACING == 0 ) {
                            gridLineNode.setStroke( MAJOR_GRID_LINE_STROKE );
                            gridLineNode.setStrokePaint( MAJOR_GRID_LINE_COLOR );
                        }
                        else {
                            gridLineNode.setStroke( MINOR_GRID_LINE_STROKE );
                            gridLineNode.setStrokePaint( MINOR_GRID_LINE_COLOR );
                        }
                    }
                }
            }

            // Vertical grid lines
            PNode verticalGridLinesNode = new PNode();
            addChild( verticalGridLinesNode );
            final int numberOfVerticalGridLines = graph.getWidth() + 1;
            {
                final double minY = mvt.modelToViewY( graph.yRange.getMax() );
                final double maxY = mvt.modelToViewY( graph.yRange.getMin() );
                // add one line for each unit of grid spacing
                for ( int i = 0; i < numberOfVerticalGridLines; i++ ) {
                    final double modelX = graph.xRange.getMin() + i;
                    if ( modelX != 0 ) { // skip origin, y axis will live here
                        final double xOffset = mvt.modelToViewX( modelX );
                        PPath gridLineNode = new PPath( new Line2D.Double( xOffset, minY, xOffset, maxY ) );
                        verticalGridLinesNode.addChild( gridLineNode );
                        if ( Math.abs( modelX ) % MAJOR_TICK_SPACING == 0 ) {
                            gridLineNode.setStroke( MAJOR_GRID_LINE_STROKE );
                            gridLineNode.setStrokePaint( MAJOR_GRID_LINE_COLOR );
                        }
                        else {
                            gridLineNode.setStroke( MINOR_GRID_LINE_STROKE );
                            gridLineNode.setStrokePaint( MINOR_GRID_LINE_COLOR );
                        }
                    }
                }
            }
        }
    }

    // x axis (horizontal)
    private static class XAxisNode extends PComposite {

        public XAxisNode( Graph graph, ModelViewTransform mvt ) {

            // horizontal line with arrows at both ends
            Point2D tailLocation = new Point2D.Double( mvt.modelToViewX( graph.xRange.getMin() - AXIS_EXTENT ), mvt.modelToViewY( 0 ) );
            Point2D tipLocation = new Point2D.Double( mvt.modelToViewX( graph.xRange.getMax() + AXIS_EXTENT ), mvt.modelToViewY( 0 ) );
            DoubleArrowNode lineNode = new DoubleArrowNode( tailLocation, tipLocation, AXIS_ARROW_SIZE.getHeight(), AXIS_ARROW_SIZE.getWidth(), AXIS_THICKNESS );
            lineNode.setPaint( AXIS_COLOR );
            lineNode.setStroke( null ); // DoubleArrowNode is a shape that we're filling, no need to stroke
            addChild( lineNode );

            // label at positive end
            PText labelNode = new PText( Strings.SYMBOL_X );
            addChild( labelNode );
            labelNode.setFont( AXIS_LABEL_FONT );
            labelNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + AXIS_LABEL_SPACING,
                                 lineNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );

            // ticks
            final int numberOfTicks = graph.getWidth() + 1;
            for ( int i = 0; i < numberOfTicks; i++ ) {
                final int modelX = graph.xRange.getMin() + i;
                if ( modelX != 0 ) { // skip the origin
                    final double x = mvt.modelToViewX( modelX );
                    final double y = mvt.modelToViewY( 0 );
                    if ( Math.abs( modelX ) % MAJOR_TICK_SPACING == 0 ) {
                        // major tick line
                        PPath tickLineNode = new PPath( new Line2D.Double( x, y - MAJOR_TICK_LENGTH, x, y + MAJOR_TICK_LENGTH ) );
                        tickLineNode.setStroke( MAJOR_TICK_STROKE );
                        tickLineNode.setPaint( MAJOR_TICK_COLOR );
                        addChild( tickLineNode );
                        // major tick label
                        PText tickLabelNode = new PText( String.valueOf( modelX ) );
                        tickLabelNode.setFont( MAJOR_TICK_FONT );
                        tickLabelNode.setTextPaint( MAJOR_TICK_COLOR );
                        addChild( tickLabelNode );
                        // center label under line, compensate for minus sign
                        final double signXOffset = ( modelX < 0 ) ? -( MINUS_SIGN_WIDTH / 2 ) : 0;
                        tickLabelNode.setOffset( tickLineNode.getFullBoundsReference().getCenterX() - ( tickLabelNode.getFullBoundsReference().getWidth() / 2 ) + signXOffset,
                                                 tickLineNode.getFullBoundsReference().getMaxY() + TICK_LABEL_SPACING );
                    }
                    else {
                        // minor tick with no label
                        PPath tickLineNode = new PPath( new Line2D.Double( x, y - MINOR_TICK_LENGTH, x, y + MINOR_TICK_LENGTH ) );
                        tickLineNode.setStroke( MINOR_TICK_STROKE );
                        tickLineNode.setPaint( MINOR_TICK_COLOR );
                        addChild( tickLineNode );
                    }
                }
            }
        }
    }

    // y axis (vertical)
    private static class YAxisNode extends PComposite {

        public YAxisNode( Graph graph, ModelViewTransform mvt ) {

            // horizontal line with arrows at both ends
            Point2D tailLocation = new Point2D.Double( mvt.modelToViewX( 0 ), mvt.modelToViewY( graph.yRange.getMin() - AXIS_EXTENT ) );
            Point2D tipLocation = new Point2D.Double( mvt.modelToViewX( 0 ), mvt.modelToViewY( graph.yRange.getMax() + AXIS_EXTENT ) );
            DoubleArrowNode lineNode = new DoubleArrowNode( tailLocation, tipLocation, AXIS_ARROW_SIZE.getHeight(), AXIS_ARROW_SIZE.getWidth(), AXIS_THICKNESS );
            lineNode.setPaint( AXIS_COLOR );
            lineNode.setStroke( null ); // DoubleArrowNode is a shape that we're filling, no need to stroke
            addChild( lineNode );

            // label at positive end
            PText labelNode = new PText( Strings.SYMBOL_Y );
            addChild( labelNode );
            labelNode.setFont( AXIS_LABEL_FONT );
            labelNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                 lineNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - AXIS_LABEL_SPACING );

            // ticks
            final int numberOfTicks = graph.getHeight() + 1;
            for ( int i = 0; i < numberOfTicks; i++ ) {
                final int modelY = graph.yRange.getMin() + i;
                if ( modelY != 0 ) { // skip the origin
                    final double x = mvt.modelToViewX( 0 );
                    final double y = mvt.modelToViewY( modelY );
                    if ( Math.abs( modelY ) % MAJOR_TICK_SPACING == 0 ) {
                        // major tick line
                        PPath tickLineNode = new PPath( new Line2D.Double( x - MAJOR_TICK_LENGTH, y, x + MAJOR_TICK_LENGTH, y ) );
                        tickLineNode.setStroke( MAJOR_TICK_STROKE );
                        tickLineNode.setPaint( MAJOR_TICK_COLOR );
                        addChild( tickLineNode );
                        // major tick label
                        PText tickLabelNode = new PText( String.valueOf( modelY ) );
                        tickLabelNode.setFont( MAJOR_TICK_FONT );
                        tickLabelNode.setTextPaint( MAJOR_TICK_COLOR );
                        addChild( tickLabelNode );
                        // center label to left of line
                        tickLabelNode.setOffset( tickLineNode.getFullBoundsReference().getMinX() - tickLabelNode.getFullBoundsReference().getWidth() - TICK_LABEL_SPACING,
                                                 tickLineNode.getFullBoundsReference().getCenterY() - ( tickLabelNode.getFullBoundsReference().getHeight() / 2 ) );
                    }
                    else {
                        // minor tick with no label
                        PPath tickLineNode = new PPath( new Line2D.Double( x - MINOR_TICK_LENGTH, y, x + MINOR_TICK_LENGTH, y ) );
                        tickLineNode.setStroke( MINOR_TICK_STROKE );
                        tickLineNode.setPaint( MINOR_TICK_COLOR );
                        addChild( tickLineNode );
                    }
                }
            }
        }
    }

    // Creates an icon for the "y = +x" feature
    public static Icon createYEqualsXIcon( double width ) {
        return createIcon( width, LGColors.Y_EQUALS_X, -3, -3, 3, 3 );
    }

    // Creates an icon for the "y = -x" feature
    public static Icon createYEqualsNegativeXIcon( double width ) {
        return createIcon( width, LGColors.Y_EQUALS_NEGATIVE_X, -3, 3, 3, -3 );
    }

    // Creates an icon for a line between 2 points on a grid with fixed dimensions.
    public static Icon createIcon( double width, final Color color, int x1, int y1, int x2, int y2 ) {
        IntegerRange axisRange = new IntegerRange( -3, 3 );
        final Graph graph = new Graph( axisRange, axisRange );
        final ModelViewTransform mvt = ModelViewTransform.createOffsetScaleMapping( new java.awt.geom.Point2D.Double( 0, 0 ), 15, -15 );
        GraphNode graphNode = new GraphNode( graph, mvt );
        Point2D p1 = mvt.modelToView( x1, y1 );
        Point2D p2 = mvt.modelToView( x2, y2 );
        graphNode.addChild( new PPath( new Line2D.Double( p1.getX(), p1.getY(), p2.getX(), p2.getY() ) ) {{
            setStrokePaint( color );
            setStroke( new BasicStroke( 4f ) );
        }} );
        graphNode.scale( width / graphNode.getFullBoundsReference().getWidth() );
        return new ImageIcon( new PadBoundsNode( graphNode ).toImage() );
    }

    // test
    public static void main( String[] args ) {

        IntegerRange axisRange = new IntegerRange( -10, 10 );
        Graph graph = new Graph( axisRange, axisRange );
        ModelViewTransform mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 300, 300 ), 20, -20 );

        GraphNode graphNode = new GraphNode( graph, mvt );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( Color.LIGHT_GRAY );
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        canvas.getLayer().addChild( graphNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
