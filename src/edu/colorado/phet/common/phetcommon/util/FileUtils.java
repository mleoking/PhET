package edu.colorado.phet.common.phetcommon.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.AccessControlException;
import java.util.jar.JarFile;

/**
 * FileUtils is a collection of file utilities.
 */
public class FileUtils {
    
    private FileUtils() {}
    
    /**
     * Gets the basename of a file.
     * For example /tmp/foo.jar has a basename of foo.
     * 
     * @param file
     * @return
     */
    public static String getBasename( File file ) {
        String fullname = file.getName();
        String basename = null;
        int index = fullname.indexOf( '.' );
        if ( index == -1 ) {
            basename = fullname;
        }
        else {
            basename = fullname.substring( 0, index );
        }
        return basename;
    }
    
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
    
    //See also copies in updater and flash-launcher; deep copies since those subprojects cannot take in all of phetcommon.
    /**
     * Gets the JAR file that this class was launched from.
     */
    public static File getCodeSource() {
        URL url = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            //TODO: consider using new File(URL.toURI) when we move to 1.5
            return new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );//whitespace are %20 if you don't decode with utf-8, and file ops will fail.  See #1308
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            try {
                return new File( URLDecoder.decode( url.getPath(), "UTF-8" ) );
            }
            catch( UnsupportedEncodingException e1 ) {
                e1.printStackTrace();
                return new File( url.getPath() );
            }
        }
    }
    
    /**
     * Is the code source a JAR file?
     * @return
     */
    public static boolean isJarCodeSource() {
        //TODO: bad style to write code that depends on exceptions
        try {
            return isJar( FileUtils.getCodeSource() );
        }
        catch( AccessControlException ace ) {
            return false;
        }
    }
    
    /**
     * Is the file a JAR?
     * @param file
     * @return
     */
    public static boolean isJar( File file ) {
        //TODO: bad style to write code that depends on exceptions
        try {
            new JarFile( file ); // throws IOException if not a jar file
            return true;
        }
        catch ( IOException e ) {
            return false;
        }
    }
}
