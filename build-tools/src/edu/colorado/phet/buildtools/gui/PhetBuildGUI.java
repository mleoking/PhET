package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.OldPhetServer;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;

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

        LoggingUtils.enableAllLogging( "edu.colorado.phet.buildtools" );

        frame = new JFrame( "PhET Build GUI" );

        final PhetBuildGUIPanel guiPanel = new PhetBuildGUIPanel( trunk );
        frame.setContentPane( guiPanel );

        JMenuBar menuBar = new JMenuBar();
        JMenu translationMenu = new TranslationsMenu( trunk );

        JMenu fileMenu = new JMenu( "File" );
        JMenuItem exitMenuItem = new JMenuItem( "Exit" );
        exitMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        fileMenu.add( exitMenuItem );
        menuBar.add( fileMenu );
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

        // recall previous size and location
        frame.setSize( PhetBuildGUIProperties.getInstance().getFrameSize() );
        frame.setLocation( PhetBuildGUIProperties.getInstance().getFrameLocation() );
        
        // save changes to size and location
        frame.addComponentListener( new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                PhetBuildGUIProperties.getInstance().setFrameSize( frame.getSize() );
            }
            public void componentMoved(ComponentEvent e) {
                PhetBuildGUIProperties.getInstance().setFrameLocation( frame.getLocation() );
            }
        });
    }

    public void start() {
        frame.setVisible( true );
    }

    public static boolean confirmProdDeploy( PhetProject project, OldPhetServer server ) {
        String message = "<html>" +
                         "Are you sure you want to deploy <font color=red>" + project.getName() + "</font> to " + "<br>" +
                         OldPhetServer.PRODUCTION.getHost() + " and " + OldPhetServer.DEVELOPMENT.getHost() + "?" + "<br>" +
                         "<br>" +
                         "(And is your <font color=red>VPN</font> connection running?)" +
                         "</html>";
        int option = JOptionPane.showConfirmDialog( new JButton( "Deploy Dev & Prod" ), message, "Confirm", JOptionPane.YES_NO_OPTION );
        return ( option == JOptionPane.YES_OPTION );
    }

    public static void main( final String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir.  The basedir is your machine-specific absolute path to trunk.  Enclose in quotes if the path contains whitespace.  You may also need to set the current working directory to be the basedir for the launch (not sure)." );
        }
        else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    new PhetBuildGUI( new File( args[0] ) ).start();
                }
            } );
        }
    }
}
