/** Sam Reid*/
package edu.colorado.phet.common.tests.utils;

import edu.colorado.phet.common.util.services.PhetServiceManager;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Jun 22, 2004
 * Time: 12:53:20 AM
 * Copyright (c) Jun 22, 2004 by Sam Reid
 */
public class TestPhetServiceManager {
    public static boolean showURL( URL url ) {
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

    public static void main( String[] args ) throws MalformedURLException, UnavailableServiceException {
        URL url = new URL( "http://www.google.com" );
        boolean ok = showURL( url );
        System.out.println( "ok = " + ok );
        PhetServiceManager.getBasicService().showDocument( url );
    }
}
