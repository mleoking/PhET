/* PIReader.java                                                   NanoXML/Java
 *
 * $Revision$
 * $Date$
 * $Name$
 *
 * This file is part of NanoXML 2 for Java.
 * Copyright (C) 2000-2002 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */

package net.n3.nanoxml;


import java.io.IOException;
import java.io.Reader;


/**
 * This reader reads data from another reader until the end of a processing
 * instruction (?&gt;) has been encountered.
 *
 * @author Marc De Scheemaecker
 * @version $Name$, $Revision$
 */
class PIReader
        extends Reader {

    /**
     * The encapsulated reader.
     */
    private IXMLReader reader;


    /**
     * True if the end of the stream has been reached.
     */
    private boolean atEndOfData;


    /**
     * Creates the reader.
     *
     * @param reader the encapsulated reader
     */
    PIReader( IXMLReader reader ) {
        this.reader = reader;
        this.atEndOfData = false;
    }


    /**
     * Cleans up the object when it's destroyed.
     */
    protected void finalize()
            throws Throwable {
        this.reader = null;
        super.finalize();
    }


    /**
     * Reads a block of data.
     *
     * @param buffer where to put the read data
     * @param offset first position in buffer to put the data
     * @param size   maximum number of chars to read
     * @return the number of chars read, or -1 if at EOF
     * @throws java.io.IOException if an error occurred reading the data
     */
    public int read( char[] buffer,
                     int offset,
                     int size )
            throws IOException {
        if( this.atEndOfData ) {
            return -1;
        }

        int charsRead = 0;

        if( ( offset + size ) > buffer.length ) {
            size = buffer.length - offset;
        }

        while( charsRead < size ) {
            char ch = this.reader.read();

            if( ch == '?' ) {
                char ch2 = this.reader.read();

                if( ch2 == '>' ) {
                    this.atEndOfData = true;
                    break;
                }

                this.reader.unread( ch2 );
            }

            buffer[charsRead] = ch;
            charsRead++;
        }

        if( charsRead == 0 ) {
            charsRead = -1;
        }

        return charsRead;
    }


    /**
     * Skips remaining data and closes the stream.
     *
     * @throws java.io.IOException if an error occurred reading the data
     */
    public void close()
            throws IOException {
        while( ! this.atEndOfData ) {
            char ch = this.reader.read();

            if( ch == '?' ) {
                char ch2 = this.reader.read();

                if( ch2 == '>' ) {
                    this.atEndOfData = true;
                }
            }
        }
    }

}
