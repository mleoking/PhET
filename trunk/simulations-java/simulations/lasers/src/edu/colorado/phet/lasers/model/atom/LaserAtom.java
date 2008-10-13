package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.ElementProperties;
import edu.colorado.phet.common.quantum.model.Photon;
import edu.colorado.phet.common.quantum.model.PropertiesBasedAtom;
import edu.colorado.phet.lasers.controller.LasersConfig;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * LaserAtom
 * <p/>
 * A PropertiesBasedAtom that has a minimum time it must psend in the ground state
 * after entering it before it can collide with a photon.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaserAtom extends PropertiesBasedAtom {

    private Boolean canCollideInGroundState = Boolean.TRUE;

    public LaserAtom( LaserModel model, ElementProperties elementProperties ) {
        super( model, elementProperties );
    }

    public LaserAtom( LaserModel model, AtomicState[] states ) {
        super( model, states );
    }

    public void collideWithPhoton( Photon photon ) {
        boolean canCollide = false;
        if ( getCurrState() != getGroundState() ) {
            canCollide = true;
        }
        else {
            synchronized( canCollideInGroundState ) {
                canCollide = canCollideInGroundState.booleanValue();
            }
        }

        if ( canCollide ) {
            super.collideWithPhoton( photon );
        }
    }

    public void setCurrState( AtomicState newState ) {
        super.setCurrState( newState );
        if ( newState == getGroundState() && canCollideInGroundState != null ) {
            new MinLifetimeTimer().start();
        }
    }

    public void setStates( AtomicState[] states ) {
        super.setStates( states );
        states[1] = ( (LaserModel) getModel() ).getMiddleEnergyState();
        if ( states.length == 3 ) {
//        if( numEnergyLevels == 3 ) {
            states[2] = ( (LaserModel) getModel() ).getHighEnergyState();
        }
    }

    /**
     * Agent that flips the flag that controls the time that an atom must remain in the ground state
     */
    private class MinLifetimeTimer extends Thread {
        long minLifetime = LasersConfig.MINIMUM_GROUND_STATE_LIFETIME;

        public void run() {
            synchronized( canCollideInGroundState ) {
                canCollideInGroundState = Boolean.FALSE;
            }
            try {
                Thread.sleep( minLifetime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            synchronized( canCollideInGroundState ) {
                canCollideInGroundState = Boolean.TRUE;
            }
        }
    }

}
