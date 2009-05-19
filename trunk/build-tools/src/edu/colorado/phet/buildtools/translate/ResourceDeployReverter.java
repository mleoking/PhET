package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

public class ResourceDeployReverter implements IProguardKeepClass {
    private File resourceDir;
    private File liveSimsDir;

    public ResourceDeployReverter( File resourceDir ) {
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

    public static void main( String[] args ) {
        new ResourceDeployReverter( new File( args[0] ) );

        System.out.println( "Resource reverted. Please verify on phet.colorado.edu" );
    }
}
