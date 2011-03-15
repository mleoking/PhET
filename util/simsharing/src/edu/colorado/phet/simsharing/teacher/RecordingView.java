// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;
import akka.actor.ActorTimeoutException;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.simsharing.SessionID;
import edu.colorado.phet.simsharing.SessionStarted;

/**
 * @author Sam Reid
 */
public class RecordingView extends JPanel {
    public JList recordingList;
    private final ActorRef server;
    private SessionStarted lastShownRecording = new SessionStarted( new SessionID( -1, "hello" ), 0 );//dummy data so comparisons don't need to use null checks

    public RecordingView( final ActorRef server, String[] args ) {
        super( new BorderLayout() );
        this.server = server;
        add( new JLabel( "All Sessions" ), BorderLayout.NORTH );
        recordingList = new JList() {{
            addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e ) {
                    final SessionStarted selectedValue = (SessionStarted) recordingList.getSelectedValue();
                    System.out.println( selectedValue );
                    if ( selectedValue != null && !lastShownRecording.equals( selectedValue ) ) {
                        showRecording( selectedValue );
                        lastShownRecording = selectedValue;
                    }
                }
            } );
        }};
        add( new JScrollPane( recordingList ), BorderLayout.CENTER );

        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        //Allow a long timeout here since it may take a long time to deliver a large recorded file.
                        Thread.sleep( 1000 );
                        final SessionList list = (SessionList) server.sendRequestReply( new GetSessionList() );
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    recordingList.setListData( list.toArray() );//TODO: remember user selection when list is refreshed
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
                    catch ( ActorTimeoutException timeoutException ) {
                        System.out.println( "Actor timed out" );
                        timeoutException.printStackTrace();
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private void showRecording( SessionStarted sessionID ) {
        System.out.println( "recording = " + sessionID );
        new SimView( new String[0], sessionID.getSessionID(), new SimView.SampleSource.RemoteActor( server, sessionID.getSessionID() ), true ).start();
    }
}