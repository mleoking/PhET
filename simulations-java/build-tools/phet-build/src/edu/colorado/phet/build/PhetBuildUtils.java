/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.taskdefs.Echo;

import java.io.File;
import java.util.Arrays;

public class PhetBuildUtils {
    private PhetBuildUtils() {
    }

    /**
     * Echos the string via the Ant echo task.
     *
     * @param taskRunner    An Ant task runner.
     *
     * @param message       The message to echo.
     */
    public static void antEcho( AntTaskRunner taskRunner, String message ) {
        Echo echo = new Echo();
        echo.setMessage( message );
        taskRunner.runTask( echo );
    }

    /**
     * Retrieves the build properties file for the dir and project name. This
     * file may not exist, if the directory does not describe a project.
     *
     * @param dir           The directory.
     * @param projectName   The project name.
     *
     * @return  The build properties file.
     */
    public static File getBuildPropertiesFile( File dir, String projectName ) {
        return new File( dir, projectName + ".properties" );
    }

    /**
     * Resolves the specified project dirname to a directory, by searching
     * through simulations, common, and contrib areas, and returning the first
     * match.
     *
     * @param antFileBaseDir    The base directory of the ant build file.
     * @param name              The project dirname.
     *
     * @return  A File representing the directory of the project.
     */
    public static File resolveProject( File antFileBaseDir, String name ) {
        File[] searchRoots = new File[]{
                new File( antFileBaseDir, "simulations" ),
                new File( antFileBaseDir, "common" ),
                new File( antFileBaseDir, "contrib" ),
        };
        for( int i = 0; i < searchRoots.length; i++ ) {
            File searchRoot = searchRoots[i];
            File dir = new File( searchRoot, name );

            File props = getBuildPropertiesFile( dir, name );

            if( dir.exists() && dir.isDirectory() && props.exists() ) {
                return searchRoot;
            }
        }
        
        throw new IllegalArgumentException( "No project found for name=" + name + ", searched in roots=" + Arrays.asList( searchRoots ) );
    }
}
