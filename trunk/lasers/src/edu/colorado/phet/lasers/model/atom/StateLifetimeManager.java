package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

/**
 *
 */
class StateLifetimeManager implements ModelElement {
    private Atom atom;
    private boolean emitOnStateChange;
    private double lifeTime;
    private double deathTime;
    private AtomicState state;
    private boolean lifetimeFixed = false;
    private BaseModel model;

    public StateLifetimeManager( Atom atom, boolean emitOnStateChange, BaseModel model ) {
        this.atom = atom;
        this.emitOnStateChange = emitOnStateChange;
        this.model = model;
        model.addModelElement( this );

        double temp = 0;
        while( temp == 0 ) {
            temp = Math.random();
        }
        state = atom.getState();

        // Get the lifetime for this state
        if( lifetimeFixed ) {
            // This line gives a fixed death time
            deathTime = state.getMeanLifeTime();
        }
        else {
            // Assign a deathtime based on an exponential distribution
            // The square root pushes the distribution toward 1.
            deathTime = Math.pow( -Math.log( temp ), 0.5 ) * state.getMeanLifeTime();
            //                deathTime = -Math.log( temp ) * state.getMeanLifeTime();
        }
        lifeTime = 0;
    }

    public void stepInTime( double dt ) {
        lifeTime += dt;
        if( lifeTime >= deathTime ) {

            if( emitOnStateChange ) {
                double speed = Photon.s_speed;
                double theta = Math.random() * Math.PI * 2;
                double x = speed * Math.cos( theta );
                double y = speed * Math.sin( theta );
                Photon emittedPhoton = Photon.create( state.getEmittedPhotonWavelength(),
                                                      new Point2D.Double( atom.getPosition().getX(), atom.getPosition().getY() ),
                                                      new Vector2D.Double( x, y ) );

                // Place the replacement photon beyond the atom, so it doesn't collide again
                // right away
                Vector2D vHat = new Vector2D.Double( emittedPhoton.getVelocity() ).normalize();
                Vector2D position = new Vector2D.Double( atom.getPosition() );
                position.add( vHat.scale( atom.getRadius() + 10 ) );
                emittedPhoton.setPosition( position.getX(), position.getY() );
                atom.emitPhoton( emittedPhoton );
            }

            // Change state
            atom.setState( state.getNextLowerEnergyState() );

            // Remove us from the model
            kill();
        }
    }

    public void kill() {
        model.removeModelElement( this );
    }
}