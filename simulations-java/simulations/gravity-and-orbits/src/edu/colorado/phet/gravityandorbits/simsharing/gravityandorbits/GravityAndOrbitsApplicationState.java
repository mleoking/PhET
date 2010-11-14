package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements Serializable {

    private GravityAndOrbitsModuleState moduleState;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication gravityAndOrbitsApplication ) {
        moduleState = new GravityAndOrbitsModuleState( gravityAndOrbitsApplication.getGravityAndOrbitsModule() );
    }

    public void apply( GravityAndOrbitsApplication application ) {
        moduleState.apply( application.getGravityAndOrbitsModule() );
    }
}
