/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DipoleOrientationAgent
 * <p>
 * Sets the spins of all dipoles in the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleOrientationAgent implements Electromagnet.ChangeListener /*, ModelElement*/ {
    private Random random = new Random();
    private double maxUpPFraction = 0.9;
    private double fractionUp;
    private MriModel model;
    private SpinDeterminationPolicy spinDeterminationPolicy = MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY;

    /**
     * Constructor
     *
     * @param model
     */
    public DipoleOrientationAgent( MriModel model ) {
        this.model = model;
    }

    private void updateSpins() {
        List dipoles = model.getDipoles();
        spinDeterminationPolicy.setSpins( dipoles, fractionUp, model );
    }

    /**
     * When the field changes, the number of dipoles with each spin changes
     *
     * @param event
     */
    public void stateChanged( Electromagnet.ChangeEvent event ) {
        double fieldStrength = model.getUpperMagnet().getFieldStrength();
        fractionUp = 1 - ( 0.5 + ( 0.5 * fieldStrength / MriConfig.MAX_FADING_COIL_FIELD ));
        fractionUp *= maxUpPFraction;
        updateSpins();
    }

    public void setPolicy( SpinDeterminationPolicy policy ) {
        spinDeterminationPolicy = policy;
    }

    //----------------------------------------------------------------
    // Strategies for determining the spins of dipoles in the model
    //----------------------------------------------------------------

    public static interface SpinDeterminationPolicy {
        void setSpins( List dipoles, double fractionUp, MriModel model );
    }

    /**
     * Sets a fixed number of dipoles to each orientation
     */
    public static class DeterministicPolicy implements SpinDeterminationPolicy {
        Random random = new Random();
        long meanFlipTimout = 100;

        public void setSpins( List dipoles, double fractionUp, MriModel model ) {

            if( dipoles.size() > 0 ) {
                // Determine how many dipoles should be spin up
                int desiredNumUp = (int)Math.round( fractionUp * dipoles.size() );

                // Identify the dipoles that are spin up and spin down
                ArrayList upDipoles = new ArrayList();
                ArrayList downDipoles = new ArrayList();
                for( int i = 0; i < dipoles.size(); i++ ) {
                    Dipole dipole = (Dipole)dipoles.get( i );
                    List list = dipole.getSpin() == Spin.UP ? upDipoles : downDipoles;
                    list.add( dipole );
                }

                // Flip dipoles until we get the desired number spin up
                while( upDipoles.size() > desiredNumUp ) {
                    Dipole dipole = (Dipole)upDipoles.get( random.nextInt( upDipoles.size() ) );
                    dipole.setSpin( Spin.DOWN );
                    upDipoles.remove( dipole );
                    downDipoles.add( dipole );
                }

                while( upDipoles.size() < desiredNumUp ) {
                    Dipole dipole = (Dipole)downDipoles.get( random.nextInt( downDipoles.size() ) );
                    dipole.setSpin( Spin.UP );
                    downDipoles.remove( dipole );
                    upDipoles.add( dipole );
                }
            }
        }
    }


    /**
     * Notifies the DipoleOrientationAgent when a dipole has been flipped by a DipoleFLipper. This allows the
     * DipoleOrientationAgent to keep the numbers of spin up and spin down dipoles at the right number
     */
    private static class FlipWatcher implements Dipole.ChangeListener {
        private final DipoleOrientationAgent orientationAgent;

        public FlipWatcher( DipoleOrientationAgent orientationAgent ) {
            this.orientationAgent = orientationAgent;
        }

        public void spinChanged( Dipole.ChangeEvent event ) {
            orientationAgent.updateSpins();
            event.getDipole().removeChangeListener( this );
        }
    }

    /**
     *
     */
    public static class StocasticPolicy implements SpinDeterminationPolicy {
        Random random = new Random();

        public void setSpins( List dipoles, double fractionUp, MriModel model ) {
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                boolean up = fractionUp > random.nextDouble();
                if( up && dipole.getSpin() != Spin.UP ) {
                    dipole.setSpin( Spin.UP );
                }
                else if( !up && dipole.getSpin() != Spin.DOWN ) {
                    dipole.setSpin( Spin.DOWN );
                }
            }
        }
    }
}
