// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;
import akka.actor.ActorTimeoutException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.simsharing.StudentID;

/**
 * @author Sam Reid
 */
public class RecordingView extends VerticalLayoutPanel {
    public JList recordingList;
    private final ActorRef server;
    StudentID lastShownRecording = new StudentID( -123, "test" );

    public RecordingView( final ActorRef server, String[] args ) {
        this.server = server;
        add( new JLabel( "Recordings" ) );
        recordingList = new JList() {{
            addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e ) {
                    final StudentID selectedValue = (StudentID) recordingList.getSelectedValue();
                    System.out.println( selectedValue );
                    if ( selectedValue != null && !lastShownRecording.equals( selectedValue ) ) {
                        showRecording( selectedValue );
                        lastShownRecording = selectedValue;
                    }
                }
            } );
        }};
        add( recordingList );

        new Timer( 5000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateRecordingList();
            }
        } ) {{setInitialDelay( 0 );}}.start();
    }

    private void showRecording( StudentID studentID ) {
        System.out.println( "recording = " + studentID );
        new SimView( new String[0], studentID, new SimView.SampleSource.RemoteActor( server, studentID ) ).start();
    }

    private void updateRecordingList() {
        //Allow a long timeout here since it may take a long time to deliver a large recorded file.
        try {
            final RecordingList list = (RecordingList) server.sendRequestReply( new GetRecordingList() );
            recordingList.setListData( list.toArray() );//TODO: remember user selection when list is refreshed
        }
        catch ( ActorTimeoutException timeoutException ) {
            System.out.println( "Actor timed out" );
            timeoutException.printStackTrace();
        }
    }
}
