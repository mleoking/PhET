/**
 * Class: NuclearPhysicsApplication
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayModule;
import edu.colorado.phet.nuclearphysics.controller.MultipleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.SingleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class NuclearPhysicsApplication extends PhetApplication {

    // Localization
    public static final String localizedStringsPath = "localization/NuclearPhysicsStrings";

    public NuclearPhysicsApplication( ApplicationModel descriptor ) {
        super( descriptor );
    }

    public static void main( String[] args ) {
        SimStrings.setStrings( localizedStringsPath );
//        SimStrings.init( args, localizedStringsPath );

        try {
            UIManager.setLookAndFeel( new NuclearAppLookAndFeel() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        String desc = SimStrings.get( "NuclearPhysicsApplication.description" );
        ApplicationModel appDesc = new ApplicationModel( SimStrings.get( "NuclearPhysicsApplication.title" ),
                                                         desc,
                                                         SimStrings.get( "NuclearPhysicsApplication.version" ),
                                                         new FrameSetup.MaxExtent() );
        // Note: a ThreadedClock here ends up looking balky
        SwingTimerClock clock = new SwingTimerClock( 10, 20, true );
        Module alphaModule = new AlphaDecayModule( clock );
        Module singleNucleusFissionModule = new SingleNucleusFissionModule( clock );
        Module multipleNucleusFissionModule = new MultipleNucleusFissionModule( clock );
        Module controlledReactionModule = new ControlledFissionModule( clock );
        Module[] modules = new Module[]{
//            alphaModule,
//            singleNucleusFissionModule,
            multipleNucleusFissionModule,
            controlledReactionModule
        };
        appDesc.setModules( modules );
                appDesc.setInitialModule( controlledReactionModule );
//        appDesc.setInitialModule( singleNucleusFissionModule );
//                appDesc.setInitialModule( alphaModule );
        appDesc.setClock( clock );
        //        app.startApplication( multipleNucleusFissionModule );
        //        app.startApplication( singleNucleusFissionModule );
        appDesc.setFrameSetup( new FrameSetup.CenteredWithSize( 1024, 768 ) );
        //                appDesc.setFrameSetup( new FrameSetup.Full() );
        //                appDesc.setFrameSetup( new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 768 ) ) );

        NuclearPhysicsApplication app = new NuclearPhysicsApplication( appDesc );
        try {
            app.startApplication();
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }
    }

    private static class NuclearAppLookAndFeel extends LandF {
        static Color backgroundColor = new Color( 60, 80, 60 );
        static Color buttonBackgroundColor = new Color( 100, 120, 60 );
        static Color controlTextColor = new Color( 230, 230, 230 );
        static Font font = new Font( "SansSerif", Font.BOLD, 16 );

        public NuclearAppLookAndFeel() {
            super( backgroundColor, buttonBackgroundColor, controlTextColor, font );
        }
    }

    static private class LandF extends MetalLookAndFeel {
        Color backgroundColor = new Color( 60, 80, 60 );
        Color buttonBackgroundColor = new Color( 60, 60, 100 );
        Color controlTextColor = new Color( 230, 230, 230 );
        Font controlFont = new Font( "SansSerif", Font.BOLD, 22 );
        static String[] controlTypes = new String[]{
            "Menu",
            "MenuItem",
            "RadioButton",
            "Button",
            "CheckBox",
            "Label"
        };

        public LandF( Color backgroundColor, Color buttonBackgroundColor, Color controlTextColor, Font controlFont ) {
            this.backgroundColor = backgroundColor;
            this.buttonBackgroundColor = buttonBackgroundColor;
            this.controlTextColor = controlTextColor;
            this.controlFont = controlFont;
        }

        protected void initComponentDefaults( UIDefaults table ) {
            super.initComponentDefaults( table );
            ArrayList def = new ArrayList();
            ColorUIResource textColor = new ColorUIResource( controlTextColor );
            FontUIResource fuir = new FontUIResource( controlFont );
            for( int i = 0; i < controlTypes.length; i++ ) {
                String controlType = controlTypes[i];
                def.add( controlType + ".foreground" );
                def.add( textColor );
                def.add( controlType + ".font" );
                def.add( fuir );
            }
            ColorUIResource background = new ColorUIResource( backgroundColor );
            ColorUIResource buttonBackground = new ColorUIResource( buttonBackgroundColor );

            Object[] defaults = {
                "Panel.background", background
                , "Menu.background", background
                , "MenuItem.background", background
                , "MenuBar.background", background
                , "Slider.background", background
                , "RadioButton.background", background
                , "CheckBox.background", background
                , "Button.background", buttonBackground
            };
            def.addAll( Arrays.asList( defaults ) );
            table.putDefaults( def.toArray() );

            Font font = (Font)table.get( "Label.font" );
            Color color = (Color)table.get( "Label.foreground" );
            Object[] moreDefaults = {
                "TextField.font", font
                , "Spinner.font", font
                , "FormattedTextField.font", font
                , "TitledBorder.font", font
                , "TitledBorder.titleColor", color
            };
            table.putDefaults( moreDefaults );
        }
    }
}
