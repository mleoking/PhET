package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import java.nio.file.Files;
//import java.nio.file.Path;

import edu.colorado.phet.translationutility.simulations.Simulation;

//Convert Flash XML strings to JavaScript RequireJS i18n format, requires Java 7
public class XMLToRequireJSI18n {
    public static class All {
        static final ArrayList<String> alreadyDone = new ArrayList<String>();

        public static void main( String[] args ) throws IOException, Simulation.SimulationException {
            visit( new File( "/Users/samreid/phet-svn-trunk/simulations-flash" ) );
            visit( new File( "/Users/samreid/phet-svn-trunk/simulations-flex" ) );
        }

        private static void visit( File f ) throws IOException, Simulation.SimulationException {
            if ( f.isDirectory() ) {
                File[] files = f.listFiles();
                for ( int i = 0; i < files.length; i++ ) {
                    File file = files[i];
                    visit( file );
                }
            }
            else {
                if ( f.getAbsolutePath().endsWith( ".xml" ) &&
                     f.getParentFile().getName().equals( "localization" ) &&
                     f.getAbsolutePath().contains( "-strings_" ) ) {
                    System.out.println( "Found XML file: " + f.getAbsolutePath() );
                    String projectName = f.getParentFile().getParentFile().getName();

                    String localizationDir = f.getParentFile().getAbsolutePath();
                    if ( !alreadyDone.contains( localizationDir ) ) {
                        File file = new File( "/Users/samreid/github/babel/autoport/" + projectName );
                        if ( file.exists() ) {
                            System.err.println( "Danger!  Directory already existed, there may be a collision: " + file.getAbsolutePath() );
                            System.exit( 0 );
                        }
                        file.mkdirs();

                        XMLToRequireJSI18n.main( new String[]{localizationDir, file.getAbsolutePath()} );
                        alreadyDone.add( localizationDir );
                    }
                }
            }
        }
    }
    public static void main( String[] args ) throws IOException, Simulation.SimulationException {
        File tempDir = new File( "/Users/samreid/temp-strings/" + System.currentTimeMillis() );
        tempDir.mkdirs();

        System.out.println( "Converting XML to properties with temp path: " + tempDir );
        XMLToProperties.main( new String[]{args[0], tempDir.getAbsolutePath()} );

        System.out.println( "Converting Properties to RequireJS" );
        PropertiesToRequireJSI18n.main( new String[]{tempDir.getAbsolutePath(), args[1]} );

        System.out.println( "Done" );
    }
}
