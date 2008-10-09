package geom.jgv.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 9, 2008
 * Time: 1:59:46 PM
 */
public class JGVFrame extends Frame {
    public JGVFrame( String title ) {
        super( title );
        JGVPanel panel = new JGVPanel( this );
        panel.addXMLFile( "cube.xml" );
        add( panel );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        setSize( 800,600 );
    }

    public static void main( String[] args ) {
        new JGVFrame("title").setVisible(true);
    }
}
