/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
            while( ( c = inStream.read() ) >= 0 ) {
                outStream.write( c );
            }
            outStream.flush();
        }
        finally {
            inStream.close();
        }

        return new String( outStream.toByteArray(), encoding );
    }


    public static void filter( File inputFile, File outputFile, HashMap map ) throws IOException {
        filter( inputFile, outputFile, map, DEFAULT_ENCODING );
    }

    public static void writeString( File outputFile, String text, String encoding ) throws IOException {
        writeBytes( outputFile, text.getBytes( encoding ) );
    }

    public static void writeString( File outputFile, String text ) throws IOException {
        writeString( outputFile, text, DEFAULT_ENCODING );
    }

    public static void writeBytes( File outputFile, byte[] bytes ) throws IOException {
        FileOutputStream outputStream = new FileOutputStream( outputFile );
        try {
            outputStream.write( bytes );
        }
        finally {
            outputStream.close();
        }
    }

    public static String filter( HashMap map, String file ) {
        Set set = map.keySet();
        for( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            String key = (String)iterator.next();
            String value = (String)map.get( key );

            //echo( "key = " + key + ", value=" + value );

            file = file.replaceAll( "@" + key + "@", value );
        }
        return file;
    }

    public static void filter( File source, File destFile, HashMap filterMap, String encoding ) throws IOException {
        String text = loadFileAsString( source, encoding );
        String result = filter( filterMap, text );
        writeString( destFile, result, encoding );
    }
}
