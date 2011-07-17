// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.servicemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jnlp.FileContents;
import javax.jnlp.JNLPRandomAccessFile;

/**
 * Adapter from File to FileContents.
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class LocalFileContent implements FileContents {
    File f;

    public LocalFileContent( File f ) {
        this.f = f;
    }

    public boolean canRead() throws IOException {
        return f.canRead();
    }

    public boolean canWrite() throws IOException {
        return f.canWrite();
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream( f );
    }

    public long getLength() throws IOException {
        return f.length();
    }

    public long getMaxLength() throws IOException {
        return Long.MAX_VALUE;
    }

    public String getName() throws IOException {
        return f.getName();
    }

    public OutputStream getOutputStream( boolean b ) throws IOException {
        if ( !b ) {
            throw new IOException( "OutputStream cannot be protected from overwrite--exiting." );
        }
        return new FileOutputStream( f );
    }

    public JNLPRandomAccessFile getRandomAccessFile( String s ) throws IOException {
        throw new RuntimeException( "Not supported." );
    }

    public long setMaxLength( long l ) throws IOException {
        throw new RuntimeException( "Not implemented." );
    }
}
