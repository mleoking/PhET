package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.colorado.phet.buildtools.resource.ResourceDeployUtils;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

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

    private static final Logger logger = Logger.getLogger( WebsiteResourceDeployPublisher.class.getName() );

    public WebsiteResourceDeployPublisher( File resourceDir ) throws IOException {
        this.resourceDir = resourceDir;

        liveSimsDir = ResourceDeployUtils.getLiveSimsDir( resourceDir );

        File[] testDirs = ResourceDeployUtils.getTestDir( resourceDir ).listFiles();

        for ( int i = 0; i < testDirs.length; i++ ) {
            File dir = testDirs[i];

            String sim = dir.getName();

            File liveSimDir = new File( liveSimsDir, sim );

            File[] testFiles = dir.listFiles();

            for ( int j = 0; j < testFiles.length; j++ ) {
                File testFile = testFiles[j];

                if ( ResourceDeployUtils.ignoreTestFile( testFile ) ) {
                    System.out.println( "Ignoring: " + testFile.getCanonicalPath() );
                    continue;
                }

                System.out.println( "Copying " + testFile.getCanonicalPath() + " to " + liveSimDir.getCanonicalPath() );
                FileUtils.copyToDir( testFile, liveSimDir );
            }

        }
    }

    /**
     * Run the resource deploy publisher
     *
     * @param args First argument should be the path to the temporary resource directory
     */
    public static void main( String[] args ) {
        try {
            new WebsiteResourceDeployPublisher( new File( args[0] ) );

            System.out.println( "Resource Deployment successfully completed." );
        }
        catch( IOException e ) {
            System.out.println( "\n\nWARNING:\nAn error was detected during the execution of WebsiteResourceDeployPublisher!:" );
            e.printStackTrace();
            System.out.println( "Please roll back the changes with WebsiteResourceDeployReverter" );
        }
    }
}