/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.jnlphacker;

import edu.colorado.phet.jnlp.JnlpFile;

import java.io.*;

public class JnlpMigrate {

    /**
     * @param args [0] Root directory to search for jnlp files
     *             [1] Target directory for altered jnlp files
     *             [2] New codebase to be written into the jnlp files
     *             [3] New homepage to be written into the jnlp files (optional)
     */
    public static void main( String[] args ) {
        File rootDir = null;
        String targetDirName = null;
        String codebase = null;
        String homepage = null;

        if( args.length < 3 ) {
            System.out.println( "A filter that changes the codebase in all the JNLP files"
                                + "in a directory tree, and writes the new files in an output directory." );
            System.out.println( "Usage: java JnlpMigrate sourceDir targetDir new-codebase [homepage]" );
            System.exit( 0 );
        }
        rootDir = new File( args[0] );
        File targetDir = null;
        targetDirName = args[1];
        targetDir = new File( targetDirName );
        targetDir.mkdirs();
        codebase = args[2];
        if( args.length >= 4 ) {
            homepage = args[3];
        }
        JnlpMigrate.processDir( rootDir, targetDir, codebase, homepage );
    }

    private static void processDir( File dir, File targetDir, String codebase, String homepage ) {

        System.out.println( "Processing directory: " + dir.getAbsolutePath() );

        // Make an output directory
        targetDir.mkdirs();

        // Process all the jnlp files in the directory
        File[] jnlpFiles = dir.listFiles( new JnlpMigrate.JnlpFileFilter() );
        for( int i = 0; i < jnlpFiles.length; i++ ) {
            System.out.println( "\t" + jnlpFiles[i].getName() );
            JnlpFile jnlpFile = new JnlpFile( jnlpFiles[i] );
            try {
                jnlpFile.setCodebase( codebase );
                if( homepage != null && jnlpFile.getHomepage() != null ) {
                    jnlpFile.setHomepage( homepage );
                }

                File targetFile = new File( targetDir + "/" + jnlpFiles[i].getName() );
                BufferedWriter out = new BufferedWriter( new FileWriter( targetFile ) );
                out.write( jnlpFile.toString() );
                out.close();
            }
            catch( IOException e ) {
            }
        }

        // Process all subdirectories
        File[] subDirs = dir.listFiles( new JnlpMigrate.DirFileFilter() );
        for( int i = 0; i < subDirs.length; i++ ) {
            File targetSubDir = new File( targetDir + "/" + subDirs[i].getName() );
            JnlpMigrate.processDir( subDirs[i], targetSubDir, codebase, homepage );
        }

    }

    private static class JnlpFileFilter implements FileFilter {
        public boolean accept( File pathname ) {
            return !pathname.isDirectory() && pathname.getName().toLowerCase().endsWith( ".jnlp" );
        }
    }

    private static class DirFileFilter implements FileFilter {
        public boolean accept( File pathname ) {
            return pathname.isDirectory();
        }
    }
}
