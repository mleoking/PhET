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

import javax.jnlp.FileContents;
import javax.jnlp.JNLPRandomAccessFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Adapter pattern from InputStream to FileContents.
 */
public class InputStreamFileContents implements FileContents {
    private InputStream is;
    private String name;

    public InputStreamFileContents( String name, InputStream is ) {
        this.name = name;
        this.is = is;
    }

    public boolean canRead() throws IOException {
        return true;
    }

    public boolean canWrite() throws IOException {
        return false;
    }

    public InputStream getInputStream() throws IOException {
        return is;
    }

    public long getLength() throws IOException {
        return is.available();
    }

    public long getMaxLength() throws IOException {
        return Long.MAX_VALUE;
    }

    public String getName() throws IOException {
        return name;
    }

    public OutputStream getOutputStream( boolean b ) throws IOException {
        return null;
    }

    public JNLPRandomAccessFile getRandomAccessFile( String s ) throws IOException {
        return null;
    }

    public long setMaxLength( long l ) throws IOException {
        throw new RuntimeException( "not supported" );
    }
}
