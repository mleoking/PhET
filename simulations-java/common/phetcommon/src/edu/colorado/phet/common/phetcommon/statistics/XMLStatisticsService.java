package edu.colorado.phet.common.phetcommon.statistics;

import java.io.*;
import java.net.*;

/**
 * Statistics service that uses PHP to deliver a statistics message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class XMLStatisticsService implements IStatisticsService {

    /**
     * Delivers a statistics message to PhET.
     *
     * @param message
     */
    public void postMessage( StatisticsMessage message ) throws IOException {
        postXML( getStatisticsURL(message ),getXML(message));
    }

    private String getXML( StatisticsMessage message ) {
        //todo: use DOM to create the XML string
        String xml="<tracking ";
        for (int i=0;i<message.getFieldCount();i++){
            xml+=message.getField( i ).getName()+"=\""+encode(message.getField( i ).getValue()+"\"")+" ";
        }
        xml+="/>";
        return xml;
    }

    private String encode( String value ) {
        //TODO: turn - into %2D as in flash?
        return value;
    }

    /*
     * The URL points to a PHP script, with name/value pairs appended to the URL.
     */
    private String getStatisticsURL( StatisticsMessage message ) {
        return "http://phet.colorado.edu/jolson/deploy/tracking/tracker.php";
    }

    public static void main( String[] args ) throws IOException {

        String URL_STRING = "http://phet.colorado.edu/jolson/deploy/tracking/tracker.php";
        String XML_STRING = "<xml>hello stats1234</xml>";
        postXML( URL_STRING, XML_STRING );

    }

    public static void postXML( String url, String xml ) throws IOException {
        // open connection
        URL urlObject = new URL( url );
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
        connection.setDoOutput( true );

        // post
        System.out.println( "posting to " + url + " ..." );
        OutputStreamWriter outStream = new OutputStreamWriter( connection.getOutputStream(), "UTF-8" );
        outStream.write( xml );
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