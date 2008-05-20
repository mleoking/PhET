package edu.colorado.phet.mazegame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Sep 11, 2006
 * Time: 10:53:32 AM
 */

public class MazeGameApplication {
    public static final String version = PhetApplicationConfig.getVersion( "maze-game" ).formatForTitleBar();
    public static final String localizedStringsPath = "maze-game/localization/maze-game-strings";

    private static void centerFrameOnScreen( JFrame f ) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int dw = size.width - f.getWidth();
        int dh = size.height - f.getHeight();

        f.setBounds( dw / 2, dh / 2, f.getWidth(), f.getHeight() );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PhetLookAndFeel().initLookAndFeel();
                SimStrings.getInstance().init( args, localizedStringsPath );

                JFrame f = new JFrame( "Maze Game (" + version + ")" );
                MazeGameApplet mg = new MazeGameApplet();
                f.setContentPane( mg );
                mg.init();
                f.setSize( 700, 500 );
                centerFrameOnScreen( f );
                f.setVisible( true );
                f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            }
        } );
    }

}
