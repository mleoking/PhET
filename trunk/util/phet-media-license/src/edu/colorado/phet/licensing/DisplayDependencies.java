package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DisplayDependencies {
    private static File trunk = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2" );

    public static void main( String[] args ) throws IOException {
        new DisplayDependencies().start();
    }

    private void start() throws IOException {
        System.out.println( "PhET Java Software Dependencies\n" + new Date() + "\n" );

        File baseDir = new File( trunk, "simulations-java" );
        String[] simNames = PhetProject.getSimNames( baseDir );
        for ( int i = 0; i < simNames.length; i++ ) {
            visitSim( simNames[i] );
        }
    }

    private void visitSim( String simName ) throws IOException {
        SimInfo sim = SimInfo.getSimInfo( trunk, simName );
        System.out.println( sim.getIssues() );
    }
}
