/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.persistence;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * ObjectIO performs XML encoding/decoding of objects, and
 * write/reads those objects to/from files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ObjectIO {

    /* Not intended for instantiation. */
    private ObjectIO() {}

    /**
     * XML encodes an object and writes it to a file.
     * 
     * @param object
     * @param filename
     * @throws IOException
     */
    public static void write( Object object, final String filename ) throws IOException {
        FileOutputStream fos = new FileOutputStream( filename );
        BufferedOutputStream bos = new BufferedOutputStream( fos );
        XMLEncoder encoder = new XMLEncoder( bos );
        encoder.setExceptionListener( new ExceptionListener() {
            public void exceptionThrown( Exception e ) {
                JFrame frame = PhetApplication.instance().getPhetFrame();
                String title = SimStrings.get( "Save.error.title" );
                String format = SimStrings.get( "Save.error.message" );
                Object[] args = { filename, e.getMessage() };
                String message = MessageFormat.format( format, args );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();
            }      
        } );
        encoder.writeObject( object );
        encoder.close();
    }

    /**
     * Reads an objects from a file and XML decodes it.
     * 
     * @param filename
     * @return Object
     * @throws IOException
     */
    public static Object read( final String filename ) throws IOException {
        Object object = null;
        FileInputStream fis = new FileInputStream( filename );
        BufferedInputStream bis = new BufferedInputStream( fis );
        XMLDecoder decoder = new XMLDecoder( bis );
        decoder.setExceptionListener( new ExceptionListener() {
            public void exceptionThrown( Exception e ) {
                JFrame frame = PhetApplication.instance().getPhetFrame();
                String title = SimStrings.get( "Load.error.title" );
                String format = SimStrings.get( "Load.error.message" );
                Object[] args = { filename, e.getMessage() };
                String message = MessageFormat.format( format, args );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();
            }      
        } );
        object = decoder.readObject();
        decoder.close();
        return object;
    }
}