// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerWindowOnScreen;

public class Teacher {
    private final String[] args;

    public Teacher( String[] args ) {
        this.args = args;
    }

    public static void main( String[] args ) throws IOException {
        Server.parseArgs( args );
        new Teacher( args ).start();
    }

    private void start() {
        final IServer server = IServer.Impl.getServer();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame( "Students" ) {{
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    setContentPane( new JPanel( new BorderLayout() ) {{
                        add( new ClassroomView( server, args ), BorderLayout.CENTER );
                        add( new RecordingView( server ), BorderLayout.EAST );
                    }} );
                    setSize( 800, 600 );
                    centerWindowOnScreen( this );
                }}.setVisible( true );
            }
        } );
    }
}