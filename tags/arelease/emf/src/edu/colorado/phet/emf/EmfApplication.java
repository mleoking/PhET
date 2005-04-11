/**
 * Class: EmfApplication
 * Package: edu.colorado.phet.emf
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.coreadditions.ClientPhetLookAndFeel;
import edu.colorado.phet.coreadditions.LecturePhetLookAndFeel;
import edu.colorado.phet.coreadditions.PhetLookAndFeel;
import edu.colorado.phet.coreadditions.clock.StaticClockModel;
import edu.colorado.phet.coreadditions.clock.SwingTimerClock;
import edu.colorado.phet.coreadditions.components.PhetFrame;

import java.util.logging.Logger;

public class EmfApplication {

    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 10;

    public static void main( String[] args ) {

        // Get a logger; the logger is automatically created if
        // it doesn't already exist
        Logger logger = Logger.getLogger( "edu.colorado.phet.PhetLogger" );

        // Web Start doesn't seem to let you specify a logging level. It
        // just logs everything.
//        ConsoleHandler logHandler = new ConsoleHandler();
//        logHandler.setLevel( Level.INFO );
//        logger.setLevel( Level.INFO );
//        logger.addHandler( logHandler );

        // Log a few message at different severity levels
        PhetLookAndFeel lookAndFeel = new ClientPhetLookAndFeel();
        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-p" ) ) {
                    lookAndFeel = new LecturePhetLookAndFeel();
                }
            }
        }

//        GuidedInquiry gi = null;
//        Script script = null;

        Module antennaModule = new EmfModule();
//        Module antennaModule = new AntennaModule();
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                "Electro-magnetic Fields", "yada-yada", ".01",
                1024, 768 );
        PhetApplication application = new PhetApplication( appDescriptor, antennaModule,
//                                                           new ThreadedClock( new DynamicClockModel( 1, 20 ), ThreadPriority.NORMAL ) );fie
                                                           new SwingTimerClock( new StaticClockModel( 1, 20 ) ) );
//                                                           new SwingTimerClock( new DynamicClockModel( 1, 20 ) ) );
        PhetFrame frame = application.getApplicationView().getPhetFrame();
        frame.setDefaultLookAndFeelDecorated( true );
        frame.setIconImage( lookAndFeel.getSmallIconImage() );

        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-gi" ) ) {
                    try {
                        String giUrl = args[i + 1];
                        logger.info( "Loading gi: " + giUrl );
//                        GILoader giLoader = new GILoader();
//                        gi = giLoader.loadGI( giUrl );
//                        script = new Script( gi, application );
                    }
                    catch( Exception e ) {
                        logger.severe( "Error loading and instantiating gi" );
                    }
                }
            }
        }

        Runtime.getRuntime().gc();
        application.startApplication( antennaModule );

//        if( gi != null ) {
//            LaunchGuidedInquiryCmd lgiCmd = new LaunchGuidedInquiryCmd( application, script );
//            lgiCmd.doIt();
//        }
    }

}
