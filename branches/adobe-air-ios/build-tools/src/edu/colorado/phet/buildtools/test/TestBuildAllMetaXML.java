package edu.colorado.phet.buildtools.test;

import java.io.File;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;

public class TestBuildAllMetaXML {
    public static void main( String[] args ) {
        File trunk = new File( args[0] );
        BuildLocalProperties.initRelativeToTrunk( trunk );
        try {
            for ( PhetProject project : JavaSimulationProject.getJavaSimulations( trunk ) ) {
                project.writeMetaXML();
            }

            for ( PhetProject project : FlashSimulationProject.getFlashProjects( trunk ) ) {
                project.writeMetaXML();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}