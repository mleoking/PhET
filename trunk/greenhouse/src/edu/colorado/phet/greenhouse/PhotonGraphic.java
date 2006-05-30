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
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class PhotonGraphic extends CompositeGraphic implements Observer {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static final String IMAGE_PATH = "images/photon-comet.png";
    private static BufferedImage baseImage;
    private static HashMap colorLUT = new HashMap();
    Point2D.Double p = new Point2D.Double();
    private double xOffset;
    private double yOffset;

    static {
        baseImage = new ImageLoader().fetchBufferedImage( IMAGE_PATH );
        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.4, 0.4 );
//        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.5, 0.5 );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        baseImage = scaleOp.filter( baseImage, null );
    }
    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------
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
    static AffineTransform scaleTx = new AffineTransform();
    static boolean init;
    static Rectangle2D origBounds;

    private Photon photon;
    private ImageGraphic photonImage;
    private ShapeGraphicType debugGraphic;
    private boolean isVisible;
    private double directionOfTravel = Double.POSITIVE_INFINITY;
    private boolean scaleChanged = false;

    public PhotonGraphic( Component component, Photon photon ) {
        this.photon = photon;
        photon.addObserver( this );
        isVisible = true;

//        component.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                if( !init ) {
//                    init = true;
//                    origBounds = e.getComponent().getBounds();
//                }
//                Component component = e.getComponent();
//                Rectangle2D newBounds = component.getBounds();
//                double scale = newBounds.getWidth() / origBounds.getWidth();
//                System.out.println( "scale = " + scale );
//                scaleTx = AffineTransform.getScaleInstance( scale, scale );
//                scaleChanged = true;
//                update();
//            }
//        } );
        this.update();
    }

    public void update() {
        double theta = Math.atan2( -photon.getVelocity().getY(), photon.getVelocity().getX() );
        if( theta != directionOfTravel || scaleChanged ) {
            scaleChanged = false;
            directionOfTravel = theta;
            DuotoneImageOp dio = new DuotoneImageOp( genColor( photon.getWavelength() ) );
            BufferedImage bi = dio.filter( baseImage, null );
            AffineTransformOp atxOp = new AffineTransformOp( scaleTx, new RenderingHints( RenderingHints.KEY_INTERPOLATION,
                                                                                          RenderingHints.VALUE_INTERPOLATION_BICUBIC ));
            scaleTx = new AffineTransform( );
            BufferedImage bi1A = atxOp.filter( bi, null );
            // Rotate the image
            BufferedImage bi2 = BufferedImageUtils.getRotatedImage( bi1A, directionOfTravel );
            // Scale the image

            if( photonImage != null ) {
                removeGraphic( photonImage );
            }
            photonImage = new ImageGraphic( bi2, p );
            this.addGraphic( photonImage, 0 );
        }

        // Adjust the position of the image so that the head of the photon graphic is coincident with the
        // position of the photon itself.
        double sy = 0;
        if( photon.getVelocity().getY() > 0 ) {
            sy = 1;
        }
        double ratio = 0.03;
        p.setLocation( photon.getLocation().getX() - photonImage.getBufferedImage().getWidth() * ratio * 0.5,
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
