// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * This implementation of Teacher connects to the server and sends requests for the latest data.  This is a polling model, since server to client push doesn't seem to be supported by Akka:
 * see http://groups.google.com/group/akka-user/browse_thread/thread/3ed4cc09c36c19e0
 *
 * @author Sam Reid
 */
public class Teacher {

    private final String[] args;

    public Teacher( String[] args ) {
        this.args = args;
    }

    public static void main( String[] args ) {
        new Teacher( args ).start();
    }

    private void start() {
        final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" ); //simsharing, append command line args to title
        application.getGravityAndOrbitsModule().setTeacherMode( true );

        final ActorRef server = Actors.remote().actorFor( "server", Config.serverIP, Config.SERVER_PORT );

        Thread t = new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    final GravityAndOrbitsApplicationState response = (GravityAndOrbitsApplicationState) server.sendRequestReply( new TeacherDataRequest() );
                    if ( response != null ) {
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    response.apply( application );
                                }
                            } );
                        }
                        catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        catch ( InvocationTargetException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } );
        t.start();
    }
}
