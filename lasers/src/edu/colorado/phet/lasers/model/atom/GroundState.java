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

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;

public class GroundState extends AtomicState {

    // The minimum time (in real time) that an atom must be in this state before a
    // collision with a photon will have an effect
    static private long minLifetime = 500;

    public static void setMinLifetime( long t ) {
        minLifetime = t;
    }

    private boolean photonCollisionEnabled = false;

    public GroundState() {
        setEnergyLevel( 0 );
//        setEnergyLevel( AtomicState.minEnergy );
        setMeanLifetime( Double.POSITIVE_INFINITY );
    }

    /**
     * This is the only AtomicState whose behavior is different from the others.
     *
     * @param atom
     * @param photon
     */
    public void collideWithPhoton( Atom atom, Photon photon ) {

        if( !photonCollisionEnabled && Math.abs( photon.getEnergy() - this.getEnergyLevel() ) <= LaserConfig.ENERGY_TOLERANCE ) {
            return;
        }

        // Only respond a specified percentage of the time
        if( Math.random() < s_collisionLikelihood ) {
            AtomicState newState = getStimulatedState( atom, photon, this.getEnergyLevel() );
//            AtomicState newState = getStimulatedState( atom, photon, 0 );
            if( newState != null ) {
                photon.removeFromSystem();
                atom.setCurrState( newState );
            }
        }
    }

    public AtomicState getNextLowerEnergyState() {
        return AtomicState.MinEnergyState.instance();
    }

    public void enterState() {
        Thread t = new Thread( new MinLifetimeTimer() );
        t.start();
        super.enterState();
    }

    public void leaveState() {
        photonCollisionEnabled = false;
        super.leaveState();
    }

    private class MinLifetimeTimer implements Runnable {
        public void run() {
            try {
                Thread.sleep( minLifetime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    photonCollisionEnabled = true;
                }
            } );
        }
    }
}
