// Copyright 2002-2013, University of Colorado

package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.ScpTo;

import com.jcraft.jsch.JSchException;

public class TestFileTransfer {

    private static final int NUM_FILES = 25;

    /**
     * Program to test uploading of multiple file to figaro.colorado.edu,
     * created as a quick way to duplicate the root issue of #3621.
     *
     * @param args
     */
    public static void main( final String[] args ) throws IOException, JSchException {

        if ( args.length != 2 ) {
            System.out.println( "usage: TestFileTransfer <username> <password>" );
        }
        for ( int i = 0; i < NUM_FILES; i++ ) {
            String fileName = "C:/temp/file-upload-test/file-" + i + ".txt";
            File fileToUpload = new File( fileName );

            ScpTo.uploadFile( fileToUpload, args[0], "figaro.colorado.edu", "/data2/file-upload-test", args[1] );
            System.out.println( "File " + fileName );
        }
    }
}