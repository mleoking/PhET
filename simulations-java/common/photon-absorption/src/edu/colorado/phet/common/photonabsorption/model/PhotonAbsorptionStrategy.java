// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.model;

import java.util.Random;

/**
 * This is the base class for the strategies that define how a molecule
 * reacts to a given photon.  It is responsible for the following:
 * - Whether a given photon should be absorbed.
 * - How the molecule reacts to the absorption,, i.e. whether it vibrates,
 *   rotates, breaks apart, etc.
 * - Maintenance of any counters or timers associated with the reaction to
 *   the absorption, such as those related to re-emission of an absorbed
 *   photon.
 */
public abstract class PhotonAbsorptionStrategy {

    private static final double MIN_PHOTON_HOLD_TIME = 600; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 1200; // Milliseconds of sim time.

    private static final Random RAND = new Random();

    private final Molecule molecule;

    // Variables involved in the holding and re-emitting of photons.
    protected Photon absorbedPhoton;
    protected boolean isPhotonAbsorbed = false;
    protected double photonHoldCountdownTime = 0;

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
    public boolean queryAndAbsorbPhoton( Photon photon ) {
        // All circumstances are correct for photon absorption, so now we decide probabilistically whether or not to
        // actually do it.  This essentially simulates the quantum nature of the absorption.
        final boolean absorbed = !isPhotonAbsorbed && RAND.nextDouble() < SingleMoleculePhotonAbsorptionProbability.getInstance().getAbsorptionsProbability();
        if (absorbed){
            isPhotonAbsorbed = true;
            photonHoldCountdownTime = MIN_PHOTON_HOLD_TIME + RAND.nextDouble() * ( MAX_PHOTON_HOLD_TIME - MIN_PHOTON_HOLD_TIME );
        }
        return absorbed;
    }

    protected boolean isPhotonAbsorbed() {
        return isPhotonAbsorbed;
    }

    public static abstract class PhotonHoldStrategy extends PhotonAbsorptionStrategy{

        private double absorbedWavelength;

        public PhotonHoldStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            photonHoldCountdownTime -= dt;
            if ( photonHoldCountdownTime <= 0 ) {
                reemitPhoton();
            }
        }

        protected void reemitPhoton() {
            getMolecule().emitPhoton( absorbedWavelength );
            getMolecule().setActiveStrategy( new NullPhotonAbsorptionStrategy( getMolecule() ) );
            isPhotonAbsorbed = false;
        }

        @Override
        public boolean queryAndAbsorbPhoton( Photon photon ) {
            final boolean absorbed = super.queryAndAbsorbPhoton( photon );
            if ( absorbed ) {
                this.absorbedWavelength = photon.getWavelength();
                photonAbsorbed();
            }
            return absorbed;
        }

        protected abstract void photonAbsorbed();
    }

    public static class VibrationStrategy extends PhotonHoldStrategy {

        public VibrationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        protected void photonAbsorbed() {
            getMolecule().setVibrating( true );
        }

        @Override
        protected void reemitPhoton() {
            super.reemitPhoton();
            getMolecule().setVibrating( false );
            getMolecule().setVibration( 0 );
        }

    }

    public static class RotationStrategy extends PhotonHoldStrategy {

        public RotationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        protected void photonAbsorbed() {
            getMolecule().setRotationDirectionClockwise( RAND.nextBoolean() );
            getMolecule().setRotating( true );
        }

        @Override
        protected void reemitPhoton() {
            super.reemitPhoton();
            getMolecule().setRotating( false );
        }

    }

    public static class BreakApartStrategy extends PhotonAbsorptionStrategy {

        public BreakApartStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            // Basically, all this strategy does is to instruct the molecule
            // to break apart, then reset the strategy.
            getMolecule().breakApart();
            getMolecule().setActiveStrategy( new NullPhotonAbsorptionStrategy( getMolecule() ) );
        }
    }

    public static class ExcitationStrategy extends PhotonHoldStrategy {

        public ExcitationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        protected void photonAbsorbed() {
            getMolecule().setHighElectronicEnergyState( true );
        }

        @Override
        protected void reemitPhoton() {
            super.reemitPhoton();
            getMolecule().setHighElectronicEnergyState( false );
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

        /**
         * This strategy never absorbs.
         * @param photon
         * @return
         */
        @Override
        public boolean queryAndAbsorbPhoton( Photon photon ) {
            return false;
        }
    }
}