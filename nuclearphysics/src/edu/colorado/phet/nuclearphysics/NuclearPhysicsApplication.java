/**
 * Class: NuclearPhysicsApplication
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.AWTSplashWindow;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayModule;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;
import edu.colorado.phet.nuclearphysics.controller.MultipleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.SingleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;
import edu.colorado.phet.nuclearphysics.util.ClockFactory;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class NuclearPhysicsApplication extends PiccoloPhetApplication {
//public class NuclearPhysicsApplication extends PhetApplication {

    // Localization
    public static final String localizedStringsPath = "localization/NuclearPhysicsStrings";

    /**
     *
     * @param args
     */
    public NuclearPhysicsApplication( String[] args ) {
        super( args, SimStrings.get( "NuclearPhysicsApplication.title" ),
               SimStrings.get( "NuclearPhysicsApplication.description" ),
               SimStrings.get( "NuclearPhysicsApplication.version" ),
               new FrameSetup.CenteredWithSize( 1024, 768 ) );

        Module alphaModule = new AlphaDecayModule( ClockFactory.create( 40, 5 ) );
        Module singleNucleusFissionModule = new SingleNucleusFissionModule( ClockFactory.create( 40, 1.5 ) );
        Module multipleNucleusFissionModule = new MultipleNucleusFissionModule( ClockFactory.create( 40, 6 ) );
        Module controlledReactionModule = new ControlledFissionModule( ClockFactory.create( 40, 20 ) );
        Module[] modules = new Module[]{
                alphaModule,
                singleNucleusFissionModule,
                multipleNucleusFissionModule,
                controlledReactionModule
        };
        setModules( modules );
    }

    /**
     *
     * @param args
     */
    public static void main( final String[] args ) {
        SimStrings.init( args, localizedStringsPath );

        AWTSplashWindow.setDefaultBackground( Color.lightGray );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
//                try {
//                    UIManager.setLookAndFeel( new NuclearAppLookAndFeel() );
//                    new NuclearPhysicsApplication( args ).startApplication();
//                }
//                catch( UnsupportedLookAndFeelException e ) {
//                    e.printStackTrace();
//                }

                new NuclearPhysicsApplication( args ).startApplication();
            }
        } );
    }


    //--------------------------------------------------------------------------------------------------
    // Look and Feel
    //--------------------------------------------------------------------------------------------------

    private static class NuclearAppLookAndFeel extends LandF {
        static Color backgroundColor = new Color( 60, 80, 60 );
        static Color buttonBackgroundColor = new Color( 100, 120, 60 );
        static Color controlTextColor = new Color( 240, 240, 240 );

        private static Color buttonTextColor = controlTextColor;

        static {
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
        }

        static Font font = new Font( "Lucida sans", Font.BOLD, 16 );
//        static Font font = new Font( "SansSerif", Font.BOLD, 16 );

        // Set the font smaller if the screen is has less resolution
        static {
            // Get the size of the default screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            if( dim.getHeight() < 800 ) {
                font = new Font( "Lucida sans", Font.BOLD, 14 );
//                font = new Font( "SansSerif", Font.BOLD, 14 );
            }

            if( System.getProperty( "java.vm.version" ).startsWith( "1.5" ) ) {
                UIManager.put("Button.foreground", Color.black );
            }
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

        }

        public NuclearAppLookAndFeel() {
            super( backgroundColor, buttonBackgroundColor, controlTextColor, font );
            UIManager.put( "TabbedPane.font", new FontUIResource( "Lucida sans", Font.BOLD, 18 ));
        }
    }

    static private class LandF extends MetalLookAndFeel {
        Color backgroundColor = new Color( 60, 80, 60 );
        Color buttonBackgroundColor = new Color( 60, 60, 100 );
        Color controlTextColor = new Color( 230, 230, 230 );
        Font controlFont = new Font( "Lucida sans", Font.BOLD, 22 );
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
                    , "CheckBox.focus", background
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