/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.util;

import java.io.File;

/**
 * FileUtil
 * <p>
 * Static utility functions for working with files
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FileUtil {

    /**
     * Deletes all files and subdirectories under dir.
     * Returns true if all deletions were successful.
     * If a deletion fails, the method stops attempting to delete and returns false.
     *
     * @param dir
     * @return true if everything is deleted successfully, false otherwise
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * Returns the path separator for the local file system
     * @return the path separator
     */
    public static String getPathSeparator() {
        return System.getProperty( "file.separator" );
    }
}
