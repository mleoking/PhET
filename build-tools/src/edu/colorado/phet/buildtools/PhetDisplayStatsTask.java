package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;
import java.util.*;

import edu.colorado.phet.buildtools.java.JavaSimulationProject;
import edu.colorado.phet.buildtools.util.PhetBuildUtils;

public class PhetDisplayStatsTask {
    public static void showStats( File trunk ) {
        displayProjectAndSimTree( trunk );

        showStats( PhetProject.getSimNames( trunk ), trunk );
    }

    private static void displayProjectAndSimTree( File trunk ) {
        String string = "";
        PhetProject[] projects = PhetProject.getAllProjects( trunk );
        for ( int i = 0; i < projects.length; i++ ) {
            PhetProject project = projects[i];
            for ( int k = 0; k < project.getSimulations().length; k++ ) {
                string += ( project.getName() + "\t" + project.getSimulations()[k].getName() ) + "\n";
            }
        }
        System.out.println( "string = \n" + string );
    }

    public static void showStats( String[] simNames, File baseDir ) {
        int simCount = 0;
        int numStandardized = 0;
        HashMap languages = new HashMap();
        for ( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            try {
                File projectParentDir = PhetBuildUtils.resolveProject( baseDir, simName );
                PhetProject phetProject = new JavaSimulationProject( projectParentDir, simName );
                System.out.println( phetProject.getName() + " : " + phetProject.getFullVersionString() + " : " + Arrays.asList( phetProject.getSimulationNames() ) + " locales: " + Arrays.asList( phetProject.getLocales() ) + " non-clash-data=" + isNonClashData( phetProject ) + ", user-readable-names=" + Arrays.asList( getUserReadableSimulationNames( phetProject ) ) );
                numStandardized += isNonClashData( phetProject ) ? 1 : 0;
                simCount += phetProject.getSimulationNames().length;
                for ( int j = 0; j < phetProject.getLocales().length; j++ ) {
                    Locale locale = phetProject.getLocales()[j];
                    languages.put( locale, "" );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        ArrayList locales = new ArrayList( languages.keySet() );
        ArrayList language = new ArrayList();
        for ( int i = 0; i < locales.size(); i++ ) {
            Locale o = (Locale) locales.get( i );
            language.add( o.getDisplayLanguage() );
        }
        System.out.println( "Number of Sims: " + simNames.length + ", number of declared simulations: " + simCount + ", number of locales used at least once: " + languages.size() + ", all locales=" + locales + ", languages=" + language + ", non-clash-data=" + numStandardized );
    }

    private static String[] getUserReadableSimulationNames( PhetProject phetProject ) {
        ArrayList list = new ArrayList();
        Simulation[] simulations = phetProject.getSimulations();
        for ( int i = 0; i < simulations.length; i++ ) {
            Simulation simulation = simulations[i];
            list.add( simulation.getTitle() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private static boolean containsExactly( File root, File[] files ) {
        List a = Arrays.asList( root.listFiles() );
        List b = Arrays.asList( files );
        Collections.sort( a );
        Collections.sort( b );
        return a.equals( b );
    }

    /**
     * Quick check to make sure resources are inside data/[simname]/
     */
    private static boolean isNonClashData( PhetProject phetProject ) {
        File dir = new File( phetProject.getProjectDir(), "data" );

        //standardized if contains "[simname]/" "[simname].properties" and optionally ".svn"
        File child = new File( dir, phetProject.getName() );
        File rootSVN = new File( dir, ".svn" );
        boolean rootOK = containsExactly( dir, new File[]{child, rootSVN} );

        File prop = new File( child, phetProject.getName() + ".properties" );
        boolean containsProperties = Arrays.asList( child.listFiles() ).contains( prop );
        return rootOK && containsProperties;
    }
}
