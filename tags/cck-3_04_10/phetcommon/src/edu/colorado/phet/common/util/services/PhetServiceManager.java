package edu.colorado.phet.common.util.services;

import edu.colorado.phet.common.util.services.LocalBasicService;
import edu.colorado.phet.common.util.services.LocalFileOpenService;
import edu.colorado.phet.common.util.services.LocalFileSaveService;

import javax.jnlp.*;
import java.awt.*;

/**
 * Provides the functionality of ServiceManager for both JNLP and local runtimes.
 * <p/>
 * We are prevented from extending ServiceManager since it is static and final, this is the workaround.
 */
public class PhetServiceManager {
    public static BasicService getBasicService() {
        try {
            return (BasicService)ServiceManager.lookup( BasicService.class.getName() );
        }
        catch( UnavailableServiceException e ) {
            return new LocalBasicService();
        }
    }

    public static FileOpenService getFileOpenService( Component owner ) {
        try {
            FileOpenService fos = (FileOpenService)ServiceManager.lookup( "javax.jnlp.FileOpenService" );
            if( fos == null ) {
                throw new UnavailableServiceException( "temp" );
            }
            return fos;
        }
        catch( UnavailableServiceException e ) {
            return new LocalFileOpenService( owner );
        }
    }

    public static FileSaveService getFileSaveService( Component owner ) {
        try {
            FileSaveService fos = (FileSaveService)ServiceManager.lookup( "javax.jnlp.FileSaveService" );
            if( fos == null ) {
                throw new UnavailableServiceException( "temp" );
            }
            return fos;
        }
        catch( UnavailableServiceException e ) {
            return new LocalFileSaveService( owner );
        }
    }
}
