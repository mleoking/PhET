package org.srr.localjnlp.local;

import javax.jnlp.FileContents;
import javax.jnlp.JNLPRandomAccessFile;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Apr 4, 2003
 * Time: 9:00:37 PM
 * To change this template use Options | File Templates.
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
        if( !b ) {
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
