/**
 * Class: GreenhouseApplication
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.IClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;
import edu.colorado.phet.common.view.plaf.PlafUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.coreadditions.clock.StaticClockModel;
import edu.colorado.phet.coreadditions.clock.SwingTimerClock;
import edu.colorado.phet.coreadditions.components.PhetFrame;

import java.util.Locale;
import javax.swing.*;

public class GreenhouseApplication extends PhetApplication {
    // Localization
    public static final String localizedStringsPath = "localization/GreenHouseStrings";

    private static PhetApplication s_application;

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, Module module, IClock iClock ) {
        super( applicationDescriptor, module, iClock );
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, Module module ) {
        super( applicationDescriptor, module );
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, Module[] modules, IClock iClock ) {
        super( applicationDescriptor, modules, iClock );
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, ApparatusPanelContainerFactory apparatusPanelContainerFactory, Module[] modules, IClock iClock ) {
        super( applicationDescriptor, apparatusPanelContainerFactory, modules, iClock );
    }

    public static void main( String[] args ) {

        // Log a few message at different severity levels
//        PhetLookAndFeel lookAndFeel = new ClientPhetLookAndFeel();

//        PhetLookAndFeel lookAndFeel = new ClientPhetLookAndFeel( new PhetLookAndFeelSpec() {
//            public Color background = new Color( 220, 250, 220 );
//            public Color buttonBackground = new Color( 210, 200, 250 );
//            public Color controlTextColor = new Color( 20, 0, 80 );
//        } );

//        if( args.length > 0 ) {
//            for( int i = 0; i < args.length; i++ ) {
//                if( args[i].toLowerCase().equals( "-p" ) ) {
//                    lookAndFeel = new LecturePhetLookAndFeel();
//                }
//            }
//        }

        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey )) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            Locale.setDefault( new Locale( locale ));
        }

        SimStrings.setStrings( localizedStringsPath );
        
        Module greenhouseModule = new GreenhouseModule();
        Module greenhouseModule2 = new GlassPaneModule();
        Module[] modules = new Module[]{
            greenhouseModule,
            greenhouseModule2
        };
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                SimStrings.get( "GreenHouseApplication.title" ),
                MessageFormatter.format( SimStrings.get( "GreenHouseApplication.description" ) ),
                SimStrings.get( "GreenHouseApplication.version" ),
                1024, 768 );
        s_application = new PhetApplication( appDescriptor, modules,
//                                             new FixedClock( new StaticClockModel( 10, 50 ), ThreadPriority.MIN ));
                                             new SwingTimerClock( new StaticClockModel( 10, 20 ) ) );
//                                             new SwingTimerClock( new DynamicClockModel( 5, 20 ) ) );
        PhetFrame frame = s_application.getApplicationView().getPhetFrame();
        JMenu plafMenu = new JMenu( SimStrings.get( "GreenhouseApplication.ViewMenuTitle" ) );
        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < items.length; i++ ) {
            JMenuItem item = items[i];
            plafMenu.add( item );
        }
        frame.addMenu( plafMenu );
        frame.setDefaultLookAndFeelDecorated( true );
//        frame.setIconImage( lookAndFeel.getSmallIconImage() );

        s_application.startApplication( greenhouseModule );

    }
}
