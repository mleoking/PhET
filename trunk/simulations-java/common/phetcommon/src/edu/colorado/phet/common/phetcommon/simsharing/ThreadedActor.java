// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This runs the actor code in a separate thread for one-way-messages so the code won't block.
 *
 * @author Sam Reid
 */
public class ThreadedActor implements IActor {
    private final IActor actor;
    private final ArrayList<String> tell = new ArrayList<String>();
    private boolean running = true;

    public ThreadedActor( final IActor actor ) {
        this.actor = actor;
        new Thread( new Runnable() {
            public void run() {
                while ( running ) {
                    final ArrayList<String> toProcess = new ArrayList<String>( tell );
                    for ( String message : toProcess ) {
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
    public String ask( String question ) throws IOException, ClassNotFoundException {
        return actor.ask( question );
    }

    //Send one-way messages in a separate thread, non-blocking
    public void tell( String statement ) throws IOException {
        synchronized ( tell ) {
            tell.add( statement );
        }
    }

    public void stop() {
        running = false;
    }
}
