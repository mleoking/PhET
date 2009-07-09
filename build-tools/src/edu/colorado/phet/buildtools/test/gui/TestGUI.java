package edu.colorado.phet.buildtools.test.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.gui.MiscMenu;
import edu.colorado.phet.buildtools.translate.CommonTranslationDeployClient;
import edu.colorado.phet.buildtools.translate.TranslationDeployClient;

public class TestGUI {

    private JFrame frame;


    public TestGUI( final File trunk ) {

        BuildLocalProperties.initRelativeToTrunk( trunk );

        frame = new JFrame( "Test Build GUI" );

        final TestGUIPanel guiPanel = new TestGUIPanel( trunk );
        frame.setContentPane( guiPanel );

        JMenuBar menuBar = new JMenuBar();
        JMenu translationMenu = new JMenu( "Translations" );
        JMenuItem deployItem = new JMenuItem( "Deploy Simulation Translation..." );
        deployItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    TranslationDeployClient translationDeployClient = new TranslationDeployClient( trunk );
                    translationDeployClient.startClient();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        translationMenu.add( deployItem );

        JMenuItem deployCommonItem = new JMenuItem( "Deploy Common Translation..." );
        deployCommonItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle( "Choose a common translation to deploy" );
                int ret = fileChooser.showOpenDialog( null );
                if ( ret != JFileChooser.APPROVE_OPTION ) {
                    System.out.println( "File was not selected, aborting" );
                    return;
                }

                File resourceFile = fileChooser.getSelectedFile();

                new CommonTranslationDeployClient( resourceFile, trunk ).deployCommonTranslation();

                JOptionPane.showMessageDialog( null, "The instructions to complete the common translation deployment have been printed to the console", "Instructions", JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        translationMenu.add( deployCommonItem );

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

        guiPanel.getProjectList().addListener( new TestProjectList.Listener() {
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
            new TestGUI( basedir ).start();
        }
    }
}
