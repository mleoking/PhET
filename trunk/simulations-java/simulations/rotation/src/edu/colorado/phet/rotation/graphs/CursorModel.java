package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 18, 2007, 3:01:50 AM
 */
public class CursorModel {
    private ArrayList listeners = new ArrayList();
    private double time;

    public CursorModel( final TimeSeriesModel timeSeriesModel ) {
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                setTime( timeSeriesModel.getPlaybackTime() );
            }
        } );
    }

    public double getTime() {
        return time;
    }

    public void setTime( double time ) {
        if( this.time != time ) {
            this.time = time;
            notifyListeners();
        }
    }

    public static interface Listener {
        void changed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).changed();
        }
    }
}
