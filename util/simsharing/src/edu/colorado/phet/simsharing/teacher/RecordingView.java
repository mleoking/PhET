// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;

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
    String lastShownRecording = "";

    public RecordingView( final ActorRef server, String[] args ) {
        this.server = server;
        add( new JLabel( "Recordings" ) );
        recordingList = new JList() {{
            addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e ) {
                    final Object selectedValue = recordingList.getSelectedValue();
                    System.out.println( selectedValue );
                    if ( selectedValue != null && !lastShownRecording.equals( selectedValue.toString() ) ) {
                        Recording recording = (Recording) server.sendRequestReply( new GetRecording( selectedValue.toString() ) );
                        showRecording( recording );
                        lastShownRecording = selectedValue.toString();
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

    private void showRecording( Recording recording ) {
        System.out.println( "recording = " + recording );
        new SimView( new String[0], new StudentID( 0, "Recorded Student" ), new SimView.SampleSource.RecordedData( recording ) ).start();
    }

    private void updateRecordingList() {
        final RecordingList list = (RecordingList) server.sendRequestReply( new GetRecordingList() );
        recordingList.setListData( list.toArray() );//TODO: remember user selection when list is refreshed
    }
}
