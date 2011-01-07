// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.test;

import java.io.File;

/**
 * TestFileRename test renaming a file, to verify that it works on Windows.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestFileRename {

    public static final void main( String[] args ) {
        if ( args.length != 2 ) {
            System.out.println( "usage: TestFileRename oldName newName" );
            System.exit( 0 );
        }
        String oldName = args[0];
        String newName = args[1];
        File oldFile = new File( oldName );
        File newFile = new File( newName );
        boolean success = oldFile.renameTo( newFile );
        if ( !success ) {
            System.err.println( "cannot rename " + oldName + " to " + newName );
        }
    }
}
