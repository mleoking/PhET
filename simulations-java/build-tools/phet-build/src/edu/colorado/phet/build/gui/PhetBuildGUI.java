package edu.colorado.phet.build.gui;

import java.io.File;

import javax.swing.*;

/**
 * Provides a front-end user interface for building and deploying phet's java simulations.
 * This entry point has no ant xml dependencies.
 */
public class PhetBuildGUI {
    private JFrame frame = new JFrame();

    public PhetBuildGUI( File baseDir ) {

        this.frame = new JFrame( "PhET Build" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetBuildGUIPanel panel = new PhetBuildGUIPanel( baseDir );
        frame.setContentPane( panel );

        frame.setSize( 1200, 400 );
    }

    public void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir.  The basedir is your machine-specific absolute path to simulations-java.  Enclose in quotes if the path contains whitespace.  You may also need to set the current working directory to be the basedir for the launch (not sure)." );
        }
        else {
            File basedir = new File( args[0] );
            new PhetBuildGUI( basedir ).start();
        }
    }
}