package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.model.BodyState;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsState implements Serializable {
    public final BodyState sunState;
    public final BodyState planetState;
    public final BodyState moonState;

    public GravityAndOrbitsState( BodyState sunState, BodyState planetState, BodyState moonState ) {
        this.sunState = sunState;
        this.planetState = planetState;
        this.moonState = moonState;
    }
    
}
