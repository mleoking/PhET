/**
 * Class: JnlpHacker
 * Package: edu.colorado.phet.jnlphacker
 * Author: Another Guy
 * Date: Sep 10, 2003
 */
package edu.colorado.phet.jnlphacker;

import edu.colorado.phet.jnlp.JnlpFile;
import edu.colorado.phet.jnlp.InvalidJnlpException;

import java.io.*;

public class JnlpHacker {

    /**
     *
     * @param args  [0] Root directory to search for jnlp files
     *              [1] Target directory for altered jnlp files
     */
    public static void main( String[] args ) {
        File rootDir = null;
        String targetDirName = null;

        if( args.length < 1 ) {
            System.out.println( "A filter that modifies all the JNLP files in a directory tree "
                                + "to reference a specified codebase directory, and write them to "
                                + "that directory. " );
            System.out.println( "Usage: java JnlpHacker sourceDir [targetDir]" );
            System.exit( 0 );
        }
        rootDir = new File( args[0] );
        File targetDir = null;
        if( args.length >=2 ) {
            targetDirName = args[1];
            targetDir = new File( targetDirName );
            targetDir.mkdirs();
        }
        processDir( rootDir, targetDir );
    }

    private static void processDir( File dir, File targetDir ) {

        System.out.println( "Processing directory: " + dir.getAbsolutePath() );

        // Process all the jnlp files in the directory
        File[] jnlpFiles = dir.listFiles( new JnlpFileFilter() );
        for( int i = 0; i < jnlpFiles.length; i++ ) {
            System.out.println( "\t" + jnlpFiles[i].getName() );
            JnlpFile jnlpFile = new JnlpFile( jnlpFiles[i] );
            try {
                String codebase = null;
                String jnlpPath = null;
                if( targetDir != null ) {
                    codebase = targetDir.getAbsolutePath();
                }
                else {
                    codebase = jnlpFiles[i].getParent();
                }

                // Strip drive letter, if there is one
                if( codebase.indexOf( ":" ) != -1 ) {
                    codebase = codebase.substring( codebase.indexOf( ":" ) + 1 );
                }

                String appDir = dir.getPath().substring( dir.getPath().lastIndexOf( "\\" ));
                jnlpPath = codebase + appDir + "/" + jnlpFiles[i].getName();
                jnlpFile.setCodebase( "file://" + codebase );
                BufferedWriter out = new BufferedWriter( new FileWriter( jnlpPath ) );
                out.write( jnlpFile.toString() );
                out.close();
            }
            catch( IOException e ) {
            }
        }

        // Process all subdirectories
        File[] subDirs = dir.listFiles( new DirFileFilter() );
        for( int i = 0; i < subDirs.length; i++ ) {
            processDir( subDirs[i], targetDir );
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
