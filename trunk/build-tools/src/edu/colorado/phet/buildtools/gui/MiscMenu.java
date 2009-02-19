package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import javax.swing.*;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.LocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.util.FileUtils;

public class MiscMenu extends JMenu {
    public MiscMenu( final File trunk ) {
        super( "Misc" );

        JMenuItem menuItem1 = new JMenuItem( "Generate License Info" );
        add( menuItem1 );
        menuItem1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( trunk );
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    project.copyLicenseInfo();
                }
            }
        } );

        JMenuItem showAllLicenseKeys = new JMenuItem( "Show Credits Keys" );
        showAllLicenseKeys.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( trunk );
                HashSet keys = new HashSet();
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    keys.addAll( Arrays.asList( project.getCreditsKeys() ) );
                }
                System.out.println( "keys = " + keys );
            }
        } );
        add( showAllLicenseKeys );

        JMenuItem buildAndDeployAll = new JMenuItem( "Build and Deploy all-dev" );
        buildAndDeployAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String message = JOptionPane.showInputDialog( "Deploying all sims to dev/.  \nEnter a message to add to the change log for all sims\n(or Cancel or Enter a blank line to omit batch message)" );
                LocalProperties localProperties = new LocalProperties( new File( trunk, "build-tools/build-local.properties" ) );
                PhetProject[] projects = PhetProject.getAllSimulations( trunk );
                BufferedWriter bufferedWriter = null;
                try {
                    File file = new File( trunk, "build-tools/deploy-report.txt" );
                    boolean deleted=file.delete();
                    System.out.println( "Delete "+file.getAbsolutePath()+" = "+deleted );
                    file.createNewFile();
                    bufferedWriter = new BufferedWriter( new FileWriter( file ) ) {
                        public void write( String str ) throws IOException {
                            super.write( str );
                            flush();
                            System.out.println( str );
                        }
                    };
                    bufferedWriter.write( "#Started batch deploy on " + new Date() );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                for ( int i = 0; i < projects.length; i++ ) {
                    if ( projects[i].getName().startsWith( "test" ) ) {
//                    if ( true ) {
                        BuildScript buildScript = new BuildScript( trunk, projects[i], new AuthenticationInfo( localProperties.getProperty( "svn.username" ), localProperties.getProperty( "svn.password" ) ), localProperties.getProperty( "browser" ) );
                        buildScript.setBatchMessage( message );
                        final BufferedWriter bufferedWriter1 = bufferedWriter;
                        buildScript.addListener( new BuildScript.Listener() {
                            public void deployFinished( BuildScript buildScript, PhetProject project, String codebase ) {
//                                System.out.println( ">>>Deploy finished, project=" + project.getName() + ", codebase=" + codebase );
                                try {
                                    bufferedWriter1.write( project.getName() + ": " + codebase + "\n" );
                                    for ( int k = 0; k < project.getSimulationNames().length; k++ ) {
                                        bufferedWriter1.write( "\t" + project.getSimulation( project.getSimulationNames()[k] ).getTitle() + "\n" );
                                    }
                                    bufferedWriter1.write( "\n" );
                                }
                                catch( IOException e1 ) {
                                    e1.printStackTrace();
                                }
                            }

                            public void deployErrorOccurred( BuildScript buildScript, PhetProject project, String error ) {
                                try {
                                    bufferedWriter1.write( "ERROR: "+project.getName()+", errror="+error +"\n\n");
                                }
                                catch( IOException e1 ) {
                                    e1.printStackTrace();
                                }
                            }
                        } );
                        buildScript.deployDev( new AuthenticationInfo( localProperties.getProperty( "deploy." + "dev" + ".username" ), localProperties.getProperty( "deploy." + "dev" + ".password" ) ) );
                    }
                }
                try {
                    bufferedWriter.close();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        add( buildAndDeployAll );
    }
}
