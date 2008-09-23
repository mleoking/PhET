package edu.colorado.phet.motion2d;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

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

    public static final String localizedStringsPath = "motion-2d/localization/motion-2d-strings";

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                SimStrings.getInstance().init( args, localizedStringsPath );
                new PhetLookAndFeel().initLookAndFeel();

                Motion2DSimulationPanel ja = new Motion2DSimulationPanel(new ConstantDtClock( 30,1) );
                ja.init();

                JFrame f = new JFrame( SimStrings.getInstance().getString( "Motion2dApplication.title" ) + " (" + PhetApplicationConfig.getVersion( "motion-2d" ).formatForTitleBar() + ")" );

                f.setContentPane( ja );

                f.setSize( 800, 500 );
                centerFrameOnScreen( f );

                f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                f.repaint();
                SwingUtilities.invokeLater( new Repaint( ja ) );

                f.setVisible( true );
            }
        } );
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
