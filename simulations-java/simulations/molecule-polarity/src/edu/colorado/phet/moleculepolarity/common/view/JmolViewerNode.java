// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D.Ammonia;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Piccolo node that display a Jmol viewer.
 * Jmol scripting language is documented at http://chemapps.stolaf.edu/jmol/docs
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolViewerNode extends PhetPNode {

    private final ViewerPanel viewerPanel;
    private boolean bondDipolesVisible, molecularDipoleVisible, electrostaticPotentialVisible;

    public JmolViewerNode( Molecule3D molecule, Color background, Dimension size ) {
        viewerPanel = new ViewerPanel( molecule, background, size );
        addChild( new PSwing( viewerPanel ) );
    }

    //TODO consider merging this with jmol-phet JmolPanel
    // Container for Jmol viewer
    private static class ViewerPanel extends JPanel {

        private final JmolViewer viewer;

        public ViewerPanel( final Molecule3D molecule, Color background, Dimension size ) {
            setPreferredSize( size );

            // configure Jmol's logging so we don't spew to the console
            Logger.setLogLevel( Logger.LEVEL_WARN );

            // create the 3D viewer
            viewer = JmolViewer.allocateViewer( ViewerPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );

            // default settings of the viewer, independent of the molecule displayed
            viewer.setBooleanProperty( "antialiasDisplay", true );
            viewer.setBooleanProperty( "autoBond", false );
            doScript( "unbind \"_popupMenu\"" ); // hide the right-click popup menu
            doScript( "frank off" ); // hide the "Jmol" watermark in the lower-right corner
            doScript( MessageFormat.format( "background [{0},{1},{2}]", background.getRed(), background.getGreen(), background.getBlue() ) ); //TODO how to make background transparent?

            setMolecule( molecule );
        }

        // Jmol's canonical example of embedding in other Java is to override the paint method, so we do that here.
        @Override public void paint( Graphics g ) {
            // copied from Jmol's Integration.java
            Dimension currentSize = new Dimension();
            getSize( currentSize ); // stores size in currentSize
            Rectangle clipBounds = new Rectangle();
            g.getClipBounds( clipBounds );
            viewer.renderScreenImage( g, currentSize, clipBounds );
        }

        public void doScript( String script ) {
            viewer.script( script );
        }

        public void setMolecule( Molecule3D molecule ) {
            // load the molecule data
            String errorString = viewer.openStringInline( molecule.getData() );
            if ( errorString != null ) {
                throw new RuntimeException( "Jmol problem: " + errorString ); //TODO improve exception handling
            }
            // adjust colors
            molecule.adjustColors( viewer );
        }
    }

    private void doScript( String script ) {
        viewerPanel.doScript( script );
    }

    public void setMolecule( Molecule3D molecule ) {
        viewerPanel.setMolecule( molecule );
        // these things need to be reset when the viewer loads a new molecule
        setBallAndStick();
        setAtomLabelsVisible( true );
        setBondDipolesVisible( bondDipolesVisible );
        setMolecularDipoleVisible( molecularDipoleVisible );
        setElectrostaticPotentialVisible( electrostaticPotentialVisible );
    }

    private void setBallAndStick() {
        doScript( "wireframe 0.2; spacefill 25%" );
    }

    private void setAtomLabelsVisible( boolean visible ) {
        if ( visible ) {
            doScript( "label on" );
        }
        else {
            doScript( "label off" );
        }
    }

    public void setBondDipolesVisible( boolean visible ) {
        bondDipolesVisible = visible;
        if ( visible ) {
            doScript( "dipole bonds on" );
        }
        else {
            doScript( "dipole bonds off" );
        }
    }

    public void setMolecularDipoleVisible( boolean visible ) {
        molecularDipoleVisible = visible;
        if ( visible ) {
            doScript( "dipole molecular on" );
        }
        else {
            doScript( "dipole molecular off" );
        }
    }

    public void setElectrostaticPotentialVisible( boolean visible ) {
        electrostaticPotentialVisible = visible;
        if ( visible ) {
            doScript( "isosurface resolution 6 solvent map mep translucent" );
        }
        else {
            doScript( "isosurface off" );
        }
    }

    // test
    public static void main( String[] args ) {
        final PhetPCanvas canvas = new PhetPCanvas() {{
            setPreferredSize( new Dimension( 1024, 768 ) );
            setBackground( Color.LIGHT_GRAY );
            JmolViewerNode viewerNode = new JmolViewerNode( new Ammonia(), getBackground(), new Dimension( 400, 400 ) );
            getLayer().addChild( viewerNode );
            viewerNode.setOffset( 100, 100 );
        }};
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
