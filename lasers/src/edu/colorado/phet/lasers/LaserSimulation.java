/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.PhotoWindow;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.controller.module.SingleAtomModule;
import edu.colorado.phet.quantum.model.AtomicState;
import edu.colorado.phet.lasers.view.AtomGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class LaserSimulation extends PhetApplication {

    static {
//        PhetLookAndFeel.setLookAndFeel();
    }

    // todo: These are static only temporarily, so I can get the look and feel right for the clock control panel
    // This should not be a permanant thing!!!!!
    private static Module singleAtomModule;
    private static Module multipleAtomModule;


    IClock singleAtomModuleClock = new SwingClock( 1000 / LaserConfig.FPS, LaserConfig.DT );
    IClock multipleAtomModuleClock = new SwingClock( 1000 / LaserConfig.FPS, LaserConfig.DT );

    private JDialog photoDlg;

    public LaserSimulation( String[] args ) {
        super( args,
               SimStrings.get( "LasersApplication.title" ),
               SimStrings.get( "LasersApplication.description" ),
               LaserConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1024, 750 ) );

        // Because we have JComponents on the apparatus panel, don't let the user resize the frame
        this.getPhetFrame().setResizable( false );

        // Set the default representation strategy for energy levels
        AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.VisibleColorStrategy() );

        singleAtomModule = new SingleAtomModule( singleAtomModuleClock );
        multipleAtomModule = new MultipleAtomModule( multipleAtomModuleClock );
        Module[] modules = new Module[]{
            singleAtomModule,
            multipleAtomModule
        };
        setModules( modules );

        for( int i = 0; i < modules.length; i++ ) {
            JButton photoBtn = new JButton( SimStrings.get( "LaserPhotoButtonLabel" ) );
            photoBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( photoDlg == null ) {
                        photoDlg = new PhotoWindow( getPhetFrame() );
                    }
                    photoDlg.setVisible( true );
                }
            } );
            Module module = modules[i];
            module.getClockControlPanel().add( photoBtn, BorderLayout.WEST );
        }

        // Options menu
        createMenuItems();

    }

    /**
     *
     */
    private void createMenuItems() {
        JMenu optionMenu = new JMenu( "Options" );
        getPhetFrame().addMenu( optionMenu );

        // Additions to the Options menu

        final JCheckBoxMenuItem cbMI = new JCheckBoxMenuItem( "Show all stimulated emissions" );
        cbMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                LaserConfig.ENABLE_ALL_STIMULATED_EMISSIONS = cbMI.isSelected();
            }
        } );
        cbMI.setSelected( true );
        optionMenu.add( cbMI );

        JMenuItem stimProbmenuItem = new JMenuItem( "Adjust stimulation likelihood..." );
        stimProbmenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final ModelSlider probSlider = new ModelSlider( "Probability", "", 0, 1, 1, new DecimalFormat( "0.00" ) );
                probSlider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        AtomicState.setStimulationLikelihood( probSlider.getValue() );
                    }
                } );
                probSlider.setValue( AtomicState.getStimulationLikelihood() );
                JPanel jp = new JPanel();
                jp.add( probSlider );
                JOptionPane.showMessageDialog( getPhetFrame(), jp );
            }
        } );
        optionMenu.add( stimProbmenuItem );

        final JRadioButtonMenuItem colorEnergyRepStrategy = new JRadioButtonMenuItem( "Colored energy levels" );
        colorEnergyRepStrategy.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( colorEnergyRepStrategy.isSelected() ) {
                    AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.VisibleColorStrategy() );
                }
            }
        } );

