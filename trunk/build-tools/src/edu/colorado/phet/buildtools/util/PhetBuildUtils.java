/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.util;

import java.io.File;
import java.util.Arrays;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.AntTaskRunner;

public class PhetBuildUtils {
    private PhetBuildUtils() {
    }

    public static String convertArrayToList( Object[] array ) {
        String list = "";

        for ( int i = 0; i < array.length; i++ ) {
            list += array[i];
            if ( i < array.length - 1 ) {
                list += ",";
            }
        }

        return list;
    }

    /**
     * Echos the string via the Ant echo task.
     *
     * @param taskRunner An Ant task runner.
     * @param message    The message to echo.
     * @param taskName   Task name.
     */
    public static void antEcho( AntTaskRunner taskRunner, String message, String taskName ) {
        System.out.println( taskName + "> " + message );
    }

    public static void antEcho( AntTaskRunner taskRunner, String message, Class theClass ) {
        String className = theClass.getName();

        String simpleName = className.substring( className.lastIndexOf( "." ) + 1 );

        antEcho( taskRunner, message, simpleName );
    }

    /**
     * Retrieves the build properties file for the dir and project name. This
     * file may not exist, if the directory does not describe a project.
     *
     * @param dir The project directory.
     * @return The build properties file.
     */
    public static File getBuildPropertiesFile( File dir ) {
        return new File( dir, dir.getName() + "-build.properties" );
    }

    /**
     * Resolves the specified project dirname to a directory, by searching
     * through simulations, common, and contrib areas, and returning the first
     * match.
     *
     * @param antFileBaseDir The base directory of the ant build file.
     * @param name           The project dirname.
     * @return A File representing the directory of the project.
     */
    public static File resolveProject( File antFileBaseDir, String name ) {
        File[] searchRoots = new File[]{
                new File( antFileBaseDir, "simulations" ),
                new File( antFileBaseDir, "common" ),
                new File( antFileBaseDir, "contrib" ),
        };
        for ( int i = 0; i < searchRoots.length; i++ ) {
            File searchRoot = searchRoots[i];
            File dir = new File( searchRoot, name );

            File props = getBuildPropertiesFile( dir );

            if ( dir.exists() && dir.isDirectory() && props.exists() ) {
                return searchRoot;
            }
        }

        throw new IllegalArgumentException( "No project found for name=" + name + ", searched in roots=" + Arrays.asList( searchRoots ) );
    }

    public static String[] toStringArray( String property, String tokens ) {
        StringTokenizer st = new StringTokenizer( property, tokens );
        String[] array = new String[st.countTokens()];
        for ( int i = 0; i < array.length; i++ ) {
            array[i] = st.nextToken();
        }
        return array;
    }
}
