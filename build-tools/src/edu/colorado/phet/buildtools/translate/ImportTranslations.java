package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Still under development.
 * <p/>
 * Utility to take many translations from a single directory and move them into the IDE workspace.
 */
public class ImportTranslations {

    private static final boolean DO_SVN_ADD = true;

    private final File basedir;

    public ImportTranslations( File basedir ) {
        this.basedir = basedir;
    }

    public static void main( String[] args ) throws IOException {
        new ImportTranslations( new File( args[0] ) ).importTranslations( new File( args[1] ) );
    }

    public void importTranslations( File dir ) throws IOException {
        ArrayList simNames = new ArrayList();
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            importTranslation( files[i] );
            simNames.add( getSimName( files[i] ) );
        }
        String s = "";
        for ( int i = 0; i < simNames.size(); i++ ) {
            String s1 = (String) simNames.get( i );
            s += s1 + " ";
        }
        System.out.println( "added simulations: " + s );
    }

    private void importTranslation( File file ) throws IOException {
        String simname = getSimName( file );
        System.out.println( "simname = " + simname );
        if ( simname == null ) {
            System.out.println( "ignoring non-localization file: " + simname );
        }
        else {
            try {
                PhetProject phetProject = new JavaSimulationProject( new File( basedir + "/simulations", simname ) );
                System.out.println( "phetProject = " + phetProject );
                File localizationDir = phetProject.getLocalizationDir();
                final File dst = new File( localizationDir, file.getName() );
                FileUtils.copyTo( file, dst );
                if ( DO_SVN_ADD ) {
                    /* 
                     * This adds the file to the SVN repository if it didn't already exist.
                     * It does not commit files that already exist, so that we have the 
                     * opportunity to manually review them.
                     */
                    String cmd = "svn add " + dst.getAbsolutePath();
                    System.out.println( cmd );
                    Process process = Runtime.getRuntime().exec( cmd );
                    int rval = process.waitFor();
                    if ( rval < 0 ) {
                        System.err.println( cmd + " failed, rval=" + rval );
                    }
                }
            }
            catch( FileNotFoundException e ) {
                System.out.println( "skipping: " + file.getAbsolutePath() );
            }
            catch( InterruptedException ie ) {
                ie.printStackTrace();
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
}
