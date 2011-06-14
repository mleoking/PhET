// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import org.codehaus.jackson.map.ObjectMapper;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.ImageFactory;

/**
 * @author Sam Reid
 */
public class Student {
    private final String[] args;
    private int count = 0;//Only send messages every count%N frames
    protected SessionID sessionID;
    private ImageFactory imageFactory = new ImageFactory();

    public Student( String[] args ) {
        this.args = args;
    }

    public static void main( final String[] args ) throws IOException, AWTException {
        Server.parseArgs( args );
        SimSharing.init();
        new Student( args ).start();
    }

    private void start() {
        final ActorRef server = Actors.remote().actorFor( "server", Server.HOST_IP_ADDRESS, Server.PORT );
        final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args, new VoidFunction0() {
            //TODO: could move exit listeners here instead of in PhetExit
            public void apply() {
                if ( sessionID != null ) {
                    server.sendOneWay( new EndSession( sessionID ) );
                }
                System.exit( 0 );
            }
        } );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Student Edition" );
        final int N = 1;

        final VoidFunction0 updateSharing = new VoidFunction0() {
            public boolean startedMessage = false;
            public boolean finishedMessage = false;

            ArrayList<GravityAndOrbitsApplicationState> stateCache = new ArrayList<GravityAndOrbitsApplicationState>();

            public void apply() {
                if ( count % N == 0 ) {
                    GravityAndOrbitsApplicationState state = new GravityAndOrbitsApplicationState( application, imageFactory );
                    stateCache.add( state );
                    if ( sessionID == null ) {
                        if ( !startedMessage ) {
                            System.out.print( "Awaiting ID" );
                            startedMessage = true;
                        }
                        else {
                            System.out.print( "." );
                        }
                    }
                    else {
                        if ( !finishedMessage ) {
                            System.out.println( "\nReceived ID: " + sessionID );
                            finishedMessage = true;
                        }

//                        server.sendOneWay( new AddStudentDataSample( sessionID, state ) );
                        if ( stateCache.size() >= 1 ) {
                            server.sendOneWay( new AddMultiSample( sessionID, yield( stateCache, new Function1<GravityAndOrbitsApplicationState, String>() {
                                public String apply( GravityAndOrbitsApplicationState state ) {
                                    return mapper.writeValueAsString( state );
                                }
                            } ) ) );
                            stateCache.clear();
                        }
                    }
                }
                count++;
            }
        };
        application.getIntro().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing.apply();
            }
        } );
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( application.getIntro().modeProperty.get().getModel().getClock().isPaused() ) {
                    updateSharing.apply();
                }
            }
        } ).start();

        new Thread( new Runnable() {
            public void run() {
                //be careful, this part blocks:
                sessionID = (SessionID) server.sendRequestReply( new StartSession() );
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ", id = " + sessionID );
                    }
                } );
            }
        } ).start();
        application.getIntro().clockPausedProperty.set( false );
    }

    static class MyMapper extends ObjectMapper {
        @Override public String writeValueAsString( Object value ) {
            try {
                return super.writeValueAsString( value );
            }
            catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    MyMapper mapper = new MyMapper();

    public static <T, U> ArrayList<U> yield( final ArrayList<T> list, final Function1<T, U> map ) {
        final ArrayList<U> arrayList = new ArrayList<U>();
        for ( T t : list ) {
            arrayList.add( map.apply( t ) );
        }
        return arrayList;
    }

    public static class Classroom {
        public static void main( String[] args ) throws IOException, AWTException {
            SimSharing.init();
            for ( int i = 0; i < 30; i++ ) {
                new Student( args ).start();
            }
        }
    }
}
