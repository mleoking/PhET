package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.buildtools.flex.FlexSimulationProject;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.BuildToolsProject;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ProcessOutputReader;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;
import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;

import com.jcraft.jsch.JSchException;

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
    private RevisionStrategy revisionStrategy = new DynamicRevisionStrategy();

    //TODO: refactor to not be public static
    public static boolean generateJARs = true;//AND'ed with project setting

    public static void setGenerateJARs( boolean _generateJARs ) {
        generateJARs = _generateJARs;
    }

    public static interface RevisionStrategy {

        int getRevision();

    }

    public class DynamicRevisionStrategy implements RevisionStrategy {

        public int getRevision() {
            return getRevisionOnTrunkREADME();
        }

    }

    public static class ConstantRevisionStrategy implements RevisionStrategy {

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
        if ( !BuildLocalProperties.getInstance().isJarsignerCredentialsSpecified() ) {
            throw new RuntimeException( "Jarsigner credentials must be specified for a deploy." );
        }
        clean();

        //Update any project files before SVN status update check, to make sure everything's in sync
        //Currently only used for synchronizing the software agreement with flash
        project.updateProjectFiles();
        if ( debugSkipStatus ) {
            System.out.println( "Skipping SVN status" );
        }
        else if ( !isSVNInSync() ) {
            notifyError( project, "SVN is out of sync; halting" );

            return;
        }

        versionIncrement.increment( project );
        int svnNumber = revisionStrategy.getRevision();
        System.out.println( "Current SVN: " + svnNumber );
        System.out.println( "Setting SVN Version" );
        int newRevision = svnNumber + 1;
        setSVNVersion( newRevision );
        setVersionTimestamp();
        System.out.println( "Adding message to change file" );
        addMessagesToChangeFile();

        if ( !debugDryRun && !debugSkipCommit ) {
            System.out.println( "Committing changes to version and change file." );
            boolean success = commitProject();//commits both changes to version and change file
            if ( !success ) {
                notifyError( project, "Commit of version and change file failed, stopping (see console)" );
                // TODO: possibly roll back .properties file and changes
                return;
            }
            success = verifyRevision( newRevision, new String[]{project.getProjectPropertiesFile().getAbsolutePath()} );
            if ( !success ) {
                notifyError( project, "Commit of version and change file did not result with the expected revision number. Stopping (see console)" );
                return;
            }
        }

        //would be nice to build before deploying new SVN number in case there are errors,
        //however, we need the correct version info in the JAR
        if ( !debugSkipBuild ) {
            System.out.println( "Starting build..." );
            boolean success = build();
            if ( !success ) {
                notifyError( project, "Stopping due to build failure, see console." );
                return;
            }
        }

        System.out.println( "Creating header." );
        createHeader( server.isDevelopmentServer() );

        try {
            project.copyChangesFileToDeployDir();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        System.out.println( "Copying version files to deploy dir." );
        copyVersionFilesToDeployDir();
        copyImageFilesToDeployDir();

        boolean ok = preDeployTask.invoke();
        if ( !ok ) {
            notifyError( project, "Pre deploy task failed" );
            return;
        }

        project.buildLaunchFiles( server.getCodebase( project ), server.isDevelopmentServer() );

        if ( !debugDryRun ) {
            System.out.println( "Sending SSH." );
            ok = sendSSH( server, authenticationInfo );
            if ( !ok ) {
                notifyError( project, "SCP Failure" );
                return;
            }
        }

        postDeployTask.invoke();

        System.out.println( "Opening Browser." );
        PhetWebsite.openBrowser( server.getCodebase( project ) );

        System.out.println( "Finished deploy to: " + server.getHost() );

        server.deployFinished();

        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.deployFinished( this, project, server.getCodebase( project ) );
        }
    }

    private void copyImageFilesToDeployDir() {
        try {
            new ScreenshotProcessor().copyScreenshotsToDeployDir( project );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void notifyError( PhetProject project, String error ) {
        System.out.println( "error: " + error );
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.deployErrorOccurred( this, project, error );
        }
    }

    //This message disables the dialog for change log messages, using the batch message instead

    public void setBatchMessage( String batchMessage ) {
        this.batchMessage = batchMessage;
    }

    private void addMessagesToChangeFile() {
        String message = batchMessage;
        if ( message == null && buildLocalProperties.isPromptUserForChangeMessage() ) {
            message = JOptionPane.showInputDialog( "Enter a message to add to the change log\n(or Cancel or Enter a blank line if change log is up to date)" );
        }
        if ( message != null && message.trim().length() > 0 ) {
            prependChange( getChangeLogEntryDateStamp() + " " + message );
        }

        prependChange( "# " + getFullVersionStr() );
    }

    private String getChangeLogEntryDateStamp() {
        SimpleDateFormat format = new SimpleDateFormat( "M/d/yy" ); // eg, 3/5/09
        return format.format( new Date() );
    }

    private String getFullVersionStr() {
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

    private boolean sendSSH( PhetServer server, AuthenticationInfo authenticationInfo ) {
        String remotePathDir = server.getServerDeployPath( project );
        // if the directory does not exist on the server, it will be created.
        boolean success = SshUtils.executeCommand( "mkdir -p -m 775 " + remotePathDir, server.getHost(), authenticationInfo );
        if ( !success ) {
            System.out.println( "Warning: failed to create or verify the existence of the deploy directory" );
            return false;
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
                    return false;
                }
                catch( IOException e ) {
                    e.printStackTrace();
                    return false;
                }
//                    sshConnection.executeTask( new ScpUpload( new ScpFile( f[i],  ) ) );
            }
        }
        return true;
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
        AuthenticationInfo auth = buildLocalProperties.getRespositoryAuthenticationInfo();
        File readmeFile = new File( trunk, "README.txt" );
        if ( !readmeFile.exists() ) {
            throw new RuntimeException( "Readme file doesn't exist, need to get version info some other way" );
        }
        String[] args = new String[]{"svn", "status", "-u", "--non-interactive", "--username", auth.getUsername(), "--password", auth.getPassword(), readmeFile.getAbsolutePath()};
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
        throw new RuntimeException( "No svn version information found: " + output.getOut() );
    }

    private boolean commitProject() {
        AuthenticationInfo auth = buildLocalProperties.getRespositoryAuthenticationInfo();
        String message = project.getName() + ": deployed version " + project.getFullVersionString();
        String path = project.getProjectDir().getAbsolutePath();
        String[] args = new String[]{"svn", "commit", "--non-interactive", "--username", auth.getUsername(), "--password", auth.getPassword(), "--message", message, path};
        //TODO: verify that SVN repository revision number now matches what we wrote to the project properties file
        ProcessOutputReader.ProcessExecResult a = ProcessOutputReader.exec( args );
        if ( a.getTerminatedNormally() ) {
            System.out.println( "Finished committing new version file with message: " + message + " output/err=" );
            System.out.println( a.getOut() );
            System.out.println( a.getErr() );
            System.out.println( "Finished committing new version file with message: " + message );
            return true;
        }
        else {
            System.out.println( "Abnormal termination: " + a );
            return false;
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

    public void createHeader( boolean dev ) {
        try {
            FileUtils.filter( new File( trunk, "build-tools/templates/header-template.html" ), project.getDeployHeaderFile(), createHeaderFilterMap( dev ), "UTF-8" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private HashMap createHeaderFilterMap( boolean dev ) {
        HashMap map = new HashMap();
        map.put( "project-name", project.getName() );
        map.put( "version", getFullVersionStr() );
        map.put( "sim-list", getSimListHTML( project, dev ) );
        map.put( "new-summary", getNewSummary() );
        return map;
    }

    private String getSimListHTML( PhetProject project, boolean dev ) {

        String s = "";
        for ( int i = 0; i < project.getSimulationNames().length; i++ ) {
            String title = project.getSimulations()[i].getTitle();
            String launchFile = project.getSimulationNames()[i] + "_en." + project.getLaunchFileSuffix();
            String prodLaunchFile = project.getSimulationNames()[i] + "_en-production." + project.getLaunchFileSuffix();
            if ( project instanceof FlexSimulationProject ) { //TODO: factor into PhetProject hierarchy
                launchFile = project.getSimulationNames()[i] + "." + project.getLaunchFileSuffix();
                prodLaunchFile = launchFile; //TODO: why is this different for Flex, and is this addition for #2142 correct?
            }

            /* 
            * See #2142, dev servers can launch with and without developer features, so create links for both.
            * On developer servers, the -dev flag is added to the standard JNLP files, and an additional
            * "production" JNLP file is created without the -dev flag.
            */
            if ( dev ) {
                // <li>@title@ : <a href="@prodLaunchFile@">production</a> : <a href="@devLaunchFile@">dev</a></li>
                s += "<li>";
                s += title;
                s += " : <a href=\"" + prodLaunchFile + "\">production</a>";
                s += " : <a href=\"" + launchFile + "\">dev</a>";
                s += "</li>";
            }
            else {
                // <li><a href="@prodLaunchFile@">@title</a></li>
                s += "<li><a href=\"" + launchFile + "\">" + title + "</a></li>";
            }
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

    public void deployToDevelopmentAndProductionServers( final AuthenticationInfo devAuth, final AuthenticationInfo prodAuth, VersionIncrement versionIncrement ) {
        deploy(
                //send a copy to dev
                new Task() {
                    public boolean invoke() {
                        //generate files for dev
                        sendCopyToDev( devAuth );
                        boolean success = prepareStagingArea( PhetServer.FIGARO, prodAuth );
                        return success;
                    }
                }, PhetServer.FIGARO, prodAuth, versionIncrement, new Task() {
                    public boolean invoke() {
                        System.out.println( "Executing website post-upload deployment process" );

                        boolean genjars = project instanceof JavaProject && ( (JavaProject) project ).getSignJar() && generateJARs;
                        SshUtils.executeCommand( "chmod -R a+rw " + PhetServer.FIGARO.getStagingArea() + "/" + project.getName(), PhetServer.FIGARO.getHost(), prodAuth );
                        SshUtils.executeCommand( "curl 'http://phetsims.colorado.edu/admin/deploy?project=" + project.getName() + "&generate-jars=" + genjars + "'", PhetServer.FIGARO.getHost(), prodAuth );

                        return true;
                    }
                } );
    }

    public void deployProd( final AuthenticationInfo devAuth, final AuthenticationInfo prodAuth ) {
        deployToDevelopmentAndProductionServers( devAuth, prodAuth, new VersionIncrement.UpdateProdMinor() );
    }

    //Run "rm" on the server to remove the phet/staging/sims/<project> directory contents, see #1529

    private boolean prepareStagingArea( PhetServer server, AuthenticationInfo authenticationInfo ) {
        assert project.getName().length() >= 2;//reduce probability of a scary rm
        return SshUtils.executeCommands( new String[]{
                "mkdir -p -m 775 " + server.getStagingArea() + "/" + project.getName(),
                "rm -f " + server.getStagingArea() + "/" + project.getName() + "/*"
        }, server.getHost(), authenticationInfo );
    }

    /*
    4. Run a server-side script that will do the following, see #1529
        > a. mv phet/sims/<project> phet/backup/sims/<project>-<timestamp>
        > b. mv phet/staging/sims/<project> phet/sims/<project>
     */

    private void copyFromStagingAreaToSimDir( PhetServer server, AuthenticationInfo authenticationInfo ) {
        SshUtils.executeCommand( BuildToolsPaths.TIGERCAT_STAGING_SWAP_SCRIPT + " " + project.getName(), server.getHost(), authenticationInfo );
    }

    private void sendCopyToDev( AuthenticationInfo devAuth ) {
        createHeader( true );
        project.buildLaunchFiles( PhetServer.DEVELOPMENT.getCodebase( project ), PhetServer.DEVELOPMENT.isDevelopmentServer() );
        if ( !debugDryRun ) {
            sendSSH( PhetServer.DEVELOPMENT, devAuth );
            generateOfflineJars( project, PhetServer.DEVELOPMENT, devAuth );
        }
        PhetWebsite.openBrowser( PhetServer.DEVELOPMENT.getCodebase( project ) );
        //TODO #2143, delete <project>_en.production.jnlp files, since they shouldn't go to tigercat
    }

    public static void generateOfflineJars( PhetProject project, PhetServer server, AuthenticationInfo authenticationInfo ) {
        // TODO: return executeCommand's success if we run it, and modify the calling locations

        //only sign jars for Java Projects, and only if it is enabled (e.g. for simulations)
        boolean projectWantsJARs = project instanceof JavaProject && ( (JavaProject) project ).getSignJar();
        if ( projectWantsJARs && generateJARs ) {
            try {
                SshUtils.executeCommand( getJARGenerationCommand( (JavaProject) project, server ), server.getHost(), authenticationInfo );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private static String getJARGenerationCommand( JavaProject project, PhetServer server ) throws IOException {
        BuildToolsProject buildToolsProject = new BuildToolsProject( new File( project.getTrunk(), BuildToolsPaths.BUILD_TOOLS_DIR ) );
        String buildScriptDir = server.getServerDeployPath( buildToolsProject );
        String projectDir = server.getServerDeployPath( project );

        String javaCmd = server.getJavaCommand();
        String jarCmd = server.getJarCommand();
        String jarName = buildToolsProject.getDefaultDeployJar().getName();
        String pathToBuildLocalProperties = server.getBuildLocalPropertiesFile();
        String command = javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " + JARGenerator.class.getName() + " " + projectDir + "/" + project.getDefaultDeployJar().getName() + " " + jarCmd + " " + pathToBuildLocalProperties;
        return command;
    }

    //regenerate server HTML caches so new material will appear

    public static void clearWebCaches() {
        System.out.println( "Clearing website cache" );
        try {
            FileUtils.download( PhetServer.PRODUCTION.getCacheClearUrl(), new File( new File( System.getProperty( "java.io.tmpdir" ) ), PhetServer.PRODUCTION.getCacheClearFile() ) );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Returns whether or not each path is up to the particular revision
     *
     * @param revision The revision we need the entries to be
     * @param paths    Various paths to check together
     * @return Whether or not all of the path's revisions were the same as the specified revision
     */
    public static boolean verifyRevision( int revision, String[] paths ) {
        String out = null;
        try {
            AuthenticationInfo auth = BuildLocalProperties.getInstance().getRespositoryAuthenticationInfo();
            List<String> args = new LinkedList<String>();
            args.add( "svn" );
            args.add( "info" );
            args.add( "--xml" ); // so we can easily parse the XML
            args.add( "--non-interactive" ); // so it doesn't pause for input
            args.add( "--username" );
            args.add( auth.getUsername() );
            args.add( "--password" );
            args.add( auth.getPassword() );
            for ( String path : paths ) {
                args.add( path );
            }
            ProcessOutputReader.ProcessExecResult result = ProcessOutputReader.exec( args.toArray( new String[0] ) );

            out = result.getOut();
            Document document = XMLUtils.toDocument( out );
            NodeList entries = document.getElementsByTagName( "entry" );

            for ( int i = 0; i < entries.getLength(); i++ ) {
                Node entryNode = entries.item( i );
                Element entryElement = (Element) entryNode;
                String revisionString = entryElement.getAttribute( "revision" );
                if ( Integer.parseInt( revisionString ) == revision ) {
                    System.out.println( entryElement.getAttribute( "path" ) + " has the correct revision " + revisionString );
                }
                else {
                    System.out.println( "Warning: " + entryElement.getAttribute( "path" ) + " has the incorrect revision "
                                        + revisionString + ", it should be " + revision );
                    return false;
                }
            }
        }
        catch( TransformerException e ) {
            e.printStackTrace();
            System.out.println( "Caused by the XML:\n" + out );
            return false;
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
            System.out.println( "Caused by the XML:\n" + out );
            return false;
        }
        return true;
    }

}