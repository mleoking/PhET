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
 * Lays out nodes vertically.
 * Various strategies are provided for common forms of horizontal alignment.
 * The layout doesn't update when children bounds change, layout is only performed when new children are added (sufficient for bending light usage).
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VBox extends Box {

    // Marker interface, to prevent use of inappropriate predefined strategies.
    public static interface HorizontalPositionStrategy extends PositionStrategy {
    }

    // predefined strategies that provide common forms of horizontal alignment
    public static final HorizontalPositionStrategy CENTER_ALIGNED = new CenterAligned();
    public static final HorizontalPositionStrategy LEFT_ALIGNED = new LeftAligned();
    public static final HorizontalPositionStrategy RIGHT_ALIGNED = new RightAligned();

    // defaults
    private static final double DEFAULT_Y_SPACING = 10;
    private static final HorizontalPositionStrategy DEFAULT_POSITION_STRATEGY = CENTER_ALIGNED;

    // Creates a VBox with default spacing and alignment.
    public VBox( PNode... children ) {
        this( DEFAULT_Y_SPACING, children );
    }

    // Creates a VBox with default alignment.
    public VBox( double ySpacing, PNode... children ) {
        this( ySpacing, DEFAULT_POSITION_STRATEGY, children );
    }

    // Creates a VBox with default spacing.
    public VBox( HorizontalPositionStrategy positionStrategy, PNode... children ) {
        this( DEFAULT_Y_SPACING, positionStrategy, children );
    }

    /**
     * Most general constructor.
     *
     * @param ySpacing         vertical space between children
     * @param positionStrategy strategy used to horizontally position a child.
     * @param children         children to be added to the box, in top to bottom order
     */
    public VBox( double ySpacing, HorizontalPositionStrategy positionStrategy, PNode... children ) {
        super( ySpacing,
               // For handling alignment, the relevant dimensions is the width of the widest child.
               new Function1<PBounds, Double>() {
                   public Double apply( PBounds bounds ) {
                       return bounds.getWidth();
                   }
               },
               // For vertical placement, the relevant dimension is a child's height.
               new Function1<PBounds, Double>() {
                   public Double apply( PBounds bounds ) {
                       return bounds.getHeight();
                   }
               },
               positionStrategy,
               children
        );
    }

    // Horizontally centers a node in a horizontal space.
    private static class CenterAligned implements HorizontalPositionStrategy {
        public Point2D getRelativePosition( PNode node, double maxWidth, double y ) {
            return new Point2D.Double( maxWidth / 2 - node.getFullBounds().getWidth() / 2, y );
        }
    }

    // Left aligns a node in a horizontal space.
    private static class LeftAligned implements HorizontalPositionStrategy {
        public Point2D getRelativePosition( PNode node, double maxWidth, double y ) {
            return new Point2D.Double( 0, y );
        }
    }

    // Right aligns a node in a horizontal space.
    private static class RightAligned implements HorizontalPositionStrategy {
        public Point2D getRelativePosition( PNode node, double maxWidth, double y ) {
            return new Point2D.Double( maxWidth - node.getFullBounds().getWidth(), y );
        }
    }

    //Test
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                addScreenChild( new ControlPanelNode( new VBox( 5, RIGHT_ALIGNED ) {{
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