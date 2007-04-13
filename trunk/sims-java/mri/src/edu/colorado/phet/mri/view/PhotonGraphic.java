/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.QuantumConfig;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
public class PhotonGraphic extends PImage implements SimpleObserver,
                                                     Photon.LeftSystemEventListener,
                                                     Photon.VelocityChangedListener {

    //----------------------------------------------------------------
    // Class
    //----------------------------------------------------------------

    static public final int s_imgHeight = (int)Photon.RADIUS;
    static public final int s_imgLength = 20;

    // List of inactive instances (the free pool)
    static private ArrayList s_inactiveInstances = new ArrayList();
    // List of active instances
    static private ArrayList s_instances = new ArrayList();
    // Cache of photon images of different colors
    static private HashMap s_colorToImage = new HashMap();

    static String s_imageName = QuantumConfig.PHOTON_IMAGE_FILE;
    static BufferedImage s_baseImage;

    static {
        try {
            s_baseImage = ImageLoader.loadBufferedImage( s_imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        double scale = (double)s_imgHeight / s_baseImage.getHeight();
        AffineTransform sTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp sTxOp = new AffineTransformOp( sTx, AffineTransformOp.TYPE_BILINEAR );
        s_baseImage = sTxOp.filter( s_baseImage, null );
    }

    // Creates an image for infrared photons
    static BufferedImage s_IRphotonGraphic;

    static {
        int bwThreshold = 180;
        s_IRphotonGraphic = new BufferedImage( s_baseImage.getWidth(), s_baseImage.getHeight(),
                                               BufferedImage.TYPE_INT_ARGB );
        ColorModel cm = s_baseImage.getColorModel();
        for( int x = 0; x < s_baseImage.getWidth(); x++ ) {
            for( int y = 0; y < s_baseImage.getHeight(); y++ ) {
                int rgb = s_baseImage.getRGB( x, y );
                int alpha = cm.getAlpha( rgb );
                int red = cm.getRed( rgb );
                int green = cm.getGreen( rgb );
                int blue = cm.getBlue( rgb );
                int newRGB = 0;

                if( alpha > 0 ) {
                    if( red + green + blue > bwThreshold ) {
                        alpha = 0;
                    }
                    else {
                        red = green = blue = 0;
                        alpha = 255;
                    }
                }
                newRGB = alpha * 0x01000000 + 0 * 0x00010000 + 0 * 0x000000100 + 0 * 0x00000001;
                s_IRphotonGraphic.setRGB( x, y, newRGB );
            }
        }

    }

    // A map that matches photon wavelengths to display colors
    static HashMap colorMap = new HashMap();

    static {
        colorMap.put( new Double( Photon.RED ), Color.red );
        colorMap.put( new Double( Photon.BLUE ), Color.blue );
        colorMap.put( new Double( Photon.DEEP_RED ), new Color( 100, 0, 0 ) );
    }

    // A map of maps for holding photon animations. Inner maps hold animations keyed
    // by their angle of travel. The outer map keys the inner maps by color
    static HashMap s_animationMap = new HashMap();
    private double xOffset;
    private double yOffset;

    static public void setAllVisible( boolean areVisible, double wavelength ) {
        Color color = VisibleColor.wavelengthToColor( wavelength );
        synchronized( s_instances ) {
            for( int i = 0; i < s_instances.size(); i++ ) {
                PhotonGraphic photonGraphic = (PhotonGraphic)s_instances.get( i );
                if( photonGraphic.color.equals( color ) ) {
                    photonGraphic.setVisible( areVisible );
                }
            }
        }
    }

    static public void removeInstance( PhotonGraphic graphic ) {
        synchronized( s_instances ) {
            s_instances.remove( graphic );
        }
        synchronized( s_inactiveInstances ) {
        }
    }


    static public PhotonGraphic getInstance( Photon photon ) {
        PhotonGraphic photonGraphic = null;
        synchronized( s_inactiveInstances ) {
            if( s_inactiveInstances.size() > 0 ) {
                int idx = s_inactiveInstances.size() - 1;
                photonGraphic = (PhotonGraphic)s_inactiveInstances.get( idx );
                s_inactiveInstances.remove( idx );
                photonGraphic.init( photon );
            }
            else {
                photonGraphic = new PhotonGraphic( photon );
            }
        }
        return photonGraphic;
    }

    //----------------------------------------------------------------
    // Instance
    //----------------------------------------------------------------

    private double theta;
    private Vector2D velocity;
    private Photon photon;
    private Color color;
    double baseImageHeight;
    double baseImageWidth;

    /**
     * Private constructor.
     *
     * @param photon
     */
    private PhotonGraphic( Photon photon ) {
        super( s_baseImage );
        init( photon );
        photon.addLeftSystemListener( this );
        s_instances.add( this );
    }

    private void init( Photon photon ) {
        this.photon = photon;
        this.color = VisibleColor.wavelengthToColor( photon.getWavelength() );
        photon.addObserver( this );
        photon.addVelocityChangedListener( this );

        velocity = new Vector2D.Double( photon.getVelocity() );
        createImage( photon );

        computeOffsets( velocity.getAngle() );
        setOffset( (int)( photon.getPosition().getX() - xOffset ), (int)( photon.getPosition().getY() - yOffset ) );
    }

    private void createImage( Photon photon ) {

        Double wavelength = new Double( photon.getWavelength() );
        BufferedImage bi = null;
        // If the wavelength is in the IR, use the special graphic
        if( photon.getWavelength() > QuantumConfig.MAX_WAVELENGTH ) {
            bi = s_IRphotonGraphic;
        }
        // Otherwise, get an image that is the appropriate duotone color
        else {
            // See if we've already got an image for this photon's color. If not, make one and cache it
            bi = (BufferedImage)s_colorToImage.get( wavelength );
            if( bi == null ) {
                BufferedImageOp op = new MakeDuotoneImageOp( VisibleColor.wavelengthToColor( photon.getWavelength() ) );
                bi = new BufferedImage( s_baseImage.getWidth(), s_baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB );
//                op.filter( s_baseImage, bi );
//                s_colorToImage.put( wavelength, bi );
            }
            else {
                System.out.println( "PhotonGraphic.createImage" );
            }
        }
        // Record the height and width of he image before it's rotated
        baseImageHeight = bi.getHeight();
        baseImageWidth = bi.getWidth();

        // Rotate the image
        theta = photon.getVelocity().getAngle();
        BufferedImage bi2 = BufferedImageUtils.getRotatedImage( bi, theta );
        setImage( bi2 );
    }

    /**
     *
     */
    public void update() {
        setOffset( (int)( photon.getPosition().getX() - xOffset ), (int)( photon.getPosition().getY() - yOffset ) );
    }

    public void velocityChanged( Photon.VelocityChangedEvent event ) {
        createImage( (Photon)event.getSource() );
        computeOffsets( photon.getVelocity().getAngle() );
        setOffset( (int)( photon.getPosition().getX() - xOffset ), (int)( photon.getPosition().getY() - yOffset ) );
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
        this.theta = theta;

        xOffset = 0;
        yOffset = 0;
        double alpha = 0;
        double w = baseImageWidth;
        double h = baseImageHeight;
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
    }


    /**
     * For debugging
     * <p/>
     * //     * @param g2
     */
//    public void paint( Graphics2D g2 ) {
//        super.paint( g2 );
////        g2.setColor( Color.green );
////        g2.draw(  new Ellipse2D.Double( this.getLocation().getX() + xOffset - 2, this.getLocation().getY() + yOffset - 2,
////                                        4, 4 ));
//    }

    //-----------------------------------------------------------------
    // LeftSystemListener implementions
    //-----------------------------------------------------------------
    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        s_instances.remove( this );
//        photon.removeObserver( this );
//        photon.removeLeftSystemListener( this );
//        photon.removeVelocityChangedListener( this );
        photon = null;
    }

}