package edu.colorado.phet.build.translate;

import java.io.File;

/**
 * Created by: Sam
 * Feb 28, 2008 at 11:55:38 PM
 */
public class ImportAndAddBatch {
    private File baseDir;

    public ImportAndAddBatch( File baseDir ) {
        this.baseDir = baseDir;
    }

    public static void main( String[] args ) throws Exception {
        if ( args.length != 1 ) {
            System.out.println( "usage: ImportAndAddBatch path-to-simulations-java-in-your-workspace" );
            System.exit( 1 );
        }
        new ImportAndAddBatch( new File( args[0] ) ).importAndAddBatch( AddTranslation.prompt( "Which Directory to import and batch add?" ) );
    }

    private void importAndAddBatch( String dir ) throws Exception {
        System.out.println( "Importing" );
        new ImportTranslations( baseDir ).importTranslations( new File( dir ) );
        System.out.println( "Finished Importing." );

        System.out.println( "Adding to tigercat" );
        String username = AddTranslation.prompt( "Production Server username:" );
        String password = AddTranslation.prompt( "Production Server password:" );
        new AddTranslationBatch( baseDir, new File( dir ), username, password ).runBatch( true );
        System.out.println( "Finished Adding" );
    }
}
