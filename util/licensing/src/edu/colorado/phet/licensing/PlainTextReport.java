package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class PlainTextReport {

    public static void main( String[] args ) throws IOException {

        if ( args.length != 1 ) {
            System.out.println( "usage: " + PlainTextReport.class.getName() + " absolute_path_to_trunk" );
            System.exit( 1 );
        }

        File trunk = new File( args[0] );
        if ( !trunk.isDirectory() ) {
            System.out.println( trunk + " is not a directory" );
            System.exit( 1 );
        }

        new PlainTextReport().start( trunk );
    }

    private void start( File trunk ) throws IOException {
        System.out.println( "PhET Java Software Dependencies\n" + new Date() + "\n" );

        File baseDir = new File( trunk, BuildToolsPaths.SIMULATIONS_JAVA );
        String[] simNames = PhetProject.getSimNames( baseDir );
        for ( int i = 0; i < simNames.length; i++ ) {
            visitSim( simNames[i] );
        }
    }

    private void visitSim( String simName ) throws IOException {
        SimInfo sim = SimInfo.getSimInfo(Config.TRUNK, simName );
        System.out.println( sim.getIssues() );
    }
}
