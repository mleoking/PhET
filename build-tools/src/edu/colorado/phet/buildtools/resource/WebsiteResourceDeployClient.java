package edu.colorado.phet.buildtools.resource;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.PhetWebsite;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;

import com.jcraft.jsch.JSchException;

/**
 * This class is used to log into a website, create the necessary temporary directory structure, and upload the necessary
 * files.
 * <p/>
 * The directory structure on the typical website is shown below:
 * <p/>
 * htdocs/sims/resources/
 * (temporary resource directory)/ -- Holds everything related to this particular deployment. ex: 1242706828232_common-strings-ar-xml
 * The first part includes the timestamp of approximately when the directory was created
 * resource/
 * resource.properties     -- Holds properties (to be described below) of what JARs to poke, generate, the resource name, etc.
 * (resource file)         -- The file that will be poked into JARs. Name specified in resource.properties
 * <p/>
 * test/                       -- Directory that holds all of the files necessary to test the new deployment.
 * (sim dirs)/
 * <p/>
 * backup/                     -- Directory that holds all files that will be changed
 * (sim dirs)/
 * <p/>
 * extras/                     -- (optional) Directory that holds additional files to be added and backed up
 * (sim dirs)/
 * <p/>
 * resource.properties properties:
 * resourceFile (the name of the file contained in the resource subdirectory)
 * resourceDestination (string path of where to put the file in JARs. should end with a slash)
 * sims (comma-separated list of sims to add the resource into)
 * mode (either 'java' or 'flash')
 * <p/>
 * ResourceDeployClient is used to first upload resource.properties and the resource file.
 * Then optionally extra files (in extras) can be added. Extras were created to handle the Flash HTMLs that need to be regenerated
 * for a common translation.
 * <p/>
 * ResourceDeployServer, when activated, follows the following steps:
 * (1) loads resource.properties (contains list of sims), locates the resource file, and creates the backup directory
 * (2) copies all JARs for each sim from their live directory into their subdirectory in the backup directory
 * ex: copies htdocs/sims/pendulum-lab/pendulum-lab_en.jar to
 * htdocs/sims/resources/1242706828232_common-strings-ar-xml/backup/pendulum-lab/pendulum-lab_en.jar
 * (3) copies JARs (if java mode, only the JARs ending in _all.jar) into the test directory
 * ex: copies to htdocs/sims/resources/1242706828232_common-strings-ar-xml/test/pendulum-lab/pendulum-lab_en.jar
 * (4) pokes the resource file into all JARs in the test directories
 * (5) resigns all of those JARs
 * (6) if java mode, generates the offline JARs with JARGenerator (in the test directories); JARGenerator signs these jars when they are made
 * (7) if files exist in the extras directories, their counterparts in the live dir are backed up
 * ex: htdocs/sims/pendulum-lab/pendulum-lab_ar.html backed up to
 * htdocs/sims/resources/1242706828232_common-strings-ar-xml/backup/pendulum-lab/pendulum-lab_ar.html
 * (8) the extras files are copied into the test directories.
 * (9) if flash mode, SWFs are copied from their live directories into both the test and backup directories
 * NOTE: during publishing, the SWFs in the live directory will not be replaced.
 * <p/>
 * ResourceDeployPublisher, when activated, follows the following steps:
 * (1) If an exception occurs in any of the following steps, it will immediately stop and print instructions to revert
 * (2) Each non-SWF file in the test directories is copied into the corresponding live sim directories (most likely replacing
 * what was there)
 * <p/>
 * ResourceDeployReverter, when activated, follows the following steps:
 * (1) For each non-SWF file in the test directory that also exists in the backup directory, the backup file is copied into the live sim directory
 */
public class WebsiteResourceDeployClient {

    private File resourceFile;
    private File propertiesFile;
    private String temporaryDirName;
    private PhetWebsite website;

    public WebsiteResourceDeployClient( PhetWebsite website, File resourceFile, File propertiesFile ) {
        this.website = website;
        this.resourceFile = resourceFile;
        this.propertiesFile = propertiesFile;
        temporaryDirName = createTemporaryDirName( resourceFile );
    }

