package edu.colorado.phet.licensing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 1:26:14 AM
 */
public class FileUtils {
    //http://www.exampledepot.com/egs/java.io/CopyFile.html
    // Copies src file to dst file.
    // If the dst file does not exist, it is created
    public static void copy( File src, File dst ) throws IOException {
        InputStream in = new FileInputStream( src );
        OutputStream out = new FileOutputStream( dst );

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ( ( len = in.read( buf ) ) > 0 ) {
            out.write( buf, 0, len );
        }
        in.close();
        out.close();
    }

    public static boolean contentEquals( File fileA, File fileB ) throws IOException {
        if ( fileA.isDirectory() || fileB.isDirectory() ) {
            throw new IllegalArgumentException( "Directories cannot be compared with this method, fileA=" + fileA.getAbsolutePath() + ", fileB=" + fileB.getAbsolutePath() );
        }
        if ( fileA.length() != fileB.length() ) {
            return false;
        }
        InputStream inA = new FileInputStream( fileA );
        InputStream inB = new FileInputStream( fileB );

        // Transfer bytes from in to out
        byte[] bufferA = new byte[1024];
        byte[] bufferB = new byte[1024];
        int lenA = Integer.MAX_VALUE;//code for requiring initialization in loop
        int lenB = Integer.MAX_VALUE;

        boolean ret = true;

        while ( lenA > 0 || lenB > 0 ) {
            lenA = inA.read( bufferA );
            lenB = inB.read( bufferB );
            if ( lenA != lenB ) {
                ret = false;
                break;
            }
            if ( !Arrays.equals( bufferA, bufferB ) ) {
                ret = false;
                break;
            }

        }

        inA.close();
        inB.close();

        return ret;
    }

}
