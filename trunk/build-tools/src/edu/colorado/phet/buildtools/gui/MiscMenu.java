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
import edu.colorado.phet.buildtools.flash.FlashBuildCommand;
import edu.colorado.phet.buildtools.flash.FlashCommonProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
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
                try {
                    PhetProject[] projects = new PhetProject[]{
                            new JavaSimulationProject( new File( trunk, "simulations-java/simulations/test-project" ) ),
                            new FlashSimulationProject( new File( trunk, "simulations-flash/simulations/test-flash-project" ) )
                    };
                    batchDeploy( projects );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }

            }
        } );
        add( batchTest );

        JMenuItem buildAndDeployAll = new JMenuItem( "Batch Deploy All sims to Dev" );
        buildAndDeployAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                batchDeploy( PhetProject.getAllSimulations( trunk ) );

                //this block of code is useful for deploying a subset of sims
//                PhetProject[]p=PhetFlashProject.getFlashSimulations(trunk );
//                ArrayList list=new ArrayList( );
//                for ( int i = 0; i < p.length; i++ ) {
//                    PhetProject phetProject = p[i];
//                    if (phetProject.getName().compareTo( "pendulum-lab" )>0){
//                        list.add(phetProject);
//                        System.out.println( "keeping: "+phetProject.getName() );
//                    }
//                }
//                batchDeploy( (PhetProject[]) list.toArray( new PhetProject[list.size()] ) );

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

        JMenuItem generateFlashAgreement = new JMenuItem( "Generate Flash Agreement" );
        generateFlashAgreement.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                FlashCommonProject.generateFlashSoftwareAgreement( trunk );
                System.out.println( "Created SoftwareAgreement.as" );
            }
        } );
        add( generateFlashAgreement );
    }

    private void batchDeploy( PhetProject[] projects ) {
        int svnVersion = new BuildScript( trunk, projects[0] ).getRevisionOnTrunkREADME();
        String message = JOptionPane.showInputDialog( "Deploying all sims to dev/.  Make sure you've update your working copy.\n" +
                                                      "Assuming you've updated already, the revision number will be: " + svnVersion + "\n" +
                                                      "Enter a message to add to the change log for all sims\n" +
                                                      "(or Cancel or Enter a blank line to omit batch message)" );
        boolean generateJARs = ProjectPanel.askUserToGenerateOfflineJARS( this );
        BufferedWriter bufferedWriter = null;
        File logFile = new File( trunk, "build-tools/deploy-report.txt" );
        try {

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
            bufferedWriter.write( "#Started batch deploy on " + new Date() );
            bufferedWriter.write( "#revision=" + svnVersion );
            bufferedWriter.write( "#generate.jars=" + generateJARs );

        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }

        for ( int i = 0; i < projects.length; i++ ) {
            BuildScript buildScript = new BuildScript( trunk, projects[i] );

            //Use the same revision number for everything
            buildScript.setRevisionStrategy( new BuildScript.ConstantRevisionStrategy( svnVersion ) );
            //Skip status checks, so that a commit during batch deploy won't cause errors
            buildScript.setDebugSkipStatus( true );
            BuildScript.setGenerateJARs( generateJARs );

            buildScript.setBatchMessage( message );
            final BufferedWriter log = bufferedWriter;
            buildScript.addListener( new BuildScript.Listener() {
                public void deployFinished( BuildScript buildScript, PhetProject project, String codebase ) {
                    try {
                        log.write( project.getName() + ": " + codebase );
                        for ( int k = 0; k < project.getSimulationNames().length; k++ ) {
                            log.write( "\t" + project.getSimulation( project.getSimulationNames()[k] ).getTitle() );
                        }
                        log.write( "" );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }

                public void deployErrorOccurred( BuildScript buildScript, PhetProject project, String error ) {
                    try {
                        log.write( "ERROR: " + project.getName() + ", errror=" + error + "\n" );
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
        System.out.println( "Finished batch deploy, see log file here: " + logFile.getAbsolutePath() );
    }

    public void setSelectedProject( PhetProject selectedProject ) {
        this.selectedProject = selectedProject;
    }
}
