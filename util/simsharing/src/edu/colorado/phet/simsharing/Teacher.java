// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * This implementation of Teacher connects to the server and sends requests for the latest data.  This is a polling model, since server to client push doesn't seem to be supported by Akka:
 * see http://groups.google.com/group/akka-user/browse_thread/thread/3ed4cc09c36c19e0
 *
 * @author Sam Reid
 */
public class Teacher {

    private final String[] args;
    protected ActorRef server;

    public Teacher( String[] args ) {
        this.args = args;
    }

    public static void main( String[] args ) throws IOException {
        Server.parseArgs( args );
        SimSharing.init();
        new Teacher( args ).start();
    }

    private void start() {
        server = Actors.remote().actorFor( "server", Server.HOST_IP_ADDRESS, Server.PORT );
        JFrame studentListFrame = new JFrame( "Students" );
        studentListFrame.setContentPane( new StudentListPanel( server, args ) );
        studentListFrame.setSize( 800, 600 );
        SwingUtils.centerWindowOnScreen( studentListFrame );
        studentListFrame.setVisible( true );
    }
}