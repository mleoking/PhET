package edu.colorado.phet.website.cache;

import java.util.Date;

import org.apache.log4j.Logger;

public class InstallerCache {
    private static long t;

    private static final Logger logger = Logger.getLogger( InstallerCache.class.getName() );

    /**
     * @param timestamp Seconds since epoch
     */
    public static synchronized void setTimestamp( long timestamp ) {
        t = timestamp;
    }

    public static synchronized Date getDate() {
        return new Date( t * 1000 );
    }

    public static synchronized int getTimestamp() {
        return (int) t;
    }

    public static synchronized void setDefault() {
        setTimestamp( ( new Date() ).getTime() / 1000 );
        logger.warn( "Setting default installer timestamp" );
    }
}
