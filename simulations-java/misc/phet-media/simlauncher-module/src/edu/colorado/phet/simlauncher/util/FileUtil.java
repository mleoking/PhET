/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/util/FileUtil.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/06/09 00:31:40 $
 */
package edu.colorado.phet.simlauncher.util;

import java.io.File;

/**
 * FileUtil
 * <p/>
 * Static utility functions for working with files
 *
 * @author Ron LeMaster
 * @version $Revision: 1.5 $
 */
public class FileUtil {

    /**
     * Deletes all files and subdirectories under file.
     * Returns true if all deletions were successful.
     * If a deletion fails, the method stops attempting to delete and returns false.
     *
     * @param file
     * @return true if everything is deleted successfully, false otherwise
     */
    public static boolean deleteDir( File file ) {
        if( file.isDirectory() ) {
            String[] children = file.list();
            for( int i = 0; i < children.length; i++ ) {
                boolean success = deleteDir( new File( file, children[i] ) );
                if( !success ) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return file.delete();
    }

    /**
     * Returns the path separator for the local file system
     *
     * @return the path separator
     */
    public static String getPathSeparator() {
        return System.getProperty( "file.separator" );
    }
}