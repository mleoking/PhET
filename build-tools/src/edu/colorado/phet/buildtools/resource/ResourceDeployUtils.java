package edu.colorado.phet.buildtools.resource;

import java.io.File;
import java.io.FileFilter;

import edu.colorado.phet.buildtools.BuildToolsPaths;

/**
 * Utility functions for the resource deploy process
 */
public class ResourceDeployUtils {

    public static final boolean DEBUG = true;

    public static File getTestDir( File resourceDir ) {
        return new File( resourceDir, "test" );
    }

    public static File getResourceSubDir( File resourceDir ) {
        return new File( resourceDir, "resource" );
    }

    public static File getBackupDir( File resourceDir ) {
        return new File( resourceDir, "backup" );
    }

    public static File getExtrasDir( File resourceDir ) {
        return new File( resourceDir, "extras" );
    }

    public static File getLiveSimsDir( File resourceDir ) {
        return new File( resourceDir, "../.." );
    }


    public static File getResourceProperties( File resourceDir ) {
        return new File( getResourceSubDir( resourceDir ), "resource.properties" );
    }

    /**
     * During the publishing process, we don't want to copy over more files than we have to. This function should
     * be able to specify which files should not be copied
     *
     * @param file The file that is being checked for whether it should be copied during publishing
     * @return Whether or not to copy it
     */
    public static boolean ignoreTestFile( File file ) {
        return file.getName().endsWith( ".swf" ) || file.getName().endsWith( ".jnlp" );
    }

    public static String getDirNameList( File[] dirs ) {
        String ret = "";

        if ( dirs.length == 0 ) {
            return ret;
        }

        for ( int i = 0; i < dirs.length; i++ ) {
            File dir = dirs[i];

            if ( i != 0 ) {
                ret += ",";
            }

            ret += dir.getName();
        }

        return ret;
    }

    /**
     * Get a list of Java simulation directories, relative to a checked out copy
     *
     * @param trunk Path to trunk
     * @return Array of java simulation directories
     */
    public static File[] getJavaSimulationDirs( File trunk ) {
        if ( DEBUG ) {
            return new File[]{new File( trunk, BuildToolsPaths.JAVA_SIMULATIONS_DIR + "/conductivity" )};
        }
        else {
            File simsDir = new File( trunk, BuildToolsPaths.JAVA_SIMULATIONS_DIR );

            File[] simDirs = simsDir.listFiles( new FileFilter() {
                public boolean accept( File file ) {
                    return file.isDirectory() && !file.getName().startsWith( "." );
                }
            } );

            return simDirs;
        }
    }

    /**
     * Get a list of flash simulation directories, relative to a checked out copy
     *
     * @param trunk Path to trunk
     * @return Array of flash simulation directories
     */

    public static File[] getFlashSimulationDirs( File trunk ) {
        if ( DEBUG ) {
            return new File[]{new File( trunk, BuildToolsPaths.FLASH_SIMULATIONS_DIR + "/curve-fitting" )};
        }
        else {
            File simsDir = new File( trunk, BuildToolsPaths.FLASH_SIMULATIONS_DIR );

            File[] simDirs = simsDir.listFiles( new FileFilter() {
                public boolean accept( File file ) {
                    return file.isDirectory() && !file.getName().startsWith( "." );
                }
            } );

            return simDirs;
        }
    }

    /**
     * Get a comma-separated list of java sim names
     *
     * @param trunk Reference to trunk
     * @return A string of comma-separated sim names
     */
    public static String getJavaSimNames( File trunk ) {
        return getDirNameList( getJavaSimulationDirs( trunk ) );
    }

    /**
     * Get a comma-separated list of flash sim names
     *
     * @param trunk Reference to trunk
     * @return A string of comma-separated sim names
     */
    public static String getFlashSimNames( File trunk ) {
        return getDirNameList( getFlashSimulationDirs( trunk ) );
    }
}
