package edu.colorado.phet.phetupdater;

import java.io.*;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URLDecoder;

/**
 * A collection of utility methods.  
 * Many of these were copied from phetcommon so that we don't have dependencies on phetcommon.
 */
public class UpdaterUtils {
    
    private UpdaterUtils() {}

    public static void copyTo( File source, File dest ) throws IOException {
        copyAndClose( new FileInputStream( source ), new FileOutputStream( dest ) );
    }

    private static void copyAndClose( InputStream source, OutputStream dest ) throws IOException {
        copy( source, dest );
        source.close();
        dest.close();
    }

    private static void copy( InputStream source, OutputStream dest ) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ( ( bytesRead = source.read( buffer ) ) >= 0 ) {
            dest.write( buffer, 0, bytesRead );
        }
    }

    // copied from phetcommon FileUtils
    /**
     * Determines if a file has a specified suffix.
     * The suffix is case insensitive.
     * You can specify either "xyz" or ".xyz" and this will do the right thing.
     *
     * @param file
     * @param suffix
     * @return
     */
    public static boolean hasSuffix( File file, String suffix ) {
        if ( !suffix.startsWith( "." ) ) {
            suffix = "." + suffix;
        }
        return file.getName().toLowerCase().endsWith( suffix );
    }

    // copied from phetcommon FileUtils
    /**
     * Gets the JAR file that this class was launched from.
     */
    public static File getCodeSource() {
        URL url = UpdaterUtils.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            return new File( url.toURI() );
        }
        catch( URISyntaxException e ) {
            e.printStackTrace();
            try {
               return new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );//whitespace are %20 if you don't decode with utf-8, and file ops will fail.  See #1308
            }
            catch( UnsupportedEncodingException ex ) {
                ex.printStackTrace();
            }
            throw new RuntimeException( "No code source found for URL: "+url);
        }
    }
    
    // copied from phetcommon StringUtil
    /**
     * Converts an exception's stack trace to a string.
     * Useful for displaying strings in error dialogs.
     * 
     * @param e
     * @return String
     */
    public static String stackTraceToString( Exception e ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        return sw.toString();
    }
}
