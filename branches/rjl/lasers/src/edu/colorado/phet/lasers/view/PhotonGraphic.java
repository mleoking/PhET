/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.graphics.ImageGraphic;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.util.Observable;
import java.util.HashMap;

public class PhotonGraphic extends ImageGraphic {

    private float radius;
    // Velocity at which the photon is traveling
    private Vector2D velocity;
    private BufferedImage buffImg = new BufferedImage( s_imgLength, s_imgHeight, BufferedImage.TYPE_INT_ARGB );
    private Image[] animation;
    private int currAnimationFrameNum;


    /**
     *
     * @param photon
     */
    private void generateAnimation( Photon photon ) {

        // Find the angle the photon is travel at, and round it to 0.1 radians. We
        // do this so the map that caches the animations will get filled quickly and
        // get a lot of hits
        float theta = (float)Math.acos( photon.getVelocity().getX() / photon.getVelocity().getLength() );
        if( photon.getVelocity().getY() < 0 ) {
            theta = (float)Math.PI * 2 - theta;
        }
        int temp = (int)( theta * 10 );
        theta = ( (float)temp ) / 10;

        // Check to see if an animation has already been generated that will work
        this.animation = null;
        HashMap colorAnimationMap = (HashMap)s_animationMap.get( new Float( photon.getWavelength() ) );
        if( colorAnimationMap != null ) {
            animation = (Image[])colorAnimationMap.get( new Float( theta ) );
        }
        else {
            colorAnimationMap = new HashMap();
            s_animationMap.put( new Float( photon.getWavelength() ), colorAnimationMap );
        }

        // If there is no existing animation that will work, generate one and add it to the map
        // Note that this should not happen, because we precompute the animations in a static
        // initializer. But this is here just in case...
        if( animation == null ) {
            int numImgs = (int)( ( 20f / 680 ) * photon.getWavelength() );
            animation = new Image[numImgs];

            // Compute the size of buffered image needed to hold the rotated copy of the
            // base generator image, and create the transform op for doing the rotation
            AffineTransform xform = AffineTransform.getRotateInstance( theta, s_imgLength / 2, s_imgHeight / 2 );
            int xPrime = (int)( s_imgLength * Math.abs( Math.cos( theta ) ) + s_imgHeight * Math.abs( Math.sin( theta ) ) );
            int yPrime = (int)( s_imgLength * Math.abs( Math.sin( theta ) ) + s_imgHeight * Math.abs( Math.cos( theta ) ) );
            AffineTransformOp xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );

            // Generate the frames for the animation
            float phaseAngleIncr = (float)( Math.PI * 2 ) / numImgs;
            for( int i = 0; i < numImgs; i++ ) {
                float phaseAngle = phaseAngleIncr * i;
                BufferedImage buffImg = computeGeneratorImage( photon.getWavelength(), phaseAngle );
                BufferedImage animationFrame = new BufferedImage( xPrime, yPrime, BufferedImage.TYPE_INT_ARGB );
                animationFrame = xformOp.filter( buffImg, null );
                animation[i] = Toolkit.getDefaultToolkit().createImage( animationFrame.getSource() );
            }
            colorAnimationMap.put( new Float( theta ), animation );
        }
    }

    /**
     *
     * @param g
     */
    public void paint( Graphics2D g ) {
        currAnimationFrameNum = ( currAnimationFrameNum + 1 ) % animation.length;
        Image img = animation[currAnimationFrameNum];
        setImage( img );
        super.paint( g );
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
    public PhotonGraphic() {
        super( s_particleImage, 0, 0 );
        init();
    }

    /**
     *
     * @param particle
     */
    public PhotonGraphic( Particle particle ) {
        // Need to subtract half the width and height of the image to locate it
        // properly
        super( s_particleImage, particle.getPosition().getX(), particle.getPosition().getY() );
        init();
        init( particle );
    }

    /**
     *
     */
    private void init() {
        this.setImage( getImage() );
    }

    /**
     *
     * @param body
     */
    public void init( Particle body ) {
        super.init( body );
        if( body instanceof Photon ) {
            Photon photon = (Photon)body;
            velocity = new Vector2D( photon.getVelocity() );
            float theta = (float)Math.acos( photon.getVelocity().getX() / photon.getVelocity().getLength() );
            if( photon.getVelocity().getY() < 0 ) {
                theta = (float)Math.PI * 2 - theta;
            }

            generateAnimation( photon );
            setImage( Toolkit.getDefaultToolkit().createImage( buffImg.getSource() ) );
            if( photon.getWavelength() == Photon.DEEP_RED ) {
                setImage( s_midEnergyImage );
            }
        }
        setPosition( body );
    }

    /**
     *
     * @return
     */
    protected Image getImage() {
        if( s_particleImage == null ) {
            ResourceLoader loader = new ResourceLoader();
            ResourceLoader.LoadedImageDescriptor imageDescriptor = loader.loadImage( s_imageName );
            s_particleImage = imageDescriptor.getImage();
            this.radius = imageDescriptor.getWidth() / 2;
        }
        return s_particleImage;
    }

    /**
     *
     * @param observable
     * @param o
     */
    public void update( Observable observable, Object o ) {

        super.update( observable, o );

        Photon photon = (Photon)observable;
        setPosition( photon );

        // If the velocity has changed, we need to get a new
        // animation.
        if( !photon.getVelocity().equals( this.velocity ) ) {
            velocity = new Vector2D( photon.getVelocity() );
            this.generateAnimation( photon );
        }
    }

    /**
     *
     * @param body
     */
    protected void setPosition( Particle body ) {
        Photon particle = (Photon)body;

        // Need to subtract half the width and height of the image to locate it
        // porperly. The particle's location is its center, but the location of
        // the graphic is the upper-left corner of the bounding box.
        // TODO: coordinate the size of the particle and the image
        // TODO: the + and - signs are dependent on the transform from world to screen coords. They should not be hard-coded
        float x = particle.getPosition().getX() - particle.getRadius();
        float y = particle.getPosition().getY() - particle.getRadius();
        setPosition( x, y );
    }

    //
    // Static fields and methods
    //
    static protected int s_imgHeight = (int)Photon.s_radius;
    static protected int s_imgLength = 20;

    static String s_imageName = LaserConfig.PHOTON_IMAGE_FILE;
    static String s_highEnergyImageName = LaserConfig.HIGH_ENERGY_PHOTON_IMAGE_FILE;
    static String s_midEnergyImageName = LaserConfig.MID_HIGH_ENERGY_PHOTON_IMAGE_FILE;
    static String s_lowEnergyImageName = LaserConfig.LOW_ENERGY_PHOTON_IMAGE_FILE;
    static Image s_particleImage;
    static Image s_highEnergyImage;
    static Image s_midEnergyImage;
    static Image s_lowEnergyImage;

    static {
        ResourceLoader loader = new ResourceLoader();
        s_particleImage = loader.loadImage( s_imageName ).getImage();
        s_highEnergyImage = loader.loadImage( s_highEnergyImageName ).getImage();
        s_midEnergyImage = loader.loadImage( s_midEnergyImageName ).getImage();
        s_lowEnergyImage = loader.loadImage( s_lowEnergyImageName ).getImage();
    }

    // A map that matches photon wavelengths to display colors
    static HashMap colorMap = new HashMap();

    static {
        colorMap.put( new Float( Photon.RED ), Color.red );
        colorMap.put( new Float( Photon.BLUE ), Color.blue );
        colorMap.put( new Float( Photon.DEEP_RED ), new Color( 100, 0, 0 ) );
    }


    // A map of maps for holding photon animations. Inner maps hold animations keyed
    // by their angle of travel. The outer map keys the inner maps by color
    static HashMap s_animationMap = new HashMap();

    // Generated all the photon animations
    static {
        HashMap blueAnimationMap = new HashMap();
        s_animationMap.put( new Float( Photon.BLUE ), blueAnimationMap );
        HashMap deepRedAnimationMap = new HashMap();
        s_animationMap.put( new Float( Photon.DEEP_RED ), deepRedAnimationMap );
        HashMap redAnimationMap = new HashMap();
        s_animationMap.put( new Float( Photon.RED ), redAnimationMap );
        generateAnimation( Photon.RED, redAnimationMap );
        generateAnimation( Photon.BLUE, blueAnimationMap );
        generateAnimation( Photon.DEEP_RED, deepRedAnimationMap );
    }

    /**
     * Generates all the images for the animation of a specified wavelength
     * @param wavelength
     * @param animationMap
     */
    static private void generateAnimation( float wavelength, HashMap animationMap ) {

        int numImgs = (int)( ( 20f / 680 ) * wavelength );
        for( float theta = 0; theta < Math.PI * 2;
             theta = ( (float)Math.round( ( theta + 0.1f ) * 10 ) ) / 10 ) {

            Image[] animation = new Image[numImgs];
            // Compute the size of buffered image needed to hold the rotated copy of the
            // base generator image, and create the transform op for doing the rotation
            AffineTransform xform = AffineTransform.getRotateInstance( theta, s_imgLength / 2, s_imgHeight / 2 );
            int xPrime = (int)( s_imgLength * Math.abs( Math.cos( theta ) ) + s_imgHeight * Math.abs( Math.sin( theta ) ) );
            int yPrime = (int)( s_imgLength * Math.abs( Math.sin( theta ) ) + s_imgHeight * Math.abs( Math.cos( theta ) ) );
            AffineTransformOp xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );

            // Generate the frames for the animation
            float phaseAngleIncr = (float)( Math.PI * 2 ) / numImgs;
            for( int i = 0; i < numImgs; i++ ) {
                float phaseAngle = phaseAngleIncr * i;
                BufferedImage buffImg = computeGeneratorImage( wavelength, phaseAngle );
                BufferedImage animationFrame = new BufferedImage( xPrime, yPrime, BufferedImage.TYPE_INT_ARGB );
                animationFrame = xformOp.filter( buffImg, null );
                animation[i] = Toolkit.getDefaultToolkit().createImage( animationFrame.getSource() );
            }
            animationMap.put( new Float( theta ), animation );
        }
    }

    /**
     *
     */
    static private BufferedImage computeGeneratorImage( float wavelength, float phaseAngle ) {

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( s_imgLength,
                                               s_imgHeight,
                                               BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = img.createGraphics();
        int kPrev = s_imgHeight / 2;
        int iPrev = 0;
        float freqFactor = 10 * wavelength / 680;
        for( int i = 0; i < s_imgLength; i++ ) {
            int k = (int)( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * s_imgHeight / 2 + s_imgHeight / 2 );
            for( int j = 0; j < s_imgHeight; j++ ) {
                if( j == k ) {
                    Color c = (Color)colorMap.get( new Float( wavelength ) );
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
}




