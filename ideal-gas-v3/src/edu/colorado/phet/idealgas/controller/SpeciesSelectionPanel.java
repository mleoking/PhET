/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.GasMolecule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A JPanel with two spinners for adding and removing molecules from the model.
 */
public abstract class SpeciesSelectionPanel extends JPanel implements IdealGasModule.ResetListener {
    private IdealGasModule module;
    private MoleculeCountSpinner heavySpinner;
    private MoleculeCountSpinner lightSpinner;
    private JLabel heavySpeciesLbl;
    private JLabel lightSpeciesLbl;


    public SpeciesSelectionPanel( final IdealGasModule module ) {
        this( module, new String[]{SimStrings.get( "Common.Heavy_Species" ), SimStrings.get( "Common.Light_Species" )} );
    }

    public SpeciesSelectionPanel( final IdealGasModule module, final String[] speciesNames ) {
        this.module = module;
        module.addResetListener( this );

        // Radio buttons
        makeRadioButtons( speciesNames );

        // Spinners for the species
        makeSpinners();

        // Lay out the panel
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 0, 0, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST, GridBagConstraints.NONE,
                                                         insets, 0, 0 );
        add( heavySpeciesLbl, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( heavySpinner, gbc );
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add( lightSpeciesLbl, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( lightSpinner, gbc );

        // For debug: button fires a molecule when pressed
        {
            boolean debug = false;
            if( debug ) {
                JButton testBtn = new JButton( "Test" );
                testBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        HeavySpecies m = new HeavySpecies( new Point2D.Double( module.getBox().getPosition().getX() + 300,
                                                                               module.getBox().getPosition().getY() + 30 ),
                                                           new Vector2D.Double( -53, -20 ),
                                                           new Vector2D.Double() );
                        new PumpMoleculeCmd( (IdealGasModel)module.getModel(), m, module ).doIt();
                        heavySpinner.setValue( new Integer( 1 ) );
                        ( (PumpControlPanel)SpeciesSelectionPanel.this ).moleculeAdded( m );
                    }
                } );
                gbc.gridy++;
                this.add( testBtn, gbc );
            }
        }

    }

    /**
     * Sets up the radio buttons for selecting a species
     */
    private void makeRadioButtons( String[] speciesNames ) {
        heavySpeciesLbl = new JLabel( speciesNames[0] );
        heavySpeciesLbl.setForeground( Color.blue );
        lightSpeciesLbl = new JLabel( speciesNames[1] );
        lightSpeciesLbl.setForeground( Color.red );
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
        heavySpinner = new MoleculeCountSpinner( heavySpinnerModel );
        heavySpinner.setPreferredSize( new Dimension( 50, 20 ) );
        heavySpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( heavySpinner.isEnabled() ) {
                    int dn = ( (Integer)heavySpinner.getValue() ).intValue() - getHeavySpeciesCnt();
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
            }
        } );

        // Spinner for light species
        SpinnerNumberModel lightSpinnerModel = new SpinnerNumberModel( value, min, max, step );
        lightSpinner = new MoleculeCountSpinner( lightSpinnerModel );
        lightSpinner.setPreferredSize( new Dimension( 50, 20 ) );
        lightSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( lightSpinner.isEnabled() ) {
                    int dn = ( (Integer)lightSpinner.getValue() ).intValue() - getLightSpeciesCnt();
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
            }
        } );
    }

    public void resetOccurred( IdealGasModule.ResetEvent event ) {
        heavySpinner.setEnabled( false );
        lightSpinner.setEnabled( false );
        heavySpinner.setValue( new Integer( 0 ) );
        lightSpinner.setValue( new Integer( 0 ) );
        heavySpinner.setEnabled( true );
        lightSpinner.setEnabled( true );
    }

    //----------------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------------
    protected IdealGasModule getModule() {
        return module;
    }

    public MoleculeCountSpinner getHeavySpinner() {
        return heavySpinner;
    }

    public MoleculeCountSpinner getLightSpinner() {
        return lightSpinner;
    }

    public void setHeavySpeciesLabelText( String text ) {
        heavySpeciesLbl.setText( text );
    }

    public void setLightSpeciesLabelText( String text ) {
        lightSpeciesLbl.setText( text );
    }

    public void setHeavySpeciesLabelColor( Color color ) {
        heavySpeciesLbl.setForeground( color );
    }

    public void setLightSpeciesLabelColor( Color color ) {
        lightSpeciesLbl.setForeground( color );
    }

    //----------------------------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------------------------
    protected abstract void createMolecule( Class moleculeClass );

    protected abstract void removeMolecule( Class moleculeClass );

    protected abstract int getHeavySpeciesCnt();

    protected abstract int getLightSpeciesCnt();

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    public class MoleculeCountSpinner extends JSpinner {

        public MoleculeCountSpinner( SpinnerModel model ) {
            super( model );
        }

        public void incrementValue() {
            changeValue( ( (Integer)getValue() ).intValue() + 1 );
        }

        public void decrementValue() {
            changeValue( ( (Integer)getValue() ).intValue() - 1 );
        }

        private void changeValue( int value ) {
            boolean isEnabled = isEnabled();
            setEnabled( false );
            setValue( new Integer( value ) );
            setEnabled( isEnabled );
        }
    }
}
