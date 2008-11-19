package edu.colorado.phet.build.java;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;

/**
 * Provides a front-end user interface for building and deploying phet's java simulations.
 * This entry point has no ant dependencies.
 */
public class PhetBuildGUI {
    private JFrame frame = new JFrame();
    private Object blocker = new Object();

    private File baseDir;
    private LocalProperties localProperties;

    public PhetBuildGUI( File baseDir ) {
        this.baseDir = baseDir;
        localProperties = new LocalProperties( baseDir );

        this.frame = new JFrame( "PhET Build" );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                synchronized( blocker ) {
                    blocker.notifyAll();
                }
            }
        } );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new ProjectPanel( baseDir ) );

        frame.setSize( 800, 600 );
//        frame.setContentPane( contentPane );
    }


//    private void runCommandWithWaitDialog( String msg, final Runnable r ) {
//        final JDialog dialog = new JDialog( frame, msg );
//        JLabel label = new JLabel( "Building " + getSelectedProject() + ", please wait..." );
//        label.setOpaque( true );
//        JPanel panel = new JPanel();
//        panel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
//        panel.add( label );
//        dialog.setContentPane( panel );
//        dialog.pack();
//        dialog.setResizable( false );
//        dialog.setLocation( frame.getX() + frame.getWidth() / 2 - dialog.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - dialog.getHeight() / 2 );
//        dialog.setVisible( true );
//
//        Runnable r2 = new Runnable() {
//            public void run() {
//                r.run();
//                dialog.dispose();
//            }
//        };
//        Thread thread = new Thread( r2 );
//        thread.start();
//    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir" );
        }
        else {
            File basedir = new File( args[0] );
            new PhetBuildGUI( basedir ).start();
        }
    }
}