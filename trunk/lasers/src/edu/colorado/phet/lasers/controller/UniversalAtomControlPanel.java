package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

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
    private BasicOptionsPanel basicBasicOptionsPanel;
    private HighLevelEmissionControlPanel highLevelEmissionControlPanel;

    public UniversalAtomControlPanel( final BaseLaserModule module ) {
        super( module );
        this.laserModule = module;

        waveViewControlPanel = new WaveViewControlPanel( module );
        highLevelEmissionControlPanel = new HighLevelEmissionControlPanel( module );
        basicBasicOptionsPanel = new BasicOptionsPanel( module.getThreeEnergyLevels() );
        addControl( basicBasicOptionsPanel );

        JPanel optionsPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );
        optionsPanel.add( new MirrorOnOffControlPanel( module ), gbc );
//        gbc.gridy++;

        // Add controls for the different views of beams and photons
        optionsPanel.add( highLevelEmissionControlPanel, gbc );
        optionsPanel.add( waveViewControlPanel, gbc );
        JPanel container = new JPanel();
        Border border = BorderFactory.createEtchedBorder();
        container.setBorder( border );
        container.add( optionsPanel );

        super.addControl( container );

//        setThreeEnergyLevels( module.getThreeEnergyLevels() );
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
        waveViewControlPanel.setVisible( threeEnergyLevels );
        highLevelEmissionControlPanel.setVisible( threeEnergyLevels );
    }

    //--------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------

    private class BasicOptionsPanel extends JPanel {
        private JRadioButton twoLevelsRB;
        private JRadioButton threeLevelsRB;

        BasicOptionsPanel( boolean threeEnergyLevels ) {
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

            JPanel energyButtonPanel = new JPanel();
            energyButtonPanel.add( twoLevelsRB );
            energyButtonPanel.add( threeLevelsRB );

            energyButtonPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.EnergyLevelsBorderTitle" ) ) );

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
            twoLevelsRB.setSelected( !threeEnergyLevels );
            threeLevelsRB.setSelected( threeEnergyLevels );
            setThreeEnergyLevels( threeEnergyLevels );
        }
    }

}
