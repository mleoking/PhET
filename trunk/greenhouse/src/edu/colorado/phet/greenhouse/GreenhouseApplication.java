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
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.coreadditions.clock.StaticClockModel;
import edu.colorado.phet.coreadditions.clock.SwingTimerClock;
import edu.colorado.phet.coreadditions.components.PhetFrame;

import javax.swing.*;

public class GreenhouseApplication extends PhetApplication {

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

        Module greenhouseModule = new GreenhouseModule();
        Module greenhouseModule2 = new GlassPaneModule();
        Module[] modules = new Module[]{
            greenhouseModule,
            greenhouseModule2
        };
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                "The Greenhouse Effect",
                MessageFormatter.format( "A simluation for exploring how\ngreenhouse gasses interact with\nthe atmosphere, and sunlight." ), ".01",
                1024, 768 );
        s_application = new PhetApplication( appDescriptor, modules,
//                                             new FixedClock( new StaticClockModel( 10, 50 ), ThreadPriority.MIN ));
                                             new SwingTimerClock( new StaticClockModel( 10, 20 ) ) );
//                                             new SwingTimerClock( new DynamicClockModel( 5, 20 ) ) );
        PhetFrame frame = s_application.getApplicationView().getPhetFrame();
        JMenu plafMenu = new JMenu( "View" );
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
