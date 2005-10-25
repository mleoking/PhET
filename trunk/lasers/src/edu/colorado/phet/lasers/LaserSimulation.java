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
import edu.colorado.phet.lasers.controller.PhotoWindow;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.controller.module.SingleAtomModule;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class LaserSimulation extends PhetApplication {
    private JDialog photoDlg;

    public static class LaserAppModel extends ApplicationModel {

        public LaserAppModel() {
            super( SimStrings.get( "LasersApplication.title" ),
                   SimStrings.get( "LasersApplication.description" ),
                   SimStrings.get( "LasersApplication.version" ) );


            LaserConfig.DT = 12;
            LaserConfig.FPS = 25;
            AbstractClock clock = new SwingTimerClock( LaserConfig.DT, LaserConfig.FPS, AbstractClock.FRAMES_PER_SECOND );
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
//            Module kaboomModule = new TestKaboomModule();
            Module[] modules = new Module[]{
                singleAtomModule,
                multipleAtomModule
//                kaboomModule
            };
            setModules( modules );
//            setInitialModule( multipleAtomModule );
            setInitialModule( singleAtomModule );
//            setInitialModule( kaboomModule );
        }
    }

    public LaserSimulation( String[] args ) {
        super( new LaserAppModel(), args );
        JButton photoBtn = new JButton( SimStrings.get( "LaserPhotoButtonLabel" ) );
        photoBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( photoDlg == null ) {
                    photoDlg = new PhotoWindow( getPhetFrame() );
                }
                photoDlg.setVisible( true );
            }
        } );
        this.getPhetFrame().getClockControlPanel().add( photoBtn, BorderLayout.WEST );

        JMenu optionMenu = new JMenu( "Options" );
        final JCheckBoxMenuItem cbMI = new JCheckBoxMenuItem( "All stimulated emissions" );
        cbMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                LaserConfig.ENABLE_ALL_STIMULATED_EMISSIONS = cbMI.isSelected();
            }
        } );
        cbMI.setSelected( true );
//        LaserConfig.ENABLE_ALL_STIMULATED_EMISSIONS = cbMI.isSelected() ;
        optionMenu.add( cbMI );
        getPhetFrame().addMenu( optionMenu );
    }

    public void displayHighToMidEmission( boolean selected ) {
        throw new RuntimeException( "TBI" );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        SimStrings.init( args, LaserConfig.localizedStringsPath );

        String arch = System.getProperty( "os.name", "" );

        // Install the look and feel. If we're not on Windows,
        // then use the native L&F
        if( !arch.toLowerCase().startsWith( "windows" ) ) {
            // Get the native look and feel class name
            String nativeLF = UIManager.getSystemLookAndFeelClassName();
            try {
                UIManager.setLookAndFeel( nativeLF );
            }
            catch( InstantiationException e ) {
            }
            catch( ClassNotFoundException e ) {
            }
            catch( UnsupportedLookAndFeelException e ) {
            }
            catch( IllegalAccessException e ) {
            }
        }
        else {
            try {
                UIManager.setLookAndFeel( new LaserAppLookAndFeel() );
            }
            catch( UnsupportedLookAndFeelException e ) {
                e.printStackTrace();
            }
        }


        LaserSimulation simulation = new LaserSimulation( args );
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
