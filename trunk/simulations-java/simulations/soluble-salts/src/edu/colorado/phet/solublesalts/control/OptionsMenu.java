/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source:
 * Branch : $Name:
 * Modified by : $Author:
 * Revision : $Revision:
 * Date modified : $Date:
 */

package edu.colorado.phet.solublesalts.control;

import java.awt.*;
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

public class OptionsMenu extends JMenu {

    public OptionsMenu( final JFrame frame ) {
        super( SolubleSaltResources.getString( "options.menu.title" ) );
        JMenu optionsMenu = this;
        optionsMenu.setMnemonic( 'O' );
        final JCheckBoxMenuItem showBondIndicatorMI = new JCheckBoxMenuItem( SolubleSaltResources.getString( "options.menu.show-bond-indicators" ) );
        optionsMenu.add( showBondIndicatorMI );
        showBondIndicatorMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IonGraphic.showBondIndicators( showBondIndicatorMI.isSelected() );
            }
        } );

        final JCheckBoxMenuItem randomWalkMI = new JCheckBoxMenuItem( SolubleSaltResources.getString( "options.menu.random-walk" ), SolubleSaltsConfig.RANDOM_WALK );
        optionsMenu.add( randomWalkMI );
        randomWalkMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsConfig.RANDOM_WALK = randomWalkMI.isSelected();
            }
        } );

        final JCheckBoxMenuItem oneCrystalMI = new JCheckBoxMenuItem( SolubleSaltResources.getString( "options.menu.one-crystal-only" ) );
        optionsMenu.add( oneCrystalMI );
        oneCrystalMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = oneCrystalMI.isSelected();
            }
        } );

        // Random walk adjustment
        final JMenuItem randomWaltkThetaMI = new JMenuItem( SolubleSaltResources.getString( "options.menu.adjust-random-walk" ) );
        randomWaltkThetaMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, SolubleSaltResources.getString( "options.menu.random-walk-adjustment" ), false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final SolubleSaltsModel model = (SolubleSaltsModel) PhetApplication.instance().getActiveModule().getModel();
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
                JButton btn = new JButton( SolubleSaltResources.getString( "options.menu.close" ) );
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
        optionsMenu.add( randomWaltkThetaMI );

        // Binding distance adjustment
        final JMenuItem bindingDistanceMI = new JMenuItem( SolubleSaltResources.getString( "options.menu.adjust-binding-distance" ) );
        bindingDistanceMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, SolubleSaltResources.getString( "options.menu.set-binding-distance" ), false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final ModelSlider sldr = new ModelSlider( SolubleSaltResources.getString( "options.menu.binding-distance" ),
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
                JButton btn = new JButton( SolubleSaltResources.getString( "options.menu.close" ) );
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
        optionsMenu.add( bindingDistanceMI );

        // Drain flow effect on ions
        final JMenuItem drainFlowMI = new JMenuItem( SolubleSaltResources.getString( "options.menu.drain-flow" ) );
        drainFlowMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, SolubleSaltResources.getString( "options.menu.set-drain-flow" ), false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final ModelSlider sldr = new ModelSlider( SolubleSaltResources.getString( "options.menu.drain-flow-effect" ),
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
                JButton btn = new JButton( SolubleSaltResources.getString( "options.menu.close" ) );
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
        optionsMenu.add( drainFlowMI );

        // Debug option
        final JCheckBoxMenuItem debugMI = new JCheckBoxMenuItem( SolubleSaltResources.getString( "options.menu.show-debug-controls" ) );
        debugMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsControlPanel controlPanel = (SolubleSaltsControlPanel) PhetUtilities.getActiveModule().getControlPanel();
                controlPanel.setDebugControlsVisible( debugMI.isSelected() );
            }
        } );
        optionsMenu.add( debugMI );

        // Color picker
        final JMenuItem colorPicker = new JMenuItem( SolubleSaltResources.getString( "options.menu.select-background-color" ) );
        colorPicker.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(), SolubleSaltResources.getString( "options.menu.select-background-color" ), Color.gray );
                UIManager.put( "Panel.background", newColor );
                UIManager.put( "MenuBar.background", newColor );
                UIManager.put( "TabbedPane.background", newColor );
                UIManager.put( "Menu.background", newColor );
//                UIManager.put( "TextField.background", newColor );
                SwingUtilities.updateComponentTreeUI( PhetUtilities.getPhetFrame() );
            }
        } );
        optionsMenu.add( colorPicker );
    }
}
