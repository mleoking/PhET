package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.PhetServer;

/**
 * Main entry point for the PhET Build GUI (currently TestGUI)
 * TODO: change documentation when replacing PBG
 * <p/>
 * This provides a user interface for building, testing and deploying different types of projects, which include Java and
 * Flash simulations.
 * <p/>
 * There is a list of projects on the left hand side which can be selected.
 * <p/>
 * Each project can have a customized project panel on the right hand side with options and information specific to that
 * project.
 */
public class PhetBuildGUI {

    private JFrame frame;

    /**
     * Constructor
     *
     * @param trunk We need a reference to trunk for many things
     */
    public PhetBuildGUI( final File trunk ) {

        BuildLocalProperties.initRelativeToTrunk( trunk );

        frame = new JFrame( "PhET Build GUI" );

        final PhetBuildGUIPanel guiPanel = new PhetBuildGUIPanel( trunk );
        frame.setContentPane( guiPanel );

        JMenuBar menuBar = new JMenuBar();
        JMenu translationMenu = new TranslationsMenu( trunk );

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
        final MiscMenu miscMenu = new MiscMenu( trunk );
        menuBar.add( miscMenu );
        frame.setJMenuBar( menuBar );

        guiPanel.getProjectList().addListener( new ProjectList.Listener() {
            public void notifyChanged() {
                miscMenu.setSelectedProject( guiPanel.getProjectList().getSelectedProject() );
            }
        } );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setSize( 1200, 700 );


    }

    public void start() {
        frame.setVisible( true );
    }

    public static boolean confirmProdDeploy( PhetProject project, PhetServer server ) {
        String message = "<html>" +
                         "Are you sure you want to deploy <font color=red>" + project.getName() + "</font> to " + "<br>" +
                         PhetServer.PRODUCTION.getHost() + " and " + PhetServer.DEVELOPMENT.getHost() + "?" + "<br>" +
                         "<br>" +
                         "(And is your <font color=red>VPN</font> connection running?)" +
                         "</html>";
        int option = JOptionPane.showConfirmDialog( new JButton( "Deploy Dev & Prod" ), message, "Confirm", JOptionPane.YES_NO_OPTION );
        return ( option == JOptionPane.YES_OPTION );
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir.  The basedir is your machine-specific absolute path to trunk.  Enclose in quotes if the path contains whitespace.  You may also need to set the current working directory to be the basedir for the launch (not sure)." );
        }
        else {
            File basedir = new File( args[0] );
            new PhetBuildGUI( basedir ).start();
        }
    }
}
