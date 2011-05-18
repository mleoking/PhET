package edu.colorado.phet.buildtools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * Subsamples sim screenshot (full size screenshot in project/screenshots/flavor-screenshot.png
 * to produce the sim-page screenshot and sim thumbnail, which are deployed in the deploy process
 * <p/>
 * See #1505
 *
 * @author Sam Reid
 */
public class ScreenshotProcessor {
    public void copyScreenshotsToDeployDir( PhetProject project ) throws IOException {
        String[] s = project.getSimulationNames();
        for ( int i = 0; i < s.length; i++ ) {
            String sim = s[i];

            //copy the animated screenshot, if it exists
            File animatedScreenshot = project.getAnimatedScreenshot( sim );
            if ( animatedScreenshot.exists() ) {
                File file = new File( project.getDeployDir(), animatedScreenshot.getName() );
                edu.colorado.phet.common.phetcommon.util.FileUtils.copyTo( animatedScreenshot, file );
                System.out.println( "Copied animated screenshot to: " + file.getAbsolutePath() );
            }

            File imageFile = project.getScreenshot( sim );


            if ( !imageFile.exists() ) {
                System.out.println( "No screenshot for: " + project.getName() + "." + sim );
            }
            else {
                BufferedImage image = ImageIO.read( imageFile );
                BufferedImage simPageScreenshot = BufferedImageUtils.multiScaleToWidth( image, 300 );
                BufferedImage thumbnail = BufferedImageUtils.multiScaleToWidth( image, 130 );
//                new ImageFrame( thumbnail).setVisible( true );
//                new ImageFrame( simPageScreenshot).setVisible( true );
                ImageIO.write( simPageScreenshot, "PNG", new File( project.getDeployDir(), sim + "-screenshot.png" ) );
//            ImageIO.write( simPageScreenshot, "JPG", new File( project.getDeployDir(), sim + "-screenshot.jpg" ) );
//            ImageIO.write( thumbnail, "PNG", new File( project.getDeployDir(), sim + "-thumbnail.png" ) );

                //convert to RGB since ImageIO has problems with alpha channel
                ImageIO.write( toRGB( thumbnail ), "JPEG", new File( project.getDeployDir(), sim + "-thumbnail.jpg" ) );
            }

            //quality = 0.9 looks worse and has larger file size than png for simPageScreenshot, let's leave that as PNG
//            float quality = 0.9f;
//            File file = new File( project.getDeployDir(), sim + "-screenshot-" + quality + ".jpg" );
//
//            writeCompressedJPG( simPageScreenshot, file, quality );

            //See SetSVNIgnoreToDeployDirectories regarding ignoring screenshots and other matter
        }
    }

    private static BufferedImage toRGB( Image src ) {
        int w = src.getWidth( null );
        int h = src.getHeight( null );
        int type = BufferedImage.TYPE_INT_RGB;  // other options
        BufferedImage dest = new BufferedImage( w, h, type );
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage( src, 0, 0, null );
        g2.dispose();
        return dest;
    }


    ////see http://www.universalwebservices.net/web-programming-resources/java/adjust-jpeg-image-compression-quality-when-saving-images-in-java
    //TODO: Java almanac suggests this doesn't work in 1.4
    private void writeCompressedJPG( BufferedImage image, File file, float quality ) throws IOException {
        Iterator iter = ImageIO.getImageWritersByFormatName( "jpeg" );
        ImageWriter writer = (ImageWriter) iter.next();
// instantiate an ImageWriteParam object with default compression options
        ImageWriteParam iwp = writer.getDefaultWriteParam();

//Now, we can set the compression quality:
        iwp.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
        iwp.setCompressionQuality( quality );   // an integer between 0 and 1
// 1 specifies minimum compression and maximum quality


//Output the file:

        FileImageOutputStream output = new FileImageOutputStream( file );
        writer.setOutput( output );
        IIOImage a = new IIOImage( image, null, null );
        writer.write( null, a, iwp );
    }

    /**
     * Running ScreenshotProcessor batch processes all image subsampling and prepares a report of missing screenshots.
     *
     * @param args
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException {
        String fallbackDir = "C:\\workingcopy\\phet\\svn\\trunk";
        if ( args.length == 0 && !new File( fallbackDir ).exists() ) {
            System.out.println( "Specify trunk on command line" );
        }
        else {
            PhetProject[] projects = PhetProject.getAllSimulationProjects( new File( args.length > 0 ? args[0] : fallbackDir ) );
            for ( int i = 0; i < projects.length; i++ ) {
                PhetProject project = projects[i];
                new ScreenshotProcessor().copyScreenshotsToDeployDir( project );
            }
        }
//        new ScreenshotProcessor().copyScreenshotsToDeployDir( new JavaSimulationProject( new File( "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\balloons" ) ) );
    }
}
