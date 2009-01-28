package edu.colorado.phet.common.phetcommon.statistics;

import java.io.*;
import java.net.*;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

/**
 * Statistics service that uses PHP to deliver a statistics message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class XMLStatisticsService implements IStatisticsService {

    private static final String STATISTICS_PATH = "tracking";//todo; this should be XML acceptor
    private static final String STATISTICS_SCRIPT = "phet-tracking.php";//todo; this should be XML acceptor

    public XMLStatisticsService() {
    }

    /**
     * Delivers a statistics message to PhET.
     *
     * @param message
     */
    public void postMessage( StatisticsMessage message ) throws IOException {
        try {
            new URL( getStatisticsURL( message ) ).openStream().close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    /*
     * The URL points to a PHP script, with name/value pairs appended to the URL.
     */
    private static String getStatisticsURL( StatisticsMessage message ) {
        return PhetCommonConstants.PHET_HOME_URL + "/" + STATISTICS_PATH + "/" + STATISTICS_SCRIPT + "?" + toPHP( message );
    }

    /*
     * Converts a statistics message to something that PHP can parse.
     */
    private static String toPHP( StatisticsMessage message ) {
        String php = "";
        for ( int i = 0; i < message.getFieldCount(); i++ ) {
            if ( i > 0 ) {
                php += "&";
            }
            php += toPHP( message.getField( i ) );
        }
        return php;
    }

    /*
     * Converts a statistics message field to something that PHP can parse.
     */
    private static String toPHP( StatisticsMessageField field ) {
        return field.getName() + "=" + valueToPHP( field.getValue() );
    }

    private static String valueToPHP( String value ) {
        if ( value == null ) {
            return "null";
        }
        else {
            //See also
            // http://forums.digitalpoint.com/showthread.php?s=1e314cbd77a6b11d904f186a60d388af&t=13647
            // regarding space characters for php
            String str = value.replaceAll( " ", "%20" );
//            str = str.replace( ".", "&#46;" );
            return str;
        }
    }

//    public static void main( String[] args ) throws IOException {
//        String message = "<tracking message_type=\"session\" message_version=\"123\" user_preference_file_creation_time=\"1232324743056\" user_total_sessions=\"65\" sim_type=\"flash\" sim_project=\"pendulum%2Dlab\" sim_name=\"pendulum%2Dlab\" sim_major_version=\"1\" sim_minor_version=\"00\" sim_dev_version=\"01\" sim_svn_revision=\"22386\" sim_locale_language=\"en\" sim_locale_country=\"none\" sim_sessions_since=\"1\" sim_sessions_ever=\"62\" sim_deployment=\"phet%2Dwebsite\" sim_distribution_tag=\"none\" sim_dev=\"true\" host_flash_os=\"Linux%202%2E6%2E20%2D17%2Dgeneric\" host_flash_version=\"LNX%209%2C0%2C124%2C0\" host_locale_language=\"en\" host_flash_time_offset=\"420\" host_flash_accessibility=\"false\" host_flash_domain=\"localhost\" />";
//
//        String dest = "http://phet.colorado.edu/jolson/deploy/tracking/tracker.php";
//
//        URL serverAddress = new URL( dest );
//        //set up out communications stuff
//
//        //Set up the initial connection
//        HttpURLConnection c = (HttpURLConnection) serverAddress.openConnection();
//
//        c.setRequestMethod( "POST" );
//        c.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
//        c.setDoOutput( true );
//        OutputStreamWriter out = new OutputStreamWriter( c.getOutputStream(), "UTF8" );
//        out.write( message );
//        out.close();
//    }

    static URL u;

    public static void main( String args[] ) {

        try {
            u = new URL( "http://phet.colorado.edu/jolson/deploy/tracking/tracker.php" );

            // Open the connection and prepare to POST
            URLConnection uc = u.openConnection();
            uc.setDoOutput( true );
            uc.setDoInput( true );
            uc.setAllowUserInteraction( false );

            DataOutputStream dstream = new DataOutputStream( uc.getOutputStream() );

            // The POST line
            dstream.writeBytes( "<?xml version='1.0'?> \n" +
                                      "<document>\n" +
                                      " <title>Forty What?</title>\n" +
                                      " <from>Joe</from>\n" +
                                      " <to>Jane</to>\n" +
                                      " <body>\n" +
                                      "  I know that's the answer -- but what's the question?\n" +
                                      " </body>\n" +
                                      "</document>" );
            dstream.close();

            // Read Response
            InputStream in = uc.getInputStream();
            int x;
            while ( ( x = in.read() ) != -1 ) {
                System.out.write( x );
            }
            in.close();

            BufferedReader r = new BufferedReader( new InputStreamReader( in ) );
            StringBuffer buf = new StringBuffer();
            String line;
            while ( ( line = r.readLine() ) != null ) {
                buf.append( line );
            }

        }
        catch( IOException e ) {
            e.printStackTrace();    // should do real exception handling
        }
    }


}