package edu.colorado.phet.buildtools.resource;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

public class ResourceDeployPublisher implements IProguardKeepClass {

    private File resourceDir;
    private File liveSimsDir;

    public ResourceDeployPublisher( File resourceDir ) throws IOException {
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

    public static void main( String[] args ) {
        try {
            new ResourceDeployPublisher( new File( args[0] ) );

            System.out.println( "Resource Deployment successfully completed." );
        }
        catch( IOException e ) {
            System.out.println( "\n\nWARNING:\nAn error was detected during the execution of ResourceDeployPublisher!:" );
            e.printStackTrace();
            System.out.println( "Please roll back the changes with ResourceDeployReverter" );
        }
    }
}
