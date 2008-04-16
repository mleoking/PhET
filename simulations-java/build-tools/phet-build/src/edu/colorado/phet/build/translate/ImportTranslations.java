package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;

/**
 * Still under development.
 * <p/>
 * Utility to take many translations from a single directory and move them into the repository.
 */
public class ImportTranslations {
    private File basedir;
    private boolean addSVN = true;
    private String prefix;

    public ImportTranslations( File basedir ) {
        this( basedir, null );
    }

    public ImportTranslations( File basedir, String prefix ) {
        this.basedir = basedir;
        this.prefix = prefix;
    }

    public static void main( String[] args ) throws IOException {
        new ImportTranslations( new File( args[0] ) ).importTranslations( new File( args[1] ) );
    }

    public void importTranslations( File dir ) throws IOException {
        ArrayList simNames=new ArrayList( );
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            importTranslation( files[i] );
            simNames.add(getSimName( files[i] ));
        }
        String s="";
        for ( int i = 0; i < simNames.size(); i++ ) {
            String s1 = (String) simNames.get( i );
            s+=s1+" ";
        }
        System.out.println( "added simulations: "+s );
    }

    private void importTranslation( File file ) throws IOException {
        String simname = getSimName( file );
        System.out.println( "simname = " + simname );
        if ( simname == null ) {
            System.out.println( "ignoring non-localization file: " + simname );
        }
        else {
            try {
                PhetProject phetProject = new PhetProject( new File( basedir + "/simulations", simname ) );
                System.out.println( "phetProject = " + phetProject );
                File localizationDir = phetProject.getLocalizationDir();
                final File dst = new File( localizationDir, file.getName() );
                FileUtils.copyTo( file, dst );
                if ( prefix != null ) {
                    FileUtils.addPrefix( dst, prefix );
                }
                if ( addSVN ) {
                    Runtime.getRuntime().exec( "svn add " + dst.getAbsolutePath() );
                }
            }
            catch ( FileNotFoundException e ) {
                System.out.println( "skipping: " + file.getAbsolutePath() );
            }
        }
    }

    private String getSimName( File file ) {
        String simname = null;
        final int index = file.getName().indexOf( "-strings_" );
        if ( index != -1 ) {
            simname = file.getName().substring( 0, index );
        }
        return simname;
    }

    public void setPrefix( String prefix ) {
        this.prefix = prefix;
    }
}
