// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Displays a 3D molecule structure using Jmol.
 */
public class JmolPanel extends JPanel {

    private JmolViewer viewer = null;

    //TODO standardize loadingString by calling PhetCommonResources.getString internally?
    public JmolPanel( final Molecule molecule, final String loadingString ) {
        super( new GridBagLayout() );

        // loading text, centered
        final JLabel loadingText = new JLabel( loadingString ) {{
            setFont( new PhetFont( 20 ) );
            setForeground( Color.WHITE );
        }};
        add( loadingText, new GridBagConstraints() );

        // the panel that will hold Jmol. NOT hooked up at the start, so that JmolViewer.allocateViewer won't mess up our "Loading" text
        final JPanel jmolPanel = new JPanel() {
            @Override public void paint( Graphics g ) {
                // copied from Jmol's Integration.java
                Dimension currentSize = new Dimension();
                getSize( currentSize );
                Rectangle clipBounds = new Rectangle();
                g.getClipBounds( clipBounds );
                viewer.renderScreenImage( g, currentSize, clipBounds );
            }
        };

        // Jmol logger, don't dump everything out into the console
        Logger.setLogLevel( Logger.LEVEL_WARN );

        // launch a non-Swing thread, so we can sleep and let the "Loading" message appear first. (Jmol was causing this to not show up quickly)
        new Thread() {
            @Override public void run() {
                // we sleep here so that the JmolViewer's creation (class of Viewer) doesn't impair our display of the "Loading" message.
                try {
                    Thread.sleep( 50 );
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                // create the 3D viewer.
                final JmolViewer viewer = JmolViewer.allocateViewer( jmolPanel, new SmarterJmolAdapter(), null, null, null, "-applet", null );
                viewer.setBooleanProperty( "antialiasDisplay", true );
                viewer.setBooleanProperty( "autoBond", false );

                // read the molecule data
                String errorString = viewer.openStringInline( molecule.getData() );
                if ( errorString != null ) {
                    //TODO unacceptable error handling for production code
                    throw new RuntimeException( "Jmol problem: " + errorString );
                }

                // adjust the visible colors to our model colors
                molecule.fixJmolColors( viewer );

                // store reference to the viewer AFTER we have processed the molecule data, so that the "Loading" text shows up until the molecule is loaded
                JmolPanel.this.viewer = viewer;

                // set default options, and start the spinning. We need the viewer instance to be set BEFORE these are called
                doScript( "unbind \"_popupMenu\"" ); // hide the right-click popup menu
                doScript( "frank off" ); // hide the "Jmol" watermark in the lower-right corner
                setSpaceFill(); //TODO should this be the default?

                // synchronously wait on this last one, so we don't show the molecule state in the default "ball-and-stick" view
                viewer.scriptWait( "spin on" ); //TODO should this be the default?

                // wait until the viewer isn't executing scripts. if there is a catastrophic failure here, it's isolated in this thread
                while ( viewer.isScriptExecuting() ) {
                    try {
                        System.out.println( "Waiting..." );
                        Thread.sleep( 50 );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }

                // hide the "loading" text and make our Jmol visible
                try {
                    SwingUtilities.invokeAndWait( new Runnable() {
                        public void run() {
                            // swap out our loading text with a Jmol panel
                            remove( loadingText );
                            setLayout( new GridLayout( 1, 1 ) );
                            add( jmolPanel );

                            // force this container to realize we changed its child, and thus paint properly from now on
                            validate();

                            repaint();
                        }
                    } );
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        }.start();

        //TODO this needs to use the same color as the viewer, see "background" script command
        setBackground( Color.BLACK ); // set this so that "loading" appears on the same background color as the molecule

        setPreferredSize( new Dimension( 400, 400 ) ); //TODO this should be configurable

        repaint();
        paintImmediately( getBounds() );
    }

    //TODO this needs to be public or protected, but it should block until viewer exists
    // Executes a Jmol script. Script syntax is described at http://jmol.sourceforge.net/docs
    private void doScript( String script ) {
        viewer.script( script );
    }

    //TODO calling this before viewer exists will cause an exception
    public void setSpaceFill() {
        doScript( "wireframe off; spacefill 60%" );
    }

    //TODO calling this before viewer exists will cause an exception
    public void setBallAndStick() {
        doScript( "wireframe 0.2; spacefill 25%" );
    }
}