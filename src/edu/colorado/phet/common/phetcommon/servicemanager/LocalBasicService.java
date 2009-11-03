/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:17484 $
 * Date modified : $Date:2007-08-23 18:23:07 -0500 (Thu, 23 Aug 2007) $
 */
package edu.colorado.phet.common.phetcommon.servicemanager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.BasicService;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Provides local implementations of some of the JNLP services, see JNLP services for more information.
 * see also http://www.javaworld.com/javaworld/javatips/jw-javatip66.html
 *
 * @author Sam Reid
 * @version $Revision:17484 $
 */
public class LocalBasicService implements BasicService {

    public boolean showDocument( URL url ) {
        BrowserControl.displayURL( url.toExternalForm() );
        return true;
    }

    public URL getCodeBase() {
        File f = File.listRoots()[0];
        try {
            return f.toURL();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public boolean isOffline() {
        return true;
    }

    public boolean isWebBrowserSupported() {
        return true;
    }


    /**
     * A simple, static class to display a URL in the system browser.
     * <p/>
     * <p/>
     * <p/>
     * Under Unix, the system browser is hard-coded to be 'netscape'.
     * Netscape must be in your PATH for this to work.  This has been
     * tested with the following platforms: AIX, HP-UX and Solaris.
     * <p/>
     * <p/>
     * <p/>
     * Under Windows, this will bring up the default browser under windows,
     * usually either Netscape or Microsoft IE.  The default browser is
     * determined by the OS.  This has been tested under Windows 95/98/NT.
     * <p/>
     * <p/>
     * <p/>
     * Examples:
     * <p/>
     * <p/>
     * <p/>
     * BrowserControl.displayURL("http://www.javaworld.com")
     * <p/>
     * BrowserControl.displayURL("file://c:\\docs\\index.html")
     * <p/>
     * BrowserContorl.displayURL("file:///user/joe/index.html");
     * <p/>
     * <p/>
     * Note - you must include the url type -- either "http://" or
     * "file://".
     */
    public static class BrowserControl {
        /**
         * Display a file in the system browser.  If you want to display a
         * file, you must include the absolute path name.
         *
         * @param url the file's url (the url must start with either "http://"
         *            or
         *            "file://").
         */
        public static void displayURL( String url ) {
            String cmd = null;
            try {
                if ( PhetUtilities.getOperatingSystem() == PhetUtilities.OS_WINDOWS ) {
                    // cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
                    cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                    Process p = Runtime.getRuntime().exec( cmd );
                }
                else if ( PhetUtilities.isMacintosh() ) {
                    cmd = "open " + url;
                    Process p = Runtime.getRuntime().exec( cmd );
                }

                else {
                    launchBrowserOnLinux( url );
                }
            }
            catch( IOException x ) {
                // couldn't exec browser
                System.err.println( "Could not invoke browser, command=" + cmd );
                System.err.println( "Caught: " + x );
            }
            catch( Exception e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        /**
         * Simple example.
         */
        public static void main( String[] args ) {
            displayURL( "http://www.javaworld.com" );
        }

        // The default system browser under windows.
        private static final String WIN_PATH = "rundll32";
        // The flag to display a url.
        private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
        // The default browser under unix.
        private static final String UNIX_PATH = "netscape";
        // The flag to display a url.
        private static final String UNIX_FLAG = "-remote openURL";
    }

        //http://www.java2s.com/Code/Java/Development-Class/LaunchBrowserinMacLinuxUnix.htm
    public static void launchBrowserOnLinux( String url ) throws Exception, InterruptedException {
        String[] browsers = {
                "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "galeon", "iceweasel"};
        String browser = null;
        for ( int count = 0; count < browsers.length && browser == null; count++ ) {
            if ( Runtime.getRuntime().exec(
                    new String[]{"which", browsers[count]} ).waitFor() == 0 ) {
                browser = browsers[count];
            }
        }
        if ( browser == null ) {
            throw new Exception( "Could not find web browser" );
        }
        else {
            Runtime.getRuntime().exec( new String[]{browser, url} );
        }
    }

}
