/**
 * Class: LaserApplication
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.PhotoModule;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.controller.module.SingleAtomModule;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class LaserSimulation2 extends PhetApplication {

    public static class LaserAppModel extends ApplicationModel {
        public LaserAppModel() {
            super( SimStrings.get( "LasersApplication.title" ),
                   SimStrings.get( "LasersApplication.description" ),
                   SimStrings.get( "LasersApplication.version" ) );


            AbstractClock clock = new SwingTimerClock( 12, 25, AbstractClock.FRAMES_PER_SECOND );
            setClock( clock );

            // Determine the resolution of the screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            setFrameCenteredSize( 1024, 750 );
            if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
                FrameSetup fs = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 750 ) );
                setFrameSetup( fs );
            }


            Module singleAtomModule = new SingleAtomModule( clock );
            Module multipleAtomModule = new MultipleAtomModule( clock );
            Module photoModule = new PhotoModule( clock );
//            Module kaboomModule = new TestKaboomModule();
            Module[] modules = new Module[]{
                singleAtomModule,
                multipleAtomModule,
                photoModule                
//                kaboomModule
            };
            setModules( modules );
//            setInitialModule( multipleAtomModule );
            setInitialModule( singleAtomModule );
//            setInitialModule( kaboomModule );
        }
    }

    public LaserSimulation2( String[] args ) {
        super( args,
               new SwingTimerClock( 12, 25, AbstractClock.FRAMES_PER_SECOND ),
               SimStrings.get( "LasersApplication.title" ),
               SimStrings.get( "LasersApplication.description" ),
               SimStrings.get( "LasersApplication.version" ) );
//        setClock( clock );

        // Determine the resolution of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 750 );
        if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
            frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 750 ) );
        }
        setFrameSetup( frameSetup );


        Module singleAtomModule = new SingleAtomModule( getClock() );
        Module multipleAtomModule = new MultipleAtomModule( getClock() );
        Module photoModule = new PhotoModule( getClock() );
//            Module kaboomModule = new TestKaboomModule();
        addModule( singleAtomModule );
        addModule( multipleAtomModule );
        addModule( photoModule );
        setInitialModule( singleAtomModule );
//        Module[] modules = new Module[]{
//            singleAtomModule,
//            multipleAtomModule,
//            photoModule
////                kaboomModule
//        };
//        setModules( modules );
////            setInitialModule( multipleAtomModule );
//        setInitialModule( singleAtomModule );
////            setInitialModule( kaboomModule );
//    }
    }

    public void displayHighToMidEmission( boolean selected ) {
        throw new RuntimeException( "TBI" );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey ) ) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            SimStrings.setLocale( new Locale( locale ) );
        }

        SimStrings.setStrings( LaserConfig.localizedStringsPath );
        try {
            UIManager.setLookAndFeel( new LaserAppLookAndFeel() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }

        LaserSimulation2 simulation = new LaserSimulation2( args );
        simulation.startApplication();
    }

//----------------------------------------------------------------
// Definition of look and feel
//----------------------------------------------------------------

    private static class LaserAppLookAndFeel extends LandF {
        static Color yellowishBackground = new Color( 255, 255, 214 );
        //        static Color yellowishBackground = new Color( 249, 221, 162 );
        static Color greenishBackground = new Color( 138, 156, 148 );
        static Color greenishButtonBackground = new Color( 154, 160, 148 );
        static Color purpleishBackground = new Color( 200, 197, 220 );
        //        static Color backgroundColor = yellowishBackground;
        //        static Color backgroundColor = yellowishBackground;
        //        static Color backgroundColor = new Color( 98, 98, 98 );
        //        static Color backgroundColor = new Color( 98, 116, 108 );
        static Color backgroundColor = greenishBackground;
        //        static Color backgroundColor = purpleishBackground;
        //        static Color buttonBackgroundColor = new Color( 220, 230, 160 );
        static Color buttonBackgroundColor = yellowishBackground;
        //        static Color buttonBackgroundColor = new Color( 180, 170, 160 );
        //        static Color buttonBackgroundColor = new Color( 165, 160, 219 );
        //        static Color controlTextColor = new Color( 220, 220, 220 );
        static Color controlTextColor = new Color( 38, 56, 48 );
        //        static Color controlTextColor = yellowishBackground;
        //        static Color controlTextColor = new Color( 0, 0, 0 );
        static Font font = new Font( "SansSerif", Font.BOLD, 12 );

        public LaserAppLookAndFeel() {
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
