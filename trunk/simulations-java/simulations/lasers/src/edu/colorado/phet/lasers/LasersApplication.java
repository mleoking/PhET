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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ClockProfiler;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.PhotoWindow;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.controller.module.SingleAtomModule;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.EnergyLevelGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;

public class LasersApplication extends PhetApplication {

    private SingleAtomModule singleAtomModule;
    private MultipleAtomModule multipleAtomModule;

    private JDialog photoDlg;
    private static final String VERSION = PhetApplicationConfig.getVersion( "lasers" ).formatForTitleBar();

    public SingleAtomModule getSingleAtomModule() {
        return singleAtomModule;
    }

    public MultipleAtomModule getMultipleAtomModule() {
        return multipleAtomModule;
    }

    public LasersApplication( String[] args ) {
        super( args,
               SimStrings.getInstance().getString( "LasersApplication.title" ),
               SimStrings.getInstance().getString( "LasersApplication.description" ),
               VERSION,
               new FrameSetup.CenteredWithSize( 1024, 750 ) );

        // Because we have JComponents on the apparatus panel, don't let the user resize the frame
        this.getPhetFrame().setResizable( false );

        // Set the default representation strategy for energy levels
        AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.VisibleColorStrategy() );

        singleAtomModule = new SingleAtomModule( new ConstantDtClock( 1000 / LaserConfig.FPS, LaserConfig.DT ) );
        multipleAtomModule = new MultipleAtomModule( new ConstantDtClock( 1000 / LaserConfig.FPS, LaserConfig.DT ) );
        Module[] modules = new Module[]{
                singleAtomModule,
                multipleAtomModule
        };
        setModules( modules );


        for ( int i = 0; i < modules.length; i++ ) {
            JButton photoBtn = new JButton( SimStrings.getInstance().getString( "LaserPhotoButtonLabel" ) );
            photoBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( photoDlg == null ) {
                        photoDlg = new PhotoWindow( getPhetFrame() );
                    }
                    photoDlg.setVisible( true );
                }
            } );
            Module module = modules[i];
            module.getClockControlPanel().add( photoBtn, BorderLayout.WEST );
        }

        // Options menu
        addMenuItems();

//        addEnergyLevelMatchIndicatorRenderDialog();
    }

    private void addEnergyLevelMatchIndicatorRenderDialog() {
        JDialog dialog = new JDialog( getPhetFrame(), "Match Indicator", false );
        VerticalLayoutPanel pane = new VerticalLayoutPanel();
        pane.add( new JButton( new AbstractAction( "Flash Gray" ) {
            public void actionPerformed( ActionEvent e ) {
                EnergyLevelGraphic.setBlinkRenderer( Color.lightGray );
            }
        } ) );
        pane.add( new JButton( new AbstractAction( "Flash White" ) {
            public void actionPerformed( ActionEvent e ) {
                EnergyLevelGraphic.setBlinkRenderer( Color.white );
            }
        } ) );
        pane.add( new JButton( new AbstractAction( "Flow" ) {
            public void actionPerformed( ActionEvent e ) {
                EnergyLevelGraphic.setFlowRenderer();
            }
        } ) );
        dialog.setContentPane( pane );
        dialog.pack();
        SwingUtils.centerDialogInParent( dialog );
        dialog.show();
    }

    private void addMenuItems() {
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
                if ( colorEnergyRepStrategy.isSelected() ) {
                    AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.VisibleColorStrategy() );
                }
            }
        } );

        final JMenuItem optionsButton = new JMenuItem( "View Options" );
        optionsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new OptionsDialog( LasersApplication.this ).show();
            }
        } );
        optionMenu.add( optionsButton );

        JMenuItem profileOption = new JMenuItem( "Profile" );
        //todo: remove need for this cast
        final ClockProfiler profiler = new ClockProfiler( getPhetFrame(),
                                                          ( getActiveModule() == null ? getModule( 0 ) : getActiveModule() ).getName(),
                                                          (ConstantDtClock) ( getActiveModule() == null ? getModule( 0 ) : getActiveModule() ).getClock() );
        addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
            }

            public void activeModuleChanged( ModuleEvent event ) {
                profiler.setModule( getActiveModule().getName(), (ConstantDtClock) getActiveModule().getClock() );
            }

            public void moduleRemoved( ModuleEvent event ) {
            }
        } );
        profileOption.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                profiler.show();
            }
        } );
        optionMenu.add( profileOption );
    }

    private static void setLAF() {
        // Install the look and feel. If we're not on Windows,
        // then use the native L&F
        if ( !PhetUtilities.isWindows() ) {
            // Get the native look and feel class name
            try {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
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
    }

    public void setBackgroundColor( Color backgroundColor ) {
        singleAtomModule.setBackgroundColor( backgroundColor );
        multipleAtomModule.setBackgroundColor( backgroundColor );
    }

    public Color getBackgroundColor() {
        return singleAtomModule.getBackgroundColor();
    }

    public void setPhotonSize( int photonSize ) {
        PhotonGraphic.setPhotonSize( photonSize );
    }

    public int getPhotonSize() {
        return PhotonGraphic.getPhotonSize();
    }

    //----------------------------------------------------------------
    // Definition of look and feel
    //----------------------------------------------------------------

    public static double ONE_ATOM_MODULE_SPEED = 0.5;
    public static double MULTI_ATOM_MODULE_SPEED = 0.5;

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                setLAF();
                SimStrings.getInstance().init( args, LaserConfig.localizedStringsPath );

                if ( Arrays.asList( args ).indexOf( "-selectspeed" ) >= 0 ) {
                    String str = JOptionPane.showInputDialog( "Enter the speed for the 1st and 2nd panel, separated by whitespace", ONE_ATOM_MODULE_SPEED + " " + MULTI_ATOM_MODULE_SPEED );
                    StringTokenizer st = new StringTokenizer( str );
                    ONE_ATOM_MODULE_SPEED = Double.parseDouble( st.nextToken() );
                    MULTI_ATOM_MODULE_SPEED = Double.parseDouble( st.nextToken() );
                }

                LasersApplication application = new LasersApplication( args );
                application.startApplication();
//                application.setActiveModule( application.getMultipleAtomModule() );
            }
        } );
    }

}
