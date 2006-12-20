package edu.colorado.phet.common.util.services;

import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Apr 4, 2003
 * Time: 9:05:31 PM
 * To change this template use Options | File Templates.
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
        if( answer == JFileChooser.CANCEL_OPTION ) {
            System.out.println( "Cancelled." );
            return null;
        }
        else if( answer == JFileChooser.ERROR_OPTION ) {
            System.out.println( "Error" );
            return null;
        }
        else if( answer == JFileChooser.APPROVE_OPTION ) {
            File f = jfc.getSelectedFile();
            System.out.println( "Selected file f=" + f );
            if( !f.exists() ) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            LocalFileContent lfc = new LocalFileContent( f );

            // get the InputStream from the file and read a few bytes
            byte [] buf = new byte[(int)source.getLength()];
            InputStream is = source.getInputStream();
            int pos = 0;
            while( ( pos = is.read( buf, pos, buf.length - pos ) ) > 0 ) {
                // just loop
            }
            is.close();

            // get the OutputStream and write the file back out
            if( f.canWrite() ) {
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
