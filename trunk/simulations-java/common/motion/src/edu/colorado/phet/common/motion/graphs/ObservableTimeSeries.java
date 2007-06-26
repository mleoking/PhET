package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.model.TimeData;

/**
 * Author: Sam Reid
* Jun 26, 2007, 12:58:59 PM
*/
public interface ObservableTimeSeries {
    void addListener( ObservableTimeSeriesListener observableTimeSeriesListener );

    static interface ObservableTimeSeriesListener {
        void dataAdded( TimeData data );
    }
}
