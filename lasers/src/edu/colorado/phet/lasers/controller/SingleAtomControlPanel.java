package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.LaserSimulation;
import edu.colorado.phet.lasers.controller.module.SingleAtomModule;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
public class SingleAtomControlPanel extends LaserControlPanel {
    private boolean threeEnergyLevels;
    private SingleAtomModule laserModule;

    public SingleAtomControlPanel( final SingleAtomModule module ) {
        super( module );
        this.laserModule = module;
        addControl( new Controls() );

        addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                setThreeEnergyLevels( threeEnergyLevels );
            }
        } );


        final String addMirrorsStr = SimStrings.get( "LaserControlPanel.AddMirrorsCheckBox" );
        final String removeMirrorsStr = SimStrings.get( "LaserControlPanel.RemoveMirrorsCheckBox" );
        final JCheckBox mirrorCB = new JCheckBox( addMirrorsStr );
        mirrorCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( mirrorCB.isSelected() ) {
                    mirrorCB.setText( removeMirrorsStr );
                    module.setMirrorsEnabled( true );
                }
                else {
                    mirrorCB.setText( addMirrorsStr );
                    module.setMirrorsEnabled( false );
                }
            }
        } );
        this.addControl( mirrorCB );
    }

    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
    }

    private class Controls extends JPanel {

        Controls() {
            GridBagConstraints gbc;

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
    }

}
