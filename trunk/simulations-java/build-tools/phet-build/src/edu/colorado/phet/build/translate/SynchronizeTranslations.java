package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;

public class SynchronizeTranslations {
    public static void main( String[] args ) throws IOException {
        CheckTranslations.clearTempDir();
        PhetProject[] p = PhetProject.getAllProjects( new File( args[0] ) );
        for ( int i = 0; i < p.length; i++ ) {
            PhetProject phetProject = p[i];
            new SynchronizeTranslations().synchronizeTranslations( phetProject );
        }
    }

    private void synchronizeTranslations( PhetProject phetProject ) throws IOException {
        TranslationDiscrepancy[] discrepancies = new CheckTranslations( false ).checkTranslations( phetProject );
        for ( int i = 0; i < discrepancies.length; i++ ) {
            discrepancies[i].resolve();
        }
    }
}
