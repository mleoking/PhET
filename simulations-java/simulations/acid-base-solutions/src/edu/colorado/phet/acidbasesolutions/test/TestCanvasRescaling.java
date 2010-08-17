package edu.colorado.phet.acidbasesolutions.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Tests a general layout problem I'm having with the sim.
 * As the main frame is made less tall, the play area initially doesn't rescale.
 * This causes the reaction equation to be clipped.
 * This happens when FRAME_SIZE is less than CANVAS_RENDERING_SIZE,
 * and appears to be a problem with PhetPCanvas' RenderingSizeStrategy.
 * The problem goes away if CANVAS_RENDERING_SIZE is square (eg 1024x1024).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCanvasRescaling extends JFrame {

    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    private static final Dimension FRAME_SIZE = new Dimension( 700, 600 );
    
    private static class TestCanvas extends PhetPCanvas {
        
        public TestCanvas( Dimension renderingSize ) {
            super( renderingSize );
            
            PNode rootNode = new PNode();
            addWorldChild( rootNode );
            
            PPath redBoxNode = new PPath( new Rectangle2D.Double( 0, 0, 700, 650 ) );
            redBoxNode.setPaint( Color.RED );
            rootNode.addChild( redBoxNode );
            
            PPath blueBoxNode = new PPath( new Rectangle2D.Double( 0, 0, 700, 100 ) );
            blueBoxNode.setPaint( Color.blue );
            rootNode.addChild( blueBoxNode );
            
            // layout
            redBoxNode.setOffset( 100, 50 );
            blueBoxNode.setOffset( 100, redBoxNode.getFullBoundsReference().getMaxY() + 20 );
        }
    }
    
    public TestCanvasRescaling() {
        super( TestCanvasRescaling.class.getName() );
        TestCanvas canvas = new TestCanvas( CANVAS_RENDERING_SIZE );
        setContentPane( canvas );
        setPreferredSize( FRAME_SIZE );
        pack();
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestCanvasRescaling();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
