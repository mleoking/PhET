// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModeState implements Serializable {
    private GravityAndOrbitsModelState modelState;
    private VectorBean measuringTapeStartPoint;
    private VectorBean measuringTapeEndPoint;

    public GravityAndOrbitsModeState() {
    }

    public GravityAndOrbitsModeState( GravityAndOrbitsMode mode ) {
        modelState = new GravityAndOrbitsModelState( mode.getModel() );
        measuringTapeStartPoint = new VectorBean( mode.getMeasuringTapeStartPoint().getValue() );
        measuringTapeEndPoint = new VectorBean( mode.getMeasuringTapeStartPoint().getValue() );
    }

    public void apply( GravityAndOrbitsMode gravityAndOrbitsMode ) {
        modelState.apply( gravityAndOrbitsMode.getModel() );
        gravityAndOrbitsMode.getMeasuringTapeStartPoint().setValue( measuringTapeStartPoint.toImmutableVector2D() );
        gravityAndOrbitsMode.getMeasuringTapeEndPoint().setValue( measuringTapeEndPoint.toImmutableVector2D() );
    }

    public GravityAndOrbitsModelState getModelState() {
        return modelState;
    }

    public void setModelState( GravityAndOrbitsModelState modelState ) {
        this.modelState = modelState;
    }

    public VectorBean getMeasuringTapeStartPoint() {
        return measuringTapeStartPoint;
    }

    public void setMeasuringTapeStartPoint( VectorBean measuringTapeStartPoint ) {
        this.measuringTapeStartPoint = measuringTapeStartPoint;
    }

    public VectorBean getMeasuringTapeEndPoint() {
        return measuringTapeEndPoint;
    }

    public void setMeasuringTapeEndPoint( VectorBean measuringTapeEndPoint ) {
        this.measuringTapeEndPoint = measuringTapeEndPoint;
    }
}
