/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 6:08:19 PM
 * Copyright (c) Oct 9, 2005 by Sam Reid
 */

public class EC3TimeSeriesModel extends TimeSeriesModel {
    private EnergySkateParkModule module;
    private static final double STEP_DT = EnergySkateParkApplication.SIMULATION_TIME_DT;

    public EC3TimeSeriesModel( EnergySkateParkModule module ) {
        super( Double.POSITIVE_INFINITY );
        this.module = module;
    }

    protected void setModelState( Object v ) {
        module.setState( (EnergySkateParkModel)v );
    }

    protected boolean confirmReset() {
        return true;
    }

    public void stepLive() {
        module.stepModel( STEP_DT );
    }

    public Object getModelState() {
        return module.getModelState();
    }

    public void updateModel( double simulationTimeChange ) {
        module.stepModel( simulationTimeChange );
    }

}
