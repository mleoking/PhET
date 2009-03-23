package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import com.jcraft.jsch.JSchException;

import edu.colorado.phet.buildtools.java.projects.BuildToolsProject;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.translate.ScpTo;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ProcessOutputReader;
import edu.colorado.phet.buildtools.flex.PhetFlexProject;

public class BuildScript {

    // debug flags that can be set via build-local.properties
    private final boolean debugDryRun;
    private final boolean debugSkipBuild;
    private boolean debugSkipStatus;
    private final boolean debugSkipCommit;

    private final PhetProject project;
    private final File trunk;
    private final BuildLocalProperties buildLocalProperties;
    private final ArrayList listeners;

    private String batchMessage;
    private RevisionStrategy revisionStrategy=new DynamicRevisionStrategy();

    //TODO: refactor to not be public static
    public static boolean generateJARs=true;//AND'ed with project setting

    public static void setGenerateJARs( boolean _generateJARs ) {
        generateJARs=_generateJARs;
    }

    public static interface RevisionStrategy{
        int getRevision();
    }
    public class DynamicRevisionStrategy implements RevisionStrategy{
        public int getRevision() {
            return getRevisionOnTrunkREADME();
        }
    }
    public static class ConstantRevisionStrategy implements RevisionStrategy{
       private int revision;

        public ConstantRevisionStrategy( int revision ) {
            this.revision = revision;
        }

        public int getRevision() {
            return revision;
        }
    }

    public void setRevisionStrategy( RevisionStrategy revisionStrategy ) {
        this.revisionStrategy = revisionStrategy;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {
        void deployFinished( BuildScript buildScript, PhetProject project, String codebase );

        void deployErrorOccurred( BuildScript buildScript, PhetProject project, String error );
    }

    public BuildScript( File trunk, PhetProject project ) {
        this.trunk = trunk;
        this.project = project;
        this.buildLocalProperties = BuildLocalProperties.getInstance();
        this.listeners = new ArrayList();

        debugDryRun = this.buildLocalProperties.getDebugDryRun();
        debugSkipBuild = this.buildLocalProperties.getDebugSkipBuild();
        debugSkipStatus = this.buildLocalProperties.getDebugSkipStatus();
        debugSkipCommit = this.buildLocalProperties.getDebugSkipCommit();
    }

    public void setDebugSkipStatus( boolean debugSkipStatus ) {
        this.debugSkipStatus = debugSkipStatus;
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

        if (debugSkipStatus){
            System.out.println( "Skipping SVN status" );
        }
        else if (!isSVNInSync() ) {
            notifyError( project, "SVN is out of sync; halting" );

            return;
        }

        versionIncrement.increment( project );
        int svnNumber = revisionStrategy.getRevision();
        System.out.println( "Current SVN: " + svnNumber );
        System.out.println( "Setting SVN Version" );
        setSVNVersion( svnNumber + 1 );
        setVersionTimestamp();
        System.out.println( "Adding message to change file" );
        addMessagesToChangeFile( svnNumber + 1 );

        if ( !debugDryRun && !debugSkipCommit ) {
            System.out.println( "Committing changes to version and change file." );
            commitProject();//commits both changes to version and change file
        }

        //TODO: check that new version number is correct

        //would be nice to build before deploying new SVN number in case there are errors,
        //however, we need the correct version info in the JAR
        if ( !debugSkipBuild ) {
            System.out.println( "Starting build..." );
            boolean success = build();
            if ( !success ) {
                notifyError( project,  "Stopping due to build failure, see console." );
                return;
            }
        }

        System.out.println( "Creating header." );
        createHeader( svnNumber );

        System.out.println( "Copying version files to deploy dir." );
        copyVersionFilesToDeployDir();
//        copyImageFilesToDeployDir();//TODO: enable image copying

        boolean ok = preDeployTask.invoke();
        if ( !ok ) {
            notifyError( project, "Pre deploy task failed");
            return;
        }

        project.buildLaunchFiles( server.getCodebase( project ), server.isDevelopmentServer() );

        if ( !debugDryRun ) {
            System.out.println( "Sending SSH." );
            sendSSH( server, authenticationInfo );
        }

        postDeployTask.invoke();

        System.out.println( "Opening Browser." );
        openBrowser( server.getCodebase( project ) );

        System.out.println( "Finished deploy to: " + server.getHost() );

        server.deployFinished();

        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.deployFinished( this, project, server.getCodebase( project ) );
        }
    }

