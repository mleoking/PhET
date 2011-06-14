// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * TestSharedLayer tests sharing of a layer between 2 canvases.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSharedLayer {
    
    private static final Dimension FRAME_SIZE = new Dimension( 640, 480 );
    private static final int SQUARE_SIZE = 25;
    private static final int NUMBER_OF_SQUARES = 50;
    
    /* node that draws a square with a random color */
    private static class SquareNode extends PPath {
        
        private static Color getRandomColor() {
            return new Color( (int) ( 255 * Math.random() ), (int) ( 255 * Math.random() ), (int) ( 255 * Math.random() ) );
        }
        
        public SquareNode() {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, SQUARE_SIZE, SQUARE_SIZE ) );
            setPaint( getRandomColor() );
        }
    }
    
    /* layer that contains a collection of squares */
    private static class SquaresLayer extends PLayer {
        
        private static Point2D getRandomPosition() {
            return new Point2D.Double( Math.random() * FRAME_SIZE.width, Math.random() * FRAME_SIZE.height / 2 );
        }
        
        public SquaresLayer() {
            super();
            for ( int i = 0; i < NUMBER_OF_SQUARES; i++ ) {
                PNode node = new SquareNode();
                addChild( node );
                node.setOffset( getRandomPosition() );
            }
        }
    }
    
    /* a specified layer is added to the canvas' camera */
    private static class TestCanvas extends PCanvas {
        public TestCanvas( PLayer layer ) {
            super();
            getCamera().addLayer( layer );
            setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
        }
    }
    
    private static class TestFrame extends JFrame {

        public TestFrame() {
            super();

            // create a layer
            SquaresLayer sharedLayer = new SquaresLayer();

            // use the same layer for both canvases
            PCanvas topCanvas = new TestCanvas( sharedLayer );
            PCanvas bottomCanvas = new TestCanvas( sharedLayer );

            // frame layout
            Box box = new Box( BoxLayout.Y_AXIS );
            box.add( topCanvas );
            box.add( bottomCanvas );
            getContentPane().add( box );
        }
    }
    
    public static final void main( String[] args ) {
        JFrame frame = new TestFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( FRAME_SIZE );
        frame.show();
    }

}
