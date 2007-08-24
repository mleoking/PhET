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

import javax.jnlp.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

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

    public static BasicService getBasicService() throws UnavailableServiceException {
        if( isJavaWebStart() ) {
            return (BasicService)ServiceManager.lookup( BasicService.class.getName() );
        }
        else {
            return new LocalBasicService();
        }
    }

    public static FileOpenService getFileOpenService( Component owner ) throws UnavailableServiceException {
        if( isJavaWebStart() ) {
            return (FileOpenService)ServiceManager.lookup( "javax.jnlp.FileOpenService" );
        }
        else {
            return new LocalFileOpenService( owner );
        }
    }

    public static FileSaveService getFileSaveService( Component owner ) throws UnavailableServiceException {
        if( isJavaWebStart() ) {
            return (FileSaveService)ServiceManager.lookup( "javax.jnlp.FileSaveService" );
        }
        return new LocalFileSaveService( owner );
    }

    /**
     * Opens a browser window to show the phet homepage.
     */
    public static void showPhetPage() {
        try {
            PhetServiceManager.getBasicService().showDocument( new URL( "http://phet.colorado.edu" ) );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( UnavailableServiceException e ) {
            e.printStackTrace();
        }
    }
}
