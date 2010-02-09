package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Full bounds calculations appear to have changed between
 * PhET's SVN snapshot of Piccolo 1.2 (r390) and Piccolo2D 1.3-rc1.
 * <p>
 * With Piccolo 1.2, this example shows PBounds[x=49.5,y=49.5,width=101.0,height=101.0]
 * With Piccolo 1.3-rc1, this example shows PBounds[x=0.0,y=0.0,width=150.5,height=150.5]
 * <p>
 * In Piccolo 1.3, a parent node's origin appears to play a role that it 
 * didn't previously play in determining the full bounds.
 * This is a significant difference that has implications for layout code
 * that relies on full bounds.
 * <p>
 * See PhET Unfuddle #2157, Piccolo Issue XXX.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugFullBounds extends JFrame {
    
    public DebugFullBounds() {
        setSize( new Dimension( 400, 300 ) );
        
        // canvas
        PCanvas canvas = new PCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );
        
        // a composite node at (0,0)
        PNode compositeNode = new MyComposite();
        compositeNode.setOffset( 0, 0 );
        canvas.getLayer().addChild( compositeNode );
        
        // display the bounds of the composite node
        PText parentBoundsNode = new PText( compositeNode.getFullBoundsReference().toString() );
        parentBoundsNode.setOffset( 20, compositeNode.getFullBoundsReference().getMaxY() + 20 );
        canvas.getLayer().addChild( parentBoundsNode );
    }
    
    /*
     * A composite node whose child is offset from the container's origin.
     * In Piccolo 1.3, this offset is added to the composite node's height.
     */
    private static class MyComposite extends PComposite {
        public MyComposite() {
            PPath rectangleNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
            rectangleNode.setOffset( 50, 50 );
            addChild( rectangleNode );
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DebugFullBounds();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
