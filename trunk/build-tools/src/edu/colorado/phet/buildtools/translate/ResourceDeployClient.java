package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.java.projects.BuildToolsProject;
import edu.colorado.phet.buildtools.util.ScpTo;

import com.jcraft.jsch.JSchException;

/**
 * This class is used to log into tigercat, create the necessary temporary directory structure, and upload the necessary
 * files.
 *
 * The directory structure on tigercat is shown below:
 *
 * htdocs/sims/resources/
 *     (temporary resource directory)/ -- Holds everything related to this particular deployment. ex: 1242706828232_common-strings-ar-xml
 *                                        The first part includes the timestamp of approximately when the directory was created
 *         resource/
 *             resource.properties     -- Holds properties (to be described below) of what JARs to poke, generate, the resource name, etc.
 *             (resource file)         -- The file that will be poked into JARs. Name specified in resource.properties
 *
 *         test/                       -- Directory that holds all of the files necessary to test the new deployment.
 *             (sim dirs)/
 *
 *         backup/                     -- Directory that holds all files that will be changed
 *             (sim dirs)/
 *
 *         extras/                     -- (optional) Directory that holds additional files to be added and backed up
 *             (sim dirs)/
 *
 * resource.properties properties:
 * resourceFile (the name of the file contained in the resource subdirectory)
 * resourceDestination (string path of where to put the file in JARs. should end with a slash)
 * sims (comma-separated list of sims to add the resource into)
 * mode (either 'java' or 'flash')
 *
 * ResourceDeployClient is used to first upload resource.properties and the resource file.
 * Then optionally extra files (in extras) can be added. Extras were created to handle the Flash HTMLs that need to be regenerated
 * for a common translation.
 *
 * ResourceDeployServer, when activated, follows the following steps:
 * (1) loads resource.properties (contains list of sims), locates the resource file, and creates the backup directory
 * (2) copies all JARs for each sim from their live directory into their subdirectory in the backup directory
 *     ex: copies htdocs/sims/pendulum-lab/pendulum-lab_en.jar to
 *         htdocs/sims/resources/1242706828232_common-strings-ar-xml/backup/pendulum-lab/pendulum-lab_en.jar
 * (3) copies JARs (if java mode, only the JARs ending in _all.jar) into the test directory
 *     ex: copies to htdocs/sims/resources/1242706828232_common-strings-ar-xml/test/pendulum-lab/pendulum-lab_en.jar 
 * (4) pokes the resource file into all JARs in the test directories
 * (5) resigns all of those JARs
 * (6) if java mode, generates the offline JARs with JARGenerator (in the test directories)
 * (7) if files exist in the extras directories, their counterparts in the live dir are backed up
 *     ex: htdocs/sims/pendulum-lab/pendulum-lab_ar.html backed up to
 *         htdocs/sims/resources/1242706828232_common-strings-ar-xml/backup/pendulum-lab/pendulum-lab_ar.html
 * (8) the extras files are copied into the test directories.
 * (9) if flash mode, SWFs are copied from their live directories into both the test and backup directories
 *     NOTE: during publishing, the SWFs in the live directory will not be replaced.
 *
 * ResourceDeployPublisher, when activated, follows the following steps:
 * (1) If an exception occurs in any of the following steps, it will immediately stop and print instructions to revert
 * (2) Each non-SWF file in the test directories is copied into the corresponding live sim directories (most likely replacing
 *     what was there)
 *
 * ResourceDeployReverter, when activated, follows the following steps:
 * (1) For each non-SWF file in the test directory that also exists in the backup directory, the backup file is copied into the live sim directory
 *
 *
 */
public class ResourceDeployClient {

    // TODO: refactor PhetServer so that this type of thing is not necessary
    public final String PROD_PATH = "/web/chroot/phet/usr/local/apache/htdocs/sims/resources/";

    private File resourceFile;
    private File propertiesFile;
    private String temporaryDirName;

