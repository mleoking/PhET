/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.util.Random;

/**
 * This is the base class for the strategies that define how a molecule
 * reacts to a given photon.  It is responsible for the following:
 * - Whether a given photon should be absorbed.
 * - How the molecule reacts to the absorption of a photon, i.e. whether
 *   it vibrates, rotates, breaks apart, etc.
 * - Maintenance of any counters or timers associated with the reaction to
 *   the absorption.
 */
public abstract class PhotonAbsorptionStrategy {

    private static final double MIN_PHOTON_HOLD_TIME = 600; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 1200; // Milliseconds of sim time.

    private static final Random RAND = new Random();

    private final Molecule molecule;
    private Photon lastPhoton;

    // Variables involved in the holding and re-emitting of photons.
    private Photon absorbedPhoton;
    private boolean isPhotonAbsorbed = false;
    private double photonHoldCountdownTime = 0;

    /**
     * Constructor.
     */
    public PhotonAbsorptionStrategy( Molecule molecule ) {
        this.molecule = molecule;
    }

    protected Molecule getMolecule(){
        return molecule;
    }

    /**
     * Step the strategy forward in time by the given time.
     *
     * @param dt
     */
    public abstract void stepInTime( double dt );

    /**
     * Reset the strategy.  In most cases, this will need to be overridden in the descendant classes, but those
     * overrides should also call this one.
     */
    public void reset(){
        absorbedPhoton = null;
        isPhotonAbsorbed = false;
        photonHoldCountdownTime = 0;
    }

    /**
     * Decide whether the provided photon should be absorbed.  By design,
     * a given photon should only be requested once, not multiple times.
     * @param photon
     * @return
     */
    public boolean quearyAbsorbPhoton( Photon photon ) {

        // Debug/test code.
        if ( lastPhoton != null ) {
            System.err.println( getClass().getName() + " - Error: Multiple requests to absorb the same photon." );
            assert ( lastPhoton != photon );
        }

        boolean absorbPhoton = false;
        if ( !isPhotonAbsorbed && photonHoldCountdownTime <= 0 ) {
            // All circumstances are correct for photon absorption, so now
            // we decide probabalistically whether or not to actually do
            // it.  This essentially simulates the quantum nature of the
            // absorption.

            if ( RAND.nextDouble() < SingleMoleculePhotonAbsorptionProbability.getInstance().getAbsorptionsProbability() ) {
                absorbPhoton = true;
                isPhotonAbsorbed = true;
                photonHoldCountdownTime = MIN_PHOTON_HOLD_TIME + RAND.nextDouble() * ( MAX_PHOTON_HOLD_TIME - MIN_PHOTON_HOLD_TIME );
            }
            else {
                // Do NOT absorb it.
                absorbPhoton = false;
            }
        }

        return absorbPhoton;
    }

    protected boolean isPhotonAbsorbed() {
        return isPhotonAbsorbed;
    }

    public static class VibrationStrategy extends PhotonAbsorptionStrategy {

        public VibrationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            getMolecule().advanceVibration( dt * Molecule.VIBRATION_FREQUENCY / 1000 * 2 * Math.PI );
        }
    }

    public static class RotationStrategy extends PhotonAbsorptionStrategy {

        public RotationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            // TODO Auto-generated method stub
        }
    }

    public static class BreakApartStrategy extends PhotonAbsorptionStrategy {

        public BreakApartStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            // TODO Auto-generated method stub
        }
    }

    public static class ExcitationStrategy extends PhotonAbsorptionStrategy {

        public ExcitationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            // TODO Auto-generated method stub
        }
    }

    public static class NullPhotonAbsorptionStrategy extends PhotonAbsorptionStrategy {
        /**
         * Constructor.
         */
        public NullPhotonAbsorptionStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            // Does nothing.
        }
    }
}