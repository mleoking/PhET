/* Copyright 2004, University of Colorado */

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
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

/**
 * Class: MiddleEnergyState
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
public class MiddleEnergyState extends AtomicState {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    private static MiddleEnergyState instance = new MiddleEnergyState();

    public static MiddleEnergyState instance() {
        return instance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private AtomicState nextHigherState = HighEnergyState.instance();

    private MiddleEnergyState() {
        setEnergyLevel( Photon.wavelengthToEnergy( Photon.RED ) );
        determineEmittedPhotonWavelength();
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( isStimulatedBy( photon ) ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() );
//            vHat.scale(atom.getRadius() + 10);
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = Photon.createStimulated( photon, position, atom );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setCurrState( GroundState.instance() );
        }

        // If the photon has the same energy level as the difference between
        // this state and another one, then we go to that state
        // Find where we are in the list of states the atom can be in
        AtomicState newState = getStimulatedState( atom, photon, HighEnergyState.instance().getEnergyLevel() - this.getEnergyLevel() );
        if( newState != null ) {
            photon.removeFromSystem();
            atom.setCurrState( newState );
        }
    }

    public AtomicState getNextLowerEnergyState() {
        return GroundState.instance();
    }

    public AtomicState getNextHigherEnergyState() {
        return nextHigherState;
    }

    public void setNextHigherEnergyState( AtomicState state ) {
        nextHigherState = state;
    }
}
