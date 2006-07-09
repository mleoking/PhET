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

/**
 * General comments, issues:
 * I wrote this using real model coordinates and units. The origin is at the center of the earth, and the positive
 * y direction is up. Unfortunately, this has turned out to cause a host of issues.
 *
 * The snow in the ice age reflects photons, but is not really in the model. Instead I do a rough estimate of where
 * it is in the background image (in the view) and use that.
 */
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

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                JFrame window = new JFrame();
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
                UIManager.put( "TabbedPane.font", new FontUIResource( "Lucidasans", Font.BOLD, 18 ) );

                SwingUtilities.updateComponentTreeUI( s_application.getApplicationView().getPhetFrame() );

                splashWindow.setVisible( false );
                s_application.startApplication( greenhouseModule );

            }
        } );
    }
}
