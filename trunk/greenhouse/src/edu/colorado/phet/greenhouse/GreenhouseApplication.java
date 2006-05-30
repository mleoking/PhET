/**
 * Class: GreenhouseApplication
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.IClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.coreadditions.SplashWindow;
import edu.colorado.phet.coreadditions.clock.StaticClockModel;
import edu.colorado.phet.coreadditions.clock.SwingTimerClock;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class GreenhouseApplication extends PhetApplication {
    // Localization
    public static final String localizedStringsPath = "localization/GreenhouseStrings";

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
        SimStrings.init( args, localizedStringsPath );

        JFrame window = new JFrame();
//        window.setUndecorated( true );
//        // Add component to the window
//        window.getContentPane().add( new JLabel( "Starting up..."), BorderLayout.CENTER );
//
//        // Set initial size
//        window.setSize( 300, 300 );
//
//        // Show the window
//        window.setVisible( true );

        SplashWindow splashWindow = new SplashWindow( window, "Starting up..." );
        splashWindow.setVisible( true );


        // Set the look and feel if we're on Windows and Java 1.4
        if( System.getProperty( "os.name" ).toLowerCase().indexOf( "windows" ) >= 0
            && System.getProperty( "java.version" ).startsWith( "1.4" ) ) {
            try {
                UIManager.setLookAndFeel( new WindowsLookAndFeel() );
            }
            catch( UnsupportedLookAndFeelException e ) {
                e.printStackTrace();
            }
        }


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
                                             new SwingTimerClock( new StaticClockModel( 10, 20 ) ) );


        Color background = GreenhouseConfig.PANEL_BACKGROUND_COLOR;
        Color foreground = Color.black;
        UIManager.put( "Panel.background", background );
        UIManager.put( "MenuBar.background", background );
        UIManager.put( "TabbedPane.background", background );
        UIManager.put( "Menu.background", background );
        UIManager.put( "Slider.background", background );
        UIManager.put( "RadioButton.background", background );
        UIManager.put( "RadioButton.foreground", foreground );
        UIManager.put( "CheckBox.background", background );
        UIManager.put( "CheckBox.foreground", foreground );
        UIManager.put( "Label.foreground", foreground );
        UIManager.put( "TitledBorder.titleColor", foreground );

        UIManager.put( "TabbedPane.font", new FontUIResource(  "Lucidasans", Font.BOLD, 18 ));


        SwingUtilities.updateComponentTreeUI( s_application.getApplicationView().getPhetFrame() );


        splashWindow.setVisible( false );
        s_application.startApplication( greenhouseModule );

    }
}
