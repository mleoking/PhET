package edu.colorado.phet.build.java;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.*;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.rev6.scf.*;

import edu.colorado.phet.build.*;
import edu.colorado.phet.build.util.ProcessOutputReader;

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

    public boolean isSVNInSync() {
        boolean upToDate = new SVNStatusChecker().isUpToDate( project );
        return upToDate;
    }

    public void deploy( PhetServer server, PhetBuildGUI.AuthenticationInfo authenticationInfo ) {
        clean();

        if ( !isSVNInSync() ) {
            System.out.println( "SVN is out of sync; halting" );
            return;
        }

        incrementDevVersion();
        int svnNumber = getSVNVersion();
        System.out.println( "Current SVN: " + svnNumber );
        setSVNVersion( svnNumber + 1 );
        commitNewVersionFile();

        build();//would be nice to build before deploying new SVN number in case there are errors,
        //however, we need the correct version info in the JAR

        String codebase = server.getURL( project );
        System.out.println( "codebase = " + codebase );
        buildJNLP( codebase );

        sendSSH( server, authenticationInfo );
        openBrowser( server.getURL( project ) );
    }

    private void openBrowser( String deployPath ) {
        try {
            Runtime.getRuntime().exec( new String[]{"C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe", deployPath} );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void buildJNLP( String codebase ) {
        String[] flavorNames = project.getFlavorNames();
        Locale[] locales = project.getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            Locale locale = locales[i];

            for ( int j = 0; j < flavorNames.length; j++ ) {
                String flavorName = flavorNames[j];
                buildJNLP( locale, flavorName, codebase );
            }
        }
    }


    private void sendSSH( PhetServer server, PhetBuildGUI.AuthenticationInfo authenticationInfo ) {
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        try {
            sshConnection.connect();
            String remotePathDir = server.getPath( project );
            sshConnection.executeTask( new SshCommand( "mkdir " + remotePathDir ) );//todo: would it be worthwhile to skip this task when possible?
            File[] f = project.getDefaultDeployDir().listFiles(); //todo: should handle recursive for future use (if we ever want to support nested directories)
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

    private void setSVNVersion( int svnVersion ) {
        project.setVersionField( "revision", svnVersion );
    }

    public int getSVNVersion() {
        File readmeFile = new File( baseDir, "README.txt" );
        if ( !readmeFile.exists() ) {
            throw new RuntimeException( "Readme file doesn't exist, need to get version info some other way" );
        }
        String[] args = new String[]{"svn", "status", "-u", readmeFile.getAbsolutePath()};
        ProcessOutputReader.ProcessExecResult output = ProcessOutputReader.exec( args );
        System.out.println( "output = " + output );
        StringTokenizer st = new StringTokenizer( output.getOut(), "\n" );
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            String key = "Status against revision:";
            if ( token.toLowerCase().startsWith( key.toLowerCase() ) ) {
                String suffix = token.substring( key.length() ).trim();
                System.out.println( "suffix = " + suffix );
                return Integer.parseInt( suffix );
            }
        }
        throw new RuntimeException( "No svn version information found: " + output );
    }

    private void commitNewVersionFile() {
        String svnusername = prompt( "SVN username" );
        String svnpassword = prompt( "SVN password" );
        String message = project.getName() + ": deployed version " + project.getVersionString();
        String[] args = new String[]{"svn", "commit", "--username", svnusername, "--password", svnpassword, "--message", message, project.getProjectDir().getAbsolutePath()};
        //TODO: verify that SVN repository revision number now matches what we wrote to the project properties file
        ProcessOutputReader.ProcessExecResult a = ProcessOutputReader.exec( args );
        if ( a.getTerminatedNormally() ) {
            System.out.println( "Finished committing new version file with message: " + message );
        }
        else {
            System.out.println( "Abnormal termination: " + a );
        }
    }

    private String prompt( String s ) {
        return JOptionPane.showInputDialog( s );
    }

    public void incrementDevVersion() {
        project.setVersionField( "dev", project.getDevVersion() + 1 );
    }

    public void buildJNLP( Locale locale, String flavorName, String codebase ) {
        System.out.println( "Building JNLP for locale=" + locale.getLanguage() + ", flavor=" + flavorName );
        PhetBuildJnlpTask j = new PhetBuildJnlpTask();
        j.setDeployUrl( codebase );
        j.setProject( project.getName() );
        j.setLocale( locale.getLanguage() );
        j.setFlavor( flavorName );
        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
        project.setBaseDir( baseDir );
        project.init();
        j.setProject( project );
        j.execute();
        System.out.println( "Finished Building JNLP" );
    }

    public void build() {
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
