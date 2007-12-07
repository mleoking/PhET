package edu.colorado.phet.build.translate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/*
 * Command line program to download data from URLs and save
 * it to local files. Run like this:
 * java FileDownload http://schmidt.devlib.org/java/file-download.html
 * @author Marco Schmidt
 * http://schmidt.devlib.org/java/file-download.html#source
 */
public class FileDownload {
    public static void download( String address, String localFileName ) throws FileNotFoundException {
        new File(localFileName).getParentFile().mkdirs();
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
//            System.out.println( localFileName + "\t" + numWritten );
        }
        catch( FileNotFoundException f ){
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

    public static void download( String address ) throws FileNotFoundException {
        int lastSlashIndex = address.lastIndexOf( '/' );
        if ( lastSlashIndex >= 0 && lastSlashIndex < address.length() - 1 ) {
            download( address, address.substring( lastSlashIndex + 1 ) );
        }
        else {
            System.err.println( "Could not figure out local file name for " + address );
        }
    }

    public static void main( String[] args ) throws FileNotFoundException {
        for ( int i = 0; i < args.length; i++ ) {
            download( args[i] );
        }
    }
}