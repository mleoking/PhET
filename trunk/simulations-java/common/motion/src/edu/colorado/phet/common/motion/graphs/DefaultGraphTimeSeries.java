package edu.colorado.phet.common.motion.graphs;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:32:56 AM
 */

public class DefaultGraphTimeSeries implements GraphTimeSeries {
    private boolean running = false;
    private ArrayList listeners = new ArrayList();
    private ArrayList data = new ArrayList();

    public void clear() {
        if( data.size() > 0 ) {
            data.clear();
            notifyCleared();
            notifyEmptyStateChanged();
        }
    }

    private void notifyEmptyStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.emptyStateChanged();
        }
    }

    private void notifyCleared() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.cleared();
        }
    }

    public void go() {
        running = true;
        takeData();
        notifyStarted();
    }

    private void takeData() {
        int origDataSize = data.size();
        Double value = new Double( 0 );
        data.add( value );

        if( origDataSize == 0 ) {
            notifyEmptyStateChanged();
        }
    }

    private void notifyStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.started();
        }
    }

    public void stop() {
        running = false;
        notifyStopped();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean isRunning() {
        return running;
    }

    private void notifyStopped() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.stopped();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
