// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import edu.colorado.phet.buildtools.RevisionStrategy.DynamicRevisionStrategy;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.html5.HTML5Project;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.BuildToolsProject;
import edu.colorado.phet.buildtools.preprocessor.ResourceGenerator;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;
import edu.colorado.phet.buildtools.util.SvnUtils;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

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
    private final ArrayList<Listener> listeners;
    private String batchMessage;
    private RevisionStrategy revisionStrategy;
    //TODO: refactor to not be public static
    public static boolean generateJARs = true;//AND'ed with project setting
    //For testing with git deployments, see #1496
    private boolean gitEnabled = false;

    public BuildScript( File trunk, PhetProject project ) {
        this.trunk = trunk;
        this.project = project;
        this.buildLocalProperties = BuildLocalProperties.getInstance();
        this.listeners = new ArrayList<Listener>();

        debugDryRun = this.buildLocalProperties.getDebugDryRun();
        debugSkipBuild = this.buildLocalProperties.getDebugSkipBuild();
        debugSkipStatus = this.buildLocalProperties.getDebugSkipStatus();
        debugSkipCommit = this.buildLocalProperties.getDebugSkipCommit();

        revisionStrategy = new DynamicRevisionStrategy( trunk );
    }

    public static void setGenerateJARs( boolean _generateJARs ) {
        generateJARs = _generateJARs;
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

    public void setDebugSkipStatus( boolean debugSkipStatus ) {
        this.debugSkipStatus = debugSkipStatus;
    }

    public void clean() {
        try {
            new PhetCleanCommand( project, new MyAntTaskRunner() ).execute();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    static interface Task {
        boolean invoke();
    }

    public void deploy( Task preDeployTask, OldPhetServer server,
                        AuthenticationInfo authenticationInfo, VersionIncrement versionIncrement, Task postDeployTask ) {
        boolean success = prepareForDeployment( versionIncrement, server.isDevelopmentServer() );
        if ( !success ) {
            return;
        }

        success = preDeployTask.invoke();
        if ( !success ) {
            notifyError( project, "Pre deploy task failed" );
            return;
        }

        String codeBase = server.getCodebase( project );
        deployPreparedToServer( server, authenticationInfo );

        postDeployTask.invoke();

        System.out.println( "Opening Browser." );
        PhetWebsite.openBrowser( codeBase );

        System.out.println( "Finished deploy to: " + server.getHost() );

        for ( Listener listener : listeners ) {
            listener.deployFinished( this, project, codeBase );
        }
    }

    public void deployToProductionAndDevelopment( final PhetWebsite productionWebsite,
                                                  final OldPhetServer devServer,
                                                  final AuthenticationInfo devAuth,
                                                  final boolean generateOfflineJARs,
                                                  VersionIncrement versionIncrement ) {
        boolean success = prepareForDeployment( versionIncrement, false );
        if ( !success ) {
            return;
        }

        // deploy a copy to dev
        String devCodebase = devServer.getCodebase( project );
        deployPreparedToServer( devServer, devAuth );
        if ( generateOfflineJARs ) {
            generateOfflineJars( project, devServer, devAuth );
        }
        System.out.println( "Opening Browser to " + devCodebase );
        PhetWebsite.openBrowser( devCodebase );

        // deploy a copy to production
        String codebase = productionWebsite.getProjectBaseUrl( project );
        deployPreparedToServer( productionWebsite, false );
        if ( generateOfflineJARs ) {
            generateOfflineJars( project, productionWebsite.getOldProductionServer(), productionWebsite.getServerAuthenticationInfo( buildLocalProperties ) );
        }
        System.out.println( "Opening Browser to " + codebase );
        PhetWebsite.openBrowser( codebase );

        System.out.println( "Finished deploy to: " + productionWebsite.getServerHost() );

        for ( Listener listener : listeners ) {
            listener.deployFinished( this, project, codebase );
        }
    }

    private boolean deployPreparedToServer( OldPhetServer server, AuthenticationInfo auth ) {
        boolean success = true;
        String codeBase = server.getCodebase( project );
        project.buildLaunchFiles( codeBase, server.isDevelopmentServer() );
        if ( !debugDryRun ) {
            System.out.println( "Sending SSH." );
            success = sendSSH( server, auth );
            if ( !success ) {
                notifyError( project, "SCP Failure" );
            }
        }
        return success;
    }

    private boolean deployPreparedToServer( PhetWebsite website, Boolean dev ) {
        boolean success = true;
        String codeBase = website.getProjectBaseUrl( project );
        project.buildLaunchFiles( codeBase, dev );
        if ( !debugDryRun ) {
            System.out.println( "Sending SSH." );
            success = uploadProject(
                    website.getServerAuthenticationInfo( buildLocalProperties ),
                    website.getProjectBasePath( project ),
                    website.getServerHost() );
            if ( !success ) {
                notifyError( project, "SCP Failure" );
            }
        }
        return success;
    }

    /**
     * Build everything that is not specific to where it is being deployed, and execute checks to make sure there is
     * nothing that would prevent a deployment.
     *
     * @param versionIncrement How to increment the version number
     * @param dev              Whether this is mainly a production or development deployment. Will generate different header
     * @return Success
     */
    private boolean prepareForDeployment( VersionIncrement versionIncrement, Boolean dev ) {
        if ( !BuildLocalProperties.getInstance().isJarsignerCredentialsSpecified() ) {
            throw new RuntimeException( "Jarsigner credentials must be specified for a deploy." );
        }
        clean();

        //If the project specifies that it should use ResourceGenerator to make its resource loading files, generate them now.
        //If this created any changes, the next step will flag an svn out of date error so that the changes can be committed before deploy.
        //Similarly, if the user has manually modified the resource file and committed that, this will revert it to the auto-generated form and flag as an svn discrepancy
        if ( project.getBuildPropertiesFileObject().getGenerateResourceFile() ) {
            try {
                boolean changed = new ResourceGenerator( trunk ).generateResources( project.getProjectDir() );
                if ( changed ) {
                    notifyError( project, "Automatically generated resource file changed.  Please review the changed and revert or commit before proceeding." );
                }
            }
            catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
        else {
            System.out.println( "Skipping automatic resource generation" );
        }

        //Update any project files before SVN status update check, to make sure everything's in sync
        //Currently only used for synchronizing the software agreement with flash
        project.updateProjectFiles();
        if ( debugSkipStatus ) {
            System.out.println( "Skipping SVN status" );
        }
        else if ( !SvnUtils.isProjectUpToDate( project ) ) {
            notifyError( project, "SVN is out of sync; halting" );
            return false;
        }

        versionIncrement.increment( project );
        int svnNumber = revisionStrategy.getRevision();
        System.out.println( "Current SVN: " + svnNumber );
        System.out.println( "Setting SVN Version" );
        int newRevision = svnNumber + 1;
        project.setSVNVersion( newRevision );
        project.setVersionTimestamp( System.currentTimeMillis() / 1000 ); // convert from ms to sec
        System.out.println( "Adding message to change file" );
        addMessagesToChangeFile();

        if ( !debugDryRun && !debugSkipCommit ) {
            System.out.println( "Committing changes to version and change file." );
            boolean success = SvnUtils.commitProject( project, buildLocalProperties.getRespositoryAuthenticationInfo() );//commits both changes to version and change file
            if ( !success ) {
                notifyError( project, "Commit of version and change file failed, stopping (see console)" );
                // TODO: possibly roll back .properties file and changes
                return false;
            }
            success = SvnUtils.verifyRevision( newRevision, new String[] { project.getProjectPropertiesFile().getAbsolutePath() } );
            if ( !success ) {
                notifyError( project, "Commit of version and change file did not result with the expected revision number. Stopping (see console)" );
                return false;
            }
        }

        //would be nice to build before deploying new SVN number in case there are errors,
        //however, we need the correct version info in the JAR
        if ( !debugSkipBuild ) {
            System.out.println( "Starting build..." );
            boolean success = build();
            if ( !success ) {
                notifyError( project, "Stopping due to build failure, see console." );
                return false;
            }
        }

        System.out.println( "Creating header." );
        createHeader( dev );

        try {
            project.copyAssets();
        }
        catch ( IOException e ) {
            throw new RuntimeException( "Problem copying files to the deployment directory", e );
        }

        // TODO: need better checking before this point to make sure everything has succeeded
        return true;
    }

    private void notifyError( PhetProject project, String error ) {
        System.out.println( "error: " + error );
        for ( Listener listener : listeners ) {
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

        prependChange( "# " + project.getFullVersionString() );
    }

    private String getChangeLogEntryDateStamp() {
        SimpleDateFormat format = new SimpleDateFormat( "M/d/yy" ); // eg, 3/5/09
        return format.format( new Date() );
    }

    private void prependChange( String message ) {
        project.prependChangesText( message );
    }

    private boolean sendSSH( OldPhetServer server, AuthenticationInfo authenticationInfo ) {
        String remotePathDir = server.getServerDeployPath( project );
        String host = server.getHost();
        return uploadProject( authenticationInfo, remotePathDir, host );
    }

    private boolean uploadProject( AuthenticationInfo authenticationInfo, String remotePathDir, String host ) {

        //Ignore "node_modules" for grunt build directories upload
        File[] f = project.getDeployDir().listFiles(new FilenameFilter() {
            public boolean accept( final File dir, final String name ) {
                return !name.equals( "node_modules" );
            }
        } ); //TODO: should handle recursive for future use (if we ever want to support nested directories)
        return uploadFiles( authenticationInfo, remotePathDir, host, f );
    }

    private boolean uploadFiles( final AuthenticationInfo authenticationInfo, final String remotePathDir, final String host, final File[] f ) {// if the directory does not exist on the server, it will be created.
        boolean success = SshUtils.executeCommand( "mkdir -p -m 775 " + remotePathDir, host, authenticationInfo );
        if ( !success ) {
            System.out.println( "Warning: failed to create or verify the existence of the deploy directory" );
            return false;
        }

        //for some reason, the securechannelfacade fails with a "server didn't expect this file" error
        //the failure is on tigercat, but scf works properly on spot
        //but our code works on both; therefore there is probably a problem with the handshaking in securechannelfacade
        for ( File fileToUpload : f ) {
            if ( fileToUpload.getName().startsWith( "." ) ) {
                //ignore
            }
            else if ( fileToUpload.isDirectory() ) {
                uploadFiles( authenticationInfo, remotePathDir + "/" + fileToUpload.getName(), host, fileToUpload.listFiles() );
            }
            else {
                //server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword()
                try {
                    ScpTo.uploadFile( fileToUpload, authenticationInfo.getUsername(), host, remotePathDir + "/" + fileToUpload.getName(), authenticationInfo.getPassword() );
                }
                catch ( JSchException e ) {
                    e.printStackTrace();
                    return false;
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                    return false;
                }
//                    sshConnection.executeTask( new ScpUpload( new ScpFile( f[i],  ) ) );
            }
        }
        return true;
    }

    public boolean build() {
        try {

            if ( gitEnabled ) {
                System.out.println( "Switching git dependencies before build" );
                project.switchGitDependencies();
            }

            boolean success = project.build();
            System.out.println( "**** Finished BuildScript.build" );
            return success;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    public void createHeader( boolean dev ) {
        try {
            FileUtils.filter( new File( trunk, "build-tools/templates/header-template.html" ), project.getDeployHeaderFile(), createHeaderFilterMap( dev ), "UTF-8" );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> createHeaderFilterMap( boolean dev ) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put( "project-name", project.getName() );
        map.put( "version", project.getFullVersionString() );
        map.put( "sim-list", getSimListHTML( project, dev ) );
        map.put( "new-summary", getNewSummary() );
        return map;
    }

    private String getSimListHTML( PhetProject project, boolean dev ) {

        String s = "";
        for ( int i = 0; i < project.getSimulationNames().length; i++ ) {
            String title = project.getSimulations()[i].getTitle();
            String mainLaunchFile = project.getSimulationNames()[i] + "_en." + project.getLaunchFileSuffix();
            String devLaunchFile = project.getSimulationNames()[i] + "_en-dev." + project.getLaunchFileSuffix();
            String interviewsLaunchFile = project.getSimulationNames()[i] + "_en-interviews." + project.getLaunchFileSuffix();

            s += "<li>";
            if ( dev ) {
                s += title;
                if ( project instanceof FlashSimulationProject ) {
                    s += " : <a href=\"" + mainLaunchFile + "\">dev</a>";
                }
                else if ( project instanceof HTML5Project ) {
                    //Omits _en suffix for now because no i18n support and main html file doesn't have "_en" in it.//TODO: i18n suffixes will need to be added
                    s += " : <a href=\"" + project.getSimulationNames()[i] + "." + project.getLaunchFileSuffix() + "\">dev</a>";
                }
                else {
                    /*
                    * See #2142, dev servers can launch with and without developer features, so create links for both.
                    * On development servers, we create an additional "dev" jnlp flag with a "-dev" argument.
                    */
                    s += " : <a href=\"" + mainLaunchFile + "\">production</a>";
                    s += " : <a href=\"" + devLaunchFile + "\">dev</a>";
                    s += " : <a href=\"" + interviewsLaunchFile + "\">interviews</a>";
                }
            }
            else {
                s += "<a href=\"" + mainLaunchFile + "\">" + title + "</a>";
            }
            s += "</li>";

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

    public void deployToDevelopment( final AuthenticationInfo devAuth, final boolean generateOfflineJARs ) {
        deploy( new Task() {
                    public boolean invoke() {
                        //generate files for dev
                        //sendCopyToDev( PhetWebsite.FIGARO.getServerAuthenticationInfo( buildLocalProperties ), OldPhetServer.FIGARO_DEV );
                        return true;
                    }
                }, OldPhetServer.DEFAULT_DEVELOPMENT_SERVER, devAuth, new VersionIncrement.UpdateDev(), new Task() {
                    public boolean invoke() {
                        if ( generateOfflineJARs ) {
                            generateOfflineJars( project, OldPhetServer.DEFAULT_DEVELOPMENT_SERVER, devAuth );
                        }
                        return true;
                    }
                }
        );
    }

    public void deployToDevelopmentAndProductionServers( final AuthenticationInfo devAuth,
                                                         final AuthenticationInfo prodAuth,
                                                         final VersionIncrement versionIncrement,
                                                         final PhetWebsite productionSite ) {
        deploy(
                //send a copy to dev
                new Task() {
                    public boolean invoke() {
                        //generate files for dev
                        //sendCopyToDev( PhetWebsite.FIGARO.getServerAuthenticationInfo( buildLocalProperties ), OldPhetServer.FIGARO_DEV );
                        sendCopyToDev( devAuth, OldPhetServer.DEFAULT_DEVELOPMENT_SERVER );
                        return prepareStagingArea( productionSite );
                    }
                }, productionSite.getOldProductionServer(), prodAuth, versionIncrement, new Task() {
                    public boolean invoke() {
                        System.out.println( "Executing website post-upload deployment process" );

                        boolean genjars = project instanceof JavaProject && ( (JavaProject) project ).getSignJar() && generateJARs;
                        SshUtils.executeCommand( productionSite, "chmod -R a+rw " + productionSite.getSimsStagingPath() + "/" + project.getName() );
                        if ( project.isSimulationProject() ) {
                            // this is a LOCAL connection that we are executing the curl from. TODO: move this to PhetWebsite
                            String deployCommand = "curl '" + productionSite.getWebBaseURL() + "/admin/deploy?project=" + project.getName() + "&generate-jars=" + genjars + "'";
                            SshUtils.executeCommand( productionSite, deployCommand );
                        }

                        return true;
                    }
                }
        );
    }

    public void deployProd( PhetWebsite productionWebsite, final AuthenticationInfo devAuth, final AuthenticationInfo prodAuth ) {
        deployToDevelopmentAndProductionServers( devAuth, prodAuth, new VersionIncrement.UpdateProdMinor(), productionWebsite );
    }

    //Run "rm" on the server to remove the phet/staging/sims/<project> directory contents, see #1529

    private boolean prepareStagingArea( PhetWebsite website ) {
        assert project.getName().length() >= 2;//reduce probability of a scary rm
        return SshUtils.executeCommands( website, new String[] {
                "mkdir -p -m 775 " + website.getSimsStagingPath() + "/" + project.getName(),
                "rm -f " + website.getSimsStagingPath() + "/" + project.getName() + "/*"
        } );
    }

    private void sendCopyToDev( AuthenticationInfo devAuth, OldPhetServer server ) {
        createHeader( true );
        // TODO: remove this sending to spot once tested and confirmed. Then refactor to not pass in devAuth, but possibly use build-local props
        project.buildLaunchFiles( server.getCodebase( project ), server.isDevelopmentServer() );
        if ( !debugDryRun ) {
            sendSSH( server, devAuth );
            generateOfflineJars( project, server, devAuth );
        }
//        PhetWebsite website = PhetWebsite.FIGARO;
//        project.buildLaunchFiles( website.getProjectDevUrl( project ), true );
//        if ( !debugDryRun ) {
//            sendDevCopyTo( website );
//            generateOfflineJars( project, OldPhetServer.SPOT, devAuth );
//        }
        PhetWebsite.openBrowser( server.getCodebase( project ) );
        //TODO #2143, delete <project>_en.production.jnlp files, since they shouldn't go to tigercat
    }

    public static void generateOfflineJars( PhetProject project, OldPhetServer server, AuthenticationInfo authenticationInfo ) {
        // TODO: return executeCommand's success if we run it, and modify the calling locations

        //only sign jars for Java Projects, and only if it is enabled (e.g. for simulations)
        boolean projectWantsJARs = project instanceof JavaProject && ( (JavaProject) project ).getSignJar();
        if ( projectWantsJARs && generateJARs ) {
            try {
                SshUtils.executeCommand( getJARGenerationCommand( (JavaProject) project, server ), server.getHost(), authenticationInfo );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private static String getJARGenerationCommand( JavaProject project, OldPhetServer server ) throws IOException {
        BuildToolsProject buildToolsProject = new BuildToolsProject( new File( project.getTrunk(), BuildToolsPaths.BUILD_TOOLS_DIR ) );
        String buildScriptDir = server.getServerDeployPath( buildToolsProject );
        String projectDir = server.getServerDeployPath( project );

        String jdkHome = server.getJDKHome();
        String javaCmd = jdkHome+"/bin/java";
        String jarName = buildToolsProject.getDefaultDeployJar().getName();
        String pathToBuildLocalProperties = server.getBuildLocalPropertiesFile();
        String command = javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " + JARGenerator.class.getName() + " " + projectDir + "/" + project.getDefaultDeployJar().getName() + " " + pathToBuildLocalProperties+" "+jdkHome;
        return command;
    }

}