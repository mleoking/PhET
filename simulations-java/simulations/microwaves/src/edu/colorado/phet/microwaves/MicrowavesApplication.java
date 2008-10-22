/**
 * Class: MicrowaveApplication
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwaves;


import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common_microwaves.application.Module;
import edu.colorado.phet.common_microwaves.view.ApplicationDescriptor;
import edu.colorado.phet.coreadditions_microwaves.clock.DynamicClockModel;
import edu.colorado.phet.coreadditions_microwaves.clock.SwingTimerClock;
import edu.colorado.phet.coreadditions_microwaves.components.PhetFrame;
import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;

public class MicrowavesApplication extends PhetApplication {


    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 10;

    public static edu.colorado.phet.common_microwaves.application.PhetApplication s_application;

    // Localization
    public static final String localizedStringsPath = "microwaves/localization/microwaves-strings";
    //todo: convert to proper use of PhetApplicationConfig for getting version
    private static final String VERSION = new PhetResources( "microwaves" ).getVersion().formatForTitleBar();

    public MicrowavesApplication( PhetApplicationConfig config ) {
        super( config );

        Module oneMoleculesModule = new OneMoleculeModule();
        Module singleLineOfMoleculesModule2 = new SingleLineOfMoleculesModule2();
        Module manyMoleculesModule = new ManyMoleculesModule();
        Module coffeeModule = new CoffeeModule();
        Module[] modules = new Module[]{
                oneMoleculesModule,
                singleLineOfMoleculesModule2,
                manyMoleculesModule,
                coffeeModule
        };
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                MicrowavesResources.getString( "MicrowavesApplication.title" )
                + " ("
                + VERSION
                + ")",
                MessageFormatter.format( MicrowavesResources.getString( "MicrowavesApplication.description" ) ),
                VERSION, new FrameSetup.CenteredWithSize( 1024, 768 ) );
        s_application = new edu.colorado.phet.common_microwaves.application.PhetApplication( appDescriptor, modules, new SwingTimerClock( new DynamicClockModel( 20, 50 ) ) );
        PhetFrame frame = s_application.getApplicationView().getPhetFrame();
//        frame.addMenu( new ViewMenu() );
        frame.addMenu( new MicrowaveModule.ControlMenu() );
//        frame.setIconImage( lookAndFeel.getSmallIconImage() );

//        s_application.startApplication( manyMoleculesModule );
        s_application.startApplication( singleLineOfMoleculesModule2 );
    }

    public void startApplication() {
//        super.startApplication();
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "microwaves", MicrowavesApplication.class );
    }
}
