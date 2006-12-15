/**
 * Class: MicrowaveApplication
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwave;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.coreadditions.ClientPhetLookAndFeel;
import edu.colorado.phet.coreadditions.LecturePhetLookAndFeel;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.coreadditions.PhetLookAndFeel;
import edu.colorado.phet.coreadditions.clock.DynamicClockModel;
import edu.colorado.phet.coreadditions.clock.SwingTimerClock;
import edu.colorado.phet.coreadditions.components.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;

import java.util.logging.Logger;
import java.util.Locale;

public class MicrowaveApplication {


    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 10;

    public static PhetApplication s_application;

    // Localization
    public static final String localizedStringsPath = "localization/MicrowavesStrings";

    public static void main( String[] args ) {
        SimStrings.init( args, localizedStringsPath );

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

//        PhetLookAndFeel lookAndFeel = new ClientPhetLookAndFeel( new PhetLookAndFeelSpec() {
//            public Color background = new Color( 220, 250, 220 );
//            public Color buttonBackground = new Color( 210, 200, 250 );
//            public Color controlTextColor = new Color( 20, 0, 80 );
//        } );

        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-p" ) ) {
                    lookAndFeel = new LecturePhetLookAndFeel();
                }
            }
        }

        Module oneMoleculesModule = new OneMoleculeModule();
        Module twoMoleculesModule = new TwoMoleculesModule();
        Module singleLineOfMoleculesModule = new SingleLineOfMoleculesModuleNoCollisions();
        Module singleLineOfMoleculesModule2 = new SingleLineOfMoleculesModule2();
        Module manyMoleculesModule = new ManyMoleculesModule();
        Module coffeeModule = new CoffeeModule();
        Module[] modules = new Module[]{
//            twoMoleculesModule,
//            singleLineOfMoleculesModule,
                oneMoleculesModule,
                singleLineOfMoleculesModule2,
                manyMoleculesModule,
                coffeeModule
        };
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                new String( SimStrings.get( "MicrowavesApplication.title")
                                            + " ("
                                            + SimStrings.get( "MicrowavesApplication.version" )
                                            + ")" ),
                MessageFormatter.format( SimStrings.get( "MicrowavesApplication.description" ) ),
                SimStrings.get( "MicrowavesApplication.version" ), 1024, 768 );
        s_application = new PhetApplication( appDescriptor, modules,
                                             new SwingTimerClock( new DynamicClockModel( 20, 50 ) ) );
        PhetFrame frame = s_application.getApplicationView().getPhetFrame();
//        frame.addMenu( new ViewMenu() );
        frame.addMenu( new MicrowaveModule.ControlMenu() );
        frame.setDefaultLookAndFeelDecorated( true );
        frame.setIconImage( lookAndFeel.getSmallIconImage() );

//        s_application.startApplication( manyMoleculesModule );
        s_application.startApplication( singleLineOfMoleculesModule2 );

    }

}
