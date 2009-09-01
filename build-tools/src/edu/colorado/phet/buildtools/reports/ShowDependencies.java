package edu.colorado.phet.buildtools.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * Indicates dependency structure (forward and backward) of phet sims.
 * @author Sam Reid
 * 9-1-2009
 */
public class ShowDependencies {
    public static void main( String[] args ) {
        HashMap<String, ArrayList<PhetProject>> invertedMap = new HashMap<String, ArrayList<PhetProject>>();
        PhetProject[] projects = PhetProject.getAllSimulations( new File( args[0] ) );
        for ( PhetProject project : projects ) {
            PhetProject[] dependencies = project.getAllDependencies();
            System.out.println( project.getName() );
            for ( PhetProject dependency : dependencies ) {
                String name = dependency.getName();
                System.out.println( "\t" + name );
                if ( invertedMap.get( name ) == null ) {
                    invertedMap.put( name, new ArrayList<PhetProject>() );
                }
                ArrayList<PhetProject> list = invertedMap.get( name );
                if ( !project.getName().equals( name ) ) {
                    list.add( project );
                }
            }
            System.out.println( "" );
        }
        System.out.println( "________" );
        for ( String key : invertedMap.keySet() ) {
            ArrayList<PhetProject> phetProjectArrayList = invertedMap.get( key );
            if ( phetProjectArrayList.size() > 0 ) {
                System.out.println( key );
                for ( PhetProject phetProject : phetProjectArrayList ) {
                    System.out.println( "\t" + phetProject.getName() );
                }
                System.out.println( "" );
            }
        }

    }
}
