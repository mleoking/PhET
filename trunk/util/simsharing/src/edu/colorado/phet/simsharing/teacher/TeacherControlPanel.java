// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.simsharing.GetStudentList;
import edu.colorado.phet.simsharing.StudentID;
import edu.colorado.phet.simsharing.StudentSummary;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * @author Sam Reid
 */
public class TeacherControlPanel extends PSwingCanvas {
    private final ActorRef server;
    private final String[] args;
    private final PNode studentNode;
    private final PNode recordingListNode;

    public TeacherControlPanel( final ActorRef server, String[] args ) {
        this.server = server;
        this.args = args;
        studentNode = new PNode();
        getLayer().addChild( studentNode );

        recordingListNode = new PNode();
        getLayer().addChild( recordingListNode );
        //Look for new students this often
        new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateStudentList();
            }
        } ) {{
            setInitialDelay( 0 );
        }}.start();

        new Timer( 5000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateRecordingList();
            }
        } ).start();
    }

    private void updateRecordingList() {
        final RecordingList list = (RecordingList) server.sendRequestReply( new GetRecordingList() );
        System.out.println( "list = " );
        for ( int i = 0; i < list.size(); i++ ) {
            System.out.println( "" + i + ": " + list.get( i ) );
        }
    }

    private StudentComponent getComponent( StudentID studentID ) {
        for ( int i = 0; i < studentNode.getChildrenCount(); i++ ) {
            PNode child = studentNode.getChild( i );
            if ( child instanceof StudentComponent && ( (StudentComponent) child ).studentID.equals( studentID ) ) {
                return (StudentComponent) child;
            }
        }
        return null;
    }

    private void updateStudentList() {
        double y = 0;
        final StudentList list = (StudentList) server.sendRequestReply( new GetStudentList() );
        for ( int i = 0; i < list.size(); i++ ) {
            StudentSummary student = list.get( i );
            final StudentID studentID = student.getStudentID();
            StudentComponent component = getComponent( studentID );
            if ( component == null ) {
                component = new StudentComponent( studentID, new VoidFunction0() {
                    public void apply() {
                        new StudentWatcher( args, studentID, server ).start();
                    }
                } );
                studentNode.addChild( component );
            }
            component.setThumbnail( student.getBufferedImage() );
            component.setUpTime( student.getUpTime() );
            component.setTimeSinceLastEvent( student.getTimeSinceLastEvent() );
            component.setOffset( 0, y + 2 );
            y = component.getFullBounds().getMaxY();
        }

        //Remove components for students that have exited
        ArrayList<PNode> toRemove = new ArrayList<PNode>();
        for ( int i = 0; i < studentNode.getChildrenCount(); i++ ) {
            PNode child = studentNode.getChild( i );
            if ( child instanceof StudentComponent && !list.containsStudent( ( (StudentComponent) child ).getStudentID() ) ) {
                toRemove.add( child );
            }
        }
        studentNode.removeChildren( toRemove );
    }

}
