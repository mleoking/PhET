// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * @author Sam Reid
 */
public class StudentListPanel extends PSwingCanvas {
    private final ActorRef server;
    private final String[] args;
    public PNode studentNode;

    public StudentListPanel( final ActorRef server, String[] args ) {
        this.server = server;
        this.args = args;
        studentNode = new PNode();
        getLayer().addChild( studentNode );
        //Look for new students this often
        new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateStudentList();
            }
        } ) {{
            setInitialDelay( 0 );
        }}.start();
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
        final StudentList newData = (StudentList) server.sendRequestReply( new GetThumbnails() );
        for ( final Pair<StudentID, SerializableBufferedImage> elm : newData.getStudentIDs() ) {
            final StudentID studentID = elm._1;
            StudentComponent component = getComponent( studentID );
            if ( component == null ) {
                component = new StudentComponent( studentID, new VoidFunction0() {
                    public void apply() {
                        new StudentWatcher( args, studentID, server ).start();
                    }
                } );
                studentNode.addChild( component );
            }
            component.setThumbnail( elm._2.getBufferedImage() );
            component.setOffset( 0, y + 2 );
            y = component.getFullBounds().getMaxY();
        }

        //Remove components for students that have exited
        ArrayList<PNode> toRemove = new ArrayList<PNode>();
        for ( int i = 0; i < studentNode.getChildrenCount(); i++ ) {
            PNode child = studentNode.getChild( i );
            if ( child instanceof StudentComponent && !newData.containsStudent( ( (StudentComponent) child ).getStudentID() ) ) {
                toRemove.add( child );
            }
        }
        studentNode.removeChildren( toRemove );
    }

}
