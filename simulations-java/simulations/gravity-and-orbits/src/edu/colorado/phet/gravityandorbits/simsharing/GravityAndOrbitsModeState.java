// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;

//REVIEW class doc

/**
 * Serializable state for simsharing
 *
 * @author Sam Reid
 */
public class GravityAndOrbitsModeState implements IProguardKeepClass {
    private GravityAndOrbitsModelState modelState;
    private VectorBean measuringTapeStartPoint;
    private VectorBean measuringTapeEndPoint;

    public GravityAndOrbitsModeState() {
    }

    public GravityAndOrbitsModeState( GravityAndOrbitsMode mode ) {
        modelState = new GravityAndOrbitsModelState( mode.getModel() );
        measuringTapeStartPoint = new VectorBean( mode.measuringTapeStartPoint.getValue() );
        measuringTapeEndPoint = new VectorBean( mode.measuringTapeEndPoint.getValue() );
    }

    public void apply( GravityAndOrbitsMode gravityAndOrbitsMode ) {
        modelState.apply( gravityAndOrbitsMode.getModel() );
        gravityAndOrbitsMode.measuringTapeStartPoint.setValue( measuringTapeStartPoint.toImmutableVector2D() );
        gravityAndOrbitsMode.measuringTapeEndPoint.setValue( measuringTapeEndPoint.toImmutableVector2D() );
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
