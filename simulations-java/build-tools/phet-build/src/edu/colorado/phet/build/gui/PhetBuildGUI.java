package edu.colorado.phet.build.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.build.translate.ImportAndAddBatch;

/**
 * Provides a front-end user interface for building and deploying phet's java simulations.
 * This entry point has no ant xml dependencies.
 */
public class PhetBuildGUI {
    private JFrame frame = new JFrame();

    public PhetBuildGUI( final File baseDir ) {

        this.frame = new JFrame( "PhET Build" );
        JMenuBar menuBar = new JMenuBar();
        JMenu translationMenu = new JMenu( "Translations" );
        JMenuItem deployItem = new JMenuItem( "Deploy" );
        deployItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    ImportAndAddBatch.startImportAndAddBatch( baseDir.getAbsolutePath() );
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        translationMenu.add( deployItem );

        JMenu c = new JMenu( "File" );
        JMenuItem menuItem = new JMenuItem( "Exit" );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        c.add( menuItem );
        menuBar.add( c );
        menuBar.add( translationMenu );
        frame.setJMenuBar( menuBar );
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