//        final ModelSlider minLifetimeSlider = new ModelSlider( "Min ground state lifetime",
//                                                         "msec",
//                                                         0, 1000,
//                                                         LaserConfig.MINIMUM_GROUND_STATE_LIFETIME,
//                                                         new DecimalFormat( "#"));
//        minLifetimeSlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                LaserConfig.MINIMUM_GROUND_STATE_LIFETIME = (int)minLifetimeSlider.getValue();
//            }
//        } );
//        optionMenu.add( minLifetimeSlider );
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

        setLAF( arch );

        LaserSimulation simulation = new LaserSimulation( args );
        simulation.startApplication();

        SwingUtilities.updateComponentTreeUI( singleAtomModule.getModulePanel() );
        SwingUtilities.updateComponentTreeUI( multipleAtomModule.getModulePanel() );
    }

    private static void setLAF( String arch ) {

        UIManager.LookAndFeelInfo[] lafs =   UIManager.getInstalledLookAndFeels();
        for( int i = 0; i < lafs.length; i++ ) {
            UIManager.LookAndFeelInfo laf = lafs[i];
            System.out.println( "laf.getName() = " + laf.getClassName() );
        }
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
//            new LaserAppLookAndFeel().apply();
            try {
                UIManager.setLookAndFeel( new LaserAppLookAndFeel() );
            }
            catch( UnsupportedLookAndFeelException e ) {
                e.printStackTrace();
            }
        }


//        try {
//            String nativeLF = UIManager.getSystemLookAndFeelClassName();
//            UIManager.setLookAndFeel( nativeLF );
//            UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
////            String nativeLF = UIManager.getSystemLookAndFeelClassName();
////            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
////            UIManager.setLookAndFeel( new MetalLookAndFeel() );
//        }
//        catch( UnsupportedLookAndFeelException e ) {
//            e.printStackTrace();
//        }
//        catch( IllegalAccessException e ) {
//            e.printStackTrace();
//        }
//        catch( InstantiationException e ) {
//            e.printStackTrace();
//        }
//        catch( ClassNotFoundException e ) {
//            e.printStackTrace();
//        }
    }

    //----------------------------------------------------------------
    // Definition of look and feel
    //----------------------------------------------------------------

//    private static class LaserAppLookAndFeel extends PhetLookAndFeel {
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
//        static Color backgroundColor = new Color( 210, 210, 210 );
//                static Color backgroundColor = purpleishBackground;
        //        static Color buttonBackgroundColor = new Color( 220, 230, 160 );
        static Color buttonBackgroundColor = yellowishBackground;
        //        static Color buttonBackgroundColor = new Color( 180, 170, 160 );
        //        static Color buttonBackgroundColor = new Color( 165, 160, 219 );
        //        static Color controlTextColor = new Color( 220, 220, 220 );
        static Color controlTextColor = new Color( 38, 56, 48 );
        //        static Color controlTextColor = yellowishBackground;
        //        static Color controlTextColor = new Color( 0, 0, 0 );
        static Font font = new Font( "SansSerif", Font.BOLD, 12 );

//        Color backgroundColor = new Color( 60, 80, 60 );
//        Color buttonBackgroundColor = new Color( 60, 60, 100 );
//        Color controlTextColor = new Color( 230, 230, 230 );
//        Font controlFont = new Font( "SansSerif", Font.BOLD, 22 );

        public LaserAppLookAndFeel() {
//            super();
            super( backgroundColor, buttonBackgroundColor, controlTextColor, font );
//            setBackgroundColor( backgroundColor );
//            setFont( font );
////            setTabFont( font );
//            setTitledBorderFont( font );
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
//                , "MenuItem.background", background
                , "MenuBar.background", background
                , "Slider.background", background
                , "RadioButton.background", background
                , "CheckBox.background", background
                , "OptionPane.background", background
                , "TabbedPane.background", background
//                , "TabbedPane.selected", new ColorUIResource( new Color( 255, 60, 60 ))
//                , "Button.background", buttonBackground
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

            // Set the background color of the buttons is we are running Java version 1.4
            if( System.getProperty( "java.version").startsWith( "1.4")) {
                table.putDefaults( new Object[] {
                    "Button.background", buttonBackground
                });
            }
        }
    }

}
