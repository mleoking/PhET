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

public class StatisticsDeployCommand {

    private File trunk;

    private BuildLocalProperties buildLocalProperties;

    private String remoteDeployServer = "tigercat.colorado.edu";

    private PhetServer server = PhetServer.PRODUCTION;

    private String remoteDeployDir = BuildToolsPaths.TIGERCAT_HTDOCS + "/statistics";

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
        // TODO: don't catch all of these failures, since one failure means a failure of deploy!
        System.out.println( "Starting statistics deploy process" );

        System.out.println( "Trunk:      " + getTrunkDir().getCanonicalPath() );
        System.out.println( "Statistics: " + getStatisticsDir().getCanonicalPath() );

        writeRevisionFile();

        File[] statisticsFiles = getStatisticsDir().listFiles( new StatisticsFileFilter() );

        File[] reportFiles = getReportDir().listFiles( new StatisticsFileFilter() );

        File[] adminFiles = getAdminDir().listFiles( new StatisticsFileFilter() );

        AuthenticationInfo authenticationInfo = buildLocalProperties.getProdAuthenticationInfo();

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

        success = success && SshUtils.executeCommand( "cd " + remoteDeployDir + "; chmod ug+x set_permissions; ./set_permissions", server, authenticationInfo );

        return success;
    }

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

    private String getSVNVersion() {
        String[] args = new String[]{"svn", "status", "-u", "--non-interactive", getTrunkPath()};
        ProcessOutputReader.ProcessExecResult output = ProcessOutputReader.exec( args );
        StringTokenizer st = new StringTokenizer( output.getOut(), "\n" );
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            String key = "Status against revision:";
            if ( token.toLowerCase().startsWith( key.toLowerCase() ) ) {
                String suffix = token.substring( key.length() ).trim();
                //return Integer.parseInt( suffix );
                return suffix;
            }
        }
        throw new RuntimeException( "No svn version information found: " + output );
    }


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

            StatisticsProject project = new StatisticsProject( trunk );
            SVNStatusChecker checker = new SVNStatusChecker();

            if ( checker.isUpToDate( project ) ) {
                StatisticsDeployCommand command = new StatisticsDeployCommand( trunk );

                command.deploy();
            }


        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

}


class StatisticsFileFilter implements FileFilter {
    public boolean accept( File file ) {
        return file.isFile() && !StatisticsDeployCommand.ignoreFile( file );
    }
}