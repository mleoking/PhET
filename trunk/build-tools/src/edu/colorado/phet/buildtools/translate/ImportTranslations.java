package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Still under development.
 * <p/>
 * Utility to take many translations from a single directory and move them into the IDE workspace.
 */
public class ImportTranslations {

    private static final boolean DO_SVN_ADD = true;

    private final File trunk;

    public ImportTranslations( File trunk ) {
        this.trunk = trunk;
    }

    public static void main( String[] args ) throws IOException {
        new ImportTranslations( new File( args[0] ) ).importTranslations( new File( args[1] ) );
    }

    public void importTranslations( File dir ) throws IOException {
        ArrayList simNames = new ArrayList();
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            Translation translation = new Translation( files[i] );
            importTranslation( translation );
            simNames.add( translation.getSimName() );
        }
        String s = "";
        for ( int i = 0; i < simNames.size(); i++ ) {
            String s1 = (String) simNames.get( i );
            s += s1 + " ";
        }
        System.out.println( "added simulations: " + s );
    }

    private void importTranslation( Translation translation ) throws IOException {
        File file = translation.getFile();
        String simName = translation.getSimName();
        String simType = translation.getType();
        System.out.println( "simname = " + simName + " (" + simType + ")" );
        if ( !translation.isValid() ) {
            System.out.println( "ignoring non-localization file: " + file.getName() );
        }
        else {
            try {
                PhetProject phetProject = translation.getProject( trunk );
                
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
}
