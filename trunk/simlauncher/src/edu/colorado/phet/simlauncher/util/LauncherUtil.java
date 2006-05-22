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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * LauncherUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherUtil {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    static private LauncherUtil instance = new LauncherUtil();

    public static LauncherUtil getInstance() {
        return instance;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Boolean online = null;
    private URL url;// = DEFAULT_URL;

    private LauncherUtil() {
        try {
            //Todo: this technique for determining whether the simulation is online fails for certain url's: e.g. the main phet page url.
//            url=new URL("http://www.colorado.edu/physics/phet/web-pages/index.html" );

//            url=new URL("http://www.colorado.edu/physics/phet/dev/balloon/v1r0/balloons.jar" );
//            url=new URL("http://www.colorado.edu/physics/phet/dev/balloon/v1r0" );
            url = new URL( "http://www.colorado.edu/physics/phet/dev/phetlauncher/data/simulations.xml" );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        System.out.println( "url = " + url );
    }

//    public void setUrl( URL url ) {
//        this.url = url;
//    }

    public boolean isRemoteAvailable() {
        if( online == null ) {
            online = new Boolean( refreshOnline() );
        }
        return online.booleanValue();
    }

    private boolean refreshOnline() {
        try {
            System.out.println( "url = " + url );
            URLConnection urlConnection = url.openConnection();
            System.out.println( "urlConnection = " + urlConnection );
            long lastModified = urlConnection.getLastModified();
            System.out.println( "lastModified = " + lastModified );
            return lastModified != 0;
        }
        catch( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }

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
