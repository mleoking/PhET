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
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
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
        private double[] wavelengths;

        protected AtomTypeSelectionAction( String name, DischargeLampModule module ) {
            super( name );
            this.module = module;
            this.wavelengths = wavelengths;
        }

        public void actionPerformed( ActionEvent e ) {
            module.setAtomicStates( this.getStates() );
        }

        protected void setWavelengths( double[] wavelengths ) {
            this.wavelengths = wavelengths;
        }

        protected AtomicState[] getStates() {
            AtomicState[] states = new AtomicState[wavelengths.length];
            states[0] = new GroundState();
            for( int i = 1; i < states.length; i++ ) {
                states[i] = new AtomicState();
                states[i].setEnergyLevel( Photon.wavelengthToEnergy( wavelengths[i] ) );
                states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
            }
            AtomicState.linkStates( states );
            return states;
        }
    }

    private class HydrogenAction extends AtomTypeSelectionAction {
        private double[] wavelengths = new double[]{0,
                                                    486.133,
                                                    656.285,
                                                    656.272,
                                                    434.047,
                                                    410.174,
                                                    397.0072,
                                                    388.905,
                                                    383.538};

        public HydrogenAction( DischargeLampModule module ) {
            super( "Hydrogen", module );
            setWavelengths( wavelengths );
        }
    }

    private class NeonAction extends AtomTypeSelectionAction {
        private double[] wavelengths = new double[]{0,
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
            setWavelengths( wavelengths );
        }
    }

    private class MercuryAction extends AtomTypeSelectionAction {
        private double[] wavelengths = new double[]{0,
                                                    579.065,
                                                    576.959,
                                                    546.074,
                                                    435.835,
                                                    407.781,
                                                    404.656};

        public MercuryAction( DischargeLampModule module ) {
            super( "Mercury", module );
            setWavelengths( wavelengths );
        }
    }

    private class SodiumAction extends AtomTypeSelectionAction {
        private double[] wavelengths = new double[]{0,
                                                    589.0,
                                                    589.6};

        public SodiumAction( DischargeLampModule module ) {
            super( "Sodium", module );
            setWavelengths( wavelengths );
        }
    }

    private class MysteryAtomAction extends AtomTypeSelectionAction {
        private double[] wavelengths = new double[]{0, Photon.RED, Photon.BLUE};
        private Random random = new Random();

        public MysteryAtomAction( DischargeLampModule module ) {
            super( "Mystery Atom", module );

            int numWavelengths = random.nextInt( 8 ) + 2;
            wavelengths = new double[numWavelengths];
            wavelengths[0] = 0;
            for( int i = 1; i < numWavelengths; i++ ) {
                double wavelength = Photon.RED + random.nextDouble() * ( Photon.BLUE - Photon.RED );
                wavelengths[i] = wavelength;
            }
            Arrays.sort( wavelengths );
            setWavelengths( wavelengths );
        }
    }
}
