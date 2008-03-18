/**
 * Class: GreenhouseApplication
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.AWTSplashWindow;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_greenhouse.application.Module;
import edu.colorado.phet.common_greenhouse.application.PhetApplication;
import edu.colorado.phet.common_greenhouse.model.IClock;
import edu.colorado.phet.common_greenhouse.view.ApplicationDescriptor;
import edu.colorado.phet.common_greenhouse.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;
import edu.colorado.phet.coreadditions_greenhouse.MessageFormatter;
import edu.colorado.phet.coreadditions_greenhouse.clock.StaticClockModel;
import edu.colorado.phet.coreadditions_greenhouse.clock.SwingTimerClock;

/**
 * General comments, issues:
 * I wrote this using real model coordinates and units. The origin is at the center of the earth, and the positive
 * y direction is up. Unfortunately, this has turned out to cause a host of issues.
 * <p/>
 * The snow in the ice age reflects photons, but is not really in the model. Instead I do a rough estimate of where
 * it is in the background image (in the view) and use that.
 */
public class GreenhouseApplication extends PhetApplication {
    // Localization
    public static final String localizedStringsPath = "greenhouse/localization/greenhouse-strings";

    private static PhetApplication s_application;
    private static SwingTimerClock clock;
    private static final String VERSION = PhetApplicationConfig.getVersion( "greenhouse" ).formatForTitleBar();

    public static SwingTimerClock getClock() {
        return clock;
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, Module[] modules, IClock iClock ) {
        super( applicationDescriptor, modules, iClock );
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, ApparatusPanelContainerFactory apparatusPanelContainerFactory, Module[] modules, IClock iClock ) {
        super( applicationDescriptor, apparatusPanelContainerFactory, modules, iClock );
    }


    public static void main( String[] args ) {
//        Locale.setDefault( new Locale( "ja" ) );
        SimStrings.getInstance().init( args, localizedStringsPath );
        SimStrings.getInstance().addStrings( "greenhouse/localization/phetcommon-strings" );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                initLookAndFeel();

                JFrame window = new JFrame();
                AWTSplashWindow splashWindow = new AWTSplashWindow( window, SimStrings.get( "GreenHouseApplication.title" ) );
                splashWindow.show();

                BaseGreenhouseModule greenhouseModule = new GreenhouseModule();
                BaseGreenhouseModule greenhouseModule2 = new GlassPaneModule();
                Module[] modules = new Module[]{
                        greenhouseModule,
                        greenhouseModule2
                };
                ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                        new String( SimStrings.get( "GreenHouseApplication.title" )
                                    + " ("
                                    + VERSION
                                    + ")" ),
                        MessageFormatter.format( SimStrings.get( "GreenHouseApplication.description" ) ),
                        VERSION,
                        1024, 768 );
//                clock = new SwingTimerClock( new StaticClockModel( 10, 20 ) );
                clock = new SwingTimerClock( new StaticClockModel( 10, 30 ) );
                s_application = new PhetApplication( appDescriptor, modules, clock );
                s_application.getApplicationView().getPhetFrame().setResizable( false );
                s_application.startApplication( greenhouseModule );
                splashWindow.setVisible( false );
                paintContentImmediately();
                s_application.getApplicationView().getPhetFrame().addWindowFocusListener( new WindowFocusListener() {
                    public void windowGainedFocus( WindowEvent e ) {
                        paintContentImmediately();
                    }

                    public void windowLostFocus( WindowEvent e ) {
                    }
                } );
            }
        } );
    }

    private static void initLookAndFeel() {
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.setBackgroundColor( GreenhouseConfig.PANEL_BACKGROUND_COLOR );
        phetLookAndFeel.setForegroundColor( Color.black );
        phetLookAndFeel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        phetLookAndFeel.setTitledBorderFont( new PhetDefaultFont( Font.PLAIN, 12 ) );
        phetLookAndFeel.initLookAndFeel();
    }

    public static void paintContentImmediately() {
        Container contentPane = s_application.getApplicationView().getPhetFrame().getContentPane();
        if ( contentPane instanceof JComponent ) {
            JComponent jComponent = (JComponent) contentPane;
            jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
        }
    }
}
