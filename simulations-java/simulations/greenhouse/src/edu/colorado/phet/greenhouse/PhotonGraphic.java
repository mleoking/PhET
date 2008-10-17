/**
 * Class: PhotonGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.greenhouse.coreadditions.graphics.BufferedImageUtils;
import edu.colorado.phet.greenhouse.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.greenhouse.phetcommon.view.CompositeGraphic;

public class PhotonGraphic extends CompositeGraphic implements Observer {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static BufferedImage baseImage;
    private static HashMap colorLUT = new HashMap();

    private static BufferedImage redImage;

    private static BufferedImage yellowImage;

    // get the base image
    static {
        baseImage = GreenhouseResources.getImage( "photon-comet.png" );
        double photonScale = 0.8 * 0.7;
        redImage = BufferedImageUtils.rescaleFractional( GreenhouseResources.getImage( "photon-660.png" ), photonScale, photonScale );
        yellowImage = BufferedImageUtils.rescaleFractional( GreenhouseResources.getImage( "thin2.png" ), photonScale, photonScale );
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

    // A cache for phton graphic

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
