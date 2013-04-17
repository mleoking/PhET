// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildtools.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * List all images in a directory in alphabetical order to help with license.txt creation.
 *
 * @author Sam Reid
 */
public class ListImages {
    public static void main( String[] args ) {
        File parent = new File( args[0] );
        ArrayList<File> f = new ArrayList<File>( Arrays.asList( parent.listFiles() ) );
        Collections.sort( f );
        for ( File file : f ) {
            if ( !file.isDirectory() && !file.getName().equals( "license.txt" ) ) {
                System.out.println( file.getName() + " source=PhET notes=" );
            }
        }
    }
}