package edu.colorado.phet.common.phetcommon.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
    
    //TODO: consolidate with copy from flash-launcher and updater
    /**
     * Gets the JAR file that this class was launched from.
     */
    public static File getCodeSource() {
        URL url = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URI uri = new URI( url.toString() );
            return new File( uri.getPath() );
        }
        catch( URISyntaxException e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
