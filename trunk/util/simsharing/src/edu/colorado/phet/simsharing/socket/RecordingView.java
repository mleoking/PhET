// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.simsharing.SessionID;
import edu.colorado.phet.simsharing.SessionStarted;
import edu.colorado.phet.simsharing.teacher.ClearDatabase;
import edu.colorado.phet.simsharing.teacher.GetSessionList;
import edu.colorado.phet.simsharing.teacher.SessionList;

/**
 * @author Sam Reid
 */
public class RecordingView extends JPanel {
    public JList recordingList;
    private SessionStarted lastShownRecording = new SessionStarted( new SessionID( -1, "hello" ), 0 );//dummy data so comparisons don't need to use null checks
    private IActor server;

    public RecordingView( final IActor server ) {
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
        add( new JButton( "Clear" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    try {
                        server.tell( new ClearDatabase() );
                    }
                    catch ( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            } );
        }}, BorderLayout.SOUTH );

        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        //Allow a long timeout here since it may take a long time to deliver a large recorded file.
                        Thread.sleep( 1000 );
                        final SessionList[] list = new SessionList[1];
                        try {
                            list[0] = (SessionList) server.ask( new GetSessionList() );
                        }
                        catch ( IOException e ) {
                            e.printStackTrace();
                        }
                        catch ( ClassNotFoundException e ) {
                            e.printStackTrace();
                        }
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    recordingList.setListData( list[0].toArray() );//TODO: remember user selection when list is refreshed
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