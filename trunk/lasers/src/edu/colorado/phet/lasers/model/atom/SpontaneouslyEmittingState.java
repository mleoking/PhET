/**
 * Class: SpontaneouslyEmittingState
 * Package: edu.colorado.phet.lasers.model.atom
 * Author: Another Guy
 * Date: Mar 24, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 *
 */
public abstract class SpontaneouslyEmittingState extends AtomicState {

    private double lifeTime;
    private double deathTime;

    /**
     *
     * @param atom
     */
    public SpontaneouslyEmittingState( Atom atom ) {
        super( atom );
        double temp = 0;
        while( temp == 0 ) {
            temp = Math.random();
        }
        // Assign a deathtime based on an exponential distribution
        deathTime = -Math.log( temp ) * getSpontaneousEmmisionHalfLife();
        lifeTime = 0;
    }

    /**
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        lifeTime += dt;
        if( lifeTime >= deathTime ) {
            Photon emittedPhoton = emitPhoton();

            double speed = emittedPhoton.getVelocity().getMagnitude();
//            double speed = emittedPhoton.getVelocity().getLength();
            double theta = Math.random() * Math.PI * 2;
            double x = speed * Math.cos( theta );
            double y = speed * Math.sin( theta );
//            double x = speed * (double) Math.cos( theta );
//            double y = speed * (double) Math.sin( theta );
            emittedPhoton.setVelocity( x, y );
            emittedPhoton.setPosition( new Point2D.Double( getAtom().getPosition().getX(), getAtom().getPosition().getY() ));
//            emittedPhoton.setPosition( new Vector2D( getAtom().getPosition() ));

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( emittedPhoton.getVelocity() ).normalize();
//            Vector2D position = new Vector2D.Double( getAtom().getPosition() );
//            Vector2D vHat = new Vector2D( emittedPhoton.getVelocity() ).normalize();
            Vector2D position = new Vector2D.Double( getAtom().getPosition() );
            position.add( vHat.scale( getAtom().getRadius() + 10 ));
//            position.add( vHat.multiply( getAtom().getRadius() + 10 ));
            emittedPhoton.setPosition( position.getX(), position.getY() );

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
    abstract protected double getSpontaneousEmmisionHalfLife();
    abstract protected AtomicState nextLowerEnergyState();
    abstract protected double getEmittedPhotonWavelength();

    //
    // Static fields and methods
    //
}
