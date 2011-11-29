// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This runs the actor code in a separate thread for one-way-messages so the code won't block.
 *
 * @author Sam Reid
 */
public class ThreadedActor<T, U> implements IActor<T, U> {
    private final IActor<T, U> actor;
    private final ArrayList<T> tell = new ArrayList<T>();
    private boolean running = true;

    public ThreadedActor( final IActor<T, U> actor ) {
        this.actor = actor;
        new Thread( new Runnable() {
            public void run() {
                while ( running ) {
                    final ArrayList<T> toProcess = new ArrayList<T>( tell );
                    for ( T message : toProcess ) {
                        try {
                            actor.tell( message );
                        }
                        catch ( IOException e ) {
                            e.printStackTrace();
                        }
                    }
                    synchronized ( tell ) {
                        tell.removeAll( toProcess );
                    }
                    Thread.yield();
                }
            }
        } ).start();
    }

    //Blocking
    public U ask( T question ) throws IOException, ClassNotFoundException {
        return actor.ask( question );
    }

    //Send one-way messages in a separate thread, non-blocking
    public void tell( T statement ) throws IOException {
        synchronized ( tell ) {
            tell.add( statement );
        }
    }

    public void stop() {
        running = false;
    }
}
