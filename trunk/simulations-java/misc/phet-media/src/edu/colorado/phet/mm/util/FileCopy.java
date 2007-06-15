package edu.colorado.phet.mm.util;

import java.io.*;

public class FileCopy {
    //http://www.exampledepot.com/egs/java.io/CopyFile.html
    // Copies src file to dst file.
    // If the dst file does not exist, it is created
    public static void copy( File src, File dst ) throws IOException {
        InputStream in = new FileInputStream( src );
        OutputStream out = new FileOutputStream( dst );

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while( ( len = in.read( buf ) ) > 0 ) {
            out.write( buf, 0, len );
        }
        in.close();
        out.close();
    }
}
