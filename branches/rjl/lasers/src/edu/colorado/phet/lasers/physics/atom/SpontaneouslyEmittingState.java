/**
 * Class: SpontaneouslyEmittingState
 * Package: edu.colorado.phet.lasers.physics.atom
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.lasers.physics.atom;

import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.physics.Vector2D;

/**
 *
 */
public abstract class SpontaneouslyEmittingState extends AtomicState {

    private float lifeTime;
    private float deathTime;

    /**
     *
     * @param atom
     */
    public SpontaneouslyEmittingState( Atom atom ) {
        super( atom );
        float temp = 0;
        while( temp == 0 ) {
            temp = (float)Math.random();
        }
        // Assign a deathtime based on an exponential distribution
        deathTime = -(float)Math.log( temp ) * getSpontaneousEmmisionHalfLife();
        lifeTime = 0;
    }

    /**
     *
     * @param dt
     */
    public void stepInTime( float dt ) {
        lifeTime += dt;
        if( lifeTime >= deathTime ) {
            Photon emittedPhoton = emitPhoton();

            float speed = emittedPhoton.getVelocity().getLength();
            double theta = Math.random() * Math.PI * 2;
            float x = speed * (float) Math.cos( theta );
            float y = speed * (float) Math.sin( theta );
            emittedPhoton.setVelocity( x, y );
            emittedPhoton.setPosition( new Vector2D( getAtom().getPosition() ));

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D( emittedPhoton.getVelocity() ).normalize();
            Vector2D position = new Vector2D( getAtom().getPosition() );
            position.add( vHat.multiply( getAtom().getRadius() + 10 ));
            emittedPhoton.setPosition( position );

            getAtom().emitPhoton( emittedPhoton );

            // Change state
            decrementNumInState();
            getAtom().setState( nextLowerEnergyState() );
        }
    }

    protected Photon emitPhoton() {
        Photon emittedPhoton = Photon.create();
        emittedPhoton.setWavelength( getEmittedPhotonWavelength() );

        return emittedPhoton;
    }

    //
    // Abstract methods
    //
    abstract protected float getSpontaneousEmmisionHalfLife();
    abstract protected AtomicState nextLowerEnergyState();
    abstract protected float getEmittedPhotonWavelength();

    //
    // Static fields and methods
    //
}
