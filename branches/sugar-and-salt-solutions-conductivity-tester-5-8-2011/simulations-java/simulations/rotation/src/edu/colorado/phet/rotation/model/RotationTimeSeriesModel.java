// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

/**
 * Created by: Sam
 * Nov 28, 2007 at 8:18:07 AM
 */
public class RotationTimeSeriesModel extends TimeSeriesModel {
    public RotationTimeSeriesModel( RecordableModel recordableModel, ConstantDtClock clock ) {
        super( recordableModel, clock );
    }

    public void addSeriesPoint( Object state, double recordTime ) {
        if ( recordTime <= getMaxRecordTime() ) {
            super.addSeriesPoint( state, recordTime );
        }
    }
}
