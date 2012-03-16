// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;

/**
 * Serializable state for simsharing, stores the state of one model.
 *
 * @author Sam Reid
 */
public class GravityAndOrbitsModelState implements IProguardKeepClass {
    private boolean paused;
    private double simulationTime;
    private ArrayList<PersistentBodyState> persistentBodyStates;

    public GravityAndOrbitsModelState( GravityAndOrbitsModel gravityAndOrbitsModel ) {
        simulationTime = gravityAndOrbitsModel.getClock().getSimulationTime();
        paused = gravityAndOrbitsModel.getClock().isPaused();
        persistentBodyStates = new ArrayList<PersistentBodyState>();
        for ( Body body : gravityAndOrbitsModel.getBodies() ) {
            persistentBodyStates.add( new PersistentBodyState( body ) );
        }
    }

    public void apply( GravityAndOrbitsModel gravityAndOrbitsModel ) {
        gravityAndOrbitsModel.getClock().setSimulationTime( simulationTime );
        gravityAndOrbitsModel.getClock().setPaused( paused );
        for ( int i = 0; i < persistentBodyStates.size(); i++ ) {
            persistentBodyStates.get( i ).apply( gravityAndOrbitsModel.getBodies().get( i ) );
        }
    }

    @Override public String toString() {
        return "GravityAndOrbitsModelState{" +
               "paused=" + paused +
               ", simulationTime=" + simulationTime +
               ", persistentBodyStates=" + persistentBodyStates +
               '}';
    }
}
