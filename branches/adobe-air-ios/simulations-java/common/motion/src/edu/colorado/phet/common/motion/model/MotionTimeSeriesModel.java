// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

/**
 * Created by: Sam
 * Nov 28, 2007 at 8:16:46 AM
 */
public class MotionTimeSeriesModel extends TimeSeriesModel {
    public MotionTimeSeriesModel( RecordableModel recordableModel, ConstantDtClock clock ) {
        super( recordableModel, clock );
    }

    //workaround for buggy state/time sequence: time is obtained from record mode before switching to playback mode
    public void rewind() {
        setPlaybackMode();
        super.rewind();
    }
}
