package edu.colorado.phet.common.phetcommon.statistics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

/**
 * Statistics service that uses PHP to deliver a statistics message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPStatisticsService implements IStatisticsService {
    
    private static final String STATISTICS_PATH = "tracking";
    private static final String STATISTICS_SCRIPT = "phet-tracking.php";

    public PHPStatisticsService() {}

    /**
     * Delivers a statistics message to PhET.
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

}
