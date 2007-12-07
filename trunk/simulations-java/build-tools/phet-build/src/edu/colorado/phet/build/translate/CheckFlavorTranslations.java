package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Dec 7, 2007 at 11:33:12 AM
 */
public class CheckFlavorTranslations {
    private static final String WEBROOT = "http://phet.colorado.edu/sims/";
    private static final String LOCAL_ROOT_DIR = "C:\\Users\\Sam\\Desktop\\jars\\";

    public static void main( String[] args ) throws IOException {
        CheckTranslations.Sim[] s = CheckTranslations.getLocalSims();
        for ( int i = 0; i < s.length; i++ ) {
            CheckTranslations.Sim sim = s[i];
            PhetProject phetProject = new PhetProject( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" ), sim.getName() );

            //check flavor jars
            for ( int j = 0; j < phetProject.getFlavorNames().length; j++ ) {
                checkJAR( sim, phetProject, phetProject.getFlavorNames()[j] );
            }
            //check main jar (if we haven't already)
            if ( !Arrays.asList( phetProject.getFlavorNames() ).contains( phetProject.getName() ) ) {
                checkJAR( sim, phetProject, phetProject.getName() );
            }
        }
    }

    private static void checkJAR( CheckTranslations.Sim sim, PhetProject phetProject, String flavor ) throws IOException {
        String webLocation = WEBROOT + sim.getName() + "/" + flavor + ".jar";
        final String fileName = LOCAL_ROOT_DIR + flavor + ".jar";
        try {
            FileDownload.download( webLocation, fileName );
            checkTranslations( sim, phetProject, fileName );
        }
        catch( FileNotFoundException fnfe ) {
            System.out.println( "File not found for: " + webLocation );
        }
    }

    private static void checkTranslations( CheckTranslations.Sim s, PhetProject phetProject, String jar ) throws IOException {
        final List local = Arrays.asList( s.getTranslations() );

        final List jarList = Arrays.asList( listTranslationsInJar( phetProject, jar ) );

        boolean same = setEquals( local, jarList );
        System.out.print( jar + ": same = " + same );
        if ( !same ) {
            System.out.print( " Remote : " + jarList + ", local: " + local );
        }
        System.out.println( "" );
    }

    private static boolean setEquals( List local, List jarList ) {
        Set a = new HashSet( local );
        Set b = new HashSet( jarList );
        return a.equals( b );
    }

    private static String[] listTranslationsInJar( PhetProject p, String jar ) throws IOException {
        ArrayList translations = new ArrayList();
        final File file = new File( jar );
        if ( file.exists() ) {
            JarFile jarFile = new JarFile( file );
            Enumeration e = jarFile.entries();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
//            System.out.println( "o = " + o );
                final String prefix = p.getName() + "/localization/" + p.getName() + "-strings_";
                if ( o.toString().startsWith( prefix ) ) {
                    String translation = o.toString().substring( prefix.length() + 0, prefix.length() + 2 );
                    translations.add( translation );
                }
            }
            return (String[]) translations.toArray( new String[0] );
        }
        else {
            System.out.println( "No such file: " + file );
            return new String[0];
        }
    }
}
