package edu.colorado.phet.common.phetcommon.util;

import java.io.File;

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
}
