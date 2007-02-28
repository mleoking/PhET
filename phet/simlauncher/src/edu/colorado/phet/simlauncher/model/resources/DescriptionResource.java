/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model.resources;

import edu.colorado.phet.simlauncher.model.PhetSiteConnection;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * DescriptionResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DescriptionResource extends SimResource {

    public DescriptionResource( URL url, File localRoot ) {
        super( url, localRoot );

        // If we're online and the local copy isn't current, go get
        try {
            if( PhetSiteConnection.instance().isConnected() && !isCurrent() ) {
                download();
            }
        }
        catch( SimResourceException e ) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader( new FileReader( getLocalFile() ) );
            String str;
            while( ( str = in.readLine() ) != null ) {
                sb.append( str );
            }
            in.close();
        }
        catch( IOException e ) {
        }
        return sb.toString();
    }

    public void download() throws SimResourceException {
        super.download();
        if( true ) return;

        StringBuffer sbIn = new StringBuffer();
        try {
            // Create a URL for the desired page

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader( new FileReader( getLocalFile() ) );
//            BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
            String str;
            while( ( str = in.readLine() ) != null ) {
                // str is one line of text; readLine() strips the newline character(s)
                sbIn.append( str );
            }
            in.close();
        }
        catch( MalformedURLException e ) {
        }
        catch( IOException e ) {
        }

        // Strip the HTML out
        StringBuffer sbOut = new StringBuffer();
        int stop = 0;
        for( int start = sbIn.indexOf( ">" ) + 1;
             start < sbIn.length() && start > -1;
             start = sbIn.indexOf( ">", stop ) + 1 ) {
            stop = sbIn.indexOf( "<", start );
            if( stop > start ) {
                sbOut.append( sbIn.substring( start, stop ) );
            }
        }
        System.out.println( "sbOut = " + sbOut );

        // Write the local file
        try {
            BufferedWriter out = new BufferedWriter( new FileWriter( getLocalFile() ) );
            out.write( sbOut.toString() );
            out.close();
        }
        catch( IOException e ) {
            System.out.println( "e = " + e );
        }
    }
}
