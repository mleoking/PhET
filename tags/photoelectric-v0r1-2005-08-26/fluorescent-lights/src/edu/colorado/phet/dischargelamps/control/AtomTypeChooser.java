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

        comboBox.addItem( new DefaultAtomItem( module ) );
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

    private class DefaultAtomItem extends AtomTypeItem {
        private double[] energies = new double[]{-13.6,
                                                 -0.378};

        public DefaultAtomItem( DischargeLampModule module ) {
            super( module );
        }

        public String toString() {
            return "Default Atom";
        }

        protected double[] getEnergies() {
            return energies;
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

}
