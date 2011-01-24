// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.solublesalts.SolubleSaltResources;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.IonFlowManager;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.view.IonGraphic;

/**
 *  Static setup method for the Developer menu.  No i18n needed, not visible to users.
 */
public class DeveloperMenuSetup extends JMenu {

    private DeveloperMenuSetup() {}

    public static void setup( JMenu menu, final JFrame frame ) {

        final JCheckBoxMenuItem showBondIndicatorMI = new JCheckBoxMenuItem( "Show bond indicators" );
        menu.add( showBondIndicatorMI );
        showBondIndicatorMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IonGraphic.showBondIndicators( showBondIndicatorMI.isSelected() );
            }
        } );

        final JCheckBoxMenuItem randomWalkMI = new JCheckBoxMenuItem( "Random walk", SolubleSaltsConfig.RANDOM_WALK );
        menu.add( randomWalkMI );
        randomWalkMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsConfig.RANDOM_WALK = randomWalkMI.isSelected();
            }
        } );

        final JCheckBoxMenuItem oneCrystalMI = new JCheckBoxMenuItem( "One crystal only" );
        menu.add( oneCrystalMI );
        oneCrystalMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = oneCrystalMI.isSelected();
            }
        } );

        // Random walk adjustment
        final JMenuItem randomWaltkThetaMI = new JMenuItem( "Adjust random walk..." );
        randomWaltkThetaMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, "Adjust Random Walk", false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final SolubleSaltsModel model = (SolubleSaltsModel) PhetApplication.getInstance().getActiveModule().getModel();
                final JSlider sldr = new JSlider( 0, 360, (int) model.getRandomWalkAgent().getTheta() );
                sldr.setMajorTickSpacing( 45 );
                sldr.setMinorTickSpacing( 15 );
                sldr.setPaintTicks( true );
                sldr.setPaintLabels( true );
                sldr.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        model.getRandomWalkAgent().setTheta( sldr.getValue() );
                    }
                } );
                dlg.getContentPane().add( sldr );
                JButton btn = new JButton( "Close" );
                btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        dlg.setVisible( false );
                    }
                } );
                JPanel btnPnl = new JPanel();
                btnPnl.add( btn );
                dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
                dlg.pack();
                dlg.setVisible( true );
            }
        } );
        menu.add( randomWaltkThetaMI );

        // Binding distance adjustment
        final JMenuItem bindingDistanceMI = new JMenuItem( "Adjust binding distance..." );
        bindingDistanceMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, "Binding Distance", false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final ModelSlider sldr = new ModelSlider( "Binding Distance",
                                                          "",
                                                          0,
                                                          4,
                                                          SolubleSaltsConfig.BINDING_DISTANCE_FACTOR,
                                                          new DecimalFormat( "0.0" ) );
                sldr.setMajorTickSpacing( 0.5 );
                sldr.setPaintTicks( true );
                sldr.setPaintLabels( true );
                sldr.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        SolubleSaltsConfig.BINDING_DISTANCE_FACTOR = sldr.getValue();
                    }
                } );
                dlg.getContentPane().add( sldr );
                JButton btn = new JButton( "Close" );
                btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        dlg.setVisible( false );
                    }
                } );
                JPanel btnPnl = new JPanel();
                btnPnl.add( btn );
                dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
                dlg.pack();
                dlg.setVisible( true );
            }
        } );
        menu.add( bindingDistanceMI );

        // Drain flow effect on ions
        final JMenuItem drainFlowMI = new JMenuItem( "Set drain flow effect on ions..." );
        drainFlowMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, SolubleSaltResources.getString( "Drain Flow Effect" ), false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final ModelSlider sldr = new ModelSlider( "Drain Flow Effect",
                                                          "",
                                                          0,
                                                          1E2,
                                                          IonFlowManager.SPEED_FACTOR,
                                                          new DecimalFormat( "0.0E0" ) );
//                sldr.setMajorTickSpacing( 1E1 );
//                sldr.setPaintTicks( true );
//                sldr.setPaintLabels( true );
                sldr.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        IonFlowManager.SPEED_FACTOR = sldr.getValue();
                    }
                } );
                dlg.getContentPane().add( sldr );
                JButton btn = new JButton( "Close" );
                btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        dlg.setVisible( false );
                    }
                } );
                JPanel btnPnl = new JPanel();
                btnPnl.add( btn );
                dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
                dlg.pack();
                dlg.setVisible( true );
            }
        } );
        menu.add( drainFlowMI );

        // Debug controls
        final JCheckBoxMenuItem debugMI = new JCheckBoxMenuItem( "Show debug controls..." );
        debugMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsControlPanel controlPanel = (SolubleSaltsControlPanel) PhetUtilities.getActiveModule().getControlPanel();
                controlPanel.setDebugControlsVisible( debugMI.isSelected() );
            }
        } );
        menu.add( debugMI );
    }
}
