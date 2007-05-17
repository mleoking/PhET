package edu.colorado.phet.build.util;

import edu.colorado.phet.build.PhetAllSimTask;
import edu.colorado.phet.build.PhetBuildUtils;
import edu.colorado.phet.build.PhetProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * May 16, 2007, 10:14:45 PM
 */
public class StandardizeSimUtil {
    public static void main( String[] args ) throws IOException {
        File simsroot = new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" );
        String[] names = PhetAllSimTask.getSimNames( simsroot );
        for( int i = 0; i < names.length; i++ ) {
            String name = names[i];
            System.out.println( "name = " + name );
            File projectParentDir = PhetBuildUtils.resolveProject( simsroot.getParentFile(), name );
            PhetProject phetProject = new PhetProject( projectParentDir, name );
            System.out.println( "phetProject = " + phetProject.getName() );
            File simDir = new File( simsroot, name + "/data/" + name );
            if( simDir.exists() ) {
                String[] f = simDir.list();
                System.out.println( "Arrays.asList( f ) = " + Arrays.asList( f ) );
            }
            else {
                System.out.println( simDir.getAbsolutePath() + " doesn't exist" );
                simDir.mkdirs();
            }
            File propFile = new File( simDir, name + ".properties" );
            if( !propFile.exists() ) {

                String text = "# " + name + ".properties\n" +
                              "#--------------------------------------------------------------------------\n" +
                              "\n" +
                              "version.major=0\n" +
                              "version.minor=00\n" +
                              "version.dev=00\n" +
                              "version.revision=sandbox";

                createFile( propFile, text );
                System.out.println( "created property file: " + propFile );
            }
            File localizationDir = new File( simDir, "localization" );
            if( !localizationDir.exists() ) {
                localizationDir.mkdirs();
            }
            File englishLocalizationFile = new File( localizationDir, name + "-strings.properties" );
            if( !englishLocalizationFile.exists() ) {
                createFile( englishLocalizationFile, "#===============================================================================\n" +
                                                     "#\n" +
                                                     "#  US English (en) localized strings for \"" + name + "\" simulation.\n" +
                                                     "#\n" +
                                                     "#===============================================================================\n" +
                                                     "\n" +
                                                     "#===============================================================================\n" +
                                                     "# Mandatory properties\n" +
                                                     "#===============================================================================\n" +
                                                     "\n" +
                                                     "" + name + ".name         =" + name + "\n" +
                                                     "" + name + ".description  =<html>\\\n" +
                                                     "" + name + "\\\n" +
                                                     "</html>" );
                System.out.println( "created englishLocalizationFile = " + englishLocalizationFile.getAbsolutePath() );
            }
        }
    }

    private static void createFile( File propFile, String text ) throws IOException {
        propFile.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( propFile ) );

        bufferedWriter.write( text );
        bufferedWriter.close();
    }
}
