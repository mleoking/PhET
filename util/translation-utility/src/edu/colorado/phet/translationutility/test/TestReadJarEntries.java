// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * TestReadJarEntries reads all the entries in a JAR file.
 * Notice that the file separator is '/' regardless of platform.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestReadJarEntries {

    public static final void main( String[] args ) {
        
        if ( args.length != 1 ) {
            System.out.println( "usage: TestReadJarEntries file.jar" );
            System.exit( 1 );
        }
        
        try {
            String jarFileName = args[0];
            InputStream inputStream = new FileInputStream( jarFileName );
            JarInputStream jarInputStream = new JarInputStream( inputStream );
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                System.out.println( jarEntry.getName() );
                jarEntry = jarInputStream.getNextJarEntry();
            }
            jarInputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            System.exit( 1 );   
        }
    }
}
