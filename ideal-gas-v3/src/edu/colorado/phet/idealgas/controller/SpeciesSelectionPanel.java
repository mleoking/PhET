/**
 * Class: SpeciesSelectionPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 27, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.SimStrings;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SpeciesSelectionPanel extends JPanel {
//    private GasSource gasSource;

    public SpeciesSelectionPanel( final GasSource gasSource ) {
//        this.gasSource = gasSource;
        setLayout( new GridBagLayout() );
        final JRadioButton heavySpeciesRB = new JRadioButton( SimStrings.get( "Common.Heavy_Species" ) );
        heavySpeciesRB.setForeground( Color.blue );
        final JRadioButton lightSpeciesRB = new JRadioButton( SimStrings.get( "Common.Light_Species" ) );
        lightSpeciesRB.setForeground( Color.red );
        final ButtonGroup speciesGroup = new ButtonGroup();
        speciesGroup.add( heavySpeciesRB );
        speciesGroup.add( lightSpeciesRB );

        Insets insets = new Insets( 4, 4, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                         insets, 0, 0 );
        add( heavySpeciesRB, gbc );
        //            heavySpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
        //            lightSpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
        gbc.gridy = 1;
        add( lightSpeciesRB, gbc );

        heavySpeciesRB.setSelected( true );
        heavySpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed
                    ( ActionEvent
                    event ) {
                if( heavySpeciesRB.isSelected() ) {
                    gasSource.setCurrentGasSpecies( HeavySpecies.class );
                }
            }
        } );

        lightSpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed
                    ( ActionEvent
                    event ) {
                if( lightSpeciesRB.isSelected() ) {
                    gasSource.setCurrentGasSpecies( LightSpecies.class );
                }
            }
        } );
    }
}