    private void copyImageFilesToDeployDir() {
        try {
            new ScreenshotProcessor().copyScreenshotsToDeployDir(project);
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void notifyError( PhetProject project, String error ) {
        System.out.println( "error: "+error );
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.deployErrorOccurred( this, project, error );
        }
    }

    //This message disables the dialog for change log messages, using the batch message instead
    public void setBatchMessage( String batchMessage ) {
        this.batchMessage = batchMessage;
    }

    private void addMessagesToChangeFile( int svn ) {
        String message = batchMessage;
        if ( message == null ) {
            message = JOptionPane.showInputDialog( "Enter a message to add to the change log\n(or Cancel or Enter a blank line if change log is up to date)" );
        }
        if ( message != null && message.trim().length() > 0 ) {
            prependChange( getChangeLogEntryDateStamp() + " " + message );
        }

        prependChange( "# " + getFullVersionStr( svn ) );
    }

    private String getChangeLogEntryDateStamp() {
        SimpleDateFormat format = new SimpleDateFormat( "M/d/yy" ); // eg, 3/5/09
        return format.format( new Date() );
    }

    private String getFullVersionStr( int svn ) {
        return project.getFullVersionString();
    }

    private void prependChange( String message ) {
        project.prependChangesText( message );
    }

    private void copyVersionFilesToDeployDir() {
        File versionFile = project.getVersionFile();
        try {
            File dest = new File( project.getDeployDir(), versionFile.getName() );
            FileUtils.copyTo( versionFile, dest );
            System.out.println( "Copied version file to " + dest.getAbsolutePath() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void openBrowser( String deployPath ) {
        String browser = buildLocalProperties.getBrowser();
        if ( browser != null ) {
            try {
                Runtime.getRuntime().exec( new String[]{browser, deployPath} );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void sendSSH( PhetServer server, AuthenticationInfo authenticationInfo ) {
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        String remotePathDir = server.getServerDeployPath( project );
        try {
            sshConnection.connect();

            // -m sets the permissions of the created directory
            //TODO: how can we detect failure of this command, e.g. due to permissions errors?  See #1164
//            sshConnection.executeTask( new SshCommand( "mkdir " + getParentDir( getParentDir( remotePathDir ) ) ) );//TODO: would it be worthwhile to skip this task when possible?
            sshConnection.executeTask( new SshCommand( "mkdir -m 775 " + getParentDir( remotePathDir ) ) );//TODO: would it be worthwhile to skip this task when possible?
            sshConnection.executeTask( new SshCommand( "mkdir -m 775 " + remotePathDir ) );//TODO: would it be worthwhile to skip this task when possible?
        }
        catch( SshException e ) {
            if ( e.toString().toLowerCase().indexOf( "auth fail" ) != -1 ) {
                // TODO: check if authentication fails, don't try logging in again
                // on tigercat, 3 (9?) unsuccessful login attepts will lock you out
                System.out.println( "Authentication on '" + server.getHost() + "' has failed, is your username and password correct?  Exiting..." );
                System.exit( 0 );
            }
            e.printStackTrace();
        }
        finally {
            sshConnection.disconnect();
        }

        //for some reason, the securechannelfacade fails with a "server didn't expect this file" error
        //the failure is on tigercat, but scf works properly on spot
        //but our code works on both; therefore there is probably a problem with the handshaking in securechannelfacade
        File[] f = project.getDeployDir().listFiles(); //TODO: should handle recursive for future use (if we ever want to support nested directories)
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
                    e.printStackTrace();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
//                    sshConnection.executeTask( new ScpUpload( new ScpFile( f[i],  ) ) );
            }
        }
    }

    private String getParentDir( String remotePathDir ) {
        if ( remotePathDir.endsWith( "/" ) ) {
            remotePathDir = remotePathDir.substring( 0, remotePathDir.length() - 1 );
        }
        int x = remotePathDir.lastIndexOf( '/' );
        return remotePathDir.substring( 0, x );
    }

    private void setSVNVersion( int svnVersion ) {
        project.setSVNVersion( svnVersion );
    }

    private void setVersionTimestamp() {
        project.setVersionTimestamp( System.currentTimeMillis() / 1000 ); // convert from ms to sec
    }

    public int getRevisionOnTrunkREADME() {
        File readmeFile = new File( trunk, "README.txt" );
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
        AuthenticationInfo auth = buildLocalProperties.getRespositoryAuthenticationInfo();
        String message = project.getName() + ": deployed version " + project.getFullVersionString();
        String path = project.getProjectDir().getAbsolutePath();
        String[] args = new String[]{"svn", "commit", "--username", auth.getUsername(), "--password", auth.getPassword(), "--message", message, path};
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

    public boolean build() {
        try {
            boolean success = project.build();
            System.out.println( "**** Finished BuildScript.build" );
            return success;
        }
        catch( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    public void runSim() {
        Locale locale = (Locale) prompt( "Choose locale: ", project.getLocales() );
        String simulationName = project.getSimulationNames()[0];
        if ( project.getSimulationNames().length > 1 ) {
            simulationName = (String) prompt( "Choose simulation: ", project.getSimulationNames() );
        }

        project.runSim( locale, simulationName );
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
            FileUtils.filter( new File( trunk, "build-tools/templates/header-template.html" ), project.getDeployHeaderFile(), createHeaderFilterMap( svn ), "UTF-8" );
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
        map.put( "new-summary", getNewSummary() );
        return map;
    }

    private String getSimListHTML( PhetProject project ) {
        //<li><a href="@jnlp-filename@">Launch @sim-name@</a></li>
        String s = "";
        for ( int i = 0; i < project.getSimulationNames().length; i++ ) {
            String launchFile = project.getSimulationNames()[i] + "_en." + project.getLaunchFileSuffix();
            if (project instanceof PhetFlexProject){//TODO: factor into PhetProject hierarchy
                launchFile = project.getSimulationNames()[i] + "." + project.getLaunchFileSuffix();
            }
            String simname = project.getSimulations()[i].getTitle();
            s += "<li><a href=\"" + launchFile + "\">Launch " + simname + "</a></li>";
            if ( i < project.getSimulationNames().length - 1 ) {
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

    public void deployDev( final AuthenticationInfo devAuth, final boolean generateOfflineJARs ) {
        deploy( new NullTask(), PhetServer.DEVELOPMENT, devAuth, new VersionIncrement.UpdateDev(), new Task() {
            public boolean invoke() {
                if ( generateOfflineJARs ) {
                    generateOfflineJars( project, PhetServer.DEVELOPMENT, devAuth );
                }
                return true;
            }
        } );
    }

    public void deployProd( final AuthenticationInfo devAuth, final AuthenticationInfo prodAuth ) {
        deploy(
                //send a copy to dev
                new Task() {
                    public boolean invoke() {
                        //generate JNLP files for dev
                        project.buildLaunchFiles( PhetServer.DEVELOPMENT.getCodebase( project ), PhetServer.DEVELOPMENT.isDevelopmentServer() );
//                        String codebase = PhetServer.DEVELOPMENT.getURL( project );
//                        buildJNLP( codebase, PhetServer.DEVELOPMENT.isDevelopmentServer() );

                        if ( !debugDryRun ) {
                            sendSSH( PhetServer.DEVELOPMENT, devAuth );
                            generateOfflineJars( project, PhetServer.DEVELOPMENT, devAuth );
                        }
                        openBrowser( PhetServer.DEVELOPMENT.getCodebase( project ) );
                        return true;
                    }
                }, PhetServer.PRODUCTION, prodAuth, new VersionIncrement.UpdateProd(), new Task() {
                    public boolean invoke() {
                        System.out.println( "Invoking server side scripts to generate simulation and language JAR files" );
                        if ( !debugDryRun ) {
                            generateOfflineJars( project, PhetServer.PRODUCTION, prodAuth );
                        }
                        return true;
                    }
                } );
    }

    public static void generateOfflineJars( PhetProject project, PhetServer server, AuthenticationInfo authenticationInfo ) {

        //only sign jars for Java Projects, and only if it is enabled (e.g. for simulations)
        boolean projectWantsJARs = project instanceof JavaProject && ( (JavaProject) project ).getSignJar();
        if ( projectWantsJARs && generateJARs ) {
            SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
            try {
                sshConnection.connect();

                BuildToolsProject buildToolsProject = new BuildToolsProject( new File( project.getTrunk(), "build-tools" ) );
                String buildScriptDir = server.getServerDeployPath( buildToolsProject );
                String projectDir = server.getServerDeployPath( project );

                String javaCmd = server.getJavaCommand();
                String jarCmd = server.getJarCommand();
                String jarName = buildToolsProject.getDefaultDeployJar().getName();
                String pathToBuildLocalProperties = server.getBuildLocalPropertiesFile();
                String command = javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " + JARGenerator.class.getName() + " " + projectDir + "/" + project.getDefaultDeployJar().getName() + " " + jarCmd + " " + pathToBuildLocalProperties;

                System.out.println( "Running command: \n" + command );
                sshConnection.executeTask( new SshCommand( command ) );
            }
            catch( SshException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            finally {
                sshConnection.disconnect();
            }
        }
    }
}