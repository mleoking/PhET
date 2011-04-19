package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import javax.swing.*;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;

public class JmolPanel extends JPanel {
    private JmolViewer viewer;

    public JmolPanel( CompleteMolecule molecule ) {
        viewer = JmolViewer.allocateViewer( this, new SmarterJmolAdapter(), null, null, null, null, null );

        setPreferredSize( new Dimension( 400, 400 ) );

        assert ( molecule.hasCmlData() );

        String errorString = viewer.openStringInline( molecule.getCmlData() );
        if ( errorString != null ) {
            throw new RuntimeException( "Jmol problem: " + errorString );
        }

        //viewer.script( "wireframe off; spacefill on;" ); // space fill
        //viewer.script( "wireframe 0.2; spacefill 25%" ); // ball and stick
        //viewer.script( "wireframe off; spacefill 25%" ); // no bonds
        viewer.script( "wireframe off; spacefill 50%" ); // no bonds
    }

    @Override
    public void paint( Graphics g ) {
        Dimension currentSize = new Dimension();
        getSize( currentSize );
        Rectangle clipBounds = new Rectangle();
        g.getClipBounds( clipBounds );
        viewer.renderScreenImage( g, currentSize, clipBounds );
    }

    public JmolViewer getViewer() {
        return viewer;
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
