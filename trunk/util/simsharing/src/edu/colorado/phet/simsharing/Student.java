// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class Student {
    private final String[] args;
    private int count = 0;//Only send messages every count%N frames

    public Student( String[] args ) {
        this.args = args;
    }

    public static void main( final String[] args ) throws IOException, AWTException {
        new Student( args ).start();
    }

    private void start() {
        final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Student Edition" );
        final int N = 1;
        final ActorRef server = Actors.remote().actorFor( "server", Server.IP_ADDRESS, Server.PORT );

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