    public ResourceDeployClient( File resourceFile, File propertiesFile ) {
        this.resourceFile = resourceFile;
        this.propertiesFile = propertiesFile;
        temporaryDirName = createTemporaryDirName( resourceFile );
    }

    // uploads the resource file and its corresponding properties file
    public void uploadResourceFile() throws JSchException, IOException {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirPath = getTemporaryDirPath();

        dirtyExecute( "mkdir -p -m 775 " + temporaryDirPath + "/resource" );

        ScpTo.uploadFile( resourceFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                          temporaryDirPath + "/resource/" + resourceFile.getName(), authenticationInfo.getPassword() );
        ScpTo.uploadFile( propertiesFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                          temporaryDirPath + "/resource/resource.properties", authenticationInfo.getPassword() );
    }

    // uploads an extra file for a particular sim
    public void uploadExtraFile( File extraFile, String sim ) throws JSchException, IOException {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirPath = getTemporaryDirPath();

        String temporarySimExtrasDir = temporaryDirPath + "/extras/" + sim;

        dirtyExecute( "mkdir -p -m 775 " + temporarySimExtrasDir );

        ScpTo.uploadFile( extraFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                          temporarySimExtrasDir + "/" + extraFile.getName(), authenticationInfo.getPassword() );
    }

    // executes the resource deploy server on tigercat
    public void executeResourceDeployServer( File trunk ) throws IOException {
        String temporaryDirPath = getTemporaryDirPath();

        PhetServer server = PhetServer.PRODUCTION;

        BuildToolsProject buildToolsProject = new BuildToolsProject( new File( trunk, "build-tools" ) );
        String buildScriptDir = server.getServerDeployPath( buildToolsProject );

        String javaCmd = server.getJavaCommand();
        String jarCmd = server.getJarCommand();
        String jarName = buildToolsProject.getDefaultDeployJar().getName();
        String pathToBuildLocalProperties = server.getBuildLocalPropertiesFile();

        String command = javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " +
                         ResourceDeployServer.class.getName() + " " + jarCmd + " " + pathToBuildLocalProperties +
                         " " + temporaryDirPath;

        //dirtyExecute( command );

        System.out.println();
        System.out.println( "****************************************************************************************" );
        System.out.println();

        System.out.println( "Please run ResourceDeployServer on tigercat by executing:" );
        System.out.println( command );
        System.out.println();

        System.out.println( "All of the files for testing (that will be copied over into the sims directory) will be " +
                            "located at http://phet.colorado.edu/sims/resources/" + temporaryDirName + "/test/" );
        System.out.println();

        System.out.println( "After testing is done, all of the files in the test subdirectory can be published via:" );
        System.out.println( javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " +
                            ResourceDeployPublisher.class.getName() + " " + temporaryDirPath );
        System.out.println();

        System.out.println( "If issues arise during publishing or later, this action can be reverted by the following command:" );
        System.out.println( javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " +
                            ResourceDeployReverter.class.getName() + " " + temporaryDirPath );

        System.out.println( "These commands can be run from scripts named 'server', 'publish' and 'revert' in htdocs/sims/resources " +
                            "if you specify the temporary directory name as above" );

    }

    public String createTemporaryDirName( File resourceFile ) {
        String ret = resourceFile.getName();
        ret = ret.replaceAll( "[^a-zA-Z0-9]", "-" );
        ret = String.valueOf( ( new Date() ).getTime() ) + "_" + ret;
        return ret;
    }

    public String getTemporaryDirName() {
        return temporaryDirName;
    }

    public String getTemporaryDirPath() {
        return PROD_PATH + getTemporaryDirName();
    }

    public void dirtyExecute( String command ) {
        System.out.println( "# " + command );
        PhetServer server = PhetServer.PRODUCTION;
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        try {
            sshConnection.connect();
            sshConnection.executeTask( new SshCommand( command ) );
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
    }

}
