/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.psl;

import javax.jnlp.*;
import java.net.URL;
import java.net.MalformedURLException;

public class CacheTracker {
    public static void main( String[] args ) {
        DownloadService ds = null;

        System.out.println( "Starting" );
        try {
            URL url = new URL( "http://www.colorado.edu/physics/phet/dev/idealgas/v3r19/idealgas.jar" );
//            boolean b = showURL( url );
//            System.out.println( "b = " + b );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }


        try {
            ds = (DownloadService)ServiceManager.lookup( "javax.jnlp.DownloadService" );
        }
        catch( Exception e ) {
            ds = null;
        }

        System.out.println( "BBB" );

        if( ds != null ) {

            System.out.println( "CCC" );

            try {
                // determine if a particular resource is cached
                URL url = new URL( "http://www.colorado.edu/physics/phet/dev/idealgas/v3r19/idealgas.jar" );
                boolean cached = false;
                cached = ds.isResourceCached( url, null );
                System.out.println( url.toString() + " cached = " + cached );

                // if it isn't in the cache, download it
                if( !cached ) {
                    System.out.println( "Downloading: " + url.toString() );
                    // reload the resource into the cache
                    DownloadServiceListener dsl = ds.getDefaultProgressWindow();
                    ds.loadResource( url, null, dsl );
                }
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }


    // Method to show a URL
    static boolean showURL( URL url ) {
        try {
            // Lookup the javax.jnlp.BasicService object
            BasicService bs = (BasicService)ServiceManager.lookup( "javax.jnlp.BasicService" );
            // Invoke the showDocument method
            return bs.showDocument( url );
        }
        catch( UnavailableServiceException ue ) {
            // Service is not supported
            return false;
        }
    }
}
