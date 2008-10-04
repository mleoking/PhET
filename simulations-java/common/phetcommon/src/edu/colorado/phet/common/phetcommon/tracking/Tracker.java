package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.*;

public class Tracker {
    private TrackingInfo trackingInformation;
    private IOException trackingException;
    private State[] states = new State[]{
            new State( "waiting for application to start                 " ),
            new State( "gathering tracking information", new Runnable() {
                public void run() {
                    trackingInformation = trackable.getTrackingInformation();
                    try {
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                for ( int i = 0; i < listeners.size(); i++ ) {
                                    ( (Listener) listeners.get( i ) ).trackingInfoChanged( trackingInformation );
                                }
                            }
                        } );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    catch( InvocationTargetException e ) {
                        e.printStackTrace();
                    }

                }
            } ),
            new State( "posting tracking information to PhET", new Runnable() {
                public void run() {
                    Thread t = new Thread( new Runnable() {
                        public void run() {
                            try {
                                new TrackingSystem().postTrackingInfo( trackingInformation );
                            }
                            catch( IOException e ) {
                                trackingException = e;
                                for ( int i = 0; i < listeners.size(); i++ ) {
                                    ( (Listener) listeners.get( i ) ).trackingFailed( trackingException);
                                }
                            }
                        }
                    } );
                    t.start();

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

    public void startTracking() {
        Thread t = new Thread( new Runnable() {
            public void run() {
                for ( int i = 1; i < states.length; i++ ) {
                    setState( states[i] );
                }
            }
        } );
        t.start();
    }

    private void setState( final State newState ) {
        final State oldState = state;
        this.state = newState;
        oldState.finished();

        try {
            notifyStateChanged( oldState, state );
            Thread.sleep( 2000 );
            newState.start();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private void notifyStateChanged( final State oldState, final State newState ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    for ( int i = 0; i < listeners.size(); i++ ) {
                        ( (Listener) listeners.get( i ) ).stateChanged( Tracker.this, oldState, newState );
                    }
                }
            } );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }

    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public TrackingInfo getTrackingInformation() {
        return trackingInformation;
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

        void trackingFailed( IOException trackingException );
    }
}
