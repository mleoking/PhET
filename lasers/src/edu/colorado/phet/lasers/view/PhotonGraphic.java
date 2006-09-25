/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
//import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.QuantumConfig;
import edu.colorado.phet.lasers.controller.LaserConfig;

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
public class PhotonGraphic extends PhetImageGraphic implements SimpleObserver,
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

    static String s_imageName = LaserConfig.PHOTON_IMAGE_FILE;
    static String s_highEnergyImageName = LaserConfig.HIGH_ENERGY_PHOTON_IMAGE_FILE;
    static String s_midEnergyImageName = LaserConfig.MID_HIGH_ENERGY_PHOTON_IMAGE_FILE;
    static String s_lowEnergyImageName = LaserConfig.LOW_ENERGY_PHOTON_IMAGE_FILE;
    static BufferedImage s_particleImage;
    static BufferedImage s_highEnergyImage;
    static BufferedImage s_midEnergyImage;
    static BufferedImage s_lowEnergyImage;

    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            s_highEnergyImage = ImageLoader.loadBufferedImage( s_highEnergyImageName );
            s_midEnergyImage = ImageLoader.loadBufferedImage( s_midEnergyImageName );
            s_lowEnergyImage = ImageLoader.loadBufferedImage( s_lowEnergyImageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        double scale = (double)s_imgHeight / s_particleImage.getHeight();
        AffineTransform sTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp sTxOp = new AffineTransformOp( sTx, AffineTransformOp.TYPE_BILINEAR );
        s_particleImage = sTxOp.filter( s_particleImage, null );
    }

    // Creates an image for infrared photons
    static BufferedImage s_IRphotonGraphic;

    static {
        int bwThreshold = 180;
        s_IRphotonGraphic = new BufferedImage( s_particleImage.getWidth(), s_particleImage.getHeight(),
                                               BufferedImage.TYPE_INT_ARGB_PRE );
        ColorModel cm = s_particleImage.getColorModel();
        for( int x = 0; x < s_particleImage.getWidth(); x++ ) {
            for( int y = 0; y < s_particleImage.getHeight(); y++ ) {
                int rgb = s_particleImage.getRGB( x, y );
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

    // Generates all the photon animations
//    static {
//        HashMap blueAnimationMap = new HashMap();
//        s_animationMap.put( new Double( Photon.BLUE ), blueAnimationMap );
//        HashMap deepRedAnimationMap = new HashMap();
//        s_animationMap.put( new Double( Photon.DEEP_RED ), deepRedAnimationMap );
//        HashMap redAnimationMap = new HashMap();
//        s_animationMap.put( new Double( Photon.RED ), redAnimationMap );
//        generateAnimation( Photon.RED, redAnimationMap );
//        generateAnimation( Photon.BLUE, blueAnimationMap );
//        generateAnimation( Photon.DEEP_RED, deepRedAnimationMap );
//    }

    /**
     * Generates all the images for the animation of a specified wavelength
     *
     * @param wavelength
     * @param animationMap
     */
//    static private void generateAnimation( double wavelength, HashMap animationMap ) {
//
//        int numImgs = (int)( ( 20f / 680 ) * wavelength );
//        for( double theta = 0; theta < Math.PI * 2;
//             theta = theta + 0.3 ) {
//            //            theta = ( Math.round( ( theta + 0.1 ) * 10 ) ) / 10 ) {
//
//            BufferedImage[] animation = new BufferedImage[numImgs];
//            //            Image[] animation = new Image[numImgs];
//            // Compute the size of buffered image needed to hold the rotated copy of the
//            // base generator image, and create the transform op for doing the rotation
//            AffineTransform xform = AffineTransform.getRotateInstance( theta, s_imgLength / 2, s_imgHeight / 2 );
//            int xPrime = (int)( s_imgLength * Math.abs( Math.cos( theta ) ) + s_imgHeight * Math.abs( Math.sin( theta ) ) );
//            int yPrime = (int)( s_imgLength * Math.abs( Math.sin( theta ) ) + s_imgHeight * Math.abs( Math.cos( theta ) ) );
//            AffineTransformOp xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
//
//            // Generate the frames for the animation
//            double phaseAngleIncr = ( Math.PI * 2 ) / numImgs;
//            for( int i = 0; i < numImgs; i++ ) {
//                double phaseAngle = phaseAngleIncr * i;
//                BufferedImage buffImg = computeGeneratorImage( wavelength, phaseAngle );
//                BufferedImage animationFrame = new BufferedImage( xPrime, yPrime, BufferedImage.TYPE_INT_ARGB );
//                animationFrame = xformOp.filter( buffImg, null );
//                animation[i] = animationFrame;
//                //                animation[i] = Toolkit.getDefaultToolkit().createImage( animationFrame.getSource() );
//            }
//            animationMap.put( new Double( theta ), animation );
//        }
//    }

    /**
     *
     */
    static private BufferedImage computeGeneratorImage( double wavelength, double phaseAngle ) {

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( s_imgLength,
                                               s_imgHeight,
                                               BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2d = img.createGraphics();
        int kPrev = s_imgHeight / 2;
        int iPrev = 0;
        double freqFactor = 10 * wavelength / 680;
        for( int i = 0; i < s_imgLength; i++ ) {
            int k = (int)( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * s_imgHeight / 2 + s_imgHeight / 2 );
            for( int j = 0; j < s_imgHeight; j++ ) {
                if( j == k ) {
                    Color c = (Color)colorMap.get( new Double( wavelength ) );
                    g2d.setColor( c );
                    g2d.drawLine( iPrev, kPrev, i, k );
                    iPrev = i;
                    kPrev = k;
                }
            }
        }
        g2d.dispose();
        return img;
    }

    /**
     * Removes all instances of PhotonGraphic from a specified ApparatusPanel
     *
     * @param apparatusPanel
     */
    static public void removeAll( ApparatusPanel apparatusPanel ) {
        synchronized( s_instances ) {
            for( int i = 0; i < s_instances.size(); i++ ) {
                PhotonGraphic photonGraphic = (PhotonGraphic)s_instances.get( i );
                photonGraphic.photon.removeLeftSystemListener( photonGraphic );
                photonGraphic.photon.removeObserver( photonGraphic );
                apparatusPanel.removeGraphic( photonGraphic );
            }
            s_instances.clear();
        }
    }

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


    static public PhotonGraphic getInstance( Component component, Photon photon ) {
        PhotonGraphic photonGraphic = null;
        synchronized( s_inactiveInstances ) {
            if( s_inactiveInstances.size() > 0 ) {
                int idx = s_inactiveInstances.size() - 1;
                photonGraphic = (PhotonGraphic)s_inactiveInstances.get( idx );
                s_inactiveInstances.remove( idx );
                photonGraphic.init( component, photon );
            }
            else {
                photonGraphic = new PhotonGraphic( component, photon );
            }
        }
        return photonGraphic;
    }


    //----------------------------------------------------------------
    // Instance
    //----------------------------------------------------------------

    private double theta;
    private Vector2D velocity;
    //    private BufferedImage buffImg = new BufferedImage( s_imgLength, s_imgHeight, BufferedImage.TYPE_INT_ARGB );
    private BufferedImage[] animation;
    private int currAnimationFrameNum;
    private Photon photon;
    private Color color;
    double baseImageHeight;
    double baseImageWidth;

    /**
     * Private constructor.
     *
     * @param component
     * @param photon
     */
    private PhotonGraphic( Component component, Photon photon ) {
        super( component, s_particleImage );
        init( component, photon );
        photon.addLeftSystemListener( this );
        s_instances.add( this );
    }

    private void init( Component component, Photon photon ) {
        this.setComponent( component );
        this.photon = photon;
        this.color = VisibleColor.wavelengthToColor( photon.getWavelength() );
        photon.addObserver( this );
        photon.addVelocityChangedListener( this );

        // This code is for the squiggle view of photons
        //        super( s_particleImage, particle.getPosition().getX(), particle.getPosition().getY() );
        //        this.setImage( buffImg );
        //        init( particle );

        velocity = new Vector2D.Double( photon.getVelocity() );
        createImage( photon );

        computeOffsets( velocity.getAngle() );
        setLocation( (int)( photon.getPosition().getX() - xOffset ), (int)( photon.getPosition().getY() - yOffset ) );

//        double w = getBounds().getBeamWidth();
//        double h = getBounds().getLength();
//        double cx = this.getBounds().getX() + w / 2;
//        double cy = this.getBounds().getY() + h / 2;
//        double x = cx + w * Math.cos( photon.getVelocity().getDirection() );
//        double y = cy + h * Math.sin( photon.getVelocity().getDirection() );
//        setLocation( (int)( photon.getPosition().getX() - x ), (int)( photon.getPosition().getY() - y ) );
    }

    private void createImage( Photon photon ) {

        // This code is for the squiggle view of photons
        //        generateAnimation( photon );
        //        setImage( animation[0] );

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
                bi = new BufferedImage( s_particleImage.getWidth(), s_particleImage.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
                op.filter( s_particleImage, bi );
                s_colorToImage.put( wavelength, bi );
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
     * @param photon
     */
    private void generateAnimation( Photon photon ) {

        this.photon = photon;

        // Find the angle the photon is travel at, and round it to 0.1 radians. We
        // do this so the map that caches the animations will get filled quickly and
        // get a lot of hits
        double theta = Math.acos( photon.getVelocity().getX() / photon.getVelocity().getMagnitude() );
        if( photon.getVelocity().getY() < 0 ) {
            theta = Math.PI * 2 - theta;
        }
        int temp = (int)( theta * 10 );
        theta = temp / 10;

        // Check to see if an animation has already been generated that will work
        this.animation = null;
        HashMap colorAnimationMap = (HashMap)s_animationMap.get( new Double( photon.getWavelength() ) );
        if( colorAnimationMap != null ) {
            animation = (BufferedImage[])colorAnimationMap.get( new Double( theta ) );
        }
        else {
            colorAnimationMap = new HashMap();
            s_animationMap.put( new Double( photon.getWavelength() ), colorAnimationMap );
        }

        // If there is no existing animation that will work, generate one and add it to the map
        // Note that this should not happen, because we precompute the animations in a static
        // initializer. But this is here just in case...
        if( animation == null ) {
            int numImgs = (int)( ( 20f / 680 ) * photon.getWavelength() );
            animation = new BufferedImage[numImgs];

            AffineTransform xform = AffineTransform.getRotateInstance( theta, s_imgLength / 2, s_imgHeight / 2 );
            int xPrime = (int)( s_imgLength * Math.abs( Math.cos( theta ) ) + s_imgHeight * Math.abs( Math.sin( theta ) ) );
            int yPrime = (int)( s_imgLength * Math.abs( Math.sin( theta ) ) + s_imgHeight * Math.abs( Math.cos( theta ) ) );
            AffineTransformOp xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );

            // Generate the frames for the animation
            double phaseAngleIncr = Math.PI * 2 / numImgs;
            for( int i = 0; i < numImgs; i++ ) {
                double phaseAngle = phaseAngleIncr * i;
                BufferedImage buffImg = computeGeneratorImage( photon.getWavelength(), phaseAngle );
                BufferedImage animationFrame = new BufferedImage( xPrime, yPrime, BufferedImage.TYPE_INT_ARGB_PRE );
                animationFrame = xformOp.filter( buffImg, null );

                // todo: this may not be right at all. I don't understand the original line
                animation[i] = animationFrame;
            }
            colorAnimationMap.put( new Double( theta ), animation );
        }
    }

    /**
     *
     */
    public void setPhaseToZero() {
        currAnimationFrameNum = 0;
    }

    /**
     *
     */
    public void update() {
//        double angle = photon.getVelocity().getDirection();
//        if( angle != theta ) {
//            computeOffsets( angle );
//        }
//        double dx = 0;
//        double dy = 0;
//        // If the photon is travelling to the right, we need to offset the image to the left
//        // so that the head of the photon graphic corresponds properly with the location of
//        // the photon itself.
//        if( angle <= Math.PI / 2 && angle >= -Math.PI / 2 ) {
//            dx = getBounds().getBeamWidth();
//        }
//        if( angle <= Math.PI && angle >= 0 ) {
//            dy = getBounds().getLength();
//        }
//
//        setLocation( (int)( photon.getPosition().getX() - dx ), (int)( photon.getPosition().getY() - dy ) );
        setLocation( (int)( photon.getPosition().getX() - xOffset ), (int)( photon.getPosition().getY() - yOffset ) );


        // Get the next frame of the animaton
        //        currAnimationFrameNum = ( currAnimationFrameNum + 1 ) % animation.length;
        //        setImage( animation[currAnimationFrameNum] );

        // If the velocity has changed, we need to get a new
        // animation.
        //        if( !photon.getVelocity().equals( this.velocity ) ) {
        //            velocity = new Vector2D.Double( photon.getVelocity() );
        //            this.generateAnimation( photon );
        //        }

        setBoundsDirty();
        repaint();
    }

    public void velocityChanged( Photon.VelocityChangedEvent event ) {
        createImage( (Photon)event.getSource() );
        computeOffsets( photon.getVelocity().getAngle() );
        setLocation( (int)( photon.getPosition().getX() - xOffset ), (int)( photon.getPosition().getY() - yOffset ) );
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
     *
     * @param g2
     */
    public void paint( Graphics2D g2 ) {
        super.paint( g2 );
//        g2.setColor( Color.green );
//        g2.draw(  new Ellipse2D.Double( this.getLocation().getX() + xOffset - 2, this.getLocation().getY() + yOffset - 2,
//                                        4, 4 ));
    }
    //-----------------------------------------------------------------
    // LeftSystemListener implementions
    //-----------------------------------------------------------------

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        s_instances.remove( this );
//        photon.removeLeftSystemListener( this );
        photon = null;
    }
}