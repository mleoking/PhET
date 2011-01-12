// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class Student {
    private final String[] args;
    private int count = 0;

    public Student( String[] args ) {
        this.args = args;
    }

    public static void main( final String[] args ) throws IOException, AWTException {
        new Student( args ).start();
    }

    private void start() {
        final GravityAndOrbitsApplication[] launchedApp = new GravityAndOrbitsApplication[1];
        new PhetApplicationLauncher().launchSim( args, GravityAndOrbitsApplication.PROJECT_NAME, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                launchedApp[0] = new GravityAndOrbitsApplication( config );
                return launchedApp[0];
            }
        } );
        final GravityAndOrbitsApplication application = launchedApp[0];
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Student Edition" );
        final int N = 1;
        final ActorRef server = Actors.remote().actorFor( "server", Config.serverIP, Config.SERVER_PORT );

        final VoidFunction0 updateSharing = new VoidFunction0() {
            public void apply() {
                if ( count % N == 0 ) {
                    GravityAndOrbitsApplicationState state = new GravityAndOrbitsApplicationState( application );
                    server.sendOneWay( state );
                }
                count++;
            }
        };
        application.getGravityAndOrbitsModule().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing.apply();
            }
        } );
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( application.getGravityAndOrbitsModule().getModeProperty().getValue().getModel().getClock().isPaused() ) {
                    updateSharing.apply();
                }
            }
        } ).start();
    }
}
