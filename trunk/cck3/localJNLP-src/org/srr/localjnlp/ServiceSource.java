package org.srr.localjnlp;

import org.srr.localjnlp.local.LocalBasicService;
import org.srr.localjnlp.local.LocalFileOpenService;
import org.srr.localjnlp.local.LocalFileSaveService;

import javax.jnlp.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Apr 5, 2003
 * Time: 1:50:40 AM
 * To change this template use Options | File Templates.
 */
public class ServiceSource {
    public BasicService getBasicService() {
        try {
            BasicService bas = (BasicService)ServiceManager.lookup( BasicService.class.getName() );
            return bas;
        }
        catch( UnavailableServiceException e ) {
            return new LocalBasicService();
        }
    }

    public FileOpenService getFileOpenService( Component owner ) {
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

    public FileSaveService getFileSaveService( Component owner ) {
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
