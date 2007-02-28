package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 10:08:51 AM
 * Copyright (c) Feb 9, 2004 by Sam Reid
 */
public class StateChain implements BandParticleState {
    ArrayList states = new ArrayList();

    public void addState( BandParticleState state ) {
        states.add( state );
    }

    BandParticleState stateAt( int i ) {
        return (BandParticleState)states.get( i );
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        if( states.size() == 0 ) {
            return true;
        }
        boolean done = stateAt( 0 ).stepInTime( particle, dt );
        if( done ) {
            states.remove( 0 );
            if( states.size() == 0 ) {
                return true;
            }
        }
        return false;
    }

}
