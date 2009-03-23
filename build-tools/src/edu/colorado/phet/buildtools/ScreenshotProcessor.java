package edu.colorado.phet.buildtools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

public class ScreenshotProcessor {
    public void copyScreenshotsToDeployDir( PhetProject project ) throws IOException {
        String[] s = project.getSimulationNames();
        for ( int i = 0; i < s.length; i++ ) {
            String sim = s[i];
            File imageFile = project.getScreenshot( sim );
            BufferedImage image = ImageIO.read( imageFile );
            BufferedImage simPageScreenshot = BufferedImageUtils.multiScaleToWidth( image, 300 );
            BufferedImage thumbnail = BufferedImageUtils.multiScaleToWidth( image, 130 );
            ImageIO.write( simPageScreenshot, "PNG", new File( project.getDeployDir(), sim + "-screenshot.png" ) );
            ImageIO.write( thumbnail, "PNG", new File( project.getDeployDir(), sim + "-thumbnail.png" ) );

            //TODO: add ignores to the folder
        }
    }

    public static void main( String[] args ) throws IOException {
        new ScreenshotProcessor().copyScreenshotsToDeployDir( new JavaSimulationProject( new File( "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\balloons" ) ) );
    }
}
