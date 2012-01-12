package edu.colorado.phet.buildtools;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import edu.colorado.phet.common.phetcommon.util.FileUtils;
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
                FileUtils.copyTo( animatedScreenshot, file );
                System.out.println( "Copied animated screenshot to: " + file.getAbsolutePath() );
            }

            File imageFile = project.getScreenshot( sim );


            if ( !imageFile.exists() ) {
                System.out.println( "No screenshot for: " + project.getName() + "." + sim );
            }
            else {
                BufferedImage image = ImageIO.read( imageFile );
                BufferedImage simPageScreenshot = highQualityMultiScaleToWidth( image, 300 );
                BufferedImage thumbnail = highQualityMultiScaleToWidth( image, 130 );
//                new ImageFrame( thumbnail).setVisible( true );
//                new ImageFrame( simPageScreenshot).setVisible( true );
                ImageIO.write( simPageScreenshot, "PNG", new File( project.getDeployDir(), sim + "-screenshot.png" ) );

                //convert to RGB since ImageIO has problems with alpha channel
                BufferedImage rgbThumbnail = toRGB( thumbnail );

                // write the JPEG thumbnail (for compatibility. recommended to keep since many external image references may use this)
                writeCompressedJPG( rgbThumbnail, new File( project.getDeployDir(), sim + "-thumbnail.jpg" ), 0.95f );

                // write the (enhanced quality) PNG thumbnail
                ImageIO.write( thumbnail, "PNG", new File( project.getDeployDir(), sim + "-thumbnail.png" ) );
            }

            //See SetSVNIgnoreToDeployDirectories regarding ignoring screenshots and other matter
        }
    }

    /**
     * Scale down an image using bicubic interpolation instead of bilinear.
     *
     * @return Scaled down copy of the image
     */
    private static BufferedImage highQualityMultiScaleToWidth( BufferedImage image, int width ) {
        double scale = (double) width / image.getWidth();
        int w = (int) ( image.getWidth() * scale );
        int h = (int) ( image.getHeight() * scale );
        if ( scale < 1 ) {
            return BufferedImageUtils.getScaledInstance( image, w, h, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true );
        }
        else if ( scale == 1 ) {
            return image;
        }
        else {
            return BufferedImageUtils.rescaleXMaintainAspectRatio( image, w );
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
