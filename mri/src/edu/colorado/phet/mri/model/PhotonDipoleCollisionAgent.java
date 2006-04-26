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
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.MriEmittedPhoton;
import edu.colorado.phet.mri.model.MriModel;

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

            // If the photon was emitted by a dipole flipping back to its lower energy state, don't consider it
            if( photon instanceof MriEmittedPhoton && !MriConfig.REABSORPTION_ALLOWED ) {
                return false;
            }

            if( dipole != null && photon != null ) {
                if( photon.getPosition().distanceSq( dipole.getPosition() ) < dipole.getRadius() * dipole.getRadius()
                    && applyProbabilityStrategy( dipole ) ) {
                    // If the difference between the energy states of the spin up and down is equal to the
                    // energy of the photon, and the dipole is in the spin up (lower energy) state, flip the
                    // dipole
                    double hEnergy = PhysicsUtil.frequencyToEnergy( model.getTotalFieldStrengthAt( photon.getPosition().getX() ) * model.getSampleMaterial().getMu() );
                    if( dipole.getSpin() == Spin.DOWN
                        && Math.abs( hEnergy - photon.getEnergy() )
                           < MriConfig.ENERGY_EPS ) {
                        dipole.collideWithPhoton( photon );
                        if( CONSUME_PHOTON_ON_COLLISION ) {
                            photon.removeFromSystem();
                            model.removeModelElement( photon );
                        }
                        model.addModelElement( new DipoleFlipper( dipole, MriConfig.SPIN_DOWN_TIMEOUT, model, true ) );
                    }
                }
            }
        }
        return false;
    }

    private boolean applyProbabilityStrategy( Dipole dipole ) {
        return random.nextDouble() <= collisionProbablilityStrategy.getProbability( dipole );
    }

    //--------------------------------------------------------------------------------------------------
    // Collision probability strategies
    //--------------------------------------------------------------------------------------------------

    public static interface CollisionProbablilityStrategy {
        double getProbability( Dipole dipole );
    }

    public static class ConstantCollisionProbability implements CollisionProbablilityStrategy {
        private double probability;

        public ConstantCollisionProbability( double probability ) {
            this.probability = probability;
        }

        public double getProbability( Dipole dipole ) {
            return probability;
        }
    }

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

