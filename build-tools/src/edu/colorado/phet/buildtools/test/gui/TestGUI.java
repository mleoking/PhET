package edu.colorado.phet.buildtools.test.gui;

import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.PhetServer;

public class TestGUI {

    private JFrame frame;


    public TestGUI( File trunk ) {

        BuildLocalProperties.initRelativeToTrunk( trunk );

        frame = new JFrame( "Test Build GUI" );

        frame.setContentPane( new TestGUIPanel( trunk ) );

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
            new TestGUI( basedir ).start();
        }
    }
}
