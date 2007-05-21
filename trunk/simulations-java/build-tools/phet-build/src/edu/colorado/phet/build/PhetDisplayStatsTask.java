package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PhetDisplayStatsTask extends PhetAllSimTask {
    public final void execute() throws BuildException {
        showStats( getSimNames() );
    }

    public void showStats( String[] simNames ) {
        File baseDir = getProject().getBaseDir();
        showStats( simNames, baseDir );
    }

    private void showStats( String[] simNames, File baseDir ) {
        int flavorCount = 0;
        int numStandardized=0;
        HashMap languages = new HashMap();
        for( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            try {
                File projectParentDir = PhetBuildUtils.resolveProject( baseDir, simName );
                PhetProject phetProject = new PhetProject( projectParentDir, simName );
                System.out.println( phetProject.getName() + ": " + Arrays.asList( phetProject.getFlavorNames() ) + " locales: " + Arrays.asList( phetProject.getLocales() ) + " standardizedData=" + isStandardizedData( phetProject ) );
                numStandardized+=isStandardizedData( phetProject )?1:0;
                flavorCount += phetProject.getFlavorNames().length;
                for( int j = 0; j < phetProject.getLocales().length; j++ ) {
                    String locale = phetProject.getLocales()[j];
                    languages.put( locale, "" );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        ArrayList locales = new ArrayList( languages.keySet() );
        ArrayList language = new ArrayList();
        for( int i = 0; i < locales.size(); i++ ) {
            String o = (String)locales.get( i );
            language.add( new Locale( o ).getDisplayLanguage() );
        }
        System.out.println( "Number of Sims: " + simNames.length + ", number of declared flavors: " + flavorCount + ", number of locales used at least once: " + languages.size() + ", all locales=" + locales + ", languages=" + language+", standardizedData="+numStandardized );
    }

    private boolean containsExactly( File root, File[] files ) {
        List a = Arrays.asList( root.listFiles() );
        List b = Arrays.asList( files );
        Collections.sort( a );
        Collections.sort( b );
        return a.equals( b );
    }

    private boolean isStandardizedData( PhetProject phetProject ) {
        File dir = new File( phetProject.getDir(), "data" );

        //standardized if contains "[simname]/" "[simname].properties" and optionally ".svn"
        File child = new File( dir, phetProject.getName() );
        File rootSVN = new File( dir, ".svn" );
        boolean rootOK = containsExactly( dir, new File[]{child, rootSVN} );

        File prop = new File( child, phetProject.getName() + ".properties" );
        boolean containsProperties = Arrays.asList( child.listFiles() ).contains( prop );
        return rootOK && containsProperties;
    }

    public static void main( String[] args ) {
        File simsroot = new File( "C:\\phet\\subversion\\trunk\\simulations-java" );
        String[] sims = PhetAllSimTask.getSimNames( new File( simsroot, "simulations" ) );
        PhetDisplayStatsTask phetDisplayStatsTask = new PhetDisplayStatsTask();
        phetDisplayStatsTask.showStats( sims, simsroot );
    }
}
