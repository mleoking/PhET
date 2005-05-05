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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Random;

/**
 * AtomTypeChooser
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AtomTypeChooser extends JDialog {
    private GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 10, 0, 10 ), 0, 0 );

    public AtomTypeChooser( DischargeLampModule module ) {
        super( PhetApplication.instance().getPhetFrame(), false );

        JPanel contentPane = new JPanel( new GridBagLayout() );
        setContentPane( contentPane );

        gbc.gridy = GridBagConstraints.RELATIVE;
        JRadioButton hydrogenRB = new JRadioButton( new HydrogenAction( module ) );
        JRadioButton neonRB = new JRadioButton( new NeonAction( module ) );
        JRadioButton mercuryRB = new JRadioButton( new MercuryAction( module ) );
        JRadioButton sodiumRB = new JRadioButton( new SodiumAction( module ) );
        JRadioButton mysteryRB = new JRadioButton( new MysteryAtomAction( module ) );

        ButtonGroup atomTypeBtnGrp = new ButtonGroup();
        atomTypeBtnGrp.add( hydrogenRB );
        atomTypeBtnGrp.add( neonRB );
        atomTypeBtnGrp.add( mercuryRB );
        atomTypeBtnGrp.add( sodiumRB );
        atomTypeBtnGrp.add( mysteryRB );

        contentPane.add( hydrogenRB, gbc );
        contentPane.add( neonRB, gbc );
        contentPane.add( mercuryRB, gbc );
        contentPane.add( sodiumRB, gbc );
        contentPane.add( mysteryRB, gbc );

        JFrame parent = PhetApplication.instance().getPhetFrame();
        Point parentLoc = parent.getLocationOnScreen();
        Point loc = new Point( (int)( parentLoc.x + 0.6 * parent.getWidth() ), (int)parentLoc.y + 50 );
        this.setLocation( loc );
        this.pack();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

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
            Function.LinearFunction lf = new Function.LinearFunction( energies[0], eBlue,
                                                                      states[0].getEnergyLevel(), Photon.wavelengthToEnergy( Photon.BLUE ) );
            double f = states[0].getEnergyLevel() / Math.abs( energies[0] );
            for( int i = 1; i < states.length; i++ ) {
                states[i] = new AtomicState();
                double energy = ( energies[i] );
//                double energy = ( lf.evaluate( energies[i] ));
//                double energy = ( energies[i] - energies[0] ) * f + states[0].getEnergyLevel();
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
        private double[] energies = new double[]{0, Photon.RED, Photon.BLUE};
        private Random random = new Random();

        public MysteryAtomAction( DischargeLampModule module ) {
            super( "Mystery Atom", module );

            int numWavelengths = random.nextInt( 8 ) + 2;
            energies = new double[numWavelengths];
            energies[0] = 0;
            for( int i = 1; i < numWavelengths; i++ ) {
                double wavelength = Photon.RED + random.nextDouble() * ( Photon.BLUE - Photon.RED );
                energies[i] = wavelength;
            }
            Arrays.sort( energies );
        }

        protected double[] getEnergies() {
            return energies;
        }
    }
}
