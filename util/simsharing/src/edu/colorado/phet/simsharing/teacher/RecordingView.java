// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * @author Sam Reid
 */
public class RecordingView extends VerticalLayoutPanel {
    public JList recordingList;
    private final ActorRef server;

    public RecordingView( final ActorRef server, String[] args ) {
        this.server = server;
        add( new JLabel( "Recordings" ) );
        recordingList = new JList() {{
            addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e ) {
                    System.out.println( recordingList.getSelectedValue() );
                    Recording recording = (Recording) server.sendRequestReply( new GetRecording( recordingList.getSelectedValue().toString() ) );
                    showRecording( recording );
                }
            } );
        }};
        add( recordingList );

        new Timer( 5000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateRecordingList();
            }
        } ).start();
    }

    private void showRecording( Recording recording ) {

    }

    private void updateRecordingList() {
        final RecordingList list = (RecordingList) server.sendRequestReply( new GetRecordingList() );
        System.out.println( "list = " );
        for ( int i = 0; i < list.size(); i++ ) {
            System.out.println( "" + i + ": " + list.get( i ) );
        }
        recordingList.setListData( list.toArray() );//TODO: remember user selection when list is refreshed
    }
}
