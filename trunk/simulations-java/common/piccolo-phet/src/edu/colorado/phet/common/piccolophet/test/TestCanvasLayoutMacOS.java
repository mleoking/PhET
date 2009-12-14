package edu.colorado.phet.common.piccolophet.test;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Demonstrates the problem reported in Unfuddle #2015.
 * On Mac OS 10.6.2 with Java 1.6.0_17, the scenegraph's layout visibly changes when the canvas becomes visible.
 * All of the nodes are briefly visible with offsets (x,y)=(0,0), then they jump to their desired locations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCanvasLayoutMacOS extends JFrame {
    
    public TestCanvasLayoutMacOS() {
        super( TestCanvasLayoutMacOS.class.getName() );
        setPreferredSize( new Dimension( 600, 400 ) );
        setContentPane( new TestCanvas() );
        pack();
    }
    
    private class TestCanvas extends PhetPCanvas {
        
        private final PText osTextNode, javaTextNode;
        
        public TestCanvas() {
            super();
            
            // OS version
            osTextNode = new PText( System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) );
            osTextNode.setFont( new PhetFont( 40 ) );
            getLayer().addChild( osTextNode );
            
            // Java version
            javaTextNode = new PText( "Java " + System.getProperty( "java.version" ) );
            javaTextNode.setFont( new PhetFont(  40 ) );
            getLayer().addChild( javaTextNode );
            
            updateLayout();
        }
        
        protected void updateLayout() {
            Dimension2D worldSize = getWorldSize();
            //WORKAROUND: Removing this conditional makes the problem go away.
            if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
                osTextNode.setOffset( 100, 100 );
                javaTextNode.setOffset( osTextNode.getXOffset(), osTextNode.getFullBoundsReference().getMaxY() + 10 );
            }
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestCanvasLayoutMacOS();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
