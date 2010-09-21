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

    private static final Logger logger = Logger.getLogger( WebsiteResourceDeployReverter.class.getName() );

    public WebsiteResourceDeployReverter( File resourceDir, File liveSimsDir ) {

        for ( File testDir : ResourceDeployUtils.getTestDir( resourceDir ).listFiles() ) {
            String sim = testDir.getName();

            File liveSimDir = new File( liveSimsDir, sim );
            File backupSimDir = new File( ResourceDeployUtils.getBackupDir( resourceDir ), sim );

            for ( File testFile : testDir.listFiles() ) {
                if ( ResourceDeployUtils.ignoreTestFile( testFile ) ) {
                    try {
                        logger.info( "Ignoring: " + testFile.getCanonicalPath() );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                    continue;
                }

                File backupFile = new File( backupSimDir, testFile.getName() );

                if ( backupFile.exists() ) {
                    try {
                        logger.info( "Copying " + backupFile.getCanonicalPath() + " to " + liveSimDir.getCanonicalPath() );

                        FileUtils.copyToDir( backupFile, liveSimDir );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
                else {
                    logger.warn( "backup file for " + sim + "/" + testFile.getName() + " does not exist." );
                    logger.warn( "    Possibly it was created by the resource deployment and should be deleted? (no action taken)" );
                }
            }

        }
    }

}