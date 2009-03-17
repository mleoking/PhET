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

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.controller.command.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * A JPanel with two spinners for adding and removing molecules from the model.
 */
public abstract class SpeciesSelectionPanel extends JPanel implements IdealGasModule.ResetListener {
    
    private static final Dimension PREFERRED_SPINNER_SIZE = new Dimension( 70, 20 );
    
    private IdealGasModule module;
    private MoleculeCountSpinner heavySpinner;
    private MoleculeCountSpinner lightSpinner;
    private JLabel heavySpeciesLbl;
    private JLabel lightSpeciesLbl;


    public SpeciesSelectionPanel( final IdealGasModule module ) {
        this( module, new String[]{IdealGasResources.getString( "Common.Heavy_Species" ), IdealGasResources.getString( "Common.Light_Species" )} );
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
        heavySpinner = new MoleculeCountSpinner( heavySpinnerModel, new IntegerValue() {
            public int getValue() {
                return getHeavySpeciesCnt();
            }
        } );
        heavySpinner.setPreferredSize( PREFERRED_SPINNER_SIZE );
        heavySpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                System.out.println( "heavySpinner stateChange " + heavySpinner.getValue() );//XXX
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
        lightSpinner = new MoleculeCountSpinner( lightSpinnerModel, new IntegerValue() {
            public int getValue() {
                return getLightSpeciesCnt();
            }
        } );
        lightSpinner.setPreferredSize( PREFERRED_SPINNER_SIZE );
        lightSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                System.out.println( "lightSpinner stateChanged " + lightSpinner.getValue() );//XXX
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
    public static interface IntegerValue {
        public int getValue();
    }

    public class MoleculeCountSpinner extends JSpinner {
        private IntegerValue value;

        public MoleculeCountSpinner( SpinnerModel model, IntegerValue value ) {
            super( model );
            this.value = value;
        }

//        public void incrementValue() {
//            changeValue( ( (Integer)getValue() ).intValue() + 1 );
//        }
//
//        public void decrementValue() {
//            changeValue( ( (Integer)getValue() ).intValue() - 1 );
//        }
//
//        private void changeValue( int value ) {
//            boolean isEnabled = isEnabled();
//            setEnabled( false );
//            setValue( new Integer( value ) );
//            setEnabled( isEnabled );
//        }

        public void updateValue() {
            setValue( new Integer( value.getValue() ) );
        }
    }
}
