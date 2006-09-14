package org.srr.localjnlp.local;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Apr 4, 2003
 * Time: 9:05:31 PM
 * To change this template use Options | File Templates.
 */
public class LocalFileOpenService implements FileOpenService {
    Component owner;

    public LocalFileOpenService( Component owner ) {
        this.owner = owner;
    }

    public FileContents openFileDialog( String s, String[] strings ) throws IOException {
        JFileChooser jfc = new JFileChooser( s );
        ExampleFileFilter eff = new ExampleFileFilter( strings );
        jfc.setFileFilter( eff );
        int answer = jfc.showOpenDialog( owner );
        if( answer == JFileChooser.CANCEL_OPTION ) {
            System.out.println( "Cancelled." );
        }
        else if( answer == JFileChooser.ERROR_OPTION ) {
            System.out.println( "Error." );
        }
        else if( answer == JFileChooser.APPROVE_OPTION ) {
            File f = jfc.getSelectedFile();
            return new LocalFileContent( f );
        }
        return null;
    }

    public FileContents[] openMultiFileDialog( String s, String[] strings ) throws IOException {
        JFileChooser jfc = new JFileChooser( s );
        ExampleFileFilter eff = new ExampleFileFilter( strings );
        jfc.setFileFilter( eff );
        int answer = jfc.showOpenDialog( owner );
        if( answer == JFileChooser.CANCEL_OPTION ) {
            System.out.println( "Cancelled." );
        }
        else if( answer == JFileChooser.ERROR_OPTION ) {
            System.out.println( "Error." );
        }
        else if( answer == JFileChooser.APPROVE_OPTION ) {
            File[] f = jfc.getSelectedFiles();
            LocalFileContent[] fx = new LocalFileContent[f.length];
            for( int i = 0; i < f.length; i++ ) {
                fx[i] = new LocalFileContent( f[i] );
            }
            return fx;
        }
        return null;
    }
}
