package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;

//Usage example:
// java edu.colorado.phet.build.translate.SynchronizeTranslations C:/phet/trunk/simulations-java reids energy-skate-park
//also don't forget to deploy any new JNLP files, using the add-translation-jnlp task
public class SynchronizeTranslations {
    public static void main( String[] args ) throws IOException {
        CheckTranslations.clearTempDir();
        PhetProject[] p = args.length == 2 ? PhetProject.getAllProjects( new File( args[0] ) ) :
                          new PhetProject[]{new PhetProject( new File( args[0], "simulations/" + args[2] ) )};
        for ( int i = 0; i < p.length; i++ ) {
            new SynchronizeTranslations().synchronizeTranslations( p[i], args[1] );
        }
    }

    private void synchronizeTranslations( PhetProject phetProject, String username ) throws IOException {
        TranslationDiscrepancy[] discrepancies = new CheckTranslations( false ).checkTranslations( phetProject );
        for ( int i = 0; i < discrepancies.length; i++ ) {
            discrepancies[i].resolve( username );
        }
    }
}
