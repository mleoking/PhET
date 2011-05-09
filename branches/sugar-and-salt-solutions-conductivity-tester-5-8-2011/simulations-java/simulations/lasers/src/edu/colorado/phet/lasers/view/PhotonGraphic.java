// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.piccolophet.util.PhotonImageFactory;
import edu.colorado.phet.common.quantum.QuantumConfig;
import edu.colorado.phet.common.quantum.model.Photon;

/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.quantum.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
public class PhotonGraphic extends PhetImageGraphic implements SimpleObserver,
                                                               Photon.LeftSystemEventListener,
                                                               Photon.VelocityChangedListener {

    private double theta;
    private Photon photon;
    private Color color;
    private double baseImageHeight;
    private double baseImageWidth;
    private double xOffset;
    private double yOffset;

    //----------------------------------------------------------------
    // Class
    //----------------------------------------------------------------

    static public final int s_imgHeight = (int) Photon.RADIUS;

    // List of inactive instances (the free pool)
    static private ArrayList s_inactiveInstances = new ArrayList();

    // List of active instances
    static private ArrayList s_instances = new ArrayList();

    // Cache of photon images of different colors
    static private HashMap s_colorToImage = new HashMap();

    static String s_imageName = QuantumConfig.PHOTON_IMAGE_FILE;

    static BufferedImage s_particleImage;


    // Creates an image for infrared photons
    static BufferedImage s_IRphotonGraphic;
//    private static int PHOTON_SIZE = 25;
    //    private static boolean COMET_GRAPHIC = true;

    private static int PHOTON_SIZE = 18;
    private static boolean COMET_GRAPHIC = false;

    public static boolean isCometGraphic() {
        return COMET_GRAPHIC;
    }

    public static void setCometGraphic( boolean cometGraphic ) {
        if ( COMET_GRAPHIC != cometGraphic ) {
            PhotonGraphic.COMET_GRAPHIC = cometGraphic;
            s_colorToImage.clear();
        }
    }

    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        double scale = (double) s_imgHeight / s_particleImage.getHeight();
        AffineTransform sTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp sTxOp = new AffineTransformOp( sTx, AffineTransformOp.TYPE_BILINEAR );
        s_particleImage = sTxOp.filter( s_particleImage, null );

        int bwThreshold = 180;
        s_IRphotonGraphic = new BufferedImage( s_particleImage.getWidth(), s_particleImage.getHeight(),
                                               BufferedImage.TYPE_INT_ARGB_PRE );
        ColorModel cm = s_particleImage.getColorModel();
        for ( int x = 0; x < s_particleImage.getWidth(); x++ ) {
            for ( int y = 0; y < s_particleImage.getHeight(); y++ ) {
                int rgb = s_particleImage.getRGB( x, y );
                int alpha = cm.getAlpha( rgb );
                int red = cm.getRed( rgb );
                int green = cm.getGreen( rgb );
                int blue = cm.getBlue( rgb );

                if ( alpha > 0 ) {
                    if ( red + green + blue > bwThreshold ) {
                        alpha = 0;
                    }
                    else {
                        alpha = 255;
                    }
                }
                int newRGB = alpha * 0x01000000;
                s_IRphotonGraphic.setRGB( x, y, newRGB );
            }
        }
    }

    /*
     * Removes all instances of PhotonGraphic from a specified ApparatusPanel
     */
    static public void removeAll( ApparatusPanel apparatusPanel ) {
        for ( int i = 0; i < s_instances.size(); i++ ) {
            PhotonGraphic photonGraphic = (PhotonGraphic) s_instances.get( i );
            photonGraphic.photon.removeLeftSystemListener( photonGraphic );
            photonGraphic.photon.removeObserver( photonGraphic );
            apparatusPanel.removeGraphic( photonGraphic );
        }
        s_instances.clear();
    }

    static public void setAllVisible( boolean areVisible, double wavelength ) {
        Color color = VisibleColor.wavelengthToColor( wavelength );
        for ( int i = 0; i < s_instances.size(); i++ ) {
            PhotonGraphic photonGraphic = (PhotonGraphic) s_instances.get( i );
            if ( photonGraphic.color.equals( color ) ) {
                photonGraphic.setVisible( areVisible );
            }
        }
    }

    static public void removeInstance( PhotonGraphic graphic ) {
        s_instances.remove( graphic );
    }

    static public PhotonGraphic getInstance( Component component, Photon photon ) {
        PhotonGraphic photonGraphic;
        //Perhaps this code is done to reduce the number of photongraphic instantiations?
        //it looks unnecessary
        if ( s_inactiveInstances.size() > 0 ) {
            int idx = s_inactiveInstances.size() - 1;
            photonGraphic = (PhotonGraphic) s_inactiveInstances.get( idx );
            s_inactiveInstances.remove( idx );
            photonGraphic.setState( component, photon );
        }
        else {
            photonGraphic = new PhotonGraphic( component, photon );
        }
        return photonGraphic;
    }

    private PhotonGraphic( Component component, Photon photon ) {
        super( component, createImage( photon.getWavelength() ) );
        setState( component, photon );
        photon.addLeftSystemListener( this );
        updateImage();
        s_instances.add( this );
    }

    private void setState( Component component, Photon photon ) {
        this.setComponent( component );
        this.photon = photon;
        this.color = VisibleColor.wavelengthToColor( photon.getWavelength() );
        //todo: where is the removeObserver call?
        photon.addObserver( this );
        photon.addVelocityChangedListener( this );

        updateImage();

        computeOffsets( photon.getVelocity().getAngle() );
        setLocation( (int) ( photon.getPosition().getX() - xOffset ), (int) ( photon.getPosition().getY() - yOffset ) );
    }

    private void updateImage() {
        Double wavelength = new Double( photon.getWavelength() );
        BufferedImage bi;
        // If the wavelength is in the IR, use the special graphic
        if ( photon.getWavelength() > QuantumConfig.MAX_WAVELENGTH ) {
//            bi = COMET_GRAPHIC?s_IRphotonGraphic:createImage( VisibleColor.MIN_WAVELENGTH -1 );
            bi = COMET_GRAPHIC ? s_IRphotonGraphic : createImage( VisibleColor.MAX_WAVELENGTH + 1 );
        }
        // Otherwise, get an image that is the appropriate duotone color
        else {
            // See if we've already got an image for this photon's color. If not, make one and cache it
            bi = (BufferedImage) s_colorToImage.get( wavelength );
            if ( bi == null ) {
                bi = createImage( photon.getWavelength() );
                s_colorToImage.put( wavelength, bi );
            }
        }
        // Record the height and width of he image before it's rotated
        baseImageHeight = bi.getHeight();
        baseImageWidth = bi.getWidth();

        // Rotate the image
        theta = photon.getVelocity().getAngle();
        setImage( BufferedImageUtils.getRotatedImage( bi, theta ) );
    }

    private static BufferedImage createCometImage( double wavelength ) {
        BufferedImageOp op = new MakeDuotoneImageOp( VisibleColor.wavelengthToColor( wavelength ) );
        BufferedImage bi = new BufferedImage( s_particleImage.getWidth(), s_particleImage.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        op.filter( s_particleImage, bi );
        return bi;
    }

    private static BufferedImage createImage( double wavelength ) {
        return COMET_GRAPHIC ? createCometImage( wavelength ) : createTwinkleImage( wavelength );
    }

    private static BufferedImage createTwinkleImage( double wavelength ) {
        return BufferedImageUtils.toBufferedImage( PhotonImageFactory.lookupPhotonImage( wavelength, PHOTON_SIZE ) );
//        return BufferedImageUtils.toBufferedImage( PhotonImageFactory.createPhotonImage( wavelength, PHOTON_SIZE ) );
    }

    public void update() {
        setLocation( (int) ( photon.getPosition().getX() - xOffset ), (int) ( photon.getPosition().getY() - yOffset ) );
        setBoundsDirty();
        repaint();
    }

    public void velocityChanged( Photon.VelocityChangedEvent event ) {
        updateImage();
        computeOffsets( photon.getVelocity().getAngle() );
        setLocation( (int) ( photon.getPosition().getX() - xOffset ), (int) ( photon.getPosition().getY() - yOffset ) );
    }

    /*
     * Computes the offsets that must be applied to the buffered image's location so that it's head is
     * at the location of the photon
     */
    private void computeOffsets( double theta ) {
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );
        this.theta = theta;

        xOffset = 0;
        yOffset = 0;
        double alpha = 0;
        double w = baseImageWidth;
        double h = baseImageHeight;
        if ( theta >= 0 && theta <= Math.PI / 2 ) {
            xOffset = w * Math.cos( theta ) + ( h / 2 ) * Math.sin( theta );
            yOffset = w * Math.sin( theta ) + ( h / 2 ) * Math.cos( theta );
        }
        if ( theta > Math.PI / 2 && theta <= Math.PI ) {
            alpha = theta - Math.PI / 2;
            xOffset = ( h / 2 ) * Math.cos( alpha );
            yOffset = w * Math.cos( alpha ) + ( h / 2 ) * Math.sin( alpha );
        }
        if ( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            alpha = theta - Math.PI;
            xOffset = ( h / 2 ) * Math.sin( alpha );
            yOffset = ( h / 2 ) * Math.cos( alpha );
        }
        if ( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            alpha = Math.PI * 2 - theta;
            xOffset = w * Math.cos( alpha ) + ( h / 2 ) * Math.sin( alpha );
            yOffset = ( h / 2 ) * Math.cos( alpha );
        }
    }

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        s_instances.remove( this );
        photon = null;
    }

    public static void setPhotonSize( int photonSize ) {
        if ( PHOTON_SIZE != photonSize ) {
            PHOTON_SIZE = photonSize;
            s_colorToImage.clear();
        }
    }

    public static int getPhotonSize() {
        return PHOTON_SIZE;
    }
}