// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModelState implements Serializable {
    private boolean paused;
    private double simulationTime;
    private ArrayList<PersistentBodyState> persistentBodyStates;

    public GravityAndOrbitsModelState() {
    }

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
}
