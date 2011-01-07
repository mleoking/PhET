// Copyright 2002-2011, University of Colorado

/*PhET, 2004.*/
package edu.colorado.phet.common.timeseries.model;

import java.util.ArrayList;


/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 1:12:18 PM
 */
public abstract class Mode {
    private TimeSeriesModel timeSeriesModel;

    public Mode( TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public abstract void step( double dt );

    public static class Live extends Mode {
        private double time = 0;

        public Live( TimeSeriesModel timeSeriesModel ) {
            super( timeSeriesModel );
        }

        public void step( double dt ) {
            time += dt;
            getTimeSeriesModel().updateModel( dt );
        }

        public double getTime() {
            return time;
        }

    }

    public static class Playback extends Mode {
        private double playbackTime;
        private ArrayList listeners = new ArrayList();

        public Playback( TimeSeriesModel timeSeriesModel ) {
            super( timeSeriesModel );
            playbackTime = 0.0;
        }

        public double getPlaybackTime() {
            return playbackTime;
        }

        public void step( double dt ) {
            playbackTime += dt;
            playbackTime = Math.min( getTimeSeriesModel().getRecordTime(), playbackTime );
            getTimeSeriesModel().setPlaybackTime( playbackTime );
            if ( playbackTime >= getTimeSeriesModel().getRecordTime() ) {
                getTimeSeriesModel().setPaused( true );
            }
            notifyPlaybackTimeChanged();
        }

        private void notifyPlaybackTimeChanged() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                TimeSeriesModel.PlaybackTimeListener playbackTimeListener = (TimeSeriesModel.PlaybackTimeListener) listeners.get( i );
                playbackTimeListener.timeChanged();
            }
        }

        public void setTime( double requestedTime ) {
            if ( this.playbackTime != requestedTime ) {
                this.playbackTime = requestedTime;
                notifyPlaybackTimeChanged();
            }
        }

        public void addListener( TimeSeriesModel.PlaybackTimeListener playbackTimeListener ) {
            listeners.add( playbackTimeListener );
        }
    }

    public static class Record extends Mode {
        private double recordTime = 0;

        public Record( final TimeSeriesModel timeSeriesModel ) {
            super( timeSeriesModel );
        }

        public void reset() {
            recordTime = 0.0;
        }

        public void step( double dt ) {
            double maxTime = getTimeSeriesModel().getMaxRecordTime();
            double newTime = recordTime + dt;
            if ( newTime > maxTime ) {
                dt = ( maxTime - recordTime );
                newTime = getTimeSeriesModel().getMaxRecordTime();
            }
            recordTime += dt;
            getTimeSeriesModel().updateModel( dt );
            getTimeSeriesModel().addSeriesPoint( getTimeSeriesModel().getModelState(), recordTime );
            if ( newTime == getTimeSeriesModel().getMaxRecordTime() ) {
                getTimeSeriesModel().recordFinished();
            }
        }

        public double getRecordTime() {
            return recordTime;
        }
    }

}