// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.model;

/**
 * It's necessary to keep some data for performing time series based computations.
 * It's also necessary to keep all data that will be visible in playback area.
 * <p/>
 * However, if we keep all data, then the simulation will crash with OutOfMemoryException; therefore we try to prune the irrelevant data.
 * <p/>
 * Created by: Sam
 * Dec 3, 2007 at 10:57:08 PM
 */
public class HeuristicPrunedTimeSeries extends DefaultTimeSeries {
    private double maxTime;

    public HeuristicPrunedTimeSeries( double maxTime ) {
        this.maxTime = maxTime;
    }

    public void addValue( double v, double time ) {
        super.addValue( v, time );
        //todo: how safe is this heuristic?
        if ( time >= maxTime * 4 ) {
            super.removeValue( getSampleCount() / 2 );
        }
    }

    public static class Factory implements TimeSeriesFactory {
        private double maxTime;

        public Factory( double maxTime ) {
            this.maxTime = maxTime;
        }

        public DefaultTimeSeries createTimeSeries() {
            return new HeuristicPrunedTimeSeries( maxTime );
        }
    }
}