    /**
     * uploads the resource file and its corresponding properties file
     *
     * @return Success
     * @throws com.jcraft.jsch.JSchException
     * @throws java.io.IOException
     */
    public boolean uploadResourceFile() throws JSchException, IOException {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirPath = getTemporaryDirPath();

        boolean success = dirtyExecute( "mkdir -p -m 777 " + temporaryDirPath + "/resource" );
        if ( !success ) {
            System.out.println( "Error creating the path for the resource file, aborting the upload" );
            return false;
        }

        uploadFile( resourceFile, authenticationInfo, PhetServer.PRODUCTION.getHost(),
                    temporaryDirPath + "/resource/" + resourceFile.getName() );
        uploadFile( propertiesFile, authenticationInfo, PhetServer.PRODUCTION.getHost(),
                    temporaryDirPath + "/resource/resource.properties" );
        return true;
    }

    /**
     * uploads an extra file for a particular sim. (like the Flash HTML files that need to be regenerated on the client)
     *
     * @param extraFile The extra file to upload
     * @param sim       The sim name
     * @return
     * @throws com.jcraft.jsch.JSchException
     * @throws java.io.IOException
     */
    public boolean uploadExtraFile( File extraFile, String sim ) throws JSchException, IOException {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirPath = getTemporaryDirPath();

        String temporarySimExtrasDir = temporaryDirPath + "/extras/" + sim;

        boolean success = dirtyExecute( "mkdir -p -m 777 " + temporarySimExtrasDir );
        if ( !success ) {
            System.out.println( "Error attempting to upload extra file!" );
            return false;
        }
        uploadFile( extraFile, authenticationInfo, PhetServer.PRODUCTION.getHost(),
                    temporarySimExtrasDir + "/" + extraFile.getName() );
        return true;
    }

    public static void uploadFile( File localFile, AuthenticationInfo auth, String host, String remoteFilePath ) throws JSchException, IOException {
        ScpTo.uploadFile( localFile, auth.getUsername(), host, remoteFilePath, auth.getPassword() );
        //SshUtils.executeCommand( "chmod g+w,a+rw " + remoteFilePath, host, auth );
    }

    private String createTemporaryDirName( File resourceFile ) {
        String ret = resourceFile.getName();
        ret = ret.replaceAll( "[^a-zA-Z0-9]", "-" );
        ret = String.valueOf( ( new Date() ).getTime() ) + "_" + ret;
        return ret;
    }

    public String getTemporaryDirName() {
        return temporaryDirName;
    }

    public String getTemporaryDirPath() {
        return website.getStagingPath() + "/" + getTemporaryDirName();
    }

    private boolean dirtyExecute( String command ) {
        System.out.println( "# " + command );
        return SshUtils.executeCommand( command, website.getServerHost(), website.getServerAuthenticationInfo( BuildLocalProperties.getInstance() ) );
    }

    /**
     * Starts the process for deploying a resource file into all Java JARs on the production server
     * The resource file and metadata will be uploaded, and instructions will be printed on how to execute
     * the rest of the steps on the production server
     *
     * @param website             The website to which we want to deploy
     * @param trunk               A reference to the trunk directory
     * @param resourceFile        The resource file to be deployed and inserted into JARs
     * @param resourceDestination The path inside the JARs to which the resource file should be placed. This string
     *                            should always start and end with "/". Ex: "/phetcommon/localization/" or "/"
     * @throws java.io.IOException           Just about everything throws this here
     * @throws com.jcraft.jsch.JSchException If something happens when attempting to upload the resource file
     */
    public static void deployJavaResourceFile( PhetWebsite website, File trunk, File resourceFile, String resourceDestination ) throws IOException, JSchException {
        deployResourceFile( website, trunk, resourceFile, resourceDestination, ResourceDeployUtils.getJavaSimNames( trunk ), "java" );
    }

    /**
     * Starts the process for deploying a resource file into all Flash JARs on the production server
     * The resource file and metadata will be uploaded, and instructions will be printed on how to execute
     * the rest of the steps on the production server
     * <p/>
     * NOTE: This alone will not update Flash HTML files. If these files need to be changed, please use
     * an instance of ResourceDeployClient with extra files (extras)
     *
     * @param website             The website to which we want to deploy
     * @param trunk               A reference to the trunk directory
     * @param resourceFile        The resource file to be deployed and inserted into JARs
     * @param resourceDestination The path inside the JARs to which the resource file should be placed. This string
     *                            should always start and end with "/". Ex: "/phetcommon/localization/" or "/"
     * @throws java.io.IOException           Just about everything throws this here
     * @throws com.jcraft.jsch.JSchException If something happens when attempting to upload the resource file
     */
    public static void deployFlashResourceFile( PhetWebsite website, File trunk, File resourceFile, String resourceDestination ) throws IOException, JSchException {
        deployResourceFile( website, trunk, resourceFile, resourceDestination, ResourceDeployUtils.getFlashSimNames( trunk ), "flash" );
    }

