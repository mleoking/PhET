// Copyright 2002-2011, University of Colorado

/**
 * Class: PhotonGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.photonabsorption.PhotonAbsorptionResources;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.common.graphics.CompositeGraphic;
import edu.colorado.phet.greenhouse.common.graphics.ImageGraphic;

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
        redImage = BufferedImageUtils.rescaleFractional( PhotonAbsorptionResources.getImage( "photon-660.png" ), photonScale, photonScale );
        yellowImage = BufferedImageUtils.rescaleFractional( PhotonAbsorptionResources.getImage( "thin2.png" ), photonScale, photonScale );
        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.4, 0.4 );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        baseImage = scaleOp.filter( baseImage, null );
    }

    // Create an LUT for colors to correspond to wavelengths
    static {
        colorLUT.put( new Double( WavelengthConstants.SUNLIGHT_WAVELENGTH ), new Color( 255, 255, 120 ) );
        colorLUT.put( new Double( WavelengthConstants.IR_WAVELENGTH ), Color.red );
        colorLUT.put( new Double( WavelengthConstants.DEBUG_WAVELENGTH ), Color.green );
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
    private Point2D drawLocation=new Point2D.Double(  );

    public PhotonGraphic( Photon photon ) {
        this.photon = photon;
        photon.addObserver( this );
        isVisible = true;

        this.update();
    }

    //Provides a means for determining the global (i.e. canvas coordinate frame = pixels) for usage with ice collisions
    public Point2D getGlobalCenter2D(){
        return drawLocation;
    }

    public void update() {
        double theta = Math.atan2( -photon.getVelocity().getY(), photon.getVelocity().getX() );
        if ( theta != directionOfTravel || scaleChanged ) {
            scaleChanged = false;
            directionOfTravel = theta;

            if ( photonImage != null ) {
                removeGraphic( photonImage );
            }
            photonImage = new ImageGraphic( photon.getWavelength() > 6E-7 ? redImage : yellowImage, position ){
                @Override
                public void paint( Graphics2D g2 ) {
                    AffineTransform at = new AffineTransform( g2.getTransform() );
                    super.paint( g2 );
                    at.concatenate( new AffineTransform( getImageTx() ) );
                    try {
                         //Keep track of the photon location for ice scattering
                        drawLocation.setLocation( at.transform( new Point2D.Double( getImage().getWidth( null ) / 2, getImage().getHeight( null ) / 2 ), null ) );
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            };
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
