/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 6:08:19 PM
 */

public class EnergySkateParkRecordableModel implements RecordableModel {
    private EnergySkateParkModule module;

    public EnergySkateParkRecordableModel( EnergySkateParkModule module ) {
        this.module = module;
    }

    public void stepInTime( double simulationTimeChange ) {
        module.stepModel( simulationTimeChange );
    }

    public Object getState() {
        return module.getModelState();
    }

    public void setState( Object o ) {
        module.setState( (EnergySkateParkModel)o );
    }

    public void resetTime() {
    }
}
