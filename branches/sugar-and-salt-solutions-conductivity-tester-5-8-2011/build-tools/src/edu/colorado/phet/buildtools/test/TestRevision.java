package edu.colorado.phet.buildtools.test;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.statistics.StatisticsProject;

public class TestRevision {
    public static void main( String[] args ) {
        try {
            File trunk = new File( args[0] );

            BuildLocalProperties.initRelativeToTrunk( trunk );

            BuildScript script = new BuildScript( trunk, new StatisticsProject( new File( trunk, BuildToolsPaths.STATISTICS ) ) );

            for ( int i = 0; i < 15; i++ ) {
                int test = script.getRevisionOnTrunkREADME();
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
