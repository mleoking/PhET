package edu.colorado.phet.acidbasesolutions.test;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Beaker;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Tests a layout problem I'm having with the sim.
 * But, unfortunately, this doesn't reproduce it.
 * <p>
 * The problem:
 * As the main frame is made less tall, the play area initially doesn't rescale,
 * and rescaling doesn't start until the bottom edge of the frame is level with
 * the bottom of the beaker.  This causes the reaction equation to be clipped.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCanvasLayout extends JFrame {

    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    public static final Dimension FRAME_SIZE = new Dimension( 700, 600 );
    public static final PDimension BEAKER_SIZE = new PDimension( 600, 450 );
    
    public TestCanvasLayout() {
        super( TestCanvasLayout.class.getName() );
        TestCanvas canvas = new TestCanvas( CANVAS_RENDERING_SIZE );
        setContentPane( canvas );
        setPreferredSize( FRAME_SIZE );
        pack();
    }
    
    private static class TestCanvas extends PhetPCanvas {
        
        public TestCanvas( Dimension renderingSize ) {
            super( renderingSize );
            
            PNode rootNode = new PNode();
            addWorldChild( rootNode );
            
            AqueousSolution solution = new PureWaterSolution();
            Point2D beakerLocation = new Point2D.Double( ( BEAKER_SIZE.getWidth() / 2 ) + 150, BEAKER_SIZE.getHeight() + 250 ); // bottom center
            Beaker beaker = new Beaker( solution, beakerLocation, true /* visible */, BEAKER_SIZE );
            
            BeakerNode beakerNode = new BeakerNode( beaker );
            rootNode.addChild( beakerNode );
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestCanvasLayout();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
