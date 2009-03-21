package edu.colorado.phet.buildtools.statistics;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.SVNStatusChecker;
import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.translate.ScpTo;
import edu.colorado.phet.buildtools.util.ProcessOutputReader;
import edu.colorado.phet.buildtools.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.util.StringTokenizer;

import com.jcraft.jsch.JSchException;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshCommand;
import org.rev6.scf.SshException;

public class StatisticsDeployCommand {

    private File trunk;

    private BuildLocalProperties buildLocalProperties;

    private String remoteDeployServer = "tigercat.colorado.edu";

    private String remoteDeployDir = "/web/chroot/phet/usr/local/apache/htdocs/statistics";

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

        buildLocalProperties = BuildLocalProperties.initRelativeToTrunk( trunk );
    }

    public boolean deploy() throws IOException {
        System.out.println( "Starting statistics deploy process" );

        System.out.println( "Trunk:      " + getTrunkDir().getCanonicalPath() );
        System.out.println( "Statistics: " + getStatisticsDir().getCanonicalPath() );

        writeRevisionFile();

        File[] statisticsFiles = getStatisticsDir().listFiles(new StatisticsFileFilter());

        File[] reportFiles = getReportDir().listFiles(new StatisticsFileFilter());

        AuthenticationInfo authenticationInfo = buildLocalProperties.getProdAuthenticationInfo();

        for( int i = 0; i < statisticsFiles.length; i++ ) {
            //System.out.println( statisticsFiles[i].getCanonicalPath() + " => " + deployFileName( statisticsFiles[i], "/" ) );
            try {
                ScpTo.uploadFile( statisticsFiles[i], authenticationInfo.getUsername(), remoteDeployServer, deployFileName( statisticsFiles[i], "/" ), authenticationInfo.getPassword() );
            } catch (JSchException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for( int i = 0; i < reportFiles.length; i++ ) {
            //System.out.println( reportFiles[i].getCanonicalPath() + " => " + deployFileName( reportFiles[i], "/report/" ) );
            try {
                ScpTo.uploadFile( reportFiles[i], authenticationInfo.getUsername(), remoteDeployServer, deployFileName( reportFiles[i], "/report/" ), authenticationInfo.getPassword() );
            } catch (JSchException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SshConnection sshConnection = new SshConnection( remoteDeployServer, authenticationInfo.getUsername(), authenticationInfo.getPassword() );

        try {
            sshConnection.connect();

            sshConnection.executeTask( new SshCommand( "cd " + remoteDeployDir + "; chmod ug+x set_permissions; ./set_permissions" )  );

            //sshConnection.executeTask( new SshCommand( "chmod ug+x " + remoteDeployDir + "/set_permissions" );
            //sshConnection.executeTask( new SshCommand( remoteDeployDir + "/set_permissions" );
        }
        catch( SshException e ) {
            if ( e.toString().toLowerCase().indexOf( "auth fail" ) != -1 ) {
                // TODO: check if authentication fails, don't try logging in again
                // on tigercat, 3 (9?) unsuccessful login attepts will lock you out
                System.out.println( "Authentication on '" + remoteDeployServer + "' has failed, is your username and password correct?  Exiting..." );
                System.exit( 0 );
            }
            e.printStackTrace();
        }
        finally {
            sshConnection.disconnect();
        }
        
        return true;
    }

    private void writeRevisionFile() throws IOException {
        FileUtils.writeString( new File( getStatisticsDir(), "db-revision.php" ), "<?php $serverVersion = \"" + getSVNVersion() + "\"; ?>\n");
    }

    private File getTrunkDir() {
        return trunk;
    }

    private String getTrunkPath() {
        try {
            return getTrunkDir().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException( "Trunk path not found." ); 
    }

    private File getStatisticsDir() {
        return new File( getTrunkDir(), "statistics" );
    }

    private File getReportDir() {
        return new File( getStatisticsDir(), "report" );
    }

    public static boolean ignoreFile( File file ) {
        String fileName = null;
        try {
            fileName = file.getCanonicalFile().getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for( int i = 0; i < ignoreFileNames.length; i++ ) {
            if( fileName.equals(ignoreFileNames[i]) ) {
                return true;
            }
        }
        return false;
    }

    private String deployFileName( File file, String deployDir ) {
        if( deployDir == null ) {
            deployDir = "/";
        }

        String resultName = file.getName();
        if( resultName.equals("htaccess") ) {
            resultName = ".htaccess";
        }

        return remoteDeployDir + deployDir + resultName;
    }

    private String getSVNVersion() {
        String[] args = new String[]{ "svn", "status", "-u", getTrunkPath() };
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
        if( args.length == 0 ) {
            System.out.println( "You just provide a system-specific path to the trunk" );
        }

        try {

            File trunk = (new File( args[0] )).getCanonicalFile();

            System.out.println( "Trunk file specified as: " + trunk.getCanonicalPath() );

            if( !trunk.getName().equals( "trunk" ) ) {
                throw new RuntimeException( "WARNING: may not be correct path to trunk" );
            }

            StatisticsProject project = new StatisticsProject( trunk );
            SVNStatusChecker checker = new SVNStatusChecker();

            if( checker.isUpToDate( project ) ) {
                StatisticsDeployCommand command = new StatisticsDeployCommand( trunk );

                command.deploy();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}


class StatisticsFileFilter implements FileFilter {
    public boolean accept(File file) {
        return file.isFile() && !StatisticsDeployCommand.ignoreFile( file );
    }
}