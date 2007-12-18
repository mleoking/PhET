package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Dec 17, 2007 at 10:06:25 PM
 */
public class ImportTranslations {
    private File basedir;
    private boolean addSVN = true;

    public ImportTranslations( File basedir ) {
        this.basedir = basedir;
    }

    public static void main( String[] args ) throws IOException {
        new ImportTranslations( new File( args[0] ) ).importTranslations( new File( args[1] ) );
    }

    private void importTranslations( File dir ) throws IOException {
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            importTranslation( files[i] );
        }
    }

    private void importTranslation( File file ) throws IOException {
        String simname = file.getName().substring( 0, file.getName().indexOf( "-strings_" ) );
        System.out.println( "simname = " + simname );
        try {
            PhetProject phetProject = new PhetProject( new File( basedir + "/simulations", simname ) );
            System.out.println( "phetProject = " + phetProject );
            File localizationDir = phetProject.getLocalizationDir();
            final File dst = new File( localizationDir, file.getName() );
            FileUtils.copyTo( file, dst );
            if ( addSVN ) {
                Runtime.getRuntime().exec( "svn add " + dst.getAbsolutePath() );
            }
        }
        catch( FileNotFoundException e ) {
            System.out.println( "skipping: " + file.getAbsolutePath() );
        }
    }
}
