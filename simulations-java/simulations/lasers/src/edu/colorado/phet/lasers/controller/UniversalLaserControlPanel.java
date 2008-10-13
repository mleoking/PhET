/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.view.LampGraphic;
import edu.colorado.phet.lasers.view.PumpBeamViewPanel;
import edu.colorado.phet.lasers.view.util.ViewUtils;

/**
 * This is the control panel for all modules
 */
public class UniversalLaserControlPanel extends LaserControlPanel {
    private BaseLaserModule laserModule;
    private WaveViewControlPanel waveViewControlPanel;
    private BasicOptionsPanel basicBasicOptionsPanel;
    private HighLevelEmissionControlPanel highLevelEmissionControlPanel;
    private PumpBeamViewPanel pumpBeamViewPanel;

    public UniversalLaserControlPanel( final BaseLaserModule module, boolean showLegend ) {
        super( module );
        this.laserModule = module;

        // Add the energy levels panel. Note that it must be wrapped in another JPanel, because
        // it has a null layout manager
        addControl( module.getEnergyLevelsMonitorPanel() );

        if ( showLegend ) {
            addControl( new LasersLegend() );
        }

        waveViewControlPanel = new WaveViewControlPanel( module );
        highLevelEmissionControlPanel = new HighLevelEmissionControlPanel( module );

        // Add the two/three level radio buttons
        basicBasicOptionsPanel = new BasicOptionsPanel( module.getThreeEnergyLevels() );
        addControl( basicBasicOptionsPanel );

        JPanel optionsPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0,
                                                         1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        gbc.anchor = GridBagConstraints.NORTHWEST;
        ViewUtils.setBorder( optionsPanel, LasersResources.getString( "LaserControlPanel.OptionsBorderTitle" ) );
        gbc.anchor = GridBagConstraints.CENTER;

        // Add the options for mirror on/off
        JPanel mirrorOptionPanel = createMirrorControlPanel( module );
        gbc.gridx = 0;
        gbc.insets = new Insets( 0, 15, 0, 0 );
        optionsPanel.add( mirrorOptionPanel, gbc );

        // Add the control for showing/hiding phtoton coming off high energy state
        gbc.gridx = 0;
        gbc.gridy = 1;
        optionsPanel.add( highLevelEmissionControlPanel, gbc );

        // Add controls for the different views of beams and photons
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets( 0, 0, 0, 0 );
        pumpBeamViewPanel = new PumpBeamViewPanel( module );
        optionsPanel.add( pumpBeamViewPanel, gbc );

        // Add controls for view of internally produced photons
        gbc.gridx = 1;
        gbc.gridy = 1;
        optionsPanel.add( waveViewControlPanel, gbc );
        JPanel container = new JPanel();
        Border border = BorderFactory.createEtchedBorder();
        container.setBorder( border );
        container.add( optionsPanel );

        super.addControl( optionsPanel );

        // Reset button
        gbc.fill = GridBagConstraints.NONE;
        JButton resetBtn = new JButton( LasersResources.getString( "LaserControlPanel.Reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int choice = JOptionPane.showConfirmDialog( PhetApplication.instance().getPhetFrame(),
                                                            LasersResources.getString( "ResetAll.confirmationMessage" ) );
                if ( choice == JOptionPane.OK_OPTION ) {
                    module.reset();
                }
            }
        } );
        JPanel resetBtnPanel = new JPanel();
        resetBtnPanel.add( resetBtn );
        super.addControl( resetBtnPanel );

        this.doLayout();
        this.setPreferredSize( new Dimension( 340, (int) this.getSize().getHeight() ) );

        // Add a listener to the pumping beam graphic that will make the pump beam view control panel
        // visible when the graphic is visible, and vice versa
        module.getPumpLampGraphic().addChangeListener( new LampGraphic.ChangeListener() {
            public void changed( LampGraphic.ChangeEvent event ) {
                pumpBeamViewPanel.setVisible( event.getLampGraphic().isVisible() );
            }
        } );
    }

    private JPanel createMirrorControlPanel( final BaseLaserModule module ) {
        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel panel2 = new JPanel( new GridLayout( 1, 2 ) );
        panel2.add( new MirrorOnOffControlPanel( module ), gbc );
        return panel2;
    }

    /**
     * @param threeEnergyLevels
     */
    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
        waveViewControlPanel.setVisible( threeEnergyLevels );
        highLevelEmissionControlPanel.setVisible( threeEnergyLevels );
    }

    /**
     * @param viewType
     */
    public void setUpperTransitionView( int viewType ) {
        pumpBeamViewPanel.setUpperTransitionView( viewType );
    }

    //--------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------

    private class BasicOptionsPanel extends JPanel {
        private JRadioButton twoLevelsRB;
        private JRadioButton threeLevelsRB;

        BasicOptionsPanel( boolean threeEnergyLevels ) {
            GridBagConstraints gbc;

            twoLevelsRB = new JRadioButton( new AbstractAction( LasersResources.getString( "LaserControlPanel.TwoLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( false );
                }
            } );
            threeLevelsRB = new JRadioButton( new AbstractAction( LasersResources.getString( "LaserControlPanel.ThreeLevelsRadioButton" ) ) {
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

            this.setLayout( new GridBagLayout() );
            gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets( 0, 0, 0, 0 ),
                                          0, 0 );
            ViewUtils.setBorder( this, LasersResources.getString( "LaserControlPanel.EnergyLevelsBorderTitle" ) );

            this.add( energyButtonPanel, gbc );

            //Set the number of energy levels we'll see
            twoLevelsRB.setSelected( !threeEnergyLevels );
            threeLevelsRB.setSelected( threeEnergyLevels );
            setThreeEnergyLevels( threeEnergyLevels );
        }
    }
}
