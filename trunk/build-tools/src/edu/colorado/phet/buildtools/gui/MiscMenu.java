package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.flash.FlashCommonProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.JavaBuildCommand;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.resource.ResourceDeployClient;
import edu.colorado.phet.licensing.reports.DependencyReport;

import com.jcraft.jsch.JSchException;

/**
 * TODO: when replacing with the Test GUI, do NOT remove this class. It is used directly by the Test GUI
 */
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

        JMenuItem generateLicenseReportItem = new JMenuItem( "Generate License Report" );
        add( generateLicenseReportItem );
        generateLicenseReportItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    DependencyReport report = new DependencyReport( trunk );
                    report.start();
                    PhetWebsite.openBrowser( report.getIndexFile().getAbsolutePath() );
                }
                catch( IOException ex ) {
                    ex.printStackTrace();
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

        JMenuItem batchTest = new JMenuItem( "Batch Deploy Test" );
        batchTest.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    PhetProject[] projects = new PhetProject[]{
                            new JavaSimulationProject( new File( trunk, BuildToolsPaths.JAVA_SIMULATIONS_DIR + "/test-project" ) ),
                            new FlashSimulationProject( new File( trunk, BuildToolsPaths.FLASH_SIMULATIONS_DIR + "/test-flash-project" ) )
                    };
                    batchDeploy( projects, selectDeployStrategy() );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }

            }
        } );
        add( batchTest );

        JMenuItem batchDeployAll = new JMenuItem( "Batch Deploy All Sims" );
        batchDeployAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                batchDeploy( select( PhetProject.getAllSimulations( trunk ) ), selectDeployStrategy() );
            }
        } );
        add( batchDeployAll );

        JMenuItem batchDeployAllJava = new JMenuItem( "Batch Deploy All Java Sims" );
        batchDeployAllJava.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                batchDeploy( select( JavaProject.getJavaSimulations( trunk ) ), selectDeployStrategy() );
            }
        } );
        add( batchDeployAllJava );

        JMenuItem generateJNLP = new JMenuItem( "Generate Prod JNLP" );
        generateJNLP.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                selectedProject.buildLaunchFiles( PhetServer.PRODUCTION.getCodebase( selectedProject ), PhetServer.PRODUCTION.isDevelopmentServer() );
                System.out.println( "Created JNLP Files" );
            }
        } );
        add( generateJNLP );

        JMenuItem updateJavaAgreement = new JMenuItem( "Update software agreement (Java)" );
        add( updateJavaAgreement );
        updateJavaAgreement.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                PhetProject[] projects = JavaProject.getJavaSimulations( trunk );
                new JavaBuildCommand( (JavaProject) projects[0], new MyAntTaskRunner(), true, null ).copySoftwareAgreement();

            }
        } );

        JMenuItem updateFlashAgreement = new JMenuItem( "Update software agreement (Flash)" );
        updateFlashAgreement.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                FlashCommonProject.generateFlashSoftwareAgreement( trunk );
                System.out.println( "Created SoftwareAgreement.as" );
                JOptionPane.showMessageDialog( null, "You still need to do the following steps manually:\n\n1. test the agreement\n2. check in SoftwareAgreeement.as" );
            }
        } );
        add( updateFlashAgreement );

        add( new JSeparator() );

        JMenuItem rebuildInstallers = new JMenuItem( "Rebuild Installers" );
        rebuildInstallers.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Rebuild installers invoked" );
                String rebuildMsg = new String(
                        "Rebuilding of the installers is not yet fully integrated into the build\n" +
                        "process.  For information on how to rebuild the installers manually,\n" +
                        "please see the \"User Guide to the Installer Builder\" in the Unfuddle\n" +
                        "notebook that can be found at the URL:\n" +
                        "https://phet.unfuddle.com/projects/9404/notebooks/3771"
                );
                JOptionPane.showMessageDialog( null, rebuildMsg );
            }
        } );
        add( rebuildInstallers );

        JMenuItem deployInstallers = new JMenuItem( "Deploy Installers" );
        deployInstallers.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Deploy installers invoked" );
                String deployInstallersMsg = new String(
                        "Deployment of the installers is not yet fully integrated into the build\n" +
                        "process.  For information on how to redeploy the installers manually,\n" +
                        "please see the \"User Guide to the Installer Builder\" in the Unfuddle\n" +
                        "notebook that can be found at the URL:\n" +
                        "https://phet.unfuddle.com/projects/9404/notebooks/3771"
                );
                JOptionPane.showMessageDialog( null, deployInstallersMsg );
            }
        } );
        add( deployInstallers );

        add( new JSeparator() );

        JMenuItem deployJavaResource = new JMenuItem( "Deploy Java Resource File" );
        deployJavaResource.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle( "Choose a resource file to deploy" );
                int ret = fileChooser.showOpenDialog( null );
                if ( ret != JFileChooser.APPROVE_OPTION ) {
                    System.out.println( "File was not selected, aborting" );
                    return;
                }

                File resourceFile = fileChooser.getSelectedFile();
                String path = JOptionPane.showInputDialog( "Please input the desired deployment path into JARs (should start and end with '/'):" );
                try {
                    ResourceDeployClient.deployJavaResourceFile( trunk, resourceFile, path );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( JSchException e1 ) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog( null, "The instructions to complete the resource deployment have been printed to the console", "Instructions", JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( deployJavaResource );

        JMenuItem deployFlashResource = new JMenuItem( "Deploy Flash Resource File" );
        deployFlashResource.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle( "Choose a resource file to deploy" );
                int ret = fileChooser.showOpenDialog( null );
                if ( ret != JFileChooser.APPROVE_OPTION ) {
                    System.out.println( "File was not selected, aborting" );
                    return;
                }

                File resourceFile = fileChooser.getSelectedFile();
                String path = JOptionPane.showInputDialog( "Please input the desired deployment path into JARs (should start and end with '/'):" );
                try {
                    ResourceDeployClient.deployFlashResourceFile( trunk, resourceFile, path );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( JSchException e1 ) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog( null, "The instructions to complete the resource deployment have been printed to the console", "Instructions", JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( deployFlashResource );
    }

    private PhetProject[] select( PhetProject[] allSimulations ) {
        System.out.println( "starting selection with jvm: " + System.getProperty( "java.version" ) );
        ArrayList todo = new ArrayList();
        String deployedAsIOMBeta = "acid-base-solutions,balloons,battery-resistor-circuit,battery-voltage,bound-states,cavendish-experiment,charges-and-fields-scala,circuit-construction-kit,color-vision,conductivity,discharge-lamps,eating-and-exercise,efield," +
                                   "electric-hockey,energy-skate-park,faraday,forces-1d,fourier,glaciers,greenhouse,hydrogen-atom," +
                                   "ideal-gas,java-common-strings";//batch 3

        ArrayList done = new ArrayList();
        StringTokenizer st = new StringTokenizer( deployedAsIOMBeta, "," );
        while ( st.hasMoreTokens() ) {
            done.add( st.nextToken() );
        }
        for ( int i = 0; i < allSimulations.length; i++ ) {
            PhetProject allSimulation = allSimulations[i];
            if ( !done.contains( allSimulation.getName() ) ) {
                todo.add( allSimulation );
            }
        }
        return (PhetProject[]) todo.toArray( new PhetProject[0] );
    }

    private DeployStrategy selectDeployStrategy() {
        DeployDev dev = new DeployDev();
        DeployStrategy sel = (DeployStrategy) JOptionPane.showInputDialog( this, "Choose a batch deploy location", "Batch deploy location", JOptionPane.QUESTION_MESSAGE, null, new Object[]{dev, new DeployProd()}, dev );
        return sel;
    }

    private static interface DeployStrategy {
        void deploy( BuildScript buildScript );
    }

    public static class DeployDev implements DeployStrategy {
        public void deploy( BuildScript buildScript ) {
            buildScript.deployDev( BuildLocalProperties.getInstance().getDevAuthenticationInfo(), true );
        }

        public String toString() {
            return "Deploy dev";
        }
    }

    public static class DeployProd implements DeployStrategy {
        public void deploy( BuildScript buildScript ) {
            buildScript.deployProd( BuildLocalProperties.getInstance().getDevAuthenticationInfo(),
                                    BuildLocalProperties.getInstance().getWebsiteProdAuthenticationInfo() );
        }

        public String toString() {
            return "deploy prod";
        }
    }

    public static boolean askUserToGenerateOfflineJARS( JComponent parent ) {
        Object[] objects = {"No", "Yes"};
        int option = JOptionPane.showOptionDialog( parent, "Generate locale-specific JARs?", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, objects, objects[0] );
        final boolean generateOfflineJars = option == 1;
        return generateOfflineJars;
    }

    private void batchDeploy( PhetProject[] projects, DeployStrategy deployStrategy ) {
        PhetServer.showReminder = false;
        int svnVersion = new BuildScript( trunk, projects[0] ).getRevisionOnTrunkREADME();
        String message = JOptionPane.showInputDialog( "Deploying all sims to dev/.  Make sure you've update your working copy.\n" +
                                                      "Assuming you've updated already, the revision number will be: " + svnVersion + "\n" +
                                                      "Enter a message to add to the change log for all sims\n" +
                                                      "(or Cancel or Enter a blank line to omit batch message)" );
        boolean generateJARs = askUserToGenerateOfflineJARS( this );
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
            deployStrategy.deploy( buildScript );
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
