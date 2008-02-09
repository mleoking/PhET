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
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Sep 3, 2006
 * Time: 12:25:52 PM
 */

public class MovingManApplicationORIG {

    private static boolean addJEP = true;

    // Localization
    public static final String localizedStringsPath = "moving-man/localization/moving-man-strings";
    private MovingManModule module;

    public static void main( final String[] args ) throws Exception {
        //Putting in swing thread avoids concurrentmodification exception in GraphicLayerSet.
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    new MovingManApplicationORIG( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }


    public MovingManApplicationORIG( String[] args ) throws IOException {
        PhetLookAndFeel plaf = new PhetLookAndFeel();
        plaf.setInsets( new Insets( 1, 1, 1, 1 ) );
        plaf.apply();
        PhetLookAndFeel.setLookAndFeel();

        SimStrings.getInstance().addStrings( localizedStringsPath );

        SwingTimerClock clock = new SwingTimerClock( 1, 30, false );
        FrameSetup setup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 800 ) );
        String version = PhetApplicationConfig.getVersion( "moving-man" ).formatForTitleBar();
        ApplicationModel desc = new ApplicationModel( SimStrings.get( "moving-man.name" ) + " (" + version + ")",
                                                      SimStrings.get( "moving-man.description" ),
                                                      version, setup );
        desc.setName( "movingman" );
        PhetApplication tpa = new PhetApplication( args, desc.getWindowTitle(), desc.getDescription(), desc.getVersion(), clock, false, setup );
        PhetFrame frame = tpa.getPhetFrame();
        this.module = new MovingManModule( clock );

        tpa.setModules( new Module[]{module} );

        module.setFrame( frame );
        if( module.getControlPanel() != null ) {
        }
        if( addJEP ) {
            MovingManModule.addMiscMenu( module );
        }

        tpa.startApplication();
        module.getTimeModel().getRecordMode().initialize();

        module.setInited( true );
        module.relayout();
        module.setSmoothingSmooth();
        RepaintDebugGraphic.enable( module.getApparatusPanel(), clock );
        if( Arrays.asList( args ).contains( "-position" ) ) {
            getModule().minimizeGraphsExceptPosition();
        }
    }

    public MovingManModule getModule() {
        return module;
    }
}
