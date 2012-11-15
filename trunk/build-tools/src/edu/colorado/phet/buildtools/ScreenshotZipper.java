package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Creates a directory of all the screenshots (for zipping)
 *
 * @author Sam Reid
 */
public class ScreenshotZipper {
    public void copyScreenshots( PhetProject project, final File destDir ) throws IOException {
        String[] s = project.getSimulationNames();
        for ( String sim : s ) {
            copyIt( destDir, project.getScreenshot( sim ) );
            copyIt( destDir, project.getAnimatedScreenshot( sim ) );
        }
    }

    private void copyIt( final File destDir, final File imageFile ) throws IOException {
        if ( imageFile.exists() ) {
            final File dest = new File( destDir, imageFile.getName() );
            if ( dest.exists() ) {
                throw new RuntimeException( "Target exists, file = " + dest.getAbsolutePath() );
            }
            FileUtils.copyTo( imageFile, dest );
        }
        else {
            System.out.println( "Source not exist: " + imageFile.getAbsolutePath() );
        }
    }

    public static void main( String[] args ) throws IOException {
        PhetProject[] projects = PhetProject.getAllSimulationProjects( new File( args[0] ) );
        for ( int i = 0; i < projects.length; i++ ) {
            new ScreenshotZipper().copyScreenshots( projects[i], new File( args[1] ) );
        }
    }
}
