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
        String xml="<tracking ";
        for (int i=0;i<message.getFieldCount();i++){
            xml+=message.getField( i ).getName()+"=\""+encode(message.getField( i ).getValue()+"\"")+" ";
        }
        xml+="/>";
//        return "<tracking message_type=\"session\" message_version=\"1\" user_preference_file_creation_time=\"111122223333\" user_total_sessions=\"65\" sim_type=\"flash\" sim_project=\"pendulum%2Dlab\" sim_name=\"pendulum%2Dlab\" sim_major_version=\"1\" sim_minor_version=\"00\" sim_dev_version=\"01\" sim_svn_revision=\"22386\" sim_locale_language=\"en\" sim_locale_country=\"none\" sim_sessions_since=\"1\" sim_sessions_ever=\"62\" sim_deployment=\"phet%2Dwebsite\" sim_distribution_tag=\"none\" sim_dev=\"true\" host_flash_os=\"Linux%202%2E6%2E20%2D17%2Dgeneric\" host_flash_version=\"LNX%209%2C0%2C124%2C0\" host_locale_language=\"en\" host_flash_time_offset=\"420\" host_flash_accessibility=\"false\" host_flash_domain=\"localhost\" />";
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