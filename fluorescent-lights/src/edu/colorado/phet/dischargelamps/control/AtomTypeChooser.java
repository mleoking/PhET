/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

/**
 * AtomTypeChooser
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AtomTypeChooser extends JPanel {
    private GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 10, 0, 10 ), 0, 0 );

    public AtomTypeChooser( DischargeLampModule module ) {
        super( new GridBagLayout() );

        JLabel label = new JLabel( SimStrings.get( "ControlPanel.AtomTypeButtonLabel" ) );
        gbc.anchor = GridBagConstraints.EAST;
        this.add( label, gbc );

        JComboBox comboBox = new JComboBox();

        comboBox.addItem( new HydrogenItem( module ) );
        comboBox.addItem( new NeonItem( module ) );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JComboBox cb = (JComboBox)e.getSource();
                // Get the selected item and tell it to do its thing
                AtomTypeItem item = (AtomTypeItem)cb.getSelectedItem();
                item.select();
            }
        } );

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        this.add( comboBox, gbc );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    abstract private class AtomTypeItem {
        private DischargeLampModule module;

        //----------------------------------------------------------------
        // Abstract methods
        //----------------------------------------------------------------

        abstract protected double[] getEnergies();

        protected AtomTypeItem( DischargeLampModule module ) {
            this.module = module;
        }

        void select() {
            module.setAtomicStates( this.getStates() );
        }

        private AtomicState[] getStates() {
            // Copy the energies into a new array, sort and normalize them
            double[] energies = new double[getEnergies().length];
            for( int i = 0; i < energies.length; i++ ) {
                energies[i] = getEnergies()[i];
            }
            Arrays.sort( energies );

            AtomicState[] states = new AtomicState[energies.length];
            states[0] = new GroundState();
            states[0].setEnergyLevel( energies[0] );
            double eBlue = PhysicsUtil.wavelengthToEnergy( Photon.BLUE );
            for( int i = 1; i < states.length; i++ ) {
                states[i] = new AtomicState();
                double energy = ( energies[i] );
                states[i].setEnergyLevel( energy );
                states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
            }
            AtomicState.linkStates( states );
            return states;
        }
    }

    private class HydrogenItem extends AtomTypeItem {
        private double[] energies = new double[]{-13.6,
                                                 -3.4,
                                                 -1.511,
                                                 -0.850,
                                                 -0.544,
                                                 -0.378};

        public HydrogenItem( DischargeLampModule module ) {
            super( module );
        }

        public String toString() {
            return "Hydrogen";
        }

        protected double[] getEnergies() {
            return energies;
        }
    }

    private class NeonItem extends AtomTypeItem {
        private double[] energies = new double[]{-13.6,
                                                 -3.4,
                                                 -1.511,
                                                 -0.850,
                                                 -0.544,
                                                 -0.378};

        public NeonItem( DischargeLampModule module ) {
            super( module );
        }

        public String toString() {
            return "Neon";
        }

        protected double[] getEnergies() {
            return energies;
        }
    }


    /**
     * An action for setting the type of atoms in the lamp
     */
    abstract private class AtomTypeSelectionAction extends AbstractAction {
        protected DischargeLampModule module;

        protected AtomTypeSelectionAction( String name, DischargeLampModule module ) {
            super( name );
            this.module = module;
        }

        public void actionPerformed( ActionEvent e ) {
            module.setAtomicStates( this.getStates() );
        }

        abstract protected double[] getEnergies();

        protected AtomicState[] getStates() {
            // Copy the energies into a new array, sort and normalize them
            double[] energies = new double[getEnergies().length];
            for( int i = 0; i < energies.length; i++ ) {
                energies[i] = getEnergies()[i];
            }
            Arrays.sort( energies );

            AtomicState[] states = new AtomicState[energies.length];
            states[0] = new GroundState();
            states[0].setEnergyLevel( energies[0] );
            double eBlue = PhysicsUtil.wavelengthToEnergy( Photon.BLUE );
            for( int i = 1; i < states.length; i++ ) {
                states[i] = new AtomicState();
                double energy = ( energies[i] );
                states[i].setEnergyLevel( energy );
                states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
            }
            AtomicState.linkStates( states );
            return states;
        }
    }

    private class HydrogenAction extends AtomTypeSelectionAction {
        private double[] energies = new double[]{-13.6,
                                                 -3.4,
                                                 -1.511,
                                                 -0.850,
                                                 -0.544,
                                                 -0.378};


        public HydrogenAction( DischargeLampModule module ) {
            super( "Hydrogen", module );
        }

        protected double[] getEnergies() {
            return energies;
        }
    }

    private class NeonAction extends AtomTypeSelectionAction {
        private double[] energies = new double[]{0,
                                                 703.2,
                                                 697.9,
                                                 659.9,
                                                 650.6,
                                                 640.2,
                                                 638.3,
                                                 633.4,
                                                 626.6,
                                                 621.7,
                                                 616.4,
                                                 607.4,
                                                 603.0,
                                                 588.2,
                                                 585.2,
                                                 540.1};

        public NeonAction( DischargeLampModule module ) {
            super( "Neon", module );
        }

        protected double[] getEnergies() {
            return energies;
        }
    }

    private class MercuryAction extends AtomTypeSelectionAction {
        private double[] energies = new double[]{0,
                                                 579.065,
                                                 576.959,
                                                 546.074,
                                                 435.835,
                                                 407.781,
                                                 404.656};

        public MercuryAction( DischargeLampModule module ) {
            super( "Mercury", module );
        }

        protected double[] getEnergies() {
            return energies;
        }
    }

    private class SodiumAction extends AtomTypeSelectionAction {
        private double[] energies = new double[]{0,
                                                 589.0,
                                                 589.6};

        public SodiumAction( DischargeLampModule module ) {
            super( "Sodium", module );
        }

        protected double[] getEnergies() {
            return energies;
        }
    }

    private class MysteryAtomAction extends AtomTypeSelectionAction {
        private double[] energies = new double[]{-10, Photon.wavelengthToEnergy( Photon.RED ), Photon.wavelengthToEnergy( Photon.BLUE )};
        private Random random = new Random();

        public MysteryAtomAction( DischargeLampModule module ) {
            super( "Mystery Atom", module );

            int numWavelengths = random.nextInt( 8 ) + 2;
            energies = new double[numWavelengths];
            energies[0] = 0;
            for( int i = 1; i < numWavelengths; i++ ) {
                double energy = PhysicsUtil.wavelengthToEnergy( Photon.RED ) +
                                random.nextDouble() * ( PhysicsUtil.wavelengthToEnergy( Photon.BLUE ) - PhysicsUtil.wavelengthToEnergy( Photon.RED ) );
                energies[i] = energy;
            }
            Arrays.sort( energies );
        }

        protected double[] getEnergies() {
            return energies;
        }
    }
}
