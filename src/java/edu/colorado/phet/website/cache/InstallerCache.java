package edu.colorado.phet.website.cache;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;

public class InstallerCache {
    private static long t;

    private static long winSize;
    private static long macSize;
    private static long linuxSize;
    private static long cdSize;

    private static final Logger logger = Logger.getLogger( InstallerCache.class.getName() );

    /*---------------------------------------------------------------------------*
    * getters
    *----------------------------------------------------------------------------*/

    public static synchronized Date getDate() {
        return new Date( t * 1000 );
    }

    public static synchronized int getTimestampSeconds() {
        return (int) t;
    }

    public static synchronized long getTimestampMilliseconds() {
        return ( (long) t ) * 1000;
    }

    public static synchronized long getWinSize() {
        return winSize;
    }

    public static synchronized long getMacSize() {
        return macSize;
    }

    public static synchronized long getLinuxSize() {
        return linuxSize;
    }

    public static synchronized long getCdSize() {
        return cdSize;
    }

    /*---------------------------------------------------------------------------*
    * setters
    *----------------------------------------------------------------------------*/

    /**
     * @param timestamp Seconds since epoch
     */
    public static synchronized void updateTimestamp( long timestamp ) {
        t = timestamp;
        updateFilesizes();
    }

    public static synchronized void setDefault() {
        updateTimestamp( ( new Date() ).getTime() / 1000 );
        logger.warn( "Setting default installer timestamp" );
        updateFilesizes();
    }

    /*---------------------------------------------------------------------------*
    * implementation
    *----------------------------------------------------------------------------*/

    private static void updateFilesizes() {
        File docRoot = PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot();
        File winFile = new File( docRoot, FullInstallPanel.WINDOWS_INSTALLER_LOCATION.substring( 1 ) );
        File macFile = new File( docRoot, FullInstallPanel.MAC_INSTALLER_LOCATION.substring( 1 ) );
        File linuxFile = new File( docRoot, FullInstallPanel.LINUX_INSTALLER_LOCATION.substring( 1 ) );
        File cdFile = new File( docRoot, FullInstallPanel.CD_INSTALLER_LOCATION.substring( 1 ) );

        winSize = winFile.length();
        macSize = macFile.length();
        linuxSize = linuxFile.length();
        cdSize = cdFile.length();
    }
}
