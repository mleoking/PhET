// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.scripts.git;

import java.io.File;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * Show the dependencies for a project and which git branch they are using.
 *
 * @author Sam Reid
 */
public class ShowDependencies {
    private final File root = new File( "C:\\workingcopy\\phet-little-gits" );

    public static void main( String[] args ) {
        System.out.println( System.getenv( "PATH" ) );
        new ShowDependencies().start();
    }

    private void start() {
        PhetProject[] projects = PhetProject.getAllSimulationProjects( root );
        for ( PhetProject project : projects ) {
            if ( project.getName().equals( "build-an-atom" ) ) {
                System.out.println( "project = " + project );
                PhetProject[] x = project.getAllDependencies();
                for ( int i = 0; i < x.length; i++ ) {
                    PhetProject phetProject = x[i];
                    System.out.println( "dependency: " + i + ", phetProject = " + phetProject.getName() + ", on git version: " + phetProject.getGitBranch() );
                }
            }
        }
    }
}
