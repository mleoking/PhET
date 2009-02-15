package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Created by: Sam
 * Dec 7, 2007 at 11:33:12 AM
 */
public class CheckTranslations {
    private boolean verbose = true;
    public static final File TRANSLATIONS_TEMP_DIR = new File( FileUtils.getTmpDir(), "phet-translations-temp" );
    private static File LOCAL_ROOT_DIR = new File( TRANSLATIONS_TEMP_DIR, "temp-jar-dir" );

    static {
        LOCAL_ROOT_DIR.mkdirs();
    }

    public CheckTranslations( boolean verbose ) {
        this.verbose = verbose;
    }

    public void checkTranslationsAllSims( File baseDir ) throws IOException {
        PhetProject[] s = PhetProject.getAllProjects( baseDir );
        for ( int i = 0; i < s.length; i++ ) {
            checkTranslations( s[i] );
        }
    }

    public TranslationDiscrepancy[] checkTranslations( PhetProject phetProject ) throws IOException {
        ArrayList list = new ArrayList();
        //check simulation jars
        for ( int j = 0; j < phetProject.getSimulationNames().length; j++ ) {
            list.add( checkJAR( phetProject, phetProject.getSimulationNames()[j] ) );
        }
        //check main jar (if we haven't already)
        if ( !Arrays.asList( phetProject.getSimulationNames() ).contains( phetProject.getName() ) ) {
            list.add( checkJAR( phetProject, phetProject.getName() ) );
        }
        return (TranslationDiscrepancy[]) list.toArray( new TranslationDiscrepancy[list.size()] );
    }

    private TranslationDiscrepancy checkJAR( PhetProject phetProject, String simulationName ) throws IOException {
        String webLocation = phetProject.getDeployedSimulationJarURL();
        final File fileName = new File( LOCAL_ROOT_DIR, simulationName + "_all.jar" );
        try {
            FileUtils.download( webLocation, fileName );
            return checkTranslations( phetProject, fileName, simulationName );
        }
        catch( FileNotFoundException fnfe ) {
            if ( verbose ) {
                System.out.println( "File not found for: " + webLocation );
            }
            return new TranslationDiscrepancy( new HashSet(), new HashSet(), phetProject, simulationName );
        }
    }

    private TranslationDiscrepancy checkTranslations( PhetProject phetProject, File jar, String simulationName ) throws IOException {
        final Set local = new HashSet( Arrays.asList( phetProject.getLocales() ) );
        final Set remote = new HashSet( Arrays.asList( listTranslationsInJar( phetProject, jar ) ) );

        boolean same = local.equals( remote );

        if ( verbose ) {
            System.out.println( "sim=" + phetProject.getName() + ", : same = " + same + " local=" + local + ", remote=" + remote );
        }

        return getDiff( phetProject, local, remote, simulationName );
    }

    private TranslationDiscrepancy getDiff( PhetProject phetProject, Set local, Set remote, String simulationName ) {
        Set extraLocal = new HashSet( local );
        extraLocal.removeAll( remote );

        Set extraRemote = new HashSet( remote );
        extraRemote.removeAll( local );

        boolean anyChange = extraLocal.size() > 0 || extraRemote.size() > 0;
        if ( anyChange ) {
            System.out.print( phetProject.getName() + "[" + simulationName + "]: " );
        }
        if ( extraRemote.size() > 0 ) {
            System.out.print( "need to be removed from remote jar: " + extraRemote + " " );
        }
        if ( extraLocal.size() > 0 ) {
            System.out.print( "need to be added to remote jar: " + extraLocal + " " );
        }

        if ( anyChange ) {
            System.out.println( "" );
        }
        return new TranslationDiscrepancy( extraLocal, extraRemote, phetProject, simulationName );
    }

    public static Locale[] listTranslationsInJar( PhetProject p, File file ) throws IOException {
        ArrayList translations = new ArrayList();
        if ( file.exists() ) {
            JarFile jarFile = new JarFile( file );
            Enumeration e = jarFile.entries();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                final String prefix = p.getName() + "/localization/" + p.getName() + "-strings_";
                if ( o.toString().startsWith( prefix ) ) {
                    String translation = o.toString().substring( prefix.length(), prefix.length() + 2 );
                    translations.add( new Locale( translation ) );
                }
            }
            //assume all jars always contain english, even though currently uses a different standard
            translations.add( new Locale( "en" ) );
            return (Locale[]) translations.toArray( new Locale[translations.size()] );
        }
        else {
            System.out.println( "No such file: " + file );
            return new Locale[0];
        }
    }


    public static void clearTempDir() {
        System.out.print( "Cleaning temp dir..." );
        FileUtils.delete( TRANSLATIONS_TEMP_DIR, false );
        System.out.println( "Finished." );
    }

    public static void main( String[] args ) throws IOException {
        new CheckTranslations( Boolean.getBoolean( args[1] ) ).checkTranslationsAllSims( new File( args[0] ) );
    }
}
