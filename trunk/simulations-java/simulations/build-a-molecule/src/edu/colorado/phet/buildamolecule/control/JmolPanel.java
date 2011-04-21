package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import javax.swing.*;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;

/**
 * Displays a 3D molecule structure that can be rotated
 */
public class JmolPanel extends JPanel {
    private JmolViewer viewer;

    public JmolPanel( CompleteMolecule molecule ) {
        viewer = JmolViewer.allocateViewer( this, new SmarterJmolAdapter(), null, null, null, null, null );

        setPreferredSize( new Dimension( 400, 400 ) );

        String errorString = viewer.openStringInline( molecule.getCmlData() );
        if ( errorString != null ) {
            throw new RuntimeException( "Jmol problem: " + errorString );
        }

        setSpaceFill();
        viewer.script( "spin on;" );
    }

    public void setSpaceFill() {
        viewer.script( "wireframe off; spacefill 60%" );
    }

    public void setBallAndStick() {
        viewer.script( "wireframe 0.2; spacefill 25%" );
    }

    @Override
    public void paint( Graphics g ) {
        Dimension currentSize = new Dimension();
        getSize( currentSize );
        Rectangle clipBounds = new Rectangle();
        g.getClipBounds( clipBounds );
        viewer.renderScreenImage( g, currentSize, clipBounds );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Hello" ) {{
            setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            setSize( 410, 410 );
        }};
        Container contentPane = frame.getContentPane();
        JmolPanel jmolPanel = new JmolPanel( CompleteMolecule.H2O );

        contentPane.add( jmolPanel );

        frame.setVisible( true );
    }
}
