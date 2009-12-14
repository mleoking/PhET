package edu.colorado.phet.common.piccolophet.test;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

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
    
    private class TestCanvas extends PCanvas { // subclass PCanvas to rule out issues with PhetPCanvas
        
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
            
            addListeners();
            updateLayout();
        }
        
        /*
         * This is similar to what's done in PhetPCanvas constructor.
         * We add listeners that tell us when to update the scenegraph layout.
         */
        private void addListeners() {

            addComponentListener( new ComponentAdapter() {

                // When the component is resized, update the layout.
                @Override
                public void componentResized( ComponentEvent e ) {
                    updateLayout();
                }

                // When the component is made visible, update the layout.
                @Override
                public void componentShown( ComponentEvent e ) {
                    updateLayout();
                }
            } );

            addAncestorListener( new AncestorListener() {

                /* 
                 * Called when the source or one of its ancestors is make visible either
                 * via setVisible(true) or by its being added to the component hierarchy.
                 */
                public void ancestorAdded( AncestorEvent e ) {
                    updateLayout();
                }

                public void ancestorMoved( AncestorEvent event ) {}

                public void ancestorRemoved( AncestorEvent event ) {}
            } );
        }
        
        protected void updateLayout() {
            PDimension canvasSize = new PDimension( getWidth(), getHeight() );
            if ( canvasSize.getWidth() > 0 && canvasSize.getHeight() > 0 ) {
                // center rootNode in the canvas
                double x = ( canvasSize.getWidth() - rootNode.getFullBoundsReference().getWidth() ) / 2;
                double y = ( canvasSize.getHeight() - rootNode.getFullBoundsReference().getHeight() ) / 2;;
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
