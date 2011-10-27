// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.simsharingcore;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class ThreadedActor implements IActor {
    private final IActor actor;
    private final ArrayList<Object> tell = new ArrayList<Object>();
    private boolean running = true;

    public ThreadedActor( final IActor actor ) {
        this.actor = actor;
        new Thread( new Runnable() {
            public void run() {
                while ( running ) {
                    final ArrayList<Object> toProcess = new ArrayList<Object>( tell );
                    for ( Object message : toProcess ) {
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
    public Object ask( Object question ) throws IOException, ClassNotFoundException {
        return actor.ask( question );
    }

    //Send one-way messages in a separate thread, non-blocking
    public void tell( Object statement ) throws IOException {
        synchronized ( tell ) {
            tell.add( statement );
        }
    }

    public void stop() {
        running = false;
    }
}
