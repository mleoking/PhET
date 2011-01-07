// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.model;

/**
 * Created by: Sam
 * Dec 3, 2007 at 10:53:44 PM
 */
public interface TimeSeriesFactory {
    DefaultTimeSeries createTimeSeries();

    public static class Default implements TimeSeriesFactory {
        public DefaultTimeSeries createTimeSeries() {
            return new DefaultTimeSeries();
        }
    }
}
