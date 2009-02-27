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

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.*;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Provides the functionality of ServiceManager for both JNLP and local runtimes.
 * <p/>
 * We are prevented from extending ServiceManager since it is static and final, this is the workaround.
 *
 * @author Sam Reid
 * @Revision : $Revision:17484 $
 */
public class PhetServiceManager {
    /**
     * Determine whether the application is running under java web start.
     *
     * @return true if the application is running under java web start.
     */
    public static boolean isJavaWebStart() {
        return System.getProperty( "javawebstart.version" ) != null;
    }
    
    /**
     * Gets the JNLP code base. 
     * Returns null if we don't have a JNLP code base.
     */
    public static URL getCodeBase() {
        URL codeBase = null;
        if ( isJavaWebStart() ) {
            try {
                codeBase = getBasicService().getCodeBase();
            }
            catch ( UnavailableServiceException e ) {
                // this shouldn't happen. if we're running from JWS, we must have a codebase.
                e.printStackTrace();
            }
        }
        return codeBase;
    }

    public static BasicService getBasicService() throws UnavailableServiceException {
        if ( isJavaWebStart() ) {
            return (BasicService) ServiceManager.lookup( BasicService.class.getName() );
        }
        else {
            return new LocalBasicService();
        }
    }

    public static FileOpenService getFileOpenService( Component owner ) throws UnavailableServiceException {
        if ( isJavaWebStart() ) {
            return (FileOpenService) ServiceManager.lookup( "javax.jnlp.FileOpenService" );
        }
        else {
            return new LocalFileOpenService( owner );
        }
    }

    public static FileSaveService getFileSaveService( Component owner ) throws UnavailableServiceException {
        if ( isJavaWebStart() ) {
            return (FileSaveService) ServiceManager.lookup( "javax.jnlp.FileSaveService" );
        }
        return new LocalFileSaveService( owner );
    }

    /**
     * Opens a browser window to show the PHET homepage.
     */
    public static void showPhetPage() {
        showWebPage( PhetCommonConstants.PHET_HOME_URL );
    }
    
    /**
     * Opens a browser to show a sim's web page.
     * 
     * @param project
     * @param sim
     */
    public static void showSimPage( String project, String sim ) {
        showWebPage( HTMLUtils.getSimURL( project, sim, "&" ) );
    }
    
    public static void showWebPage( String url ) {
        try {
            showWebPage( new URL( url ) );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }
    
    public static void showWebPage( URL url ) {
        try {
            PhetServiceManager.getBasicService().showDocument( url );
        }
        catch( UnavailableServiceException e ) {
            e.printStackTrace();
        }
    }
}
