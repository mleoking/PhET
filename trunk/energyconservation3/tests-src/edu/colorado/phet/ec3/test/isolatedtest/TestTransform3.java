package edu.colorado.phet.ec3.test.isolatedtest;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * Test case for getting coordinate systems correct.
 *
 * @author Sam Reid
 */
public class TestTransform3 {
    private JFrame frame;
    private static boolean invertY = false;

    public TestTransform3() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final PCanvas pCanvas = new PCanvas();
        final WorldNode world = new WorldNode( pCanvas, 10, 10 );
        pCanvas.getLayer().addChild( world );
        /**
         * Add some nodes so we can see the coordinate system.
         */
        for( int i = 0; i <= 10; i++ ) {
            for( int j = 0; j <= 10; j++ ) {
                PPath child = new PPath( new Rectangle2D.Double( 0, 0, 0.1, 0.1 ) );
                child.setOffset( i, j );
                child.setStroke( null );
                child.setPaint( Color.blue );
                world.addChild( child );

                PText text = new PText( "" + i + ", " + j );
                ModelNode modelNode = new ModelNode( text, 0.5 );
                modelNode.setOffset( i, j );
                world.addChild( modelNode );
            }
        }
        /**Draw a rectangle in world coordinates*/
        PPath path = new PPath( new Rectangle2D.Double( 1, 2, 3, 4 ) );
        path.setStroke( new BasicStroke( 0.02f ) );
        world.addChild( path );

        PText screenLabel = new PText( "Screen Label" );
        pCanvas.getLayer().addChild( screenLabel );
        pCanvas.setPanEventHandler( null );
        pCanvas.setZoomEventHandler( null );

        frame.setContentPane( pCanvas );
    }

    public static void main( String[] args ) {
        new TestTransform3().start();
    }

    private void start() {
        frame.setVisible( true );
    }

    /**
     * Node that is supposed to maintain aspect ratio when the screen resizes.
     */
    static class WorldNode extends PNode {
        PCanvas pCanvas;
        private double minWidth;
        private double minHeight;


        public WorldNode( final PCanvas pCanvas, final double minWidth, final double minHeight ) {
            this.pCanvas = pCanvas;
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            pCanvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    //choose a scale based on aspect ratio.
                    updateScale();
                }
            } );
            updateScale();
        }

        public Dimension2D getMinDimension() {
            return new PDimension( minWidth, minHeight );
        }

        public void setMinDimension( double minWidth, double minHeight ) {
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            updateScale();
        }

        protected void updateScale() {
            double minSX = pCanvas.getWidth() / getMinDimension().getWidth();
            double minSY = pCanvas.getHeight() / getMinDimension().getHeight();
            double scale = Math.min( minSX, minSY );
            System.out.println( "scale = " + scale );
            if( scale > 0 ) {
                AffineTransform t = getTransformReference( true );

                if( invertY ) {
                    t.setTransform( scale, t.getShearY(), t.getShearX(), -scale, t.getTranslateX(), t.getTranslateY() + 600 );
                }
                else {
                    t.setTransform( scale, t.getShearY(), t.getShearX(), scale, t.getTranslateX(), t.getTranslateY() );
                }
            }
        }
    }

    /**
     * Utility class for maintaining aspect ratio for a node while setting its width.
     */
    static class ModelNode extends PhetPNode {
        private PNode node;

        public ModelNode( PNode node ) {
            super( node );
            this.node = node;
        }

        public ModelNode( PNode node, double width ) {
            this( node );
            setModelWidth( width );
        }

        //this setter maintains aspect ratio of the underlying node. (as opposed to setWidth())
        public void setModelWidth( double width ) {
            setScale( width / node.getFullBounds().getWidth() );
        }
    }
}
