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

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.flash.PhetFlashProject;
import edu.colorado.phet.buildtools.java.JavaBuildCommand;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;

public class MiscMenu extends JMenu {
    private PhetProject selectedProject;
    private File trunk;

    public MiscMenu( final File trunk ) {
        super( "Misc" );
        this.trunk = trunk;
        JMenuItem generateLicenseInfoItem = new JMenuItem( "Generate License Info" );
        add( generateLicenseInfoItem );
        generateLicenseInfoItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = PhetProject.getAllProjects( trunk );
                for ( int i = 0; i < projects.length; i++ ) {
                    PhetProject project = projects[i];
                    project.copyLicenseInfo();
                }
            }
        } );

        JMenuItem copyAgreementItem = new JMenuItem( "Copy software agreement" );
        add( copyAgreementItem );
        copyAgreementItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                PhetProject[] projects = JavaProject.getJavaSimulations( trunk );
                new JavaBuildCommand( (JavaProject) projects[0], new MyAntTaskRunner(), true, null ).copySoftwareAgreement();

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

        JMenuItem batchTest = new JMenuItem( "Batch Deploy-Test" );
        batchTest.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetProject[] projects = new PhetProject[0];
                try {
                    projects = new PhetProject[]{new JavaSimulationProject( new File( trunk, "simulations-java/simulations/test-project" ) ),
                            new PhetFlashProject( trunk, "simulations-flash/simulations/test-flash-project" )};
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }

                batchDeploy( projects );
            }
        } );
        add( batchTest );

        JMenuItem buildAndDeployAll = new JMenuItem( "Batch Deploy All to Dev" );
        buildAndDeployAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                batchDeploy( PhetProject.getAllSimulations( trunk ) );

            }
        } );
        add( buildAndDeployAll );


        JMenuItem generateJNLP = new JMenuItem( "Generate Prod JNLP" );
        generateJNLP.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                selectedProject.buildLaunchFiles( PhetServer.PRODUCTION.getCodebase( selectedProject ), PhetServer.PRODUCTION.isDevelopmentServer() );
                System.out.println( "Created JNLP Files" );
            }
        } );
        add( generateJNLP );
    }

    private void batchDeploy( PhetProject[] projects ) {
        String message = JOptionPane.showInputDialog( "Deploying all sims to dev/.  \n" +
                                                      "Enter a message to add to the change log for all sims\n" +
                                                      "(or Cancel or Enter a blank line to omit batch message)" );
        BufferedWriter bufferedWriter = null;
        try {
            File logFile = new File( trunk, "build-tools/deploy-report.txt" );
            boolean deleted = logFile.delete();
            System.out.println( "Delete " + logFile.getAbsolutePath() + " = " + deleted );
            logFile.createNewFile();
            System.out.println( "Started logging to: " + logFile.getAbsolutePath() );
            bufferedWriter = new BufferedWriter( new FileWriter( logFile ) ) {
                public void write( String str ) throws IOException {//log should write to console and file
                    super.write( str + "\n" );
                    flush();
                    System.out.println( str );
                }
            };
            bufferedWriter.write( "#Started batch deploy on " + new Date() + "\n" );

        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
        for ( int i = 0; i < projects.length; i++ ) {
            BuildScript buildScript = new BuildScript( trunk, projects[i] );
            buildScript.setBatchMessage( message );
            final BufferedWriter bufferedWriter1 = bufferedWriter;
            buildScript.addListener( new BuildScript.Listener() {
                public void deployFinished( BuildScript buildScript, PhetProject project, String codebase ) {
                    try {
                        bufferedWriter1.write( project.getName() + ": " + codebase );
                        for ( int k = 0; k < project.getSimulationNames().length; k++ ) {
                            bufferedWriter1.write( "\t" + project.getSimulation( project.getSimulationNames()[k] ).getTitle() );
                        }
                        bufferedWriter1.write( "" );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }

                public void deployErrorOccurred( BuildScript buildScript, PhetProject project, String error ) {
                    try {
                        bufferedWriter1.write( "ERROR: " + project.getName() + ", errror=" + error + "\n" );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            } );
            buildScript.deployDev( BuildLocalProperties.getInstance().getDevAuthenticationInfo(), true );
        }
        try {
            bufferedWriter.close();
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
    }

    public void setSelectedProject( PhetProject selectedProject ) {
        this.selectedProject = selectedProject;
    }
}
