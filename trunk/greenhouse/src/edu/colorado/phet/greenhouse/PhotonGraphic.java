/**
 * Class: PhotonGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.BufferedImageUtils;
import edu.colorado.phet.coreadditions.graphics.DuotoneImageOp;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class PhotonGraphic extends CompositeGraphic implements Observer /*, ShapeGraphicType*/ {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static final String IMAGE_PATH = "images/photon-comet.png";
    private static BufferedImage baseImage;
    private static HashMap colorLUT = new HashMap();
    private double xOffset;
    private double yOffset;

    static {
        baseImage = new ImageLoader().fetchBufferedImage( IMAGE_PATH );
        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.4, 0.4 );
//        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.5, 0.5 );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        baseImage = scaleOp.filter( baseImage, null );
    }

    static {
        colorLUT.put( new Double( GreenhouseConfig.sunlightWavelength ), new Color( 255, 255, 120 ) );
        colorLUT.put( new Double( GreenhouseConfig.irWavelength ), Color.red );
        colorLUT.put( new Double( GreenhouseConfig.debug_wavelength ), Color.green );
    }

    private static Color genColor( double wavelength ) {
        return (Color)colorLUT.get( new Double( wavelength ) );
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private Photon photon;
    private ImageGraphic photonImage;
    private boolean isVisible;
    private double directionOfTravel = Double.POSITIVE_INFINITY;

    public PhotonGraphic( Photon photon ) {
        this.photon = photon;
        photon.addObserver( this );
        isVisible = true;
        this.update();
    }

    Point2D.Double p = new Point2D.Double( );

    public void update() {
        double theta = Math.atan2( -photon.getVelocity().getY(), photon.getVelocity().getX() );
        computeOffsets( theta );
        p.setLocation( photon.getLocation().getX() - xOffset * 0.001, photon.getLocation().getY() + yOffset * 0.01);
        if( theta != directionOfTravel ) {
            directionOfTravel = theta;
            DuotoneImageOp dio = new DuotoneImageOp( genColor( photon.getWavelength() ) );
            BufferedImage bi = dio.filter( baseImage, null );
            // Rotate the image
            BufferedImage bi2 = BufferedImageUtils.getRotatedImage( bi, directionOfTravel );

            if( photonImage != null ) {
                removeGraphic( photonImage );
            }
//            computeOffsets( theta );
//            Point2D.Double p = new Point2D.Double( photon.getLocation().getX(), photon.getLocation().getY() );
//            Point2D.Double p = new Point2D.Double( photon.getLocation().getX() - xOffset, photon.getLocation().getY() - yOffset );
            photonImage = new ImageGraphic( bi2, p );
//            photonImage = new ImageGraphic( bi2, photon.getLocation() );
//            photonImage = new ImageGraphic( bi2, photon.getLocation() );
            this.addGraphic( photonImage, 0 );
        }
//        photonImage.location = photon.getLocation();
//        photonImage.location.setLocation( photon.getLocation().getX() - xOffset, photon.getLocation().getY() - yOffset );
//        photonImage.location.setLocation( (int)( photon.getLocation().getX() - xOffset ), (int)( photon.getLocation().getY() - yOffset ) );
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


    public void paint( Graphics2D g2 ) {
        super.paint( g2 );
    }

    /**
     * Computes the offsets that must be applied to the buffered image's location so that it's head is
     * at the location of the photon
     *
     * @param theta
     */
    private void computeOffsets( double theta ) {
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );
//        this.theta = theta;

        xOffset = 0;
        yOffset = 0;
        double alpha = 0;
        double w = baseImage.getWidth();
        double h = baseImage.getHeight();
        if( theta >= 0 && theta <= Math.PI / 2 ) {
            xOffset = w * Math.cos( theta ) + ( h / 2 ) * Math.sin( theta );
            yOffset = w * Math.sin( theta ) + ( h / 2 ) * Math.cos( theta );
        }
        if( theta > Math.PI / 2 && theta <= Math.PI ) {
            alpha = theta - Math.PI / 2;
            xOffset = ( h / 2 ) * Math.cos( alpha );
            yOffset = w * Math.cos( alpha ) + ( h / 2 ) * Math.sin( alpha );
        }
        if( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            alpha = theta - Math.PI;
            xOffset = ( h / 2 ) * Math.sin( alpha );
            yOffset = ( h / 2 ) * Math.cos( alpha );
        }
        if( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            alpha = Math.PI * 2 - theta;
            xOffset = w * Math.cos( alpha ) + ( h / 2 ) * Math.sin( alpha );
            yOffset = ( h / 2 ) * Math.cos( alpha );
        }

        return;
    }
}
