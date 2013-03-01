// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class TestHttpPost {

    private static final String URL_STRING = "http://phet.colorado.edu/jolson/deploy/tracking/tracker.php";
    private static final String XML_STRING = "<xml>hello stats</xml>";

    public static void main( String[] args ) throws IOException {

        // open connection
        URL url = new URL( URL_STRING );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
        connection.setDoOutput( true );

        // post
        System.out.println( "posting to " + URL_STRING + " ..." );
        OutputStreamWriter outStream = new OutputStreamWriter( connection.getOutputStream(), "UTF-8" );
        outStream.write( XML_STRING );
        outStream.close();

        // Get the response
        BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
        System.out.println( "reading response ..." );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            System.out.println( line );
        }
        reader.close();
        System.out.println( "done." );
    }
}
