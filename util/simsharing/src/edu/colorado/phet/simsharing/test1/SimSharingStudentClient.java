// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simsharing.test1;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;
import edu.colorado.phet.simsharing.Config;

/**
 * @author Sam Reid
 */
public class SimSharingStudentClient {
    int N = 1;
    int count = 0;
    private final GravityAndOrbitsApplication application;
    protected ActorRef server;

    public SimSharingStudentClient( final GravityAndOrbitsApplication application ) throws AWTException, IOException {
        this.application = application;
        server = Actors.remote().actorFor( "server", Config.serverIP, Config.SERVER_PORT );

        application.getGravityAndOrbitsModule().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing();
            }
        } );
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( application.getGravityAndOrbitsModule().getModeProperty().getValue().getModel().getClock().isPaused() ) {
                    updateSharing();
                }
            }
        } ).start();
    }

    private void updateSharing() {
        if ( count % N == 0 ) {
            GravityAndOrbitsApplicationState state = new GravityAndOrbitsApplicationState( application );
            sendToServer( state );
        }
        count++;
    }

    private void sendToServer( GravityAndOrbitsApplicationState state ) {
        server.sendOneWay( state );
//        System.out.println( "response = " + response + ", round trip = " + ( System.currentTimeMillis() - start ) );
    }

    public void start() {
        System.out.println( "started student" );
    }
}
