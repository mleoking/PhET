package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

/**
 * Tracking service that uses PHP to deliver a tracking message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPTrackingService implements ITrackingService {
    
    private static final String TRACKING_PATH = "tracking";
    private static final String TRACKING_SCRIPT = "phet-tracking.php";

    public PHPTrackingService() {}
    
    /**
     * Delivers a tracking message to PhET.
     * @param message
     */
    public void postMessage( TrackingMessage message ) throws IOException {
        try {
            new URL( getTrackingURL( message ) ).openStream().close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    /*
     * The URL points to a PHP script, with name/value pairs appended to the URL.
     */
    private static String getTrackingURL( TrackingMessage message ) {
        return PhetCommonConstants.PHET_HOME_URL + "/" + TRACKING_PATH + "/" + TRACKING_SCRIPT + "?" + toPHP( message );
    }
    
    /*
     * Converts a tracking message to something that PHP can parse.
     */
    private static String toPHP( TrackingMessage message ) {
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
     * Converts a tracking message field to something that PHP can parse.
     */
    private static String toPHP( TrackingMessageField field ) {
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
