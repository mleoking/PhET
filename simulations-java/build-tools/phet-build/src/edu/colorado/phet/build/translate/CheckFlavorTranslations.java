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
    public static void main( String[] args ) throws IOException {
        CheckTranslations.Sim[] s = CheckTranslations.getLocalSims();
        ArrayList checked = new ArrayList();
        for ( int i = 0; i < s.length; i++ ) {
            CheckTranslations.Sim sim = s[i];
            PhetProject phetProject = new PhetProject( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" ), sim.getName() );
//            System.out.println( "phetProject = " + Arrays.asList( phetProject.getFlavorNames() ) );

            //check flavor jars
            for ( int j = 0; j < phetProject.getFlavorNames().length; j++ ) {
                String flavor = phetProject.getFlavorNames()[j];
                String webLocation = "http://phet.colorado.edu/sims/" + sim.getName() + "/" + flavor + ".jar";
                final String fileName = "C:\\Users\\Sam\\Desktop\\jars\\" + flavor + ".jar";
                try {
                    FileDownload.download( webLocation, fileName );
//                    System.out.println( "downloaded: " + webLocation );
                    checkTranslations( sim, phetProject, fileName );
                    checked.add( webLocation );
                }
                catch( FileNotFoundException fnfe ) {
                    System.out.println( "File not found for: " + webLocation );
                }
            }

            //check main jar
            String webLocation = "http://phet.colorado.edu/sims/" + sim.getName() + "/" + sim.getName() + ".jar";
            if ( !checked.contains( webLocation ) ) {
                final String fileName = "C:\\Users\\Sam\\Desktop\\jars\\" + sim.getName() + ".jar";
                try {
                    FileDownload.download( webLocation, fileName );
//                System.out.println( "downloaded: " + webLocation );
                    checkTranslations( sim, phetProject, fileName );
                    checked.add( webLocation );
                }
                catch( FileNotFoundException fnfe ) {
                    System.out.println( "File not found for: " + webLocation );
                }
            }
        }
    }

    private static void checkTranslations( CheckTranslations.Sim s, PhetProject phetProject, String jar ) throws IOException {
        final List local = Arrays.asList( s.getTranslations() );

        final List jarList = Arrays.asList( listTranslationsInJar( phetProject, jar ) );

        boolean same = setEquals( local, jarList );
        System.out.print( jar + ": same = " + same );
        if ( !same ) {
            System.out.print( "Remote : " + jarList + ", local: " + local );
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
