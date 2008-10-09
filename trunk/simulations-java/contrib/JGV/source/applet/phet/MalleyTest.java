package phet;

import geom.jgv.gui.JGVPanel;

import javax.swing.JFrame;


public class MalleyTest extends JFrame {
    
    public MalleyTest() {
        super( "Malley Test" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        JGVPanel jgvPanel = new JGVPanel( this );
        jgvPanel.addXMLFile( "/tmp/cube.xml" );
    }

    public static void main( String[] args ) {
        new MalleyTest().setVisible( true );
    }
}
