/**
 * Class: SpeciesSelectionPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 27, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public abstract class SpeciesSelectionPanel extends JPanel {
    private IdealGasModule module;
    private GasSource gasSource;
    private JSpinner heavySpinner;
    private JSpinner lightSpinner;
    private JRadioButton heavySpeciesRB;
    private JRadioButton lightSpeciesRB;


    public SpeciesSelectionPanel( final IdealGasModule module, final GasSource gasSource ) {
        this.module = module;
        this.gasSource = gasSource;

        // Radio buttons
        makeRadioButtons();

        // Spinners for the species
        makeSpinners();

        // Lay out the panel
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 0, 0, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                         insets, 0, 0 );
        add( heavySpeciesRB, gbc );
        gbc.gridx = 1;
        add( heavySpinner, gbc );
        gbc.gridx = 0;
        gbc.gridy = 1;
        add( lightSpeciesRB, gbc );
        gbc.gridx = 1;
        add( lightSpinner, gbc );
    }

    /**
     * Sets up the radio buttons for selecting a species
     */
    private void makeRadioButtons() {
        heavySpeciesRB = new JRadioButton( SimStrings.get( "Common.Heavy_Species" ) );
        heavySpeciesRB.setForeground( Color.blue );
        lightSpeciesRB = new JRadioButton( SimStrings.get( "Common.Light_Species" ) );
        lightSpeciesRB.setForeground( Color.red );
        final ButtonGroup speciesGroup = new ButtonGroup();
        speciesGroup.add( heavySpeciesRB );
        speciesGroup.add( lightSpeciesRB );

        heavySpeciesRB.setSelected( true );
        heavySpeciesRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
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

    /**
     * Sets up the spinners
     */
    private void makeSpinners() {
        // Set up the spinner for controlling the number of particles in
        // the hollow sphere
        Integer value = new Integer( 0 );
        Integer min = new Integer( 0 );
        Integer max = new Integer( 1000 );
        Integer step = new Integer( 1 );

        // Spinner for heavy species
        SpinnerNumberModel heavySpinnerModel = new SpinnerNumberModel( value, min, max, step );
        heavySpinner = new JSpinner( heavySpinnerModel );
        heavySpinner.setPreferredSize( new Dimension( 50, 20 ) );
        heavySpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dn = ((Integer)heavySpinner.getValue()).intValue()  - ( module.getIdealGasModel().getHeavySpeciesCnt() );
                if( dn > 0 ) {
                    for( int i = 0; i < dn; i++ ) {
                        createMolecule( HeavySpecies.class );
                    }
                }
                else if( dn < 0 ) {
                    for( int i = 0; i < -dn; i++ ) {
                        removeMolecule( HeavySpecies.class );
                    }
                }
            }
        } );

        // Hook the spinner up so it will track molecules put in the box by the pump
        module.getModel().addObserver( new SimpleObserver() {
            public void update() {
                int h = module.getIdealGasModel().getHeavySpeciesCnt();
                heavySpinner.setValue( new Integer( h ) );
            }
        } );


        // Spinner for light species
        SpinnerNumberModel lightSpinnerModel = new SpinnerNumberModel( value, min, max, step );
        lightSpinner = new JSpinner( lightSpinnerModel );
        lightSpinner.setPreferredSize( new Dimension( 50, 20 ) );
        lightSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dn = ((Integer)lightSpinner.getValue()).intValue()  - ( module.getIdealGasModel().getLightSpeciesCnt());
                if( dn > 0 ) {
                    for( int i = 0; i < dn; i++ ) {
                        createMolecule( LightSpecies.class );
                    }
                }
                else if( dn < 0 ) {
                    for( int i = 0; i < -dn; i++ ) {
                        removeMolecule( LightSpecies.class );
                    }
                }
            }
        } );

        // Hook the spinner up so it will track molecules put in the box by the pump
        module.getModel().addObserver( new SimpleObserver() {
            public void update() {
                int h = module.getIdealGasModel().getLightSpeciesCnt();
                lightSpinner.setValue( new Integer( h ) );
            }
        } );
    }

    //----------------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------------
    protected IdealGasModule getModule() {
        return module;
    }

    //----------------------------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------------------------
    protected abstract void createMolecule( Class moleculeClass );
    protected abstract void removeMolecule( Class moleculeClass );
}
