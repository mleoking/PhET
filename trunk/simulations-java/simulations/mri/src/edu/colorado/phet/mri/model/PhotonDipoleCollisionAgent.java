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

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.CollisionUtil;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.Photon;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public class PhotonDipoleCollisionAgent implements CollisionExpert {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static final boolean CONSUME_PHOTON_ON_COLLISION = true;

    private static Random random = new Random();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();
    private MriModel model;
    private CollisionProbablilityStrategy collisionProbablilityStrategy;

    public PhotonDipoleCollisionAgent( MriModel model ) {
        this.model = model;
        classifiedBodies.put( Photon.class, null );
        classifiedBodies.put( Dipole.class, null );

//        collisionProbablilityStrategy = new ConstantCollisionProbability( .7 );
        collisionProbablilityStrategy = new LinearCollisionProbability( 0, 1 );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {

        if( CollisionUtil.areConformantToClasses( body1, body2, Photon.class, Dipole.class ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Dipole dipole = (Dipole)classifiedBodies.get( Dipole.class );
            Photon photon = (Photon)classifiedBodies.get( Photon.class );

            if( collisionShouldOccur( photon, dipole ) ) {
                dipole.collideWithPhoton( photon );
                if( CONSUME_PHOTON_ON_COLLISION && !( dipole instanceof TumorDipole ) ) {
                    photon.removeFromSystem();
                    model.removeModelElement( photon );
                }
                double timeout = MriConfig.SPIN_DOWN_TIMEOUT * ( 1 + random.nextGaussian() );
                model.addModelElement( new DipoleFlipper( dipole, Spin.DOWN, timeout, model, true ) );
            }
        }
        return false;
    }

    private boolean applyProbabilityStrategy( Dipole dipole ) {
        return random.nextDouble() <= collisionProbablilityStrategy.getProbability( dipole );
    }

    private boolean collisionShouldOccur( Photon photon, Dipole dipole ) {
        boolean result = true;

        // Only spin down dipoles (lower energy state) can be affected by photons
        result &= dipole.getSpin() == Spin.DOWN;

        // If the photon was emitted by a dipole flipping back to its lower energy state, don't consider it
        result &= !( photon instanceof MriEmittedPhoton
                     && !MriConfig.REABSORPTION_ALLOWED );

        // Check for physical proximity
        result &= photon.getPosition().distanceSq( dipole.getPosition() ) < dipole.getRadius() * dipole.getRadius();

        // Apply the likelihood strategy
        result &= applyProbabilityStrategy( dipole );

        // The energy of the photon must be aprox. equal to the strength of the net magnetic field at the
        // dipole's location times the sample material's mu.
        double hEnergy = PhysicsUtil.frequencyToEnergy( model.getTotalFieldStrengthAt( dipole.getPosition() ) * model.getSampleMaterial().getMu() );
//        double hEnergy = PhysicsUtil.frequencyToEnergy( model.getTotalFieldStrengthAt( dipole.getPosition().getX() ) * model.getSampleMaterial().getMu() );
        result &= Math.abs( hEnergy - photon.getEnergy() ) < MriConfig.ENERGY_EPS;

        return result;
    }

    //--------------------------------------------------------------------------------------------------
    // Collision probability strategies
    //--------------------------------------------------------------------------------------------------

    public static interface CollisionProbablilityStrategy {
        double getProbability( Dipole dipole );
    }

    /**
     *
     */
    public static class ConstantCollisionProbability implements CollisionProbablilityStrategy {
        private double probability;

        public ConstantCollisionProbability( double probability ) {
            this.probability = probability;
        }

        public double getProbability( Dipole dipole ) {
            return probability;
        }
    }

    /**
     *
     */
    public class LinearCollisionProbability implements CollisionProbablilityStrategy {
        private double m;
        private double minProbability;
        private double maxProbability;

        public LinearCollisionProbability( double minProbability, double maxProbability ) {
            this.minProbability = minProbability;
            this.maxProbability = maxProbability;
            double yMax = model.getRadiowaveSource().getPosition().getY() - MriConfig.SAMPLE_CHAMBER_LOCATION.getY();
//            double yMax = model.getRadiowaveSource().getPosition().getY() - model.getSampleChamber().getPosition().getY();
            m = ( maxProbability - minProbability ) / yMax;
        }

        public double getProbability( Dipole dipole ) {
            double dy = model.getRadiowaveSource().getPosition().getY() - MriConfig.SAMPLE_CHAMBER_LOCATION.getY() - dipole.getPosition().getY();
            double probability = minProbability + m * dy;
            return probability;
        }
    }
}

