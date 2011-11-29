// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.simsharing.IActor;
import edu.colorado.phet.common.phetcommon.simsharing.ObjectActor;
import edu.colorado.phet.simsharing.server.Server;
import edu.colorado.phet.simsharing.teacher.ClassroomView;
import edu.colorado.phet.simsharing.teacher.RecordingView;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerWindowOnScreen;

public class Teacher {
    private void start() throws IOException, ClassNotFoundException {
        final IActor actor = new ObjectActor();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame( "Students" ) {{
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    setContentPane( new JPanel( new BorderLayout() ) {{
                        add( new ClassroomView( actor ), BorderLayout.CENTER );
                        add( new RecordingView( actor ), BorderLayout.EAST );
                    }} );
                    setSize( 800, 600 );
                    centerWindowOnScreen( this );
                }}.setVisible( true );
            }
        } );
    }

    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        Server.parseArgs( args );
        new Teacher().start();
    }
}