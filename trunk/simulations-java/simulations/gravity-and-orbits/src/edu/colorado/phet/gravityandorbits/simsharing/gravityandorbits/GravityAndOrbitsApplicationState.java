// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements Serializable {

    private GravityAndOrbitsModuleState moduleState;
    private long timestamp;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication gravityAndOrbitsApplication ) {
        moduleState = new GravityAndOrbitsModuleState( gravityAndOrbitsApplication.getGravityAndOrbitsModule() );
        timestamp = System.currentTimeMillis();
    }

    public void apply( GravityAndOrbitsApplication application ) {
        System.out.println( "round trip time: " + ( System.currentTimeMillis() - timestamp ) );
        moduleState.apply( application.getGravityAndOrbitsModule() );
    }

    @Override
    public String toString() {
        return "GravityAndOrbitsApplicationState{" +
               "moduleState=" + moduleState +
               ", timestamp=" + timestamp +
               '}';
    }
}
