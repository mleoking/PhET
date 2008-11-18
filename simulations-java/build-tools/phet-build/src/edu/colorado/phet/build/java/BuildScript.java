package edu.colorado.phet.build.java;

import java.io.File;
import java.util.Locale;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.rev6.scf.ScpFile;
import org.rev6.scf.ScpUpload;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.build.*;

public class BuildScript {
    private PhetProject project;
    private File baseDir;

    public BuildScript( File baseDir, PhetProject project ) {
        this.baseDir = baseDir;
        this.project = project;
    }

    public void clean() {
        try {
            new PhetCleanCommand( project, new MyAntTaskRunner() ).execute();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public boolean isSVNInSync( ) {

        boolean upToDate = new SVNStatusChecker().isUpToDate( project );
        System.out.println( "upToDate = " + upToDate );
        return upToDate;
    }

//    public void deployDev( PhetProject selectedProject ) {
//        deploy( selectedProject, PhetServer.DEVELOPMENT, getDevelopmentAuthentication() );
//    }

    public void deploy( PhetServer server, PhetBuildGUI.AuthenticationInfo authenticationInfo ) {
        clean();
        build();//build before deploying new SVN number in case there are errors

        incrementVersionNumber();
//        commitNewVersionNumber( getSelectedProject() );

        boolean upToDate = isSVNInSync( );
        if ( !upToDate ) {
            System.out.println( "SVN is out of sync; halting" );
        }
        else {
//        String codebase=server.getCodebase(selectedProject);
//        String codebase=server.getUrl()+"/"+selectedProject.getName()+

//        String newVersion=""+3;
//        int devVersion =buildJNLP( getSelectedProject(), server.getUrl() + "/" + selectedProject.getName() + "/" + newVersion );

            SshConnection sshConnection = new SshConnection( "spot.colorado.edu", authenticationInfo.getUsername(), authenticationInfo.getPassword() );
            try {
                sshConnection.connect();
                String remotePathDir = server.getPath();
//            sshConnection.executeTask( new SshCommand( "mkdir " + remotePathDir ) );
                File[] f = project.getDefaultDeployDir().listFiles();
                //todo: should handle recursive for future use (if we ever want to support nested directories)
                for ( int i = 0; i < f.length; i++ ) {
                    if ( f[i].getName().startsWith( "." ) ) {
                        //ignore
                    }
                    else {
                        sshConnection.executeTask( new ScpUpload( new ScpFile( f[i], remotePathDir + "/" + f[i].getName() ) ) );
                    }
                }
            }
            catch( SshException e ) {
                e.printStackTrace();
            }
            finally {
                sshConnection.disconnect();
            }
        }
    }

    public void incrementVersionNumber() {
        project.setVersionField( "dev", project.getDevVersion() + 1 );
    }

    public void buildJNLP( PhetProject selectedSimulation, Locale locale, String flavorName, String codebase ) {
        System.out.println( "Building JNLP for locale=" + locale.getLanguage() + ", flavor=" + flavorName );
        PhetBuildJnlpTask j = new PhetBuildJnlpTask();
        j.setDeployUrl( codebase );
        j.setProject( selectedSimulation.getName() );
        j.setLocale( locale.getLanguage() );
        j.setFlavor( flavorName );
        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
        project.setBaseDir( baseDir );
        project.init();
        j.setProject( project );
        j.execute();
        System.out.println( "Finished Building JNLP" );
    }

    public void build( ) {
        try {
            new PhetBuildCommand( project, new MyAntTaskRunner(), true, project.getDefaultDeployJar() ).execute();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void runSim() {
        String locale = "en";
        String flavor = "balloons";
        Java java = new Java();

        if ( project != null ) {
            java.setClassname( project.getFlavor( "balloons" ).getMainclass() );
            java.setFork( true );
            String args = "";
            String[] a = project.getFlavor( flavor ).getArgs();
            for ( int i = 0; i < a.length; i++ ) {
                String s = a[i];
                args += s + " ";
            }
            java.setArgs( args );

            org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
            project.init();

            Path classpath = new Path( project );
            FileSet set = new FileSet();
            set.setFile( this.project.getDefaultDeployJar() );
            classpath.addFileset( set );
            java.setClasspath( classpath );
            if ( !locale.equals( "en" ) ) {
                java.setJvmargs( "-Djavaws.phet.locale=" + locale );
            }

            new MyAntTaskRunner().runTask( java );
//            runTask( java );
        }
    }

}
