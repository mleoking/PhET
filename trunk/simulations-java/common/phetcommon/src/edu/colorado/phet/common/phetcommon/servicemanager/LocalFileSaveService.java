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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * Performs local file save feature that conforms to JNLP file save service interface.
 *
 * @author Sam Reid
 */
public class LocalFileSaveService implements FileSaveService {
    Component owner;

    public LocalFileSaveService( Component owner ) {
        this.owner = owner;
    }

    public FileContents saveAsFileDialog( String s, String[] strings, FileContents source ) throws IOException {
        JFileChooser jfc = new JFileChooser( s );
        LocalFileFilter eff = new LocalFileFilter( strings );
        jfc.setFileFilter( eff );
        int answer = jfc.showSaveDialog( owner );
        if ( answer == JFileChooser.CANCEL_OPTION ) {
            System.out.println( "Cancelled." );
            return null;
        }
        else if ( answer == JFileChooser.ERROR_OPTION ) {
            System.out.println( "Error" );
            return null;
        }
        else if ( answer == JFileChooser.APPROVE_OPTION ) {
            File f = jfc.getSelectedFile();
            System.out.println( "Selected file f=" + f );
            if ( !f.exists() ) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            else {
                String message = SimStrings.getInstance().getString( "Save.confirm.message" );
                int reply = DialogUtils.showConfirmDialog( owner, message, JOptionPane.YES_NO_CANCEL_OPTION );
                if ( reply != JOptionPane.YES_OPTION ) {
                    return null;
                }
            }

            LocalFileContent lfc = new LocalFileContent( f );

            // get the InputStream from the file and read a few bytes
            byte[] buf = new byte[(int) source.getLength()];
            InputStream is = source.getInputStream();
            int pos = 0;
            while ( ( pos = is.read( buf, pos, buf.length - pos ) ) > 0 ) {
                // just loop
            }
            is.close();

            // get the OutputStream and write the file back out
            if ( f.canWrite() ) {
                FileOutputStream destination = new FileOutputStream( f );
                // don't append
                destination.write( buf );
            }

            return lfc;
        }
        return null;
    }

    public FileContents saveFileDialog( String s, String[] strings, InputStream inputStream, String s1 ) throws IOException {
        return saveAsFileDialog( s, strings, new InputStreamFileContents( s1, inputStream ) );
    }
}
