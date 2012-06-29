// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.slideshow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Download all the jars for a dev sim in order to visualize how it has evolved.
 *
 * @author Sam Reid
 */
public class GenerateSlideshow {
    public static void main( String[] args ) throws IOException {
        String string = loadString();
        Pattern regexes = Pattern.compile( "\\d\\.\\d+.\\d+" );
        Matcher matcher = regexes.matcher( string );
        ArrayList<String> m = new ArrayList<String>();
        while ( matcher.find() ) {
            String result = matcher.group();
            m.add( result );
        }
        Collections.sort( m, new Comparator<String>() {
            public int compare( final String o1, final String o2 ) {
                return Double.compare( suffix( o1 ), suffix( o2 ) );
            }
        } );
        for ( String s : m ) {
            System.out.println( s );
        }

        //download jars
        for ( String s : m ) {
            URL url = new URL( "http://www.colorado.edu/physics/phet/dev/fractions/" + s + "/fractions_all.jar" );
            download( url, s );
        }

    }

    private static void download( final URL url, String version ) throws IOException {

        InputStream reader = url.openStream();

        /*
        * Setup a buffered file writer to write
        * out what we read from the website.
        */
        final String fileName = "C:/Users/Sam/Desktop/fractions-timeline/" + version;
        File file = new File( fileName );
        file.getParentFile().mkdirs();
        file.mkdirs();
        FileOutputStream writer = new FileOutputStream( new File( file, "fractions_all.jar" ) );
        byte[] buffer = new byte[153600];
        int bytesRead;

        while ( ( bytesRead = reader.read( buffer ) ) > 0 ) {
            writer.write( buffer, 0, bytesRead );
            buffer = new byte[153600];
        }

        writer.close();
        reader.close();
    }

    private static double suffix( final String o1 ) {
        int lastdot = o1.lastIndexOf( '.' );
        return Integer.parseInt( o1.substring( lastdot + 1 ) );
    }

    private static String loadString() throws IOException {
        URL url = new URL( "http://www.colorado.edu/physics/phet/dev/fractions/" );
        InputStream stream = url.openStream();
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( stream ) );
        String line = bufferedReader.readLine();
        StringBuffer total = new StringBuffer( line );
        while ( line != null ) {
            System.out.println( line );
            line = bufferedReader.readLine();
            total.append( line );
        }
        return total.toString();
    }
}