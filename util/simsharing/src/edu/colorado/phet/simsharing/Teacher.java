// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * This implementation of Teacher connects to the server and sends requests for the latest data.  This is a polling model, since server to client push doesn't seem to be supported by Akka:
 * see http://groups.google.com/group/akka-user/browse_thread/thread/3ed4cc09c36c19e0
 *
 * @author Sam Reid
 */
public class Teacher {

    private final String[] args;
    protected ActorRef server;

    public Teacher( String[] args ) {
        this.args = args;
    }

    public static void main( String[] args ) throws IOException {
        SimSharing.init();
        new Teacher( args ).start();
    }

    private StudentID selected = null;

    public static class StudentComponent extends PNode {
        private StudentID studentID;

        public StudentComponent( StudentID studentID, final VoidFunction0 watch ) {
            addChild( new PText( studentID.getName() ) );
            final ButtonNode buttonNode = new ButtonNode( "Watch" ) {{
                setOffset( 100, 0 );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        watch.apply();
                    }
                } );
            }};
            addChild( buttonNode );
            int width = 200;
            double aspectRatio = 1024.0 / 768;
            addChild( new PImage( new BufferedImage( width, (int) ( width / aspectRatio ), BufferedImage.TYPE_INT_ARGB_PRE ) {{
                Graphics2D g2 = createGraphics();
                g2.setPaint( Color.black );
                g2.fill( new Rectangle( 0, 0, getWidth(), getHeight() ) );
                g2.dispose();
            }} ) {{
                setOffset( buttonNode.getFullBounds().getMaxX() + 10, 0 );
            }} );
        }
    }

    private void start() {
        server = Actors.remote().actorFor( "server", Server.IP_ADDRESS, Server.PORT );
        JFrame studentListFrame = new JFrame( "Students" );
        studentListFrame.setContentPane( new PSwingCanvas() {{
            getLayer().addChild( new PNode() {{
                new Timer( 1000, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        double y = 0;
                        final StudentID[] newData = ( (StudentList) server.sendRequestReply( new GetStudentList() ) ).toArray();
                        removeAllChildren();
                        for ( final StudentID studentID : newData ) {
                            final StudentComponent child = new StudentComponent( studentID, new VoidFunction0() {
                                public void apply() {
                                    watch( studentID );
                                }
                            } );
                            child.setOffset( 0, y + 2 );
                            y = child.getFullBounds().getMaxY();
                            addChild( child );
                        }
                    }
                } ).start();
            }} );
        }} );
        studentListFrame.setSize( 800, 600 );
        SwingUtils.centerWindowOnScreen( studentListFrame );
        studentListFrame.setVisible( true );
    }

    public void watch( final StudentID studentID ) {
        Thread t = new Thread( new Runnable() {
            public void run() {
                //Have to launch from non-swing-thread otherwise receive:
                //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
                final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args );
                application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" );
                application.getGravityAndOrbitsModule().setTeacherMode( true );
                while ( true ) {
                    final GravityAndOrbitsApplicationState response = (GravityAndOrbitsApplicationState) server.sendRequestReply( new TeacherDataRequest( studentID ) );
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
