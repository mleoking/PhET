/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.energyskatepark.model.EnergyConservationModel;
import edu.colorado.phet.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 6:08:19 PM
 * Copyright (c) Oct 9, 2005 by Sam Reid
 */

public class EC3TimeSeriesModel extends TimeSeriesModel {
    private EnergySkateParkModule module;

    public EC3TimeSeriesModel( EnergySkateParkModule module ) {
        super( Double.POSITIVE_INFINITY );
        this.module = module;
    }

    protected void setModelState( Object v ) {
        EnergyConservationModel model = (EnergyConservationModel)v;
        module.setState( model );
    }

    protected boolean confirmReset() {
        return true;
    }

    public Object getModelState() {
        return module.getModelState();
    }

    public void updateModel( ClockEvent clockEvent ) {
        module.stepModel( clockEvent.getSimulationTimeChange() );
    }
}
