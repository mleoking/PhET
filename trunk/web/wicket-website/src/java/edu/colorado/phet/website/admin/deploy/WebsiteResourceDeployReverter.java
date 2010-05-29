package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.colorado.phet.buildtools.resource.ResourceDeployUtils;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * Reverts a resource deployment
 * <p/>
 * NOTE: This can only be guaranteed to work for the most recent resource deployment.
 * <p/>
 * NOTE: run on the server, so do not rename / move this without changing the other references under
 * edu.colorado.phet.buildtools.resource
 */
public class WebsiteResourceDeployReverter implements IProguardKeepClass {
    private File resourceDir;
    private File liveSimsDir;

    private static final Logger logger = Logger.getLogger( WebsiteResourceDeployReverter.class.getName() );

    public WebsiteResourceDeployReverter( File resourceDir ) {
        this.resourceDir = resourceDir;

        liveSimsDir = ResourceDeployUtils.getLiveSimsDir( resourceDir );

        File[] testDirs = ResourceDeployUtils.getTestDir( resourceDir ).listFiles();

        for ( int i = 0; i < testDirs.length; i++ ) {
            File testDir = testDirs[i];
            String sim = testDir.getName();

            File liveSimDir = new File( liveSimsDir, sim );
            File backupSimDir = new File( ResourceDeployUtils.getBackupDir( resourceDir ), sim );

            File[] testFiles = testDir.listFiles();

            for ( int j = 0; j < testFiles.length; j++ ) {
                File testFile = testFiles[j];

                if ( ResourceDeployUtils.ignoreTestFile( testFile ) ) {
                    try {
                        System.out.println( "Ignoring: " + testFile.getCanonicalPath() );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                    continue;
                }

                File backupFile = new File( backupSimDir, testFile.getName() );

                if ( backupFile.exists() ) {
                    try {
                        System.out.println( "Copying " + backupFile.getCanonicalPath() + " to " + liveSimDir.getCanonicalPath() );

                        FileUtils.copyToDir( backupFile, liveSimDir );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.println( "WARNING: backup file for " + sim + "/" + testFile.getName() + " does not exist." );
                    System.out.println( "\tPossibly it was created by the resource deployment and should be deleted? (no action taken)" );
                }
            }

        }
    }

    /**
     * Reverts the live sim dir to what was there before
     *
     * @param args First argument should be the temporary resource directory
     */
    public static void main( String[] args ) {
        new WebsiteResourceDeployReverter( new File( args[0] ) );

        System.out.println( "Resource reverted. Please verify on phet.colorado.edu" );
    }
}