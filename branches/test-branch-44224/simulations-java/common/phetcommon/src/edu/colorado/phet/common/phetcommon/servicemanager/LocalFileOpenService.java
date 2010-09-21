/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.servicemanager;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.swing.*;

/**
 * Provides a File Open service that adheres to the JNLP FileOpenService interface
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class LocalFileOpenService implements FileOpenService {
    private Component owner;

    public LocalFileOpenService( Component owner ) {
        this.owner = owner;
    }

    /**
     * @param s
     * @param strings may be null if there is no specified filter set; a null array is different
     *                than an array of zero size
     * @return
     * @throws IOException
     */
    public FileContents openFileDialog( String s, String[] strings ) throws IOException {
        JFileChooser jfc = new JFileChooser( s );
        if ( strings != null && strings.length > 0) {
            LocalFileFilter eff = new LocalFileFilter( strings );
            jfc.setFileFilter( eff );
        }
        int answer = jfc.showOpenDialog( owner );
        if ( answer == JFileChooser.CANCEL_OPTION ) {
            System.out.println( "Cancelled." );
        }
        else if ( answer == JFileChooser.ERROR_OPTION ) {
            System.out.println( "Error." );
        }
        else if ( answer == JFileChooser.APPROVE_OPTION ) {
            File f = jfc.getSelectedFile();
            return new LocalFileContent( f );
        }
        return null;
    }

    public FileContents[] openMultiFileDialog( String s, String[] strings ) throws IOException {
        JFileChooser jfc = new JFileChooser( s );
        LocalFileFilter eff = new LocalFileFilter( strings );
        jfc.setFileFilter( eff );
        int answer = jfc.showOpenDialog( owner );
        if ( answer == JFileChooser.CANCEL_OPTION ) {
            System.out.println( "Cancelled." );
        }
        else if ( answer == JFileChooser.ERROR_OPTION ) {
            System.out.println( "Error." );
        }
        else if ( answer == JFileChooser.APPROVE_OPTION ) {
            File[] f = jfc.getSelectedFiles();
            LocalFileContent[] fx = new LocalFileContent[f.length];
            for ( int i = 0; i < f.length; i++ ) {
                fx[i] = new LocalFileContent( f[i] );
            }
            return fx;
        }
        return null;
    }
}
