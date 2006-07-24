/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * LauncherUtil
 * <p/>
 * Provides some utilities specific to the SimLauncher application
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherUtil {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    static private LauncherUtil instance = new LauncherUtil();

    public static LauncherUtil instance() {
        return instance;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Boolean online = null;

    /**
     * Private constructor to insure singleton
     */
    private LauncherUtil() {
    }

    public boolean isRemoteAvailable( URL url ) {
        if( online == null ) {
            online = new Boolean( refreshOnline( url ) );
        }
        return online.booleanValue();
    }

    private boolean refreshOnline( URL url ) {
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            long lastModified = urlConnection.getLastModified();
            return lastModified != 0;
        }
        catch( IOException e ) {
            return false;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    static public class OutputRedirection implements Runnable {
        InputStream in;

        public OutputRedirection( InputStream in ) {
            this.in = in;
        }

        public void run() {
            int c;
            try {
                while( ( c = in.read() ) != -1 ) {
                    System.out.write( c );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            try {
                in.close();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}