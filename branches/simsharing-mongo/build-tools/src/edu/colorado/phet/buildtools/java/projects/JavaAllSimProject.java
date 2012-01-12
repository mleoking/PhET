// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * This standalone main class generates build file and titles for all java simulations.  So after running this main, you can build/run/deploy all-sims from PBG.
 *
 * @author Sam Reid
 */
public class JavaAllSimProject {
    public static void main( String[] args ) throws IOException {
        //Make sure the user supplied a path for trunk
        if ( args.length == 0 || !( new File( args[0] ).exists() ) ) {
            System.out.println( "Usage: args[0] = trunk" );
            System.exit( 0 );
        }
        File trunk = new File( args[0] );

        //Create the build file
        PhetProject[] all = PhetProject.getAllSimulationProjects( trunk );
        String buildFile = "#auto-generated build file for java all-sims project\n";
        buildFile += "project.depends.data=\n" +
                     "project.depends.source=\n" +
                     "project.depends.lib=";

        //List all sims as dependencies so they will all be build
        String separator = " : ";
        for ( PhetProject phetProject : all ) {
            if ( phetProject instanceof JavaProject ) {
                if ( !phetProject.getName().equalsIgnoreCase( "all-sims" ) ) {
                    JavaProject jp = (JavaProject) phetProject;
                    buildFile += jp.getName() + separator;
                }
            }
        }

        //Strip off extraneous " : ";
        buildFile = buildFile.substring( 0, buildFile.length() - separator.length() );
        buildFile += "\n";

        //Add main classes for each flavor so they can be launched
        HashSet<String> done = new HashSet<String>();
        for ( PhetProject phetProject : all ) {
            if ( phetProject instanceof JavaProject ) {
                if ( !phetProject.getName().equalsIgnoreCase( "all-sims" ) ) {
                    JavaProject jp = (JavaProject) phetProject;
                    for ( Simulation sim : jp.getSimulations() ) {
                        String key = phetProject.getName() + " * " + sim.getName();
                        if ( !done.contains( key ) ) {
                            buildFile += "project.flavor." + sim.getName() + ".mainclass=" + sim.getMainclass() + "\n";
                            done.add( key );
                        }
                    }
                }
            }
        }

        //Save the build file
        FileUtils.writeString( new File( trunk, "simulations-java/simulations/all-sims/all-sims-build.properties" ), buildFile );

        //Create the translation file containing names that will be displayed in the sim launcher
        String translationFile = new String( "#auto generated flavor names for all-sims\n" );
        for ( PhetProject phetProject : all ) {
            if ( !phetProject.getName().equalsIgnoreCase( "all-sims" ) ) {
                if ( phetProject instanceof JavaProject ) {
                    JavaProject jp = (JavaProject) phetProject;
                    for ( Simulation simulation : jp.getSimulations() ) {
                        translationFile += simulation.getName() + ".name = " + simulation.getTitle() + "\n";
                    }
                }
            }
        }

        //Save the translation file
        FileUtils.writeString( new File( trunk, "simulations-java/simulations/all-sims/data/all-sims/localization/all-sims-strings.properties" ), translationFile );
    }
}