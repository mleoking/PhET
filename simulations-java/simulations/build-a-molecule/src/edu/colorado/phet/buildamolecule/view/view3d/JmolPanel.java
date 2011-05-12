package edu.colorado.phet.buildamolecule.view.view3d;

import java.awt.*;

import javax.swing.*;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Displays a 3D molecule structure that can be rotated
 */
public class JmolPanel extends JPanel {
    private JmolViewer viewer = null;

    public JmolPanel( final CompleteMolecule molecule ) {

        // create the 3D view after we have shown the "loading" text and dialog
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // create the 3D view
                JmolViewer viewer = JmolViewer.allocateViewer( JmolPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );

                String errorString = viewer.openStringInline( molecule.getCmlData() );
                if ( errorString != null ) {
                    throw new RuntimeException( "Jmol problem: " + errorString );
                }

                // set the visible colors to our model colors
                molecule.fixJmolColors( viewer );

                // store reference to this AFTER we have processed the molecule data, so that the "Loading" text shows up until the molecule is loaded
                JmolPanel.this.viewer = viewer;

                // set default options, and start the spinning. We need the viewer instance to be set BEFORE these are called
                setSpaceFill();
                viewer.script( "spin on;" );

                repaint();
            }
        } );

        setBackground( Color.BLACK );

        setPreferredSize( new Dimension( 400, 400 ) );
    }

    public void setSpaceFill() {
        viewer.script( "wireframe off; spacefill 60%" );
    }

    public void setBallAndStick() {
        viewer.script( "wireframe 0.2; spacefill 25%" );
    }

    @Override
    public void paint( Graphics g ) {
        // Jmol's canonical example of embedding in other Java is to override the paint method, so we do it here

        if ( viewer == null ) {
            // if we have no viewer yet, we show the loading text on a black background
            super.paint( g );

            // create a piccolo node (helpful for centering and styling)
            PText text = new PText( BuildAMoleculeStrings.JMOL_3D_LOADING ) {{
                setTextPaint( Color.WHITE );
                setFont( new PhetFont( 20 ) );

                // center in the panel
                setOffset( ( JmolPanel.this.getWidth() - getFullBounds().getWidth() ) / 2, ( JmolPanel.this.getHeight() - getFullBounds().getHeight() ) / 2 );
            }};

            // paint the piccolo node onto the panel
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

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Hello" ) {{
            setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            setSize( 410, 410 );
        }};
        Container contentPane = frame.getContentPane();
        JmolPanel jmolPanel = new JmolPanel( MoleculeList.H2O );

        contentPane.add( jmolPanel );

        frame.setVisible( true );
    }
}
