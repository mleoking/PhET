/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

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
        outputFile.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream( outputFile );
        try {
            outputStream.write( bytes );
        }
        finally {
            outputStream.close();
        }
    }

    private static String replaceAll(String body, String find, String replacement) {
        boolean changed;

        do {
            changed = false;

            int indexOfFindText = body.indexOf(find);

            if (indexOfFindText != -1) {
                changed = true;

                String before = body.substring(0, indexOfFindText);
                String after  = body.substring(indexOfFindText + find.length());

                body = before + replacement + after;
            }

        }
        while (changed);

        return body;
    }

    public static String filter( HashMap map, String file ) {
        Set set = map.keySet();
        for( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            String key = (String)iterator.next();
            String value = (String)map.get( key );

            //echo( "key = " + key + ", value=" + value );

            file = replaceAll(file, "@" + key + "@", value );
        }
        return file;
    }

    public static void filter( File source, File destFile, HashMap filterMap, String encoding ) throws IOException {
        String text = loadFileAsString( source, encoding );
        String result = filter( filterMap, text );
        writeString( destFile, result, encoding );
    }

    public static void delete( File file ) {
        if ( file.isDirectory() ) {
            File[] children = file.listFiles();

            for ( int i = 0; i < children.length; i++ ) {
                delete( children[i] );
            }
        }

        file.delete();
    }

    public static void copyTo( File source, File dest ) throws IOException {
        InputStream in = new BufferedInputStream( new FileInputStream( source ) );

        try {
            OutputStream out = new BufferedOutputStream( new FileOutputStream( dest ) );

            try {
                int bytesRead;

                byte[] buffer = new byte[1024];

                while ( ( bytesRead = in.read( buffer) ) >= 0 ) {
                    out.write( buffer, 0, bytesRead );
                }
            }
            finally {
                out.close();
            }

        }
        finally {
            in.close();
        }
    }
}
