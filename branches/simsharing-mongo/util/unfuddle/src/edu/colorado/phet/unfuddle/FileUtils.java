/* Copyright 2007, University of Colorado */
package edu.colorado.phet.unfuddle;

import java.io.*;

/**
 * This file was copied from phet-build (relevant parts only).
 */
public class FileUtils {
    private static String DEFAULT_ENCODING = "utf-8";

    public static String loadFileAsString( File file ) throws IOException {
        return loadFileAsString( file, DEFAULT_ENCODING );
    }

    public static String loadFileAsString( File file, String encoding ) throws IOException {
        InputStream inStream = new FileInputStream( file );

        ByteArrayOutputStream outStream;

        try {
            outStream = new ByteArrayOutputStream();

            int c;
            while ( ( c = inStream.read() ) >= 0 ) {
                outStream.write( c );
            }
            outStream.flush();
        }
        finally {
            inStream.close();
        }

        return new String( outStream.toByteArray(), encoding );
    }


    public static void writeString( File outputFile, String text, String encoding ) throws IOException {
        writeBytes( outputFile, text.getBytes( encoding ) );
    }

    public static void writeString( File outputFile, String text ) throws IOException {
        writeString( outputFile, text, DEFAULT_ENCODING );
    }

    public static void writeBytes( File outputFile, byte[] bytes ) throws IOException {
        outputFile.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream( outputFile );
        try {
            outputStream.write( bytes );
        }
        finally {
            outputStream.close();
        }
    }

    public static void delete( File file, boolean verbose ) {
        if ( file.isDirectory() ) {
            File[] children = file.listFiles();
            for ( File child : children ) {
                delete( child, verbose );
            }
        }
        if ( verbose ) {
            System.out.println( "Deleting: " + file.getAbsolutePath() );
        }
        file.delete();
    }

}
