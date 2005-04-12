/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

/**
 * An object that manages the lifetime of an AtomicEnergyState.
 * <p/>
 * When it is created, it sets a time of death for an atom's current state, and when
 * that time comes, causes a photon to be emitted and the atom to change to the next
 * appropriate energy state.
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
        state = atom.getCurrState();

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

            AtomicState nextState = atom.getEnergyStateAfterEmission();
//            AtomicState nextState = state.getNextLowerEnergyState();
            if( emitOnStateChange ) {
                System.out.println( "photon emitted" );
                double speed = Photon.SPEED;
                double theta = Math.random() * Math.PI * 2;
                double x = speed * Math.cos( theta );
                double y = speed * Math.sin( theta );

                Photon emittedPhoton = Photon.create( state.determineEmittedPhotonWavelength( nextState ),
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
            atom.setCurrState( nextState );

            // Remove us from the model
            kill();
        }
    }

    public void kill() {
        model.removeModelElement( this );
    }
}