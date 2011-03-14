// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.simsharing.Server;
import edu.colorado.phet.simsharing.SimSharing;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerWindowOnScreen;

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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame( "Students" ) {{
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    setContentPane( new JPanel( new BorderLayout() ) {{
                        add( new ClassroomView( server, args ), BorderLayout.CENTER );
                        add( new RecordingView( server, args ), BorderLayout.EAST );
                    }} );
                    setSize( 800, 600 );
                    centerWindowOnScreen( this );
                }}.setVisible( true );
            }
        } );
    }
}