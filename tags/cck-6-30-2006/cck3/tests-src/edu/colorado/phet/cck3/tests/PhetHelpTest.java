/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import org.srr.localjnlp.ServiceSource;

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
public class PhetHelpTest {
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

    public static void main( String[] args ) throws MalformedURLException {
        URL url = new URL( "http://www.google.com" );
        boolean ok = showURL( url );
        System.out.println( "ok = " + ok );
//        JApplet applet=new JApplet();
//        applet.setVisible( true );
//        applet.getAppletContext().showDocument(url );
//        BrowserControl.displayURL( url.toExternalForm() );
        ServiceSource ss = new ServiceSource();
        BasicService bs = ss.getBasicService();
        bs.showDocument( url );
    }
}
