/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.proguard;

import proguard.ant.ProGuardTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.buildtools.AntTaskRunner;

import static edu.colorado.phet.common.phetcommon.util.FileUtils.loadFileAsString;

/**
 * This command runs the ProGuard task given the ProGuard configuration and an
 * Ant task runner.
 * <p/>
 * The most complicated part of running ProGuard is setting up the configuration parameters.
 * This is done in Java (ProguardCommand.createConfigurationFile) for simplicity and flexibility
 * (would be more complicated to write this in ant.)
 * Given that the ProGuard configuration file is created in Java, it makes sense that the ProGuard command is also called from Java,
 * so that these two steps can be done as an atomic operation.  Also, this makes the build-jar Java code look like:
 * <p/>
 * clean();
 * compile();
 * jar();
 * proguard();
 * <p/>
 * instead of sometimes relying on the caller to proguard.
 */
public class ProguardCommand {
    private final ProguardConfig config;
    private final AntTaskRunner antTaskRunner;

    public ProguardCommand( ProguardConfig config, AntTaskRunner antTaskRunner ) {
        this.config = config;
        this.antTaskRunner = antTaskRunner;
    }

    public void execute() throws Exception {
        createConfigurationFile();

        ProGuardTask proGuardTask = new ProGuardTask();
        proGuardTask.setProject( antTaskRunner.getProject() );
        proGuardTask.setConfiguration( config.getProguardOutputFile() );
        // WARNING! If you are going to configure proguardTask using its setters, do so after calling setConfiguration.

        antTaskRunner.runTask( proGuardTask );
    }

    /*
     * Creates a ProGuard configuration file.
     * The top lines of this file are written explicitly here.
     * The bottom lines are appended from a template file.
     * WARNING! Settings in the template file will override whatever you have specified explicitly here.
     */
    private void createConfigurationFile() throws IOException {
        String newline = System.getProperty( "line.separator" );


        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( config.getProguardOutputFile() ) );

        try {
            bufferedWriter.write( "# Proguard configuration file for " + config.getName() + "." + newline );
            bufferedWriter.write( "# Automatically generated" + newline );

            ArrayList done = new ArrayList();
            for ( int i = 0; i < config.getInputJars().length; i++ ) {
                File file = config.getInputJars()[i].getCanonicalFile();
                if ( !done.contains( file ) ) {
                    bufferedWriter.write( "-injars '" + file.getAbsolutePath() + "'" + newline );
                    System.out.println( "i=" + i + ", config.getInputJars()[i].getAbsolutePath() = " + file );
                    done.add( file );
                }
                else {
                    System.out.println( "Already contained JAR, not writing duplicate: " + file );
                }
            }

            bufferedWriter.write( "-outjars '" + config.getOutputJar().getAbsolutePath() + "'" + newline );

            // all platforms (Java 1.7 required on Mac OS)
            bufferedWriter.write( "-libraryjars <java.home>/lib/rt.jar" + newline );

            for ( int i = 0; i < config.getMainClasses().length; i++ ) {
                bufferedWriter.write( "-keepclasseswithmembers public class " + config.getMainClasses()[i] + "{" + newline +
                                      "    public static void main(java.lang.String[]);" + newline +
                                      "}" + newline );
                bufferedWriter.newLine();
            }

            bufferedWriter.write( "# shrink = " + config.getShrink() + newline );
            if ( !config.getShrink() ) {
                bufferedWriter.write( "-dontshrink" + newline );
            }

            //Write the primary Proguard configuration file.
            String text = loadFileAsString( config.getProguardTemplate() );
            bufferedWriter.write( text );

            //Write any project-specific proguard configuration files.
            for ( File file : config.getAdditionalConfigFiles() ) {

                //Make sure the last and first lines don't run together.
                //This is also necessary from the original config.getProguardTemplate as well as between all pairs of additional config files.
                bufferedWriter.write( '\n' );

                //Write the config file to the destination
                bufferedWriter.write( loadFileAsString( file ) );
            }

        }
        finally {
            bufferedWriter.close();
        }

    }

}
