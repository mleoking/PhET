package edu.colorado.phet.buildtools.statistics;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ProcessOutputReader;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;

import com.jcraft.jsch.JSchException;

/**
 * Handles deploying the statistics database code to tigercat.
 * TODO: for refactor, allow it to deploy to any "production" server
 */
public class StatisticsDeployCommand {

    private File trunk;

    private BuildLocalProperties buildLocalProperties;

    private String remoteDeployServer = "tigercat.colorado.edu";

    private OldPhetServer server = OldPhetServer.PRODUCTION;

    private String remoteDeployDir = BuildToolsPaths.TIGERCAT_HTDOCS + "/statistics";

    /**
     * List of file names that should not be uploaded to the server
     */
    private static String[] ignoreFileNames = {
            "raw-log.txt",
            "parsed-log.txt",
            "db-stats-login.php",
            "create_remote_backup",
            "deploy",
            "reset_db",
            "reset_db_for_restore",
            "db-auth.php"
    };

    public StatisticsDeployCommand( File trunkFile ) {
        trunk = trunkFile;

        buildLocalProperties = BuildLocalProperties.getInstanceRelativeToTrunk( trunk );
    }

    public boolean deploy() throws IOException {
        boolean success = true;

        System.out.println( "Starting statistics deploy process" );

        System.out.println( "Trunk:      " + getTrunkDir().getCanonicalPath() );
        System.out.println( "Statistics: " + getStatisticsDir().getCanonicalPath() );

        // write the revision into the correct source file
        writeRevisionFile();

        // get lists of files for each directory
        File[] statisticsFiles = getStatisticsDir().listFiles( new StatisticsFileFilter() );
        File[] reportFiles = getReportDir().listFiles( new StatisticsFileFilter() );
        File[] adminFiles = getAdminDir().listFiles( new StatisticsFileFilter() );

        AuthenticationInfo authenticationInfo = buildLocalProperties.getWebsiteProdAuthenticationInfo();

        // SCP all of the files to tigercat
        // TODO: bad design for adding more directories! improve it!
        for ( int i = 0; i < statisticsFiles.length; i++ ) {
            try {
                ScpTo.uploadFile( statisticsFiles[i], authenticationInfo.getUsername(), remoteDeployServer, deployFileName( statisticsFiles[i], "/" ), authenticationInfo.getPassword() );
            }
            catch( JSchException e ) {
                e.printStackTrace();
                success = false;
            }
            catch( IOException e ) {
                e.printStackTrace();
                success = false;
            }
        }

        for ( int i = 0; i < reportFiles.length; i++ ) {
            try {
                ScpTo.uploadFile( reportFiles[i], authenticationInfo.getUsername(), remoteDeployServer, deployFileName( reportFiles[i], "/report/" ), authenticationInfo.getPassword() );
            }
            catch( JSchException e ) {
                e.printStackTrace();
                success = false;
            }
            catch( IOException e ) {
                e.printStackTrace();
                success = false;
            }
        }

        for ( int i = 0; i < adminFiles.length; i++ ) {
            try {
                ScpTo.uploadFile( adminFiles[i], authenticationInfo.getUsername(), remoteDeployServer, deployFileName( adminFiles[i], "/admin/" ), authenticationInfo.getPassword() );
            }
            catch( JSchException e ) {
                e.printStackTrace();
                success = false;
            }
            catch( IOException e ) {
                e.printStackTrace();
                success = false;
            }
        }

        // set the permissions of the server-side files
        success = success && SshUtils.executeCommand( "cd " + remoteDeployDir + "; chmod ug+x set_permissions; ./set_permissions", server.getHost(), authenticationInfo );

        return success;
    }

    /**
     * We need to write the revision of the deployment into the server-side code so that it will log messages with the
     * correct server revision
     *
     * @throws IOException
     */
    private void writeRevisionFile() throws IOException {
        FileUtils.writeString( new File( getStatisticsDir(), "db-revision.php" ), "<?php $serverVersion = \"" + getSVNVersion() + "\"; ?>\n" );
    }

    private File getTrunkDir() {
        return trunk;
    }

    private String getTrunkPath() {
        try {
            return getTrunkDir().getCanonicalPath();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        throw new RuntimeException( "Trunk path not found." );
    }

    private File getStatisticsDir() {
        return new File( getTrunkDir(), BuildToolsPaths.STATISTICS );
    }

    private File getReportDir() {
        return new File( getStatisticsDir(), "report" );
    }

    private File getAdminDir() {
        return new File( getStatisticsDir(), "admin" );
    }

    public static boolean ignoreFile( File file ) {
        String fileName = null;
        try {
            fileName = file.getCanonicalFile().getName();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        for ( int i = 0; i < ignoreFileNames.length; i++ ) {
            if ( fileName.equals( ignoreFileNames[i] ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * A mapping from local repository file names to what they are on the server.
     * This is mainly used for mapping htaccess to .htaccess currently
     *
     * @param file      The local file
     * @param deployDir The directory where the file will be deployed (backups, reports, main dir, etc.)
     * @return The string representing the full path of where it should be deployed
     */
    private String deployFileName( File file, String deployDir ) {
        if ( deployDir == null ) {
            deployDir = "/";
        }

        String resultName = file.getName();
        if ( resultName.equals( "htaccess" ) ) {
            resultName = ".htaccess";
        }

        return remoteDeployDir + deployDir + resultName;
    }

    /**
     * Gets the SVN revision of the latest code. This may not be the last commit where changes were made to statistics,
     * but instead represent the CURRENT SVN revision at the time of the deploy.
     *
     * @return String representing the SVN revision
     */
    private String getSVNVersion() {
        AuthenticationInfo auth = BuildLocalProperties.getInstance().getRespositoryAuthenticationInfo();
        String[] args = new String[]{"svn", "status", "-u", "--non-interactive", "--username", auth.getUsername(), "--password", auth.getPassword(), getTrunkPath()};
        ProcessOutputReader.ProcessExecResult output = ProcessOutputReader.exec( args );
        StringTokenizer st = new StringTokenizer( output.getOut(), "\n" );
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            String key = "Status against revision:";
            if ( token.toLowerCase().startsWith( key.toLowerCase() ) ) {
                String suffix = token.substring( key.length() ).trim();
                return suffix;
            }
        }
        throw new RuntimeException( "No svn version information found: " + output.getOut() );
    }

    /**
     * Runs the statistics deployment
     *
     * @param args First argument must be the path to trunk
     */
    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "You just provide a system-specific path to the trunk" );
        }

        try {

            File trunk = ( new File( args[0] ) ).getCanonicalFile();

            System.out.println( "Trunk file specified as: " + trunk.getCanonicalPath() );

            if ( !trunk.getName().equals( "trunk" ) ) {
                throw new RuntimeException( "WARNING: may not be correct path to trunk" );
            }

            StatisticsProject project = new StatisticsProject( new File( trunk, BuildToolsPaths.STATISTICS ) );
            SVNStatusChecker checker = new SVNStatusChecker();

            // make sure SVN of statistics is up to date before deploying
            if ( checker.isUpToDate( project ) ) {
                StatisticsDeployCommand command = new StatisticsDeployCommand( trunk );

                // actually deploy everything
                command.deploy();
            }


        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    private class StatisticsFileFilter implements FileFilter {
        public boolean accept( File file ) {
            return file.isFile() && !StatisticsDeployCommand.ignoreFile( file );
        }
    }

}


