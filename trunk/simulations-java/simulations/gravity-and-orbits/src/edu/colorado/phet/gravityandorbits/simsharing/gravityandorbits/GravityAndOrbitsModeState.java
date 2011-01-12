// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModeState implements Serializable {
    protected GravityAndOrbitsModelState modelState;

    public GravityAndOrbitsModeState( GravityAndOrbitsMode mode ) {
        modelState = new GravityAndOrbitsModelState( mode.getModel() );
    }

    public void apply( GravityAndOrbitsMode gravityAndOrbitsMode ) {
        modelState.apply( gravityAndOrbitsMode.getModel() );
    }
}
