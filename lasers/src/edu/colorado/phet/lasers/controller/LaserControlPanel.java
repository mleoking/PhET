/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Oct 27, 2004
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.LaserSimulation;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LaserControlPanel extends PhetControlPanel {
    private boolean threeEnergyLevels;
    private PumpingBeamControl pumpingBeamControl;
    private HighEnergyHalfLifeControl highEnergyLifetimeControl;
    private BaseLaserModule laserModule;

    public LaserControlPanel( BaseLaserModule module, AbstractClock clock ) {
        super( module );
        this.laserModule = module;
        super.setControlPane( new ControlPanel( (LaserModel)module.getModel(), clock ) );

        addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                setThreeEnergyLevels( threeEnergyLevels );
            }
        } );
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        pumpingBeamControl.setVisible( threeEnergyLevels );
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
        //        if( threeEnergyLevels ) {
        //            laserModule.setEnergyMonitorPanel( new ThreeEnergyLevelMonitorPanel( (LaserModel)laserModule.getModel() ) );
        //        }
        //        else {
        //            laserModule.setEnergyMonitorPanel( new TwoEnergyLevelMonitorPanel( (LaserModel)laserModule.getModel() ) );
        //        }
    }

    private class ControlPanel extends JPanel {

        ControlPanel( LaserModel model, AbstractClock clock ) {

            final JRadioButton twoLevelsRB = new JRadioButton( new AbstractAction( SimStrings.get( "LaserControlPanel.TwoLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( false );
                }
            } );
            final JRadioButton threeLevelsRB = new JRadioButton( new AbstractAction( SimStrings.get( "LaserControlPanel.ThreeLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( true );
                }
            } );
            final ButtonGroup energyButtonGroup = new ButtonGroup();
            energyButtonGroup.add( twoLevelsRB );
            energyButtonGroup.add( threeLevelsRB );

            JPanel energyButtonPanel = new JPanel();
            energyButtonPanel.add( twoLevelsRB );
            energyButtonPanel.add( threeLevelsRB );
            energyButtonPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.EnergyLevelsBorderTitle" ) ) );

            final String addMirrorsStr = SimStrings.get( "LaserControlPanel.AddMirrorsCheckBox" );
            final String removeMirrorsStr = SimStrings.get( "LaserControlPanel.RemoveMirrorsCheckBox" );
            final JCheckBox mirrorCB = new JCheckBox( addMirrorsStr );
            mirrorCB.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if( mirrorCB.isSelected() ) {
                        mirrorCB.setText( removeMirrorsStr );
                        laserModule.setMirrorsEnabled( true );
                    }
                    else {
                        mirrorCB.setText( addMirrorsStr );
                        laserModule.setMirrorsEnabled( false );
                    }
                }
            } );

            final JCheckBox energyDialogCB = new JCheckBox( SimStrings.get( "LaserControlPanel.EnergyLevelCheckBox" ) );
            energyDialogCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    laserModule.setEnergyLevelsVisible( energyDialogCB.isSelected() );
                }
            } );

            String s = GraphicsUtil.formatMessage( SimStrings.get( "LaserControlPanel.EmissionCheckBox" ) );
            final JCheckBox showHighToMidEmissionCB = new JCheckBox( s );
            showHighToMidEmissionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (LaserSimulation)PhetApplication.instance() ).displayHighToMidEmission( showHighToMidEmissionCB.isSelected() );
                }
            } );
            JPanel optionsPanel = new JPanel( new GridLayout( 3, 1 ) );
            optionsPanel.add( mirrorCB );
            optionsPanel.add( showHighToMidEmissionCB );
            optionsPanel.add( energyDialogCB );
            optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );


            pumpingBeamControl = new PumpingBeamControl( model.getPumpingBeam() );
            pumpingBeamControl.setVisible( threeEnergyLevels );
            //            highEnergyLifetimeControl.setVisible( threeEnergyLevels );

            this.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             new Insets( 0, 0, 0, 0 ),
                                                             0, 0 );
            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );

            this.add( energyButtonPanel, gbc );
            gbc.gridy++;
            this.add( optionsPanel, gbc );
            gbc.gridy++;
            //            this.add( showHighToMidEmissionCB, gbc );
            //            gbc.gridy++;

            //            stimulatingBeamControl = new StimulatingBeamControl( model );
            //            this.add( stimulatingBeamControl, gbc );
            //            gbc.gridy++;
            this.add( pumpingBeamControl, gbc );
            gbc.gridy++;
            ResonatingCavity cavity = model.getResonatingCavity();
            this.add( new RightMirrorReflectivityControlPanel( cavity ), gbc );
            //            gbc.gridy++;
            //            this.add( new SimulationRateControlPanel( clock, 1, 40, 10 ), gbc );

            // Set the number of energy levels we'll see
            twoLevelsRB.setSelected( true );
            setThreeEnergyLevels( false );
        }
    }
}
