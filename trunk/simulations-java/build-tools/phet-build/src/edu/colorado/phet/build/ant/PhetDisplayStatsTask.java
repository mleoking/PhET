package edu.colorado.phet.build.ant;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;
import edu.colorado.phet.build.util.PhetBuildUtils;

public class PhetDisplayStatsTask extends PhetAllSimTask {
    public final void execute() throws BuildException {
        showStats( PhetProject.getSimNames( getBaseDir() ) );
    }

    public void showStats( String[] simNames ) {
        File baseDir = getProject().getBaseDir();
        showStats( simNames, baseDir );
    }

    private void showStats( String[] simNames, File baseDir ) {
        int flavorCount = 0;
        int numStandardized = 0;
        HashMap languages = new HashMap();
        for ( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            try {
                File projectParentDir = PhetBuildUtils.resolveProject( baseDir, simName );
                PhetProject phetProject = new PhetProject( projectParentDir, simName );
                System.out.println( phetProject.getName() + " (" + phetProject.getVersionString() + ") : " + Arrays.asList( phetProject.getFlavorNames() ) + " locales: " + Arrays.asList( phetProject.getLocales() ) + " non-clash-data=" + isNonClashData( phetProject ) + ", user-readable-names=" + Arrays.asList( getUserReadableFlavorNames( phetProject ) ) );
                numStandardized += isNonClashData( phetProject ) ? 1 : 0;
                flavorCount += phetProject.getFlavorNames().length;
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
        System.out.println( "Number of Sims: " + simNames.length + ", number of declared flavors: " + flavorCount + ", number of locales used at least once: " + languages.size() + ", all locales=" + locales + ", languages=" + language + ", non-clash-data=" + numStandardized );
    }

    private String[] getUserReadableFlavorNames( PhetProject phetProject ) {
        ArrayList list = new ArrayList();
        PhetProjectFlavor[] flavors = phetProject.getFlavors();
        for ( int i = 0; i < flavors.length; i++ ) {
            PhetProjectFlavor flavor = flavors[i];
            list.add( flavor.getTitle() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private boolean containsExactly( File root, File[] files ) {
        List a = Arrays.asList( root.listFiles() );
        List b = Arrays.asList( files );
        Collections.sort( a );
        Collections.sort( b );
        return a.equals( b );
    }

    /**
     * Quick check to make sure resources are inside data/[simname]/
     */
    private boolean isNonClashData( PhetProject phetProject ) {
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
