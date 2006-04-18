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

import java.util.HashMap;
import java.util.Map;

public class PhotonDipoleExpert implements CollisionExpert {
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();
    private MriModel model;

    public PhotonDipoleExpert( MriModel model ) {
        this.model = model;
        classifiedBodies.put( Photon.class, null );
        classifiedBodies.put( Dipole.class, null );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if( CollisionUtil.areConformantToClasses( body1, body2, Photon.class, Dipole.class ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Dipole dipole = (Dipole)classifiedBodies.get( Dipole.class );
            Photon photon = (Photon)classifiedBodies.get( Photon.class );
            if( dipole != null && photon != null ) {
                if( photon.getPosition().distanceSq( dipole.getPosition() ) < dipole.getRadius() * dipole.getRadius() )
                {
                    // If the difference between the energy states of the spin up and down is equal to the
                    // energy of the photon, and the dipole is in the spin up (lower energy) state, flip the
                    // dipole
                    double hEnergy = PhysicsUtil.frequencyToEnergy( model.getLowerMagnet().getFieldStrength() * model.getSampleMaterial().getMu() );
                    if( dipole.getSpin() == Spin.UP
                        && Math.abs( hEnergy - photon.getEnergy() )
                           < MriConfig.ENERGY_EPS ) {
                        dipole.collideWithPhoton( photon );
                        photon.removeFromSystem();
                        model.removeModelElement( photon );
                        model.addModelElement( new DipoleFlipper( dipole, MriConfig.SPIN_DOWN_TIMEOUT, model ) );
                    }
                }
            }
        }
        return false;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class DipoleFlipper implements ModelElement {
        private Dipole dipole;
        private long timeout;
        private MriModel model;
        private long elapsedTime;

        DipoleFlipper( Dipole dipole, long timeout, MriModel model ) {
            this.dipole = dipole;
            this.timeout = timeout;
            this.model = model;
        }

        public void stepInTime( double dt ) {
            elapsedTime += dt;
            if( elapsedTime >= timeout ) {
                dipole.setSpin( Spin.UP );
                model.removeModelElement( this );

                Vector2D velocity = new Vector2D.Double( MriConfig.EMITTED_PHOTON_DIRECTION ).normalize().scale( Photon.SPEED );
                double wavelength = PhysicsUtil.frequencyToWavelength( model.getLowerMagnet().getFieldStrength() * model.getSampleMaterial().getMu() );
                Photon photon = Photon.create( wavelength, dipole.getPosition(), velocity );
                model.addModelElement( photon );
            }
        }
    }
}
