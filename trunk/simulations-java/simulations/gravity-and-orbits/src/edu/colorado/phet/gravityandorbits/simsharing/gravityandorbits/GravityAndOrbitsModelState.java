// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModelState implements Serializable {
    private boolean paused;
    private double simulationTime;
    private PersistentBodyState sunState;
    private PersistentBodyState planetState;
    private PersistentBodyState moonState;

    public GravityAndOrbitsModelState( GravityAndOrbitsModel gravityAndOrbitsModel ) {
        simulationTime = gravityAndOrbitsModel.getClock().getSimulationTime();
        paused = gravityAndOrbitsModel.getClock().isPaused();
        //TODO: enable multi-mode for sim-sharing
//        sunState = new PersistentBodyState( gravityAndOrbitsModel.getSun() );
//        planetState = new PersistentBodyState( gravityAndOrbitsModel.getPlanet() );
//        moonState = new PersistentBodyState( gravityAndOrbitsModel.getMoon() );
    }

    public void apply( GravityAndOrbitsModel gravityAndOrbitsModel ) {
        gravityAndOrbitsModel.getClock().setSimulationTime( simulationTime );
        gravityAndOrbitsModel.getClock().setPaused( paused );

//        sunState.apply( gravityAndOrbitsModel.getSun() );
//        planetState.apply( gravityAndOrbitsModel.getPlanet() );
//        moonState.apply( gravityAndOrbitsModel.getMoon() );
    }
}
