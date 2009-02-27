package edu.colorado.phet.common.phetcommon.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    
    /**
     * Gets the JAR file that this class was launched from.
     */
    public static File getCodeSource() {
        URL url = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
        return new File( url.getFile() );
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
