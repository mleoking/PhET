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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ClockProfiler;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.controller.module.SingleAtomModule;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.EnergyLevelGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;

public class LasersApplication extends PiccoloPhetApplication {
    
    public static double ONE_ATOM_MODULE_SPEED = 0.5;
    public static double MULTI_ATOM_MODULE_SPEED = 0.5;

    private SingleAtomModule singleAtomModule;
    private MultipleAtomModule multipleAtomModule;

    public SingleAtomModule getSingleAtomModule() {
        return singleAtomModule;
    }

    public MultipleAtomModule getMultipleAtomModule() {
        return multipleAtomModule;
    }

    public LasersApplication( PhetApplicationConfig config ) {
        super( config );

        // Because we have JComponents on the apparatus panel, don't let the user resize the frame
        this.getPhetFrame().setResizable( false );

        // Set the default representation strategy for energy levels
        AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.VisibleColorStrategy() );

        singleAtomModule = new SingleAtomModule( getPhetFrame(), new ConstantDtClock( 1000 / LasersConfig.FPS, LasersConfig.DT ) );
        multipleAtomModule = new MultipleAtomModule( getPhetFrame(), new ConstantDtClock( 1000 / LasersConfig.FPS, LasersConfig.DT ) );
        Module[] modules = new Module[]{
                singleAtomModule,
                multipleAtomModule
        };
        setModules( modules );

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
        dialog.setVisible( true );
    }

    private void addMenuItems() {
        JMenu optionMenu = new JMenu( LasersResources.getString( "options.menu.title" ));
        getPhetFrame().addMenu( optionMenu );

        // Additions to the Options menu

        final JCheckBoxMenuItem cbMI = new JCheckBoxMenuItem( LasersResources.getString( "options.show-emissions" ));
        cbMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                LasersConfig.ENABLE_ALL_STIMULATED_EMISSIONS = cbMI.isSelected();
            }
        } );
        cbMI.setSelected( true );
        optionMenu.add( cbMI );

        JMenuItem stimProbmenuItem = new JMenuItem( LasersResources.getString( "options.adjust-stimulation-likelihood" ));
        stimProbmenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final ModelSlider probSlider = new ModelSlider( LasersResources.getString( "options.probability" ), "", 0, 1, 1, new DecimalFormat( "0.00" ) );
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

        final JRadioButtonMenuItem colorEnergyRepStrategy = new JRadioButtonMenuItem( LasersResources.getString( "options.colored-energy-levels" ) );
        colorEnergyRepStrategy.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( colorEnergyRepStrategy.isSelected() ) {
                    AtomGraphic.setEnergyRepColorStrategy( new AtomGraphic.VisibleColorStrategy() );
                }
            }
        } );

        final JMenuItem optionsButton = new JMenuItem( LasersResources.getString( "options.view" ) );
        optionsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new OptionsDialog( LasersApplication.this ).show();
            }
        } );
        optionMenu.add( optionsButton );

        JMenuItem profileOption = new JMenuItem( LasersResources.getString( "options.profile" ) );
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
//        optionMenu.add( profileOption );
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

    private static class LasersLookAndFeel extends PhetLookAndFeel {
        public LasersLookAndFeel() {
            setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
            setTitledBorderFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
            setBackgroundColor( new Color( 138, 156, 148 ) );
            setButtonBackgroundColor( new Color( 255, 255, 214 ) );
        }
    }
    
    public static void main( final String[] args ) {
        
        //XXX this is poorly designed code, global variables!
        if ( Arrays.asList( args ).indexOf( "-selectspeed" ) >= 0 ) {
            String str = JOptionPane.showInputDialog( "Enter the speed for the 1st and 2nd panel, separated by whitespace", ONE_ATOM_MODULE_SPEED + " " + MULTI_ATOM_MODULE_SPEED );
            StringTokenizer st = new StringTokenizer( str );
            ONE_ATOM_MODULE_SPEED = Double.parseDouble( st.nextToken() );
            MULTI_ATOM_MODULE_SPEED = Double.parseDouble( st.nextToken() );
        }
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new LasersApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, LasersConfig.PROJECT_NAME );
        appConfig.setLookAndFeel( new LasersLookAndFeel() );     
        appConfig.setFrameSetup( LasersConfig.FRAME_SETUP );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

}
