package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileFilter;

public class ResourceDeployUtils {
    public static final boolean DEBUG = false;

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

    public static boolean ignoreTestFile( File file ) {
        return file.getName().endsWith( ".swf" );
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

    public static File[] getJavaSimulationDirs( File trunk ) {
        if ( DEBUG ) {
            return new File[]{new File( trunk, "simulations-java/simulations/test-project" )};
        }
        else {
            File simsDir = new File( trunk, "simulations-java/simulations" );

            File[] simDirs = simsDir.listFiles( new FileFilter() {
                public boolean accept( File file ) {
                    return file.isDirectory() && !file.getName().startsWith( "." );
                }
            } );

            return simDirs;
        }
    }

    public static File[] getFlashSimulationDirs( File trunk ) {
        if ( DEBUG ) {
            return new File[]{new File( trunk, "simulations-flash/simulations/test-flash-project" )};
        }
        else {
            File simsDir = new File( trunk, "simulations-flash/simulations" );

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
