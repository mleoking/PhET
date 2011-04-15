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

    public boolean isPaused() {
        return paused;
    }

    public void setPaused( boolean paused ) {
        this.paused = paused;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime( double simulationTime ) {
        this.simulationTime = simulationTime;
    }

    public ArrayList<PersistentBodyState> getPersistentBodyStates() {
        return persistentBodyStates;
    }

    public void setPersistentBodyStates( ArrayList<PersistentBodyState> persistentBodyStates ) {
        this.persistentBodyStates = persistentBodyStates;
    }
}
