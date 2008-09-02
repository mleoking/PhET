/**
 * Class: PhotonGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.greenhouse.common_greenhouse.view.CompositeGraphic;
import edu.colorado.phet.greenhouse.coreadditions.graphics.BufferedImageUtils;
import edu.colorado.phet.greenhouse.coreadditions.graphics.DuotoneImageOp;
import edu.colorado.phet.greenhouse.coreadditions.graphics.ImageGraphic;

public class PhotonGraphic extends CompositeGraphic implements Observer {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static final String IMAGE_PATH = "greenhouse/images/photon-comet.png";
    private static final String IMAGE_PATH_RED = "greenhouse/images/photon-660.png";

    private static final String IMAGE_PATH_YELLOW = "greenhouse/images/thin2.png";
    private static BufferedImage baseImage;
    private static HashMap colorLUT = new HashMap();

    private static BufferedImage redImage;

    private static BufferedImage yellowImage;

    // get the base image
    static {
        try {
            baseImage = ImageLoader.loadBufferedImage( IMAGE_PATH );
            double photonScale = 0.8 * 0.7;
            redImage = BufferedImageUtils.rescaleFractional( ImageLoader.loadBufferedImage( IMAGE_PATH_RED ), photonScale, photonScale );
            yellowImage = BufferedImageUtils.rescaleFractional( ImageLoader.loadBufferedImage( IMAGE_PATH_YELLOW ), photonScale, photonScale );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.4, 0.4 );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        baseImage = scaleOp.filter( baseImage, null );
    }

    // Create an LUT for colors to correspond to wavelengths
    static {
        colorLUT.put( new Double( GreenhouseConfig.sunlightWavelength ), new Color( 255, 255, 120 ) );
        colorLUT.put( new Double( GreenhouseConfig.irWavelength ), Color.red );
        colorLUT.put( new Double( GreenhouseConfig.debug_wavelength ), Color.green );
    }

    private static Color genColor( double wavelength ) {
        return (Color) colorLUT.get( new Double( wavelength ) );
    }

    // A cache for phton graphic

    private static AffineTransform scaleTx = new AffineTransform();
    private static boolean init;
    private static Rectangle2D origBounds;

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    Point2D.Double position = new Point2D.Double();
    private Photon photon;
    private ImageGraphic photonImage;
    private boolean isVisible;
    private double directionOfTravel = Double.POSITIVE_INFINITY;
    private static boolean scaleChanged = false;

    public PhotonGraphic( Photon photon ) {
        DuotoneImageOp duotoneImageOp = new DuotoneImageOp( genColor( photon.getWavelength() ) );
        this.photon = photon;
        photon.addObserver( this );
        isVisible = true;

        this.update();
    }

    public void update() {
        double theta = Math.atan2( -photon.getVelocity().getY(), photon.getVelocity().getX() );
        if ( theta != directionOfTravel || scaleChanged ) {
            scaleChanged = false;
            directionOfTravel = theta;

            if ( photonImage != null ) {
                removeGraphic( photonImage );
            }
            photonImage = new ImageGraphic( photon.getWavelength() > 6E-7 ? redImage : yellowImage, position );
            this.addGraphic( photonImage, 0 );
        }

        // Adjust the position of the image so that the head of the photon graphic is coincident with the
        // position of the photon itself.
        double sy = 0;
        if ( photon.getVelocity().getY() > 0 ) {
            sy = 1;
        }
        double ratio = 0.03;
        position.setLocation( photon.getLocation().getX() - photonImage.getBufferedImage().getWidth() * ratio * 0.5,
                              photon.getLocation().getY() - sy * photonImage.getBufferedImage().getHeight() * ratio );
    }

    public void update( Observable o, Object arg ) {
        update();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible( boolean visible ) {
        isVisible = visible;
    }

    public Image getImage() {
        return photonImage.getBufferedImage();
    }

}
