// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;

/**
 * Serializable state for simsharing, stores the state of one mode.
 *
 * @author Sam Reid
 */
public class GravityAndOrbitsModeState implements IProguardKeepClass {
    private GravityAndOrbitsModelState modelState;
    private VectorState measuringTapeStartPoint;
    private VectorState measuringTapeEndPoint;

    public GravityAndOrbitsModeState() {
    }

    public GravityAndOrbitsModeState( GravityAndOrbitsMode mode ) {
        modelState = new GravityAndOrbitsModelState( mode.getModel() );
        measuringTapeStartPoint = new VectorState( mode.measuringTapeStartPoint.get() );
        measuringTapeEndPoint = new VectorState( mode.measuringTapeEndPoint.get() );
    }

    public void apply( GravityAndOrbitsMode gravityAndOrbitsMode ) {
        modelState.apply( gravityAndOrbitsMode.getModel() );
        gravityAndOrbitsMode.measuringTapeStartPoint.set( measuringTapeStartPoint.toImmutableVector2D() );
        gravityAndOrbitsMode.measuringTapeEndPoint.set( measuringTapeEndPoint.toImmutableVector2D() );
    }

    public GravityAndOrbitsModelState getModelState() {
        return modelState;
    }

    public void setModelState( GravityAndOrbitsModelState modelState ) {
        this.modelState = modelState;
    }

    public VectorState getMeasuringTapeStartPoint() {
        return measuringTapeStartPoint;
    }

    public void setMeasuringTapeStartPoint( VectorState measuringTapeStartPoint ) {
        this.measuringTapeStartPoint = measuringTapeStartPoint;
    }

    public VectorState getMeasuringTapeEndPoint() {
        return measuringTapeEndPoint;
    }

    public void setMeasuringTapeEndPoint( VectorState measuringTapeEndPoint ) {
        this.measuringTapeEndPoint = measuringTapeEndPoint;
    }
}
