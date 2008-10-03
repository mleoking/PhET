package edu.colorado.phet.common.phetcommon.tracking;

import java.util.ArrayList;

public class Tracker {
    private TrackingInfo trackingInformation;
    private State[] states = new State[]{
            new State( "waiting for application to start                 " ),
            new State( "gathering tracking information", new Runnable() {
                public void run() {
                    trackingInformation = trackable.getTrackingInformation();
                    for ( int i = 0; i < listeners.size(); i++ ) {
                                            ((Listener) listeners.get( i )).trackingInfoChanged(trackingInformation);
                    }
                }
            } ),
            new State( "posting tracking information to PhET", new Runnable() {
                public void run() {
                    new TrackingSystem().postTrackingInfo( trackingInformation );
                }
            } ),
            new State( "finished tracking" )
    };

    private State state = states[0];
    private ArrayList listeners = new ArrayList();
    private Trackable trackable;

    public String getStatus() {
        return state.getName();
    }

    public Tracker( Trackable trackable ) {
        this.trackable = trackable;
    }

    public void applicationStarted() {
        for ( int i = 1; i < states.length; i++ ) {
            setState( states[i] );
        }
    }

    private void setState( State newState ) {
        State oldState = state;
        this.state = newState;
        oldState.finished();
        try {
            Thread.sleep( 2000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        newState.start();
        notifyStateChanged( oldState, state );
    }

    private void notifyStateChanged( State oldState, State newState ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).stateChanged( this, oldState, newState );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public class State {
        private String name;
        private Runnable runnable;

        public State( String name ) {
            this( name, new Runnable() {
                public void run() {
                }
            } );
        }

        public State( String name, Runnable runnable ) {
            this.name = name;
            this.runnable = runnable;
        }

        public String getName() {
            return name;
        }

        public void finished() {
        }

        public void start() {
            runnable.run();
        }
    }

    public static interface Listener {
        public void stateChanged( Tracker tracker, State oldState, State newState );

        void trackingInfoChanged( TrackingInfo trackingInformation );
    }
}
