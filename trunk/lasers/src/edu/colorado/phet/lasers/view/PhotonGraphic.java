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
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.coreadditions.ColorFromWavelength;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
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

    //////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    static protected int s_imgHeight = (int)Photon.RADIUS;
    static protected int s_imgLength = 20;

    // List on s_instances
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
    // The angle at which the photon is moving

    // Generated all the photon animations
    static {
        HashMap blueAnimationMap = new HashMap();
        s_animationMap.put( new Double( Photon.BLUE ), blueAnimationMap );
        HashMap deepRedAnimationMap = new HashMap();
        s_animationMap.put( new Double( Photon.DEEP_RED ), deepRedAnimationMap );
        HashMap redAnimationMap = new HashMap();
        s_animationMap.put( new Double( Photon.RED ), redAnimationMap );
        generateAnimation( Photon.RED, redAnimationMap );
        generateAnimation( Photon.BLUE, blueAnimationMap );
        generateAnimation( Photon.DEEP_RED, deepRedAnimationMap );
    }

    /**
     * Generates all the images for the animation of a specified wavelength
     *
     * @param wavelength
     * @param animationMap
     */
    static private void generateAnimation( double wavelength, HashMap animationMap ) {

        int numImgs = (int)( ( 20f / 680 ) * wavelength );
        for( double theta = 0; theta < Math.PI * 2;
             theta = theta + 0.3 ) {
            //            theta = ( Math.round( ( theta + 0.1 ) * 10 ) ) / 10 ) {

            BufferedImage[] animation = new BufferedImage[numImgs];
            //            Image[] animation = new Image[numImgs];
            // Compute the size of buffered image needed to hold the rotated copy of the
            // base generator image, and create the transform op for doing the rotation
            AffineTransform xform = AffineTransform.getRotateInstance( theta, s_imgLength / 2, s_imgHeight / 2 );
            int xPrime = (int)( s_imgLength * Math.abs( Math.cos( theta ) ) + s_imgHeight * Math.abs( Math.sin( theta ) ) );
            int yPrime = (int)( s_imgLength * Math.abs( Math.sin( theta ) ) + s_imgHeight * Math.abs( Math.cos( theta ) ) );
            AffineTransformOp xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );

            // Generate the frames for the animation
            double phaseAngleIncr = ( Math.PI * 2 ) / numImgs;
            for( int i = 0; i < numImgs; i++ ) {
                double phaseAngle = phaseAngleIncr * i;
                BufferedImage buffImg = computeGeneratorImage( wavelength, phaseAngle );
                BufferedImage animationFrame = new BufferedImage( xPrime, yPrime, BufferedImage.TYPE_INT_ARGB );
                animationFrame = xformOp.filter( buffImg, null );
                animation[i] = animationFrame;
                //                animation[i] = Toolkit.getDefaultToolkit().createImage( animationFrame.getSource() );
            }
            animationMap.put( new Double( theta ), animation );
        }
    }

    /**
     *
     */
    static private BufferedImage computeGeneratorImage( double wavelength, double phaseAngle ) {

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( s_imgLength,
                                               s_imgHeight,
                                               BufferedImage.TYPE_INT_ARGB );
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
                photonGraphic.photon.removeListener( photonGraphic );
                photonGraphic.photon.removeObserver( photonGraphic );
                apparatusPanel.removeGraphic( photonGraphic );
            }
            s_instances.clear();
        }
    }

    public static void removeInstance( PhotonGraphic graphic ) {
        synchronized( s_instances ) {
            s_instances.remove( graphic );
            System.out.println( "PhotonGraphic.removeInstance " + s_instances.size() );
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private double theta;
    private Rectangle2D rect = new Rectangle2D.Double();
    private Vector2D velocity;
    //    private BufferedImage buffImg = new BufferedImage( s_imgLength, s_imgHeight, BufferedImage.TYPE_INT_ARGB );
    private BufferedImage[] animation;
    private int currAnimationFrameNum;
    private Photon photon;
    private Color color;


    public class Foo {
        String s = "FOO";
    }

    Foo[] foo = new Foo[1000];


    public PhotonGraphic( Component component, Photon photon ) {
        // Need to subtract half the width and height of the image to locate it
        // properly
        super( component, s_particleImage );
        s_instances.add( this );

        // Create objects so we'll show up in the profiler
        for( int i = 0; i < foo.length; i++ ) {
            foo[i] = new Foo();
        }

        this.photon = photon;
        this.color = VisibleColor.wavelengthToColor( photon.getWavelength() );
        photon.addObserver( this );
        photon.addListener( this );

        // This code is for the squiggle view of photons
        //        super( s_particleImage, particle.getPosition().getX(), particle.getPosition().getY() );
        //        this.setImage( buffImg );
        //        init( particle );

        velocity = new Vector2D.Double( photon.getVelocity() );
        createImage( photon );

        double w = getBounds().getWidth();
        double h = getBounds().getHeight();
        double cx = this.getBounds().getX() + w / 2;
        double cy = this.getBounds().getY() + h / 2;
        double x = cx + w * Math.cos( photon.getVelocity().getAngle() );
        double y = cy + h * Math.sin( photon.getVelocity().getAngle() );
        setPosition( (int)( photon.getPosition().getX() - x ), (int)( photon.getPosition().getY() - y ) );
    }

    private void createImage( Photon photon ) {
        theta = photon.getVelocity().getAngle();

        // This code is for the squiggle view of photons
        //        generateAnimation( photon );
        //        setImage( animation[0] );

        // Set the color of the image
        Double wavelength = new Double( photon.getWavelength() );
        BufferedImage bi = (BufferedImage)s_colorToImage.get( wavelength );
        if( bi == null ) {
            BufferedImageOp op = new ColorFromWavelength( photon.getWavelength() );
            bi = new BufferedImage( s_particleImage.getWidth(), s_particleImage.getHeight(), BufferedImage.TYPE_INT_ARGB );
            op.filter( s_particleImage, bi );
            s_colorToImage.put( wavelength, bi );
        }

        // Rotate the image
        BufferedImage bi2 = GraphicsUtil.getRotatedImage( bi, theta );
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
                BufferedImage animationFrame = new BufferedImage( xPrime, yPrime, BufferedImage.TYPE_INT_ARGB );
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

    public void update() {
        double angle = photon.getVelocity().getAngle();
        double dx = 0;
        double dy = 0;
        // If the photon is travelling to the right, we need to offset the image to the left
        // so that the head of the photon graphic corresponds properly with the location of
        // the photon itself.
        if( angle <= Math.PI / 2 && angle >= -Math.PI / 2 ) {
            dx = getBounds().getWidth();
        }
        if( angle <= Math.PI && angle >= 0 ) {
            dy = getBounds().getHeight();
        }
        setPosition( (int)( photon.getPosition().getX() - dx ), (int)( photon.getPosition().getY() - dy ) );

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

    /**
     * @param body
     */
    protected void setPosition( Particle body ) {
        Photon particle = (Photon)body;

        // Need to subtract half the width and height of the image to locate it
        // porperly. The particle's location is its center, but the location of
        // the graphic is the upper-left corner of the bounding box.
        // TODO: coordinate the size of the particle and the image
        double dx = s_particleImage.getWidth() * Math.cos( theta );
        double dy = s_particleImage.getHeight() * Math.sin( theta );
        double x = particle.getPosition().getX() - dx;
        double y = particle.getPosition().getY() - dy;
        //        double x = particle.getPosition().getX() - getImage().getWidth();/* - particle.getRadius() */
        //        double y = particle.getPosition().getY() /* - particle.getRadius()*/;
        super.setPosition( (int)x, (int)y );
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        super.paint( g );
        rect.setRect( this.getBounds().getX() + getImage().getMinX(),
                      this.getBounds().getY() + getImage().getMinY(),
                      getImage().getWidth(),
                      getImage().getHeight() );

        //        double w = getBounds().getWidth();
        //        double h = getBounds().getHeight();
        //        double cx = this.getBounds().getX() + w / 2;
        //        double cy = this.getBounds().getY() + h / 2;
        //        double x = cx + w * Math.cos( photon.getVelocity().getAngle() );
        //        double y = cy + h * Math.sin( photon.getVelocity().getAngle() );
        //        g.setColor( Color.GREEN );
        //        g.draw( rect );
        //        g.fillArc( (int)x - 4, (int)y - 4, 8, 8, 0, 360 );

        restoreGraphicsState();
    }

    public void velocityChanged( Photon.VelocityChangedEvent event ) {
        createImage( (Photon)event.getSource() );
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener implementions
    //
    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        PhotonGraphic.removeInstance( this );
        //        photon.removeListener( this );
    }
}