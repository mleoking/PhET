package edu.colorado.phet.buildtools.translate;

import java.io.File;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

public class ResourceDeployPublisher implements IProguardKeepClass {

    private File resourceDir;
    private File testDir;
    private File liveSimDir;

    public ResourceDeployPublisher( File resourceDir ) {
        this.resourceDir = resourceDir;

        testDir = new File( resourceDir, "test" );
        liveSimDir = new File( resourceDir, "../../" );

        File[] testDirs = testDir.listFiles();

        for ( int i = 0; i < testDirs.length; i++ ) {
            File dir = testDirs[i];

            String sim = dir.getName();

            
        }
    }

    public static void main( String[] args ) {
        new ResourceDeployPublisher( new File( args[0] ) );
    }
}
