package edu.colorado.phet.rotation.graphs;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:27:46 AM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */
public interface GraphTimeSeries {
    void clear();

    void go();

    void stop();

    boolean isEmpty();

    boolean isRunning();

    public static interface Listener {
        void started();

        void stopped();

        void cleared();

        void emptyStateChanged();
    }

    public void addListener( Listener listener );

}
