// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.test;

import java.awt.*;

import javax.swing.*;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Tests integration of Jmol with a Piccolo canvas.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestJmolCanvas extends PhetPCanvas {

    public TestJmolCanvas() {
        setPreferredSize( new Dimension( 1024, 768 ) );
        JmolPanel jmolPanel = new JmolPanel();
        PSwing jmolPSwing = new PSwing( jmolPanel );
        getLayer().addChild( jmolPSwing );
        jmolPSwing.setOffset( 100, 100 );
    }

    private static class JmolPanel extends JPanel {

        private JmolViewer viewer = null;
        private final String loadingString;

        public JmolPanel() {

            this.loadingString = "Loading Jmol...";

            // create the 3D view after we have shown the "loading" text and dialog
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    // don't dump everything out into the console
                    Logger.setLogLevel( Logger.LEVEL_WARN );

                    // create the 3D view
                    JmolViewer viewer = JmolViewer.allocateViewer( JmolPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );
                    viewer.setBooleanProperty( "antialiasDisplay", true );
                    viewer.setBooleanProperty( "autoBond", false );

//                    String errorString = viewer.openStringInline( molecule.getCmlData() );
//                    if ( errorString != null ) {
//                        throw new RuntimeException( "Jmol problem: " + errorString );
//                    }
//
//                    // set the visible colors to our model colors
//                    molecule.fixJmolColors( viewer );

                    // store reference to this AFTER we have processed the molecule data, so that the "Loading" text shows up until the molecule is loaded
                    JmolPanel.this.viewer = viewer;

                    // set default options, and start the spinning. We need the viewer instance to be set BEFORE these are called
                    viewer.script( "wireframe 0.2; spacefill 25%" );
                    viewer.script( "spin on;" );

                    repaint();
                }
            } );

            setPreferredSize( new Dimension( 400, 400 ) );//XXX how to determine this dynamically?
            setBackground( Color.BLACK );//XXX this doesn't seem to determine the Jmol background
        }

        @Override public void paint( Graphics g ) {
            // Jmol's canonical example of embedding in other Java is to override the paint method, so we do it here

            if ( viewer == null ) {
                // if we have no viewer yet, we show the loading text on a black background
                super.paint( g );

                // create a piccolo node (helpful for centering and styling)
                PText text = new PText( loadingString ) {{
                    setTextPaint( Color.WHITE ); //XXX this should depend on the background color
                    setFont( new PhetFont( 20 ) );

                    // center in the panel
                    setOffset( ( getWidth() - getFullBounds().getWidth() ) / 2, ( getHeight() - getFullBounds().getHeight() ) / 2 );
                }};

                // paint the piccolo node onto the panel
                text.fullPaint( new PPaintContext( (Graphics2D) g ) );
            }
            else {
                // copied from Jmol's Integration.java
                Dimension currentSize = new Dimension();
                getSize( currentSize ); // stores size in currentSize
                Rectangle clipBounds = new Rectangle();
                g.getClipBounds( clipBounds );
                viewer.renderScreenImage( g, currentSize, clipBounds );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame() {{
            setContentPane( new TestJmolCanvas() );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
