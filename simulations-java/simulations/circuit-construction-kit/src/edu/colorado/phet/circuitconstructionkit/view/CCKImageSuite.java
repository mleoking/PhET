/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.circuitconstructionkit.view;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:38:59 PM
 * <p/>
 * Testing cvs.
 */
public class CCKImageSuite {

    private ImageSuite lifelikeImageSuite;
    private BufferedImage closedImage;

    private BufferedImage resistorImage;
    private ImageLoader imageLoader = new ImageLoader();
    private BufferedImage capacitorImage;

    private BufferedImage acImage;
    private BufferedImage inductorImage;
    private String inductorImageLoc = "circuit-construction-kit/images/inductor2.gif";
    private String batteryImageLocation = "circuit-construction-kit/images/AA-battery-100.gif";
    private String resistorImageLocation = "circuit-construction-kit/images/resistor3.gif";
    private String capImageLoc = "circuit-construction-kit/images/cap5.gif";
    private String acImageLoc = "circuit-construction-kit/images/ac.gif";

    public CCKImageSuite() throws IOException {
        loadImages();
    }

    public BufferedImage getParticleImage() throws IOException {
//        return imageLoader.loadImage( "cck/images/electron9.gif" );
        return imageLoader.loadImage( "circuit-construction-kit/images/electron10.png" );
//        return imageLoader.loadImage( "cck/images/electron-2.png" );
    }

    private void loadImages() throws IOException {
        resistorImage = ImageLoader.loadBufferedImage( resistorImageLocation );
        BufferedImage batteryImage = ImageLoader.loadBufferedImage( batteryImageLocation );
        lifelikeImageSuite = new ImageSuite( resistorImage, batteryImage, closedImage );
        capacitorImage = ImageLoader.loadBufferedImage( capImageLoc );
//        acImage = BufferedImageUtils.flipY( ImageLoader.loadBufferedImage( acImageLoc ) );//todo used in phetgraphics implementation
        acImage = ImageLoader.loadBufferedImage( acImageLoc );
        inductorImage = ImageLoader.loadBufferedImage( inductorImageLoc );
    }

    public ImageSuite getLifelikeSuite() {
        return lifelikeImageSuite;
    }

    public BufferedImage getKnifeHandleImage() {
        try {
            return imageLoader.loadImage( "circuit-construction-kit/images/handle8.gif" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getKnifeBoardImage() {
        try {
            return imageLoader.loadImage( "circuit-construction-kit/images/knifeBoard.gif" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public BufferedImage getACVoltageSourceImage() {
        return acImage;
    }

    public BufferedImage getInductorImage() {
        return inductorImage;
    }

    public static CCKImageSuite getInstance() {
        try {
            return new CCKImageSuite();
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
