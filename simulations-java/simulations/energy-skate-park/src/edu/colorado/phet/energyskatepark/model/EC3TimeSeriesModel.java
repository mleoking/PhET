/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 6:08:19 PM
 *
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
