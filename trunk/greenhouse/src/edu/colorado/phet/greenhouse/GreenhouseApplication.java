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

import javax.swing.*;
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
        PhetFrame frame = s_application.getApplicationView().getPhetFrame();
        JMenu plafMenu = new JMenu( SimStrings.get( "GreenhouseApplication.ViewMenuTitle" ) );
        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < items.length; i++ ) {
            JMenuItem item = items[i];
            plafMenu.add( item );
        }
//        frame.addMenu( plafMenu );
        
        frame.setDefaultLookAndFeelDecorated( true );

        

        Color background = GreenhouseConfig.PANEL_BACKGROUND_COLOR;
//        Color background = new Color( 110, 110, 110 );
        Color foreground = Color.black;
//        Color foreground = Color.white;
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


        SwingUtilities.updateComponentTreeUI( s_application.getApplicationView().getPhetFrame() );
        s_application.startApplication( greenhouseModule );

    }
}
