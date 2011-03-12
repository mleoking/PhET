// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModeState implements Serializable {
    private GravityAndOrbitsModelState modelState;
    private ImmutableVector2D measuringTapeStartPoint;
    private ImmutableVector2D measuringTapeEndPoint;

    public GravityAndOrbitsModeState() {
    }

    public GravityAndOrbitsModeState( GravityAndOrbitsMode mode ) {
        modelState = new GravityAndOrbitsModelState( mode.getModel() );
        measuringTapeStartPoint = mode.getMeasuringTapeStartPoint().getValue();
        measuringTapeEndPoint = mode.getMeasuringTapeEndPoint().getValue();
    }

    public void apply( GravityAndOrbitsMode gravityAndOrbitsMode ) {
        modelState.apply( gravityAndOrbitsMode.getModel() );
        gravityAndOrbitsMode.getMeasuringTapeStartPoint().setValue( measuringTapeStartPoint );
        gravityAndOrbitsMode.getMeasuringTapeEndPoint().setValue( measuringTapeEndPoint );
    }
}
