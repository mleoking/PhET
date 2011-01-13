// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * This implementation of Teacher connects to the server and sends requests for the latest data.  This is a polling model, since server to client push doesn't seem to be supported by Akka:
 * see http://groups.google.com/group/akka-user/browse_thread/thread/3ed4cc09c36c19e0
 *
 * @author Sam Reid
 */
public class Teacher {

    private final String[] args;

    public Teacher( String[] args ) {
        this.args = args;
    }

    public static void main( String[] args ) throws IOException {
        SimSharing.init();
        new Teacher( args ).start();
    }

    private StudentID selected = null;

    private void start() {
        final ActorRef server = Actors.remote().actorFor( "server", Server.IP_ADDRESS, Server.PORT );

        JFrame studentListFrame = new JFrame( "Students" );
        studentListFrame.setContentPane( new JPanel() {{
            add( new JScrollPane(
                    new JList() {{
                        new Timer( 1000, new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                final StudentID[] newData = ( (StudentList) server.sendRequestReply( new GetStudentList() ) ).toArray();
                                if ( !Arrays.equals( newData, getElements( getModel() ) ) ) {
                                    //store and restore selection because when setting new model selection is dropped
                                    Object[] selected = getSelectedValues();
                                    setListData( newData );
                                    setSelectedIndices( getIndices( selected, newData ) );
                                }
                            }
                        } ).start();
                        addListSelectionListener( new ListSelectionListener() {
                            public void valueChanged( ListSelectionEvent e ) {
                                selected = (StudentID) getSelectedValue();
                            }
                        } );
                    }} ) );
        }} );
        studentListFrame.setSize( 800, 600 );
        SwingUtils.centerWindowOnScreen( studentListFrame );
        studentListFrame.setVisible( true );

        final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" );
        application.getGravityAndOrbitsModule().setTeacherMode( true );

        Thread t = new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    final GravityAndOrbitsApplicationState response = (GravityAndOrbitsApplicationState) server.sendRequestReply( new TeacherDataRequest( selected ) );
                    if ( response != null ) {
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    response.apply( application );
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
                }
            }
        } );
        t.start();
    }

    /*
     * Determine the indices of 'selected' in 'all'
     */
    private int[] getIndices( Object[] selected, StudentID[] all ) {
        int[] s = new int[selected.length];
        for ( int i = 0; i < s.length; i++ ) {
            s[i] = Arrays.asList( all ).indexOf( selected[i] );
        }
        return s;
    }

    /*
    * Converts from ListModel to Array.
     */
    private Object[] getElements( ListModel model ) {
        Object[] m = new Object[model.getSize()];
        for ( int i = 0; i < m.length; i++ ) {
            m[i] = model.getElementAt( i );
        }
        return m;
    }
}
