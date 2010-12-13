package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.colorado.phet.buildtools.resource.ResourceDeployUtils;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.website.data.Project;

/**
 * Prints instructions on how to revert to standard output
 * Copies each non-SWF file from the temporary resource directories to the corresponding live sim directory
 * <p/>
 * NOTE: run on the server, so do not rename / move this without changing the other references under
 * edu.colorado.phet.buildtools.resource
 */
public class WebsiteResourceDeployPublisher implements IProguardKeepClass {

    private File resourceDir;
    private File liveSimsDir;

    private List<String> deployedProjectNames = new LinkedList<String>();

    private static final Logger logger = Logger.getLogger( WebsiteResourceDeployPublisher.class.getName() );

    public WebsiteResourceDeployPublisher( File resourceDir, File liveSimsDir, File docRoot ) throws IOException {
        this.resourceDir = resourceDir;

        this.liveSimsDir = liveSimsDir;

        File[] testDirs = ResourceDeployUtils.getTestDir( resourceDir ).listFiles();

        for ( File dir : testDirs ) {
            String projectName = dir.getName();

            deployedProjectNames.add( projectName );

            Project.backupProject( docRoot, projectName );

            File liveSimDir = new File( liveSimsDir, projectName );

            for ( File testFile : dir.listFiles() ) {
                if ( ResourceDeployUtils.ignoreTestFile( testFile ) ) {
                    logger.info( "Ignoring: " + testFile.getCanonicalPath() );
                    continue;
                }

                logger.info( "Copying " + testFile.getCanonicalPath() + " to " + liveSimDir.getCanonicalPath() );
                FileUtils.copyToDir( testFile, liveSimDir );
            }

        }
    }

    public List<String> getDeployedProjectNames() {
        return deployedProjectNames;
    }

}