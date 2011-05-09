/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.proguard;

import proguard.ant.ProGuardTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.util.FileUtils;

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

            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac os x" ) ) {
                String macPath = "/System/Library/Frameworks/JavaVM.framework/Classes";
                bufferedWriter.write( "-libraryjars " + macPath + "/classes.jar" + newline );
                bufferedWriter.write( "-libraryjars " + macPath + "/ui.jar" + newline );
            }
            else {
                bufferedWriter.write( "-libraryjars <java.home>/lib/rt.jar" + newline ); // Windows, Linux
            }
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

            String text = FileUtils.loadFileAsString( config.getProguardTemplate() );
            bufferedWriter.write( text );

        }
        finally {
            bufferedWriter.close();
        }

    }

}
