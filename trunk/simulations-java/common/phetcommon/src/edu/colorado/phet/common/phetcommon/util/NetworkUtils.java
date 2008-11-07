package edu.colorado.phet.common.phetcommon.util;

import java.io.*;
import java.net.URLConnection;
import java.net.URL;

public class NetworkUtils {

    /*//todo consolidate with many copies in other phet subprojects such as build process, util/updater
    * Download data from URLs and save
    * it to local files. Run like this:
    * java FileDownload http://schmidt.devlib.org/java/file-download.html
    * @author Marco Schmidt
    * http://schmidt.devlib.org/java/file-download.html#source
    */
    public static void download( String address, File localFileName ) throws FileNotFoundException {
        localFileName.getParentFile().mkdirs();
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL( address );
            out = new BufferedOutputStream( new FileOutputStream( localFileName ) );
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            while ( ( numRead = in.read( buffer ) ) != -1 ) {
                out.write( buffer, 0, numRead );
                numWritten += numRead;
            }
//            println( localFileName + "\t" + numWritten );
        }
        catch( FileNotFoundException f ) {
            throw f;
        }
        catch( Exception exception ) {
            exception.printStackTrace();
        }
        finally {
            try {
                if ( in != null ) {
                    in.close();
                }
                if ( out != null ) {
                    out.close();
                }
            }
            catch( IOException ioe ) {
            }
        }
    }

}
