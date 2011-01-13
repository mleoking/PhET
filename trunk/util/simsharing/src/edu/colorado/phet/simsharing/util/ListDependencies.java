// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.util;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class ListDependencies {
    public static void main( String[] args ) {
        File root = new File( args[0] );
        ArrayList<File> all = visit( root );
        String s = "";
        for ( File file : all ) {
            if ( file.getName().toLowerCase().endsWith( ".jar" ) &&
                 !file.getName().toLowerCase().endsWith( "-docs.jar" ) &&
                 !file.getName().toLowerCase().endsWith( "-sources.jar" ) ) {
                s = s + " : " + file.getAbsolutePath().substring( root.getAbsoluteFile().toString().length() + 1 ).replace( '\\', '/' );
            }
        }
        System.out.println( s );
    }

    private static ArrayList<File> visit( File root ) {
        ArrayList<File> all = new ArrayList<File>();
        for ( File file : root.listFiles() ) {
            if ( file.isDirectory() ) {
                all.addAll( visit( file ) );
            }
            else {
                all.add( file );
            }
        }
        return all;
    }
}
