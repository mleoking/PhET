package edu.colorado.phet.common.piccolophet.test;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Demonstrates the problem reported in Unfuddle #2015.
 * On Mac OS 10.6.2 with Java 1.6.0_17, the scenegraph's layout visibly changes when the canvas becomes visible.
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
        
        private final PNode rootNode;
        private final PText osTextNode, javaTextNode;
        
        public TestCanvas() {
            super();
            
            rootNode = new PNode();
            getLayer().addChild( rootNode );
            
            // OS version
            osTextNode = new PText( System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) );
            osTextNode.setFont( new PhetFont( 40 ) );
            rootNode.addChild( osTextNode );
            
            // Java version
            javaTextNode = new PText( "Java " + System.getProperty( "java.version" ) );
            javaTextNode.setFont( new PhetFont(  40 ) );
            rootNode.addChild( javaTextNode );
            
            // static layout, relative to rootNode
            osTextNode.setOffset( 0, 0 );
            javaTextNode.setOffset( osTextNode.getXOffset(), osTextNode.getFullBoundsReference().getMaxY() + 10 );
            
            updateLayout();
        }
        
        protected void updateLayout() {
            Dimension2D worldSize = getWorldSize();
            if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
                // center rootNode in the canvas
                double x = ( worldSize.getWidth() - rootNode.getFullBoundsReference().getWidth() ) / 2;
                double y = ( worldSize.getHeight() - rootNode.getFullBoundsReference().getHeight() ) / 2;;
                rootNode.setOffset( x, y );
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
