package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Dec 18, 2007 at 12:29:24 AM
 */
public class SynchronizeJNLP {
    private File basedir;

    public SynchronizeJNLP( File basedir ) {
        this.basedir = basedir;
    }

    public static void main( String[] args ) throws IOException {
        CheckTranslations.clearTempDir();
        PhetProject[] p = args.length == 2 ? PhetProject.getAllProjects( new File( args[0] ) ) :
                          new PhetProject[]{new PhetProject( new File( args[0], "simulations/" + args[2] ) )};
        for ( int i = 0; i < p.length; i++ ) {
            new SynchronizeJNLP( new File( args[0] ) ).synchronizeAllJNLP( p );
        }
    }

    private void synchronizeAllJNLP( PhetProject[] p ) throws IOException {
        for ( int i = 0; i < p.length; i++ ) {
            PhetProject phetProject = p[i];
            synchronizeAllJNLP( phetProject );
        }
    }

    private void synchronizeAllJNLP( PhetProject phetProject ) throws IOException {
        for ( int i = 0; i < phetProject.getFlavorNames().length; i++ ) {
            synchronizeAllJNLP( phetProject, phetProject.getFlavorNames()[i] );
        }
    }

    private void synchronizeAllJNLP( PhetProject phetProject, String flavor ) throws IOException {
        File downloaded = downloadJAR( phetProject, flavor );
        //for all languages declared locally and remotely, make sure we also have remote JNLP files
        Locale[] locales = phetProject.getLocales();

        for ( int i = 0; i < locales.length; i++ ) {
            Locale locale = locales[i];

        }
    }

    private File downloadJAR( PhetProject phetProject, String flavor ) throws IOException {
        String deployUrl = phetProject.getDeployedFlavorJarURL( flavor );

//        File jarFile = File.createTempFile( flavor, ".jar" );
        File jarFile = new File( CheckTranslations.TRANSLATIONS_TEMP_DIR, flavor + ".jar" );

        FileDownload.download( deployUrl, jarFile );
        return jarFile;
    }
}
