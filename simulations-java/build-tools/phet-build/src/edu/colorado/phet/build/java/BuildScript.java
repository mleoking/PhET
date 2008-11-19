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
import edu.colorado.phet.build.util.FileUtils;

public class BuildScript {
    private PhetProject project;
    private AuthenticationInfo svnAuth;
    private String browser;
    private File baseDir;

    public BuildScript( File baseDir, PhetProject project, AuthenticationInfo svnAuth, String browser ) {
        this.baseDir = baseDir;
        this.project = project;
        this.svnAuth = svnAuth;
        this.browser = browser;
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

    public void deploy( PhetServer server, AuthenticationInfo authenticationInfo, VersionIncrement versionIncrement ) {
        clean();

        if ( !isSVNInSync() ) {
            System.out.println( "SVN is out of sync; halting" );
            return;
        }

        versionIncrement.increment( project );
        int svnNumber = getSVNVersion();
        System.out.println( "Current SVN: " + svnNumber );
        setSVNVersion( svnNumber + 1 );
        commitNewVersionFile();//todo: check that new version number is correct

        //would be nice to build before deploying new SVN number in case there are errors,
        //however, we need the correct version info in the JAR
        build();

        String codebase = server.getURL( project );
        System.out.println( "codebase = " + codebase );
        buildJNLP( codebase );

        copyVersionFilesToDeploy();
        sendSSH( server, authenticationInfo );
        openBrowser( server.getURL( project ) );

        System.out.println( "Finished deploy to: " + server.getHost() );
    }

    private void copyVersionFilesToDeploy() {
        File versionFile = project.getVersionFile();
        try {
            File dest = new File( project.getDefaultDeployDir(), versionFile.getName() );
            FileUtils.copyTo( versionFile, dest );
            System.out.println( "Copied version file to " + dest.getAbsolutePath() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void openBrowser( String deployPath ) {
        if ( browser != null ) {
            try {
                Runtime.getRuntime().exec( new String[]{browser, deployPath} );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
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


    private void sendSSH( PhetServer server, AuthenticationInfo authenticationInfo ) {
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
        StringTokenizer st = new StringTokenizer( output.getOut(), "\n" );
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            String key = "Status against revision:";
            if ( token.toLowerCase().startsWith( key.toLowerCase() ) ) {
                String suffix = token.substring( key.length() ).trim();
                return Integer.parseInt( suffix );
            }
        }
        throw new RuntimeException( "No svn version information found: " + output );
    }

    private void commitNewVersionFile() {
        String svnusername = svnAuth.getUsername();
        String svnpassword = svnAuth.getPassword();
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
        Locale locale = (Locale) prompt( "Choose locale: ", project.getLocales() );
        String flavor = project.getFlavorNames()[0];
        if ( project.getFlavorNames().length > 1 ) {
            flavor = (String) prompt( "Choose flavor: ", project.getFlavorNames() );
        }

        Java java = new Java();

        if ( project != null ) {
            java.setClassname( project.getFlavor( flavor ).getMainclass() );
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
            if ( !locale.getLanguage().equals( "en" ) ) {
                java.setJvmargs( "-Djavaws.phet.locale=" + locale );
            }

            new MyAntTaskRunner().runTask( java );
        }
    }

    private Object prompt( String msg, Object[] locales ) {

        Object[] possibilities = locales;
        Object s = JOptionPane.showInputDialog(
                null,
                msg,
                msg,
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0] );

        if ( s == null ) {
            System.out.println( "Canceled" );
            return null;
        }
        else {
            return s;
        }
    }
}