    /**
     * Starts the process for deploying a resource file into all Flash or Java JARs on the production server
     * The resource file and metadata will be uploaded, and instructions will be printed on how to execute
     * the rest of the steps on the production server
     *
     * @param website             The website to which we want to deploy
     * @param trunk               A reference to the trunk directory
     * @param resourceFile        The resource file to be deployed and inserted into JARs
     * @param resourceDestination The path inside the JARs to which the resource file should be placed. This string
     *                            should always start and end with "/". Ex: "/phetcommon/localization/" or "/"
     * @param simList             A comma-separated list of simulations
     * @param mode                Currently either "flash" or "java"
     * @throws java.io.IOException           Just about everything throws this here
     * @throws com.jcraft.jsch.JSchException If something happens when attempting to upload the resource file
     */
    public static void deployResourceFile( PhetWebsite website, File trunk, File resourceFile, String resourceDestination, String simList, String mode ) throws IOException, JSchException {
        File propertiesFile = File.createTempFile( "resource", ".properties" );
        String propertiesString = "resourceFile=" + resourceFile.getName() + "\n";
        propertiesString += "sims=" + simList + "\n";
        propertiesString += "resourceDestination=" + resourceDestination + "\n";
        propertiesString += "mode=" + mode + "\n";
        FileUtils.writeString( propertiesFile, propertiesString );

        WebsiteResourceDeployClient client = new WebsiteResourceDeployClient( website, resourceFile, propertiesFile );
        System.out.println( "****** Uploading resource file and properties" );

        // uploads just the resource file and properties file (also creates the temporary directory structure)
        boolean success = client.uploadResourceFile();

        if ( !success ) {
            System.out.println( "Upload failure, stopping process" );
            return;
        }

        // display to the user the commands needed to execute the server side
        //client.displayResourceDeployServerInstructions( trunk );

        PhetWebsite.openBrowser( website.getDeployResourceUrl( client.getTemporaryDirPath() ) );
    }

    /**
     * Class used on the command line to deploy a single resource
     * <p/>
     * Usage: ResourceDeploy (trunk) (mode) (resourceFile) (resourceDestination)
     * <p/>
     * (trunk) is the path to the trunk directory
     * (mode) is either 'java' or 'flash'
     * (resourceFile) is the path to the resource file
     * (resourceDestination) is where to put the resource file in the JARs (should always start and end with '/')
     */
    public static class ResourceDeploy {

        private static void printUsageAndExit() {
            System.out.println( "Usage: ResourceDeploy <trunk> <mode> <resourceFile> <resourceDestination>" );
            System.out.println( "<trunk> is the path to the trunk directory" );
            System.out.println( "<mode> is either 'java' or 'flash'" );
            System.out.println( "<resourceFile> is the path to the resource file" );
            System.out.println( "<resourceDestination> is where to put the resource file in the JARs (should always start and end with '/')" );
            System.exit( 1 );
        }

        public static void main( String[] args ) {
            if ( args.length != 4 ) {
                printUsageAndExit();
            }
            File trunk = new File( args[0] );
            String mode = args[1];
            File resourceFile = new File( args[2] );
            String resourceDestination = args[3];

            if ( !trunk.exists() || !trunk.isDirectory() ) {
                System.out.println( "Path to trunk is invalid" );
                printUsageAndExit();
            }

            if ( !resourceFile.exists() ) {
                System.out.println( "Path to resource file is invalid" );
                printUsageAndExit();
            }

            BuildLocalProperties.initRelativeToTrunk( trunk );

            if ( mode.equals( "java" ) ) {
                try {
                    deployJavaResourceFile( PhetWebsite.FIGARO, trunk, resourceFile, resourceDestination );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( JSchException e ) {
                    e.printStackTrace();
                }
            }
            else if ( mode.equals( "flash" ) ) {
                try {
                    deployFlashResourceFile( PhetWebsite.FIGARO, trunk, resourceFile, resourceDestination );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( JSchException e ) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println( "Unknown mode" );
                printUsageAndExit();
            }
        }

    }
}