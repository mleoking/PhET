// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Displays a 3D molecule structure using Jmol.
 */
public class JmolPanel extends JPanel {

    private JmolViewer viewer = null;
    private final String loadingString;

    //TODO standardize loadingString by calling PhetCommonResources.getString internally?
    public JmolPanel( final Molecule molecule, String loadingString ) {
        this.loadingString = loadingString;

        //TODO should "Loading" text be optional?
        // create the 3D view after we have shown the "Loading" text
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // Jmol logger, don't dump everything out into the console
                Logger.setLogLevel( Logger.LEVEL_WARN );

                // create the 3D viewer
                JmolViewer viewer = JmolViewer.allocateViewer( JmolPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );
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
                doScript( "spin on" ); //TODO should this be the default?

                repaint();
            }
        } );

        //TODO this needs to use the same color as the viewer, see "background" script command
        setBackground( Color.BLACK ); // set this so that "loading" appears on the same background color as the molecule

        setPreferredSize( new Dimension( 400, 400 ) ); //TODO this should be configurable
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

    @Override
    public void paint( Graphics g ) {
        // Jmol's canonical example of embedding in other Java is to override the paint method, so we do it here

        if ( viewer == null ) {
            // if we have no viewer yet, we show the "Loading" text
            super.paint( g );

            // create a Piccolo node (helpful for centering and styling)
            PText text = new PText( loadingString ) {{
                setTextPaint( Color.WHITE ); //TODO this depends on background color
                setFont( new PhetFont( 20 ) ); //TODO this should be configurable

                // center in the panel
                setOffset( ( JmolPanel.this.getWidth() - getFullBounds().getWidth() ) / 2, ( JmolPanel.this.getHeight() - getFullBounds().getHeight() ) / 2 );
            }};

            // paint the Piccolo node onto the panel
            text.fullPaint( new PPaintContext( (Graphics2D) g ) );
        }
        else {
            // copied from Jmol's Integration.java
            Dimension currentSize = new Dimension();
            getSize( currentSize );
            Rectangle clipBounds = new Rectangle();
            g.getClipBounds( clipBounds );
            viewer.renderScreenImage( g, currentSize, clipBounds );
        }
    }
}