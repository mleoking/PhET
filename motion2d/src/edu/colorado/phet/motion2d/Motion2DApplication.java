package edu.colorado.phet.motion2d;

//import phet.utils.ExitOnClose;

//import edu.colorado.phet.common.view.util.GraphicsUtil;
//import edu.colorado.phet.common.view.plaf.PlafUtil;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jan 17, 2003
 * Time: 7:50:58 PM
 * To change this template use Options | File Templates.
 */
public class Motion2DApplication {
    // Localization
    public static final String version = "1.02";

    public static final String localizedStringsPath = "localization/Motion2dStrings";

    public static void main( String[] args ) {
        SimStrings.init( args, localizedStringsPath );
        new Motion2DLookAndFeel().initLookAndFeel();

        Motion2DApplet ja = new Motion2DApplet();
        ja.init();

        JFrame f = new JFrame( SimStrings.get( "Motion2dApplication.title" ) + " (" + version + ")" );

        f.setContentPane( ja );

        f.setSize( 800, 500 );
        centerFrameOnScreen( f );

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.repaint();
        SwingUtilities.invokeLater( new Repaint( ja ) );

        f.setVisible( true );
    }

    private static void centerFrameOnScreen( JFrame f ) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int dw = size.width - f.getWidth();
        int dh = size.height - f.getHeight();

        f.setBounds( dw / 2, dh / 2, f.getWidth(), f.getHeight() );
    }

    static final class Repaint implements Runnable {
        Component c;

        public Repaint( Component c ) {
            this.c = c;
        }

        public void run() {
            c.repaint();
            c.validate();
        }

    }
}
