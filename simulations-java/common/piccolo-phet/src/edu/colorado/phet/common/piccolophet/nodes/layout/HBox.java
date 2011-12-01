// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Lays out nodes horizontally.
 * Various strategies are provided for common forms of vertical alignment.
 * The layout doesn't update when children bounds change, layout is only performed when new children are added (sufficient for bending light usage).
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HBox extends Box {

    // Marker interface, to prevent use of inappropriate predefined strategies.
    public static interface VerticalPositionStrategy extends PositionStrategy {
    }

    // predefined strategies that provide common forms of vertical alignment
    public static final VerticalPositionStrategy CENTER_ALIGNED = new CenterAligned();
    public static final VerticalPositionStrategy TOP_ALIGNED = new TopAligned();
    public static final VerticalPositionStrategy BOTTOM_ALIGNED = new BottomAligned();

    // defaults
    private static final double DEFAULT_X_SPACING = 10;
    private static final VerticalPositionStrategy DEFAULT_POSITION_STRATEGY = CENTER_ALIGNED;

    // Creates a VBox with default spacing and alignment.
    public HBox( PNode... children ) {
        this( 10, children );
    }

    public HBox( double spacing, PNode... children ) {
        this( spacing, DEFAULT_POSITION_STRATEGY, children );
    }

    public HBox( VerticalPositionStrategy positionStrategy, PNode... children ) {
        this( DEFAULT_X_SPACING, positionStrategy, children );
    }

    // Creates a VBox with default alignment.
    public HBox( double spacing, VerticalPositionStrategy positionStrategy, PNode... children ) {
        super( spacing,
               // For handling alignment, the relevant dimensions is the height of the tallest child.
               new Function1<PBounds, Double>() {
                   public Double apply( PBounds bounds ) {
                       return bounds.getHeight();
                   }
               },
               // For horizontal placement, the relevant dimension is a child's width.
               new Function1<PBounds, Double>() {
                   public Double apply( PBounds bounds ) {
                       return bounds.getWidth();
                   }
               },
               positionStrategy,
               children
        );
    }

    // Vertically centers a node in a vertical space.
    private static class CenterAligned implements VerticalPositionStrategy {
        public Point2D getRelativePosition( PNode node, double maxHeight, double x ) {
            return new Point2D.Double( x, maxHeight / 2 - node.getFullBounds().getHeight() / 2 );
        }
    }

    // Top aligns a node in a vertical space.
    private static class TopAligned implements VerticalPositionStrategy {
        public Point2D getRelativePosition( PNode node, double maxHeight, double x ) {
            return new Point2D.Double( x, 0 );
        }
    }

    // Bottom aligns a node in a vertical space.
    private static class BottomAligned implements VerticalPositionStrategy {
        public Point2D getRelativePosition( PNode node, double maxHeight, double x ) {
            return new Point2D.Double( x, maxHeight - node.getFullBounds().getHeight() );
        }
    }

    //Test
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                addScreenChild( new ControlPanelNode( new HBox( 5, BOTTOM_ALIGNED ) {{
                    addChild( new PText( "Testing" ) );
                    addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 50 ) ) );
                    addChild( new PhetPPath( new Ellipse2D.Double( 0, 0, 75, 75 ) ) );
                    addChild( new PhetPPath( new Ellipse2D.Double( -100, -100, 100, 100 ) ) );
                }} ) );
            }} );
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}