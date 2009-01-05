package edu.colorado.phet.build.java;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.*;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.build.*;
import edu.colorado.phet.build.translate.ScpTo;
import edu.colorado.phet.build.util.FileUtils;
import edu.colorado.phet.build.util.ProcessOutputReader;

import com.jcraft.jsch.JSchException;

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
        return new SVNStatusChecker().isUpToDate( project );
    }

    static interface Task {
        boolean invoke();
    }

    static class NullTask implements Task {
        public boolean invoke() {
            return true;
        }
    }

    public void deploy( PhetServer server, AuthenticationInfo authenticationInfo, VersionIncrement versionIncrement ) {
        deploy( new NullTask(), server, authenticationInfo, versionIncrement, new NullTask() );
    }

    public void deploy( Task preDeployTask, PhetServer server,
                        AuthenticationInfo authenticationInfo, VersionIncrement versionIncrement, Task postDeployTask ) {
        clean();

        if ( !isSVNInSync() ) {
            System.out.println( "SVN is out of sync; halting" );
            return;
        }

        versionIncrement.increment( project );
        int svnNumber = getSVNVersion();
        System.out.println( "Current SVN: " + svnNumber );
        setSVNVersion( svnNumber + 1 );
        addMessagesToChangeFile( svnNumber + 1 );

        commitProject();//commits both changes to version and change file

        //todo: check that new version number is correct

        //would be nice to build before deploying new SVN number in case there are errors,
        //however, we need the correct version info in the JAR
        build();

        String codebase = server.getURL( project );
        System.out.println( "codebase = " + codebase );
        buildJNLP( codebase, server.isDevelopmentServer() );

        createHeader( svnNumber );

        copyVersionFilesToDeployDir();

        boolean ok = preDeployTask.invoke();
        if ( !ok ) {
            System.out.println( "Pre deploy task failed" );
            return;
        }
        sendSSH( server, authenticationInfo );

        postDeployTask.invoke();

        openBrowser( server.getURL( project ) );

        System.out.println( "Finished deploy to: " + server.getHost() );
    }

    private void addMessagesToChangeFile( int svn ) {
        String message = JOptionPane.showInputDialog( "Enter a message to add to the change log\n(or Enter if change log is up to date)" );
        if ( message.trim().length() > 0 ) {
            prependChange( message );
        }

        prependChange( "# " + getFullVersionStr( svn ) );
    }

    private String getFullVersionStr( int svn ) {
        return project.getVersionString() + " (" + svn + ") " + new SimpleDateFormat( "MM-dd-yyyy" ).format( new Date() );
    }

    private void prependChange( String message ) {
        project.prependChangesText( message );
    }

    private void copyVersionFilesToDeployDir() {
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

    public void buildJNLP( String codebase, boolean dev ) {
        String[] flavorNames = project.getFlavorNames();
        Locale[] locales = project.getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            Locale locale = locales[i];

            for ( int j = 0; j < flavorNames.length; j++ ) {
                String flavorName = flavorNames[j];
                buildJNLP( locale, flavorName, codebase, dev );
            }
        }
    }


    private void sendSSH( PhetServer server, AuthenticationInfo authenticationInfo ) {
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        String remotePathDir = server.getPath( project );
        try {
            sshConnection.connect();

            sshConnection.executeTask( new SshCommand( "mkdir " + remotePathDir ) );//todo: would it be worthwhile to skip this task when possible?
        }
        catch( SshException e ) {
            e.printStackTrace();
        }
        finally {
            sshConnection.disconnect();
        }

        //for some reason, the securechannelfacade fails with a "server didn't expect this file" error
        //the failure is on tigercat, but scf works properly on spot
        //but our code works on both; therefore there is probably a problem with the handshaking in securechannelfacade
        File[] f = project.getDefaultDeployDir().listFiles(); //todo: should handle recursive for future use (if we ever want to support nested directories)
        for ( int i = 0; i < f.length; i++ ) {
            if ( f[i].getName().startsWith( "." ) ) {
                //ignore
            }
            else {
                //server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword()
                try {
                    ScpTo.uploadFile( f[i], authenticationInfo.getUsername(), server.getHost(), remotePathDir + "/" + f[i].getName(), authenticationInfo.getPassword() );
                }
                catch( JSchException e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                catch( IOException e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
//                    sshConnection.executeTask( new ScpUpload( new ScpFile( f[i],  ) ) );
            }
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

    private void commitProject() {
        String svnusername = svnAuth.getUsername();
        String svnpassword = svnAuth.getPassword();
        String message = project.getName() + ": deployed version " + project.getVersionString();
        String path = project.getProjectDir().getAbsolutePath();
        String[] args = new String[]{"svn", "commit", "--username", svnusername, "--password", svnpassword, "--message", message, path};
        //TODO: verify that SVN repository revision number now matches what we wrote to the project properties file
        ProcessOutputReader.ProcessExecResult a = ProcessOutputReader.exec( args );
        if ( a.getTerminatedNormally() ) {
            System.out.println( "Finished committing new version file with message: " + message + " output/err=" );
            System.out.println( a.getOut() );
            System.out.println( a.getErr() );
            System.out.println( "Finished committing new version file with message: " + message );
        }
        else {
            System.out.println( "Abnormal termination: " + a );
        }
    }

    public void buildJNLP( Locale locale, String flavorName, String codebase, boolean dev ) {
        System.out.println( "Building JNLP for locale=" + locale.getLanguage() + ", flavor=" + flavorName );
        PhetBuildJnlpTask j = new PhetBuildJnlpTask();
        j.setDev( dev );
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
            FileUtils.copyTo( project.getDefaultDeployJar(), new File( project.getDefaultDeployDir(), "" + project.getName() + "_all.jar" ) );

            //todo: should clean up old jar
//            FileUtils.delete( project.getDefaultDeployJar() );
            System.out.println( "**** Finished BuildScript.build" );
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

        runSim( locale, flavor );
    }

    public void runSim( Locale locale, String flavor ) {
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

            java.setArgs( "-dev" ); // program arg to run in developer mode

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

    public void createHeader( int svn ) {
        try {
            FileUtils.filter( new File( baseDir, "build-tools/phet-build/templates/header-template.html" ), new File( project.getDefaultDeployDir(), "HEADER" ), createHeaderFilterMap( svn ), "UTF-8" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private HashMap createHeaderFilterMap( int svn ) {
        HashMap map = new HashMap();
        map.put( "project-name", project.getName() );
        map.put( "version", getFullVersionStr( svn ) );
        map.put( "sim-list", getSimListHTML( project ) );
        map.put( "jnlp-filename", project.getFlavorNames()[0] + ".jnlp" );
        map.put( "new-summary", getNewSummary() );
        return map;
    }

    private String getSimListHTML( PhetProject project ) {
        //<li><a href="@jnlp-filename@">Launch @sim-name@</a></li>
        String s = "";
        for ( int i = 0; i < project.getFlavorNames().length; i++ ) {
            String jnlpFilename = project.getFlavorNames()[i] + ".jnlp";
            String simname = project.getFlavors()[i].getTitle();
            s += "<li><a href=\"" + jnlpFilename + "\">Launch " + simname + "</a></li>";
            if ( i < project.getFlavorNames().length - 1 ) {
                s += "\n";
            }
        }
        return s;
    }

    private String getNewSummary() {
        String output = "<ul>\n";
        String changes = project.getChangesText();
        StringTokenizer st = new StringTokenizer( changes, "\n" );
        int poundCount = 0;
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            if ( token.trim().startsWith( "#" ) ) {
                poundCount++;
                if ( poundCount >= 2 ) {
                    break;
                }
            }
            if ( !token.trim().startsWith( "#" ) ) {
                output += "<li>" + token + "\n";
            }
        }
        return output + "</ul>";
    }

    public void deployDev( AuthenticationInfo devAuth ) {
        deploy( PhetServer.DEVELOPMENT, devAuth, new VersionIncrement.UpdateDev() );
    }

    public void deployProd( final AuthenticationInfo devAuth, final AuthenticationInfo prodAuth ) {
        deploy( new Task() {
            public boolean invoke() {
                sendSSH( PhetServer.DEVELOPMENT, devAuth );
                openBrowser( PhetServer.DEVELOPMENT.getURL( project ) );
                return true;
            }
        }, PhetServer.PRODUCTION, prodAuth, new VersionIncrement.UpdateProd(), new Task() {
            public boolean invoke() {
                System.out.println( "Invoking server side scripts to generate flavor and language JAR files" );
                generateFlavorAndLanguageJARFiles( PhetServer.PRODUCTION, prodAuth );
                return true;
            }
        } );
    }

    private void generateFlavorAndLanguageJARFiles( PhetServer server, AuthenticationInfo authenticationInfo ) {
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        try {
            sshConnection.connect();
            for ( int i = 0; i < project.getFlavorNames().length; i++ ) {
                String command = "cd /web/htdocs/phet/cl_utils/ ; /web/htdocs/phet/cl_utils/update-localized-jars.php " + project.getName() + " " + project.getFlavorNames()[i];
                System.out.println( "Running command: " + command );

                sshConnection.executeTask( new SshCommand( command ) );
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