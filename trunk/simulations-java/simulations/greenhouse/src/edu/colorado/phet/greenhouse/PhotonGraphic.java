/**
 * Class: PhotonGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common_greenhouse.view.CompositeGraphic;
import edu.colorado.phet.coreadditions_greenhouse.graphics.BufferedImageUtils;
import edu.colorado.phet.coreadditions_greenhouse.graphics.DuotoneImageOp;
import edu.colorado.phet.coreadditions_greenhouse.graphics.ImageGraphic;
import edu.colorado.phet.coreadditions_greenhouse.graphics.ShapeGraphicType;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;

public class PhotonGraphic extends CompositeGraphic implements Observer {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static ArrayList instances = new ArrayList();
    private static double scale = 1;
    private static final String IMAGE_PATH = "greenhouse/images/photon-comet.png";
    private static final String IMAGE_PATH_RED = "greenhouse/images/photon-660.png";
    private static final String IMAGE_PATH_YELLOW = "greenhouse/images/photon-575.png";
    private static BufferedImage baseImage;
    private static HashMap colorLUT = new HashMap();
    private DuotoneImageOp duotoneImageOp;
    private AffineTransformOp scaleAtxOp;
    private BufferedImage bi;

    private static BufferedImage redImage;

    private static BufferedImage yellowImage;

    // get the base image
    static {
//        baseImage = new ImageLoader().fetchBufferedImage( IMAGE_PATH );
        try {
            baseImage = ImageLoader.loadBufferedImage( IMAGE_PATH );
            redImage=ImageLoader.loadBufferedImage( IMAGE_PATH_RED );
            yellowImage=ImageLoader.loadBufferedImage( IMAGE_PATH_YELLOW );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.4, 0.4 );
//        AffineTransform scaleTx = AffineTransform.getScaleInstance( 0.5, 0.5 );
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
        return (Color)colorLUT.get( new Double( wavelength ) );
    }

    private static class ResizeHandler extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            if( !init ) {
                init = true;
                origBounds = e.getComponent().getBounds();
            }
            Component component = e.getComponent();
            Rectangle2D newBounds = component.getBounds();
            scale = newBounds.getWidth() / origBounds.getWidth();
            System.out.println( "scale = " + scale );
            scaleTx = AffineTransform.getScaleInstance( scale, scale );
            scaleChanged = true;
            for( int i = 0; i < instances.size(); i++ ) {
                PhotonGraphic photonGraphic = (PhotonGraphic)instances.get( i );
                photonGraphic.update();
            }
        }
    }

    // A cache for phton graphic

    private static ResizeHandler resizeHandler;
    private static AffineTransform scaleTx = new AffineTransform();
    private static boolean init;
    private static Rectangle2D origBounds;

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    Point2D.Double position = new Point2D.Double();
    private Photon photon;
    private ImageGraphic photonImage;
    private ShapeGraphicType debugGraphic;
    private boolean isVisible;
    private double directionOfTravel = Double.POSITIVE_INFINITY;
    private static boolean scaleChanged = false;

    public PhotonGraphic( Component component, Photon photon ) {
        instances.add( this );

        this.duotoneImageOp = new DuotoneImageOp( genColor( photon.getWavelength() ) );
        bi = duotoneImageOp.filter( baseImage, null );
        this.photon = photon;
        photon.addObserver( this );
        isVisible = true;

//        if( resizeHandler == null && component instanceof ApparatusPanel ) {
////            resizeHandler = new ResizeHandler();
////            component.addComponentListener( resizeHandler );
////        }
//            component.addComponentListener( new ComponentAdapter() {
//                public void componentResized( ComponentEvent e ) {
//                    if( !init ) {
//                        init = true;
//                        origBounds = e.getComponent().getBounds();
//                    }
//                    Component component = e.getComponent();
//                    Rectangle2D newBounds = component.getBounds();
//                    scale = newBounds.getWidth() / origBounds.getWidth();
//                    scaleTx = AffineTransform.getScaleInstance( scale, scale );
//                    scaleChanged = true;
//                    scaleAtxOp = new AffineTransformOp( scaleTx, new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION,
//                                                                                     RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY ) );
//                    update();
//                }
//            } );
//        }
        scaleAtxOp = new AffineTransformOp( scaleTx, new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION,
                                                                         RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY ) );
        this.update();

        // TEST
//        ApparatusPanel ap = (ApparatusPanel)component;

    }

    public void leaveSystem() {
        instances.remove( this );
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void update() {

        double theta = Math.atan2( -photon.getVelocity().getY(), photon.getVelocity().getX() );
        if( theta != directionOfTravel || scaleChanged ) {
            scaleChanged = false;
            directionOfTravel = theta;
            // Scale the image
            BufferedImage bi1A = scaleAtxOp.filter( bi, null );
            // Rotate the image
            BufferedImage bi2 = BufferedImageUtils.getRotatedImage( bi1A, directionOfTravel );

            if( photonImage != null ) {
                removeGraphic( photonImage );
            }
            photonImage = new ImageGraphic( photon.getWavelength()>6E-7?redImage:yellowImage, position );
            this.addGraphic( photonImage, 0 );
        }

        // Adjust the position of the image so that the head of the photon graphic is coincident with the
        // position of the photon itself.
        double sy = 0;
        if( photon.getVelocity().getY() > 0 ) {
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

    public void paint( Graphics2D g2 ) {
        super.paint( g2 );

        // DEBUG
//        Color orgColor = g2.getColor();
//        g2.setColor( Color.green );
//        Ellipse2D e = new Ellipse2D.Double( photon.getLocation().getX() - 0.1,
//                                            photon.getLocation().getY() - 0.1,
//                                            0.2, 0.2 );
//        g2.fill( e );
//        g2.setColor( orgColor );
    }
}
