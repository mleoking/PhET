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

import edu.colorado.phet.common.util.PhetUtilities;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import net.sf.wraplog.SystemLogger;

import java.io.IOException;
import java.util.List;

/**
 * HtmlViewer
 * <p>
 * Launches a web browser on a local file. This is specifically intended for launching Flash
 * simulations that have been installed on the local computer
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HtmlViewer {

    private static boolean DEBUG;
    private Process process;

    public void view( String urlString ) {

        if( PhetUtilities.isMacintosh() ) {
            // Strip the URL portion from the urlString, so we have a local reference to
            // the file
            String urlHeader = "://";
            String fileName = urlString.substring( urlString.indexOf(urlHeader) + urlHeader.length() );
            String[]commands = new String[]{"open", "-a", "/Applications/Safari.app", fileName };
            for( int i = 0; i < commands.length; i++ ) {
                System.out.println( "commands[i] = " + commands[i] );
            }
            try {
                process = Runtime.getRuntime().exec( commands );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            // Get the input stream and read from it
            new Thread( new LauncherUtil.OutputRedirection( process.getInputStream() ) ).start();
        }
        else {
            try {
                BrowserLauncher browserLauncher = new BrowserLauncher( new SystemLogger() );
                List list = browserLauncher.getBrowserList();
                if( DEBUG ) {
                    System.out.println( "list = " + list );
                }
                if( list.size() > 1 ) {
                    browserLauncher.openURLinBrowser( list.get( 1 ).toString(), urlString );
                }
            }
            catch( BrowserLaunchingInitializingException e ) {
                e.printStackTrace();
            }
            catch( UnsupportedOperatingSystemException e ) {
                e.printStackTrace();
            }
            catch( BrowserLaunchingExecutionException e ) {
                e.printStackTrace();
            }
        }
    }
}
