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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

/**
 *
 */
public abstract class SpontaneouslyEmittingState extends AtomicState {

    private static BaseModel s_model;

    public static void setModel( BaseModel model ) {
        s_model = model;
    }

    /**
     *
     */
    public static class StateLifetimeManager implements ModelElement {
        private Atom atom;
        private double lifeTime;
        private double deathTime;
        private SpontaneouslyEmittingState state;
        private boolean lifetimeFixed = false;

        public StateLifetimeManager( Atom atom ) {
            this.atom = atom;
            s_model.addModelElement( this );

            double temp = 0;
            while( temp == 0 ) {
                temp = Math.random();
            }

            if( atom.getState() instanceof SpontaneouslyEmittingState ) {
                state = (SpontaneouslyEmittingState)atom.getState();
            }
            else {
                throw new RuntimeException( "Atom not in a spontaneously emitting state" );
            }

            // Get the lifetime for this state
            if( lifetimeFixed ) {
                // This line gives a fixed death time
                deathTime = state.getMeanLifeTime();
            }
            else {
                // Assign a deathtime based on an exponential distribution
                deathTime = -Math.log( temp ) * state.getMeanLifeTime();
            }
            lifeTime = 0;
        }

        public void stepInTime( double dt ) {
            lifeTime += dt;
            if( lifeTime >= deathTime ) {
                Photon emittedPhoton = emitPhoton();

                double speed = emittedPhoton.getVelocity().getMagnitude();
                double theta = Math.random() * Math.PI * 2;
                double x = speed * Math.cos( theta );
                double y = speed * Math.sin( theta );
                emittedPhoton.setVelocity( x, y );
                emittedPhoton.setPosition( new Point2D.Double( atom.getPosition().getX(), atom.getPosition().getY() ) );

                // Place the replacement photon beyond the atom, so it doesn't collide again
                // right away
                Vector2D vHat = new Vector2D.Double( emittedPhoton.getVelocity() ).normalize();
                Vector2D position = new Vector2D.Double( atom.getPosition() );
                position.add( vHat.scale( atom.getRadius() + 10 ) );
                emittedPhoton.setPosition( position.getX(), position.getY() );
                atom.emitPhoton( emittedPhoton );

                // Change state
                atom.setState( state.getNextLowerEnergyState() );

                // Remove us from the model
                kill();
            }
        }

        protected Photon emitPhoton() {
            Photon emittedPhoton = Photon.create();
            emittedPhoton.setWavelength( state.getEmittedPhotonWavelength() );
            return emittedPhoton;
        }

        public void kill() {
            s_model.removeModelElement( this );
        }
    }

    //    abstract protected AtomicState getNextLowerEnergyState();
    //
    //    abstract protected double getEmittedPhotonWavelength();
}
