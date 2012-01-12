// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.test;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * TestJarInsert tests insertion of a file into a JAR.
 * This fails on Windows at the point where we try to rename the tmp file (tmpFile.renameTo).
 * No idea why...
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestJarInsert {

    public static final void main( String[] args ) throws IOException {

        if ( args.length != 2 ) {
            System.out.println( "usage: TestJarInsert jarFile insertFile" );
            System.exit( 1 );
        }
        String jarFileName = args[0];
        String insertFileName = args[1];

        // the file to insert
        File insertFile = new File( insertFileName );
        InputStream insertStream = new FileInputStream( insertFile );

        // the original JAR file
        File jarFile = new File( jarFileName );
        JarInputStream jarStream = new JarInputStream( new FileInputStream( jarFile ) );
        Manifest manifest = jarStream.getManifest();
        if ( manifest == null ) {
            System.out.println( "JAR file is missing manifest" );
            System.exit( 1 );
        }

        // output goes to a temp JAR file
        String tmpFileName = jarFileName + ".tmp";
        File tmpFile = new File( tmpFileName );
        JarOutputStream tmpStream = new JarOutputStream( new FileOutputStream( tmpFile ), manifest );

        byte[] buffer = new byte[1024];
        int bytesRead;
        
        // copy all entries from input to output, skipping the insert file if it already exists
        JarEntry jarEntry = jarStream.getNextJarEntry();
        while ( jarEntry != null ) {
            if ( !jarEntry.getName().equals( insertFileName ) ) {
                tmpStream.putNextEntry( jarEntry );
                while ( ( bytesRead = jarStream.read( buffer ) ) > 0 ) {
                    tmpStream.write( buffer, 0, bytesRead );
                }
            }
            jarEntry = jarStream.getNextJarEntry();
        }

        // add properties file to output
        jarEntry = new JarEntry( insertFileName );
        tmpStream.putNextEntry( jarEntry );
        while ( ( bytesRead = insertStream.read( buffer ) ) != -1 ) {
            tmpStream.write( buffer, 0, bytesRead );
        }

        // close the streams
        insertStream.close();
        jarStream.close();
        tmpStream.close();

        // if everything went OK, move tmp file to JAR file
        boolean renameSuccess = tmpFile.renameTo( jarFile );
        if ( !renameSuccess ) {
            System.err.println( "failed to rename " + tmpFileName + " to " + jarFileName );
            System.exit( 1 );
        }
    }
}
