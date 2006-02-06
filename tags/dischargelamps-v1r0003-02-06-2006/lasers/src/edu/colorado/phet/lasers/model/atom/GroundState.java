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
import java.util.HashMap;

/**
 * The ground state is special in that it has a minimum lifetime.
 * <p/>
 * todo: This minimum lifetime code may be unnecessary now that we've got a minimum
 * lifetime on all states
 */
public class GroundState extends AtomicState {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------

    // The minimum time (in real time) that an atom must be in this state before a
    // collision with a photon will have an effect
    static private long minLifetime = LaserConfig.MINIMUM_GROUND_STATE_LIFETIME;

    public static void setMinLifetime( long t ) {
        minLifetime = t;
    }

    public static long getMinLifetime() {
        return minLifetime;
    }

    //-----------------------------------------------------------------
    // Instace data and methods
    //-----------------------------------------------------------------
    private HashMap atomToPhotonCollisionEnabledFlag = new HashMap();

    public GroundState() {
        setEnergyLevel( 0 );
        setMeanLifetime( Double.POSITIVE_INFINITY );
    }

    /**
     * This is the only AtomicState whose behavior is different from the others.
     * <p/>
     * When an atom enters the ground state, it must stay there for a minimum amount of time
     * before it can be bumped to a higher one.
     *
     * @param atom
     * @param photon
     */
    public void collideWithPhoton( Atom atom, Photon photon ) {
/*
        // If this state hasn't been yet enabled to be stimulated by a photon that would bump it to the
        // next highest energy level, don't do anything.
//        double de = getNextHigherEnergyState().getEnergyLevel() - this.getEnergyLevel();
        if( !( (Boolean)atomToPhotonCollisionEnabledFlag.get( atom ) ).booleanValue()
//            && Math.abs( photon.getEnergy() - de ) <= LaserConfig.ENERGY_TOLERANCE ) {
//            if( !( (Boolean)atomToPhotonCollisionEnabledFlag.get( atom ) ).booleanValue() ) {
        ){
            return;
//            }
        }
*/
        super.collideWithPhoton( atom, photon );
    }

    public AtomicState getNextLowerEnergyState() {
        return AtomicState.MinEnergyState.instance();
    }

    public void enterState( Atom atom ) {
//        Thread t = new Thread( new MinLifetimeTimer( atom ) );
//        t.start();
        super.enterState( atom );
    }

    public void leaveState( Atom atom ) {
//        atomToPhotonCollisionEnabledFlag.put( atom, new Boolean( false ) );
        super.leaveState( atom );
    }

    /**
     * Agent that flips the flag that controls the time that an atom must remain in the ground state
     */
    private class MinLifetimeTimer implements Runnable {
        private Atom atom;

        public MinLifetimeTimer( Atom atom ) {
            this.atom = atom;
        }

        public void run() {
            try {
                Thread.sleep( minLifetime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    Boolean flag = (Boolean)atomToPhotonCollisionEnabledFlag.get( atom );
                    if( flag == null ) {
                        atomToPhotonCollisionEnabledFlag.put( atom, new Boolean( true ) );
                    }
                    atomToPhotonCollisionEnabledFlag.put( atom, new Boolean( true ) );
                }
            } );
        }
    }
}
