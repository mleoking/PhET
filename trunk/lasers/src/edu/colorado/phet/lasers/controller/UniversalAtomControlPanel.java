package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.LaserSimulation;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class: MultipleAtomControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Nov 23, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class UniversalAtomControlPanel extends LaserControlPanel {
    private boolean threeEnergyLevels;
    private BaseLaserModule laserModule;
    private WaveViewControlPanel waveViewControlPanel;
    private Controls basicControls;

    public UniversalAtomControlPanel( final BaseLaserModule module ) {
        super( module );
        this.laserModule = module;

        waveViewControlPanel = new WaveViewControlPanel( module );
        basicControls = new Controls();
        addControl( basicControls );

        JPanel optionsPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );
        optionsPanel.add( new MirrorOnOffControlPanel( module ), gbc );
//        gbc.gridy++;

        // Add controls for the different views of beams and photons
        optionsPanel.add( waveViewControlPanel, gbc );
        optionsPanel.add( new HighLevelEmissionControlPanel( module ), gbc );
        JPanel container = new JPanel();
        Border border = BorderFactory.createEtchedBorder();
        container.setBorder( border );
        container.add( optionsPanel );

        super.addControl( container );

        setThreeEnergyLevels( module.getThreeEnergyLevels() );
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
        waveViewControlPanel.setVisible( threeEnergyLevels );
        basicControls.setThreeEnergyLevels( threeEnergyLevels );
    }

    //--------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------

    private class Controls extends JPanel {
        private JRadioButton twoLevelsRB;
        private JRadioButton threeLevelsRB;

        Controls() {
            GridBagConstraints gbc;

            twoLevelsRB = new JRadioButton( new AbstractAction( SimStrings.get( "LaserControlPanel.TwoLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( false );
                }
            } );
            threeLevelsRB = new JRadioButton( new AbstractAction( SimStrings.get( "LaserControlPanel.ThreeLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( true );
                }
            } );
            final ButtonGroup energyButtonGroup = new ButtonGroup();
            energyButtonGroup.add( twoLevelsRB );
            energyButtonGroup.add( threeLevelsRB );
            twoLevelsRB.setSelected( true );

            JPanel energyButtonPanel = new JPanel();
            energyButtonPanel.add( twoLevelsRB );
            energyButtonPanel.add( threeLevelsRB );
            energyButtonPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.EnergyLevelsBorderTitle" ) ) );

            String s = SimStrings.get( "LaserControlPanel.EmissionCheckBox" );
            //            String s = GraphicsUtil.formatMessage( SimStrings.get( "LaserControlPanel.EmissionCheckBox" ) );
            final JCheckBox showHighToMidEmissionCB = new JCheckBox( s );
            showHighToMidEmissionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (LaserSimulation)PhetApplication.instance() ).displayHighToMidEmission( showHighToMidEmissionCB.isSelected() );
                }
            } );
            JPanel optionsPanel = new JPanel( new GridLayout( 1, 1 ) );
            optionsPanel.add( showHighToMidEmissionCB );
            optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );

            this.setLayout( new GridBagLayout() );
            gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets( 0, 0, 0, 0 ),
                                          0, 0 );
            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );
            this.add( energyButtonPanel, gbc );

            //Set the number of energy levels we'll see
            twoLevelsRB.setSelected( true );
            setThreeEnergyLevels( false );
        }

        private void setThreeEnergyLevels( boolean threeEnergyLevels ) {
            if( threeEnergyLevels ) {
                threeLevelsRB.setSelected( true );
            }
            else {
                twoLevelsRB.setSelected( true );
            }
        }
    }

}
