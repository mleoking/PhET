package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_movingman.application.ApplicationModel;
import edu.colorado.phet.common_movingman.application.Module;
import edu.colorado.phet.common_movingman.application.PhetApplication;
import edu.colorado.phet.common_movingman.model.clock.SwingTimerClock;
import edu.colorado.phet.common_movingman.view.PhetFrame;
import edu.colorado.phet.common_movingman.view.PhetLookAndFeel;
import edu.colorado.phet.common_movingman.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common_movingman.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 3, 2006
 * Time: 12:25:52 PM
 */

public class MovingManApplication {

    private static boolean addJEP = true;

    // Localization
    public static final String localizedStringsPath = "moving-man/localization/moving-man-strings";

    public static void main( final String[] args ) throws Exception {
        //Putting in swing thread avoids concurrentmodification exception in GraphicLayerSet.
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    runMain( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private static void runMain( String[] args ) throws IOException {
        PhetLookAndFeel plaf = new PhetLookAndFeel();
        plaf.setInsets( new Insets( 1, 1, 1, 1 ) );
        plaf.apply();
        PhetLookAndFeel.setLookAndFeel();

        SimStrings.getInstance().addStrings( localizedStringsPath );

        SwingTimerClock clock = new SwingTimerClock( 1, 30, false );
        FrameSetup setup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 800 ) );
        String version = PhetApplicationConfig.getVersion( "moving-man" ).formatForTitleBar();
        ApplicationModel desc = new ApplicationModel( SimStrings.get( "MovingManApplication.title" ) + " (" + version + ")",
                                                      SimStrings.get( "MovingManApplication.description" ),
                                                      version, setup );
        desc.setName( "movingman" );
        PhetApplication tpa = new PhetApplication( args, desc.getWindowTitle(), desc.getDescription(), desc.getVersion(), clock, false, setup );
        PhetFrame frame = tpa.getPhetFrame();
        final MovingManModule m = new MovingManModule( clock );

        tpa.setModules( new Module[]{m} );

        m.setFrame( frame );
        if( m.getControlPanel() != null ) {
        }
        if( addJEP ) {
            MovingManModule.addMiscMenu( m );
        }

        tpa.startApplication();
        m.getTimeModel().getRecordMode().initialize();

        m.setInited( true );
        m.relayout();
        m.setSmoothingSmooth();
        RepaintDebugGraphic.enable( m.getApparatusPanel(), clock );
    }
}
