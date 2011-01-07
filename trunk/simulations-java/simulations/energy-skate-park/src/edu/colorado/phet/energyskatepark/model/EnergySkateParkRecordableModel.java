// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.timeseries.model.RecordableModel;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 6:08:19 PM
 */

public class EnergySkateParkRecordableModel implements RecordableModel {
    private EnergySkateParkModel model;

    public EnergySkateParkRecordableModel( EnergySkateParkModel model ) {
        this.model = model;
    }

    public void stepInTime( double simulationTimeChange ) {
        model.stepInTime( simulationTimeChange );
    }

    public Object getState() {
        return model.copyState();
    }

    public void setState( Object o ) {
        model.setState( (EnergySkateParkModel)o );
    }

    public void resetTime() {
    }

    public void clear() {
    }
}
