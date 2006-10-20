/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.services;

import javax.jnlp.*;
import java.awt.*;

/**
 * Provides the functionality of ServiceManager for both JNLP and local runtimes.
 * <p/>
 * We are prevented from extending ServiceManager since it is static and final, this is the workaround.
 *
 * @author Sam Reid
 * @Revision : $Revision$
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
}
