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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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
    private GridBagConstraints gbc;
    private ControlPanel laserControlPane;

    public LaserControlPanel( BaseLaserModule module, AbstractClock clock ) {
        super( module );
        this.laserModule = module;
        laserControlPane = new ControlPanel( (LaserModel)module.getModel(), clock );
        super.setControlPane( laserControlPane );

        addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                setThreeEnergyLevels( threeEnergyLevels );
            }
        } );
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
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
            twoLevelsRB.setSelected( true );

            JPanel energyButtonPanel = new JPanel();
            energyButtonPanel.add( twoLevelsRB );
            energyButtonPanel.add( threeLevelsRB );
            energyButtonPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.EnergyLevelsBorderTitle" ) ) );

            String s = GraphicsUtil.formatMessage( SimStrings.get( "LaserControlPanel.EmissionCheckBox" ) );
            final JCheckBox showHighToMidEmissionCB = new JCheckBox( s );
            showHighToMidEmissionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (LaserSimulation)PhetApplication.instance() ).displayHighToMidEmission( showHighToMidEmissionCB.isSelected() );
                }
            } );
            JPanel optionsPanel = new JPanel( new GridLayout( 1, 1 ) );
            optionsPanel.add( showHighToMidEmissionCB );
            optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );

            pumpingBeamControl = new PumpingBeamControl( model.getPumpingBeam() );
            pumpingBeamControl.setVisible( threeEnergyLevels );

            this.setLayout( new GridBagLayout() );
            gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
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
            this.add( pumpingBeamControl, gbc );

            // Set the number of energy levels we'll see
            twoLevelsRB.setSelected( true );
            setThreeEnergyLevels( false );
        }
    }

    public void addControl( Component component ) {
        gbc.gridy++;
        laserControlPane.add( component, gbc );
    }
}
