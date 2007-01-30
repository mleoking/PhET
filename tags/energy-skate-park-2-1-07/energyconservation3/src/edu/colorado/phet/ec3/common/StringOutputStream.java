/**
 * StringOuputStream: output stream which redirects
 * the output to a string. It uses a deprecated method
 * but it is useful if you want to collect System.out
 * for example and write it out in a text area at once
 * which increases performance.
 * @version 1.0
 * @author Alexandre David
 * 
 * @license GPL
 * @website http://jassistant.sourceforge.net/
 */

package edu.colorado.phet.ec3.common;

import java.io.OutputStream;

/**
 * StringOuputStream
 * output stream to a string
 */
public class StringOutputStream extends OutputStream {

    /**
     * where to output
     */
    StringBuffer buffer;

    /**
     * default constructor: to a string
     */
    public StringOutputStream() {
        buffer = new StringBuffer( 2048 );
    }

    /**
     * or fix the buffer size
     */
    public StringOutputStream( int size ) {
        buffer = new StringBuffer( size );
    }

    /**
     * or takes the buffer directly
     */
    public StringOutputStream( StringBuffer buf ) {
        if( buf != null ) {
            buffer = buf;
        }
        else {
            buffer = new StringBuffer( 2048 );
        }
    }

    /**
     * implements the stream: writes the byte to the string buffer
     */
    public void write( int b ) {
        buffer.append( (char)b );
    }

    /**
     * returns the written string
     */
    public String toString() {
        return buffer.toString();
    }

    /**
     * forward StringBuffer functions
     */
    public int length() {
        return buffer.length();
    }

    public void setLength( int l ) {
        buffer.setLength( l );
    }

}
