package edu.colorado.phet.buildtools.scripts;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * This command line script can be used to identify all phet simulations with a declared dependency on the specified project.
 *
 * @author Sam Reid
 */
public class CheckForDependency {

    public static void main( String[] args ) throws IOException {
//        args = new String[]{"C:\\workingcopy\\phet\\svn\\trunk", "motion"};//SR's configuration
        if ( args.length != 2 ) { System.out.println( "usage: args[0] = path to simulation-java, args[1] = dependency to check for" ); }
        PhetProject[] p = PhetProject.getAllProjects( new File( args[0] ) );
        for ( int i = 0; i < p.length; i++ ) { checkForDependency( p[i], args[1] ); }
    }

    private static void checkForDependency( PhetProject project, String dependency ) throws IOException {
        PhetProject[] dep = project.getAllDependencies();
        for ( int i = 0; i < dep.length; i++ ) {
            if ( dep[i].getName().equals( dependency ) ) {
                System.out.println( project.getName() );
            }
        }
    }
}