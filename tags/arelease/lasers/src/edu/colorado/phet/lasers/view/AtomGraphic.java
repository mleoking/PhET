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
import edu.colorado.phet.lasers.physics.atom.*;
import edu.colorado.phet.graphics.util.GraphicsUtil;
import edu.colorado.phet.physics.body.Particle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.util.Observable;

public class AtomGraphic extends ImageGraphic {

    private Image image;

    public AtomGraphic() {
        super( s_particleImage, 0, 0 );
        init();
    }

    public AtomGraphic( Particle particle ) {
        // Need to subtract half the width and height of the image to locate it
        // porperly
        super( s_particleImage, particle.getPosition().getX(), particle.getPosition().getY() );
        init();
        init( particle );
        setPosition( particle );
    }

    private void init() {
        this.setRep( getImage() );
    }

    public void paint( Graphics2D g ) {
        super.paint( g );
    }

    protected Image getImage() {
        if( s_particleImage == null ) {
            ResourceLoader loader = new ResourceLoader();
            ResourceLoader.LoadedImageDescriptor imageDescriptor = loader.loadImage( s_imageName );
            s_particleImage = imageDescriptor.getImage();
            this.s_radius = imageDescriptor.getWidth() / 2;
        }
        return s_particleImage;
    }

    public void update( Observable observable, Object o ) {
        super.update( observable, o );
        setPosition( (edu.colorado.phet.physics.body.Particle)observable );
        AtomicState state = ( (Atom)this.getBody() ).getState();
        if( state instanceof GroundState ) {
//            this.setRep( s_groundStateImage );
            this.setImage( groundImg );
        }
        if( state instanceof SpontaneouslyEmittingState ) {
//            BufferedImage img = GraphicsUtil.toBufferedImage( s_highEnergyStateImage );
            this.setImage( highImg );
//            this.setRep( s_highEnergyStateImage );
        }
        if( state instanceof MiddleEnergyState ) {
//            BufferedImage img = GraphicsUtil.toBufferedImage( s_middleEnergyStateImage );
            this.setImage( middleImg );
//            this.setRep( s_middleEnergyStateImage );
        }
    }

    protected void setPosition( Particle body ) {
        Atom particle = (Atom)body;

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
    static private double s_radius = 10;

    static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;
    static String s_groundStateImageName = LaserConfig.GROUND_STATE_IMAGE_FILE;
    static String s_highEnergyStateImageName = LaserConfig.HIGH_ENERGY_STATE_IMAGE_FILE;
    static String s_middleEnergyStateImageName = LaserConfig.MIDDLE_ENERGY_STATE_IMAGE_FILE;
    static Image s_particleImage;
    static Image s_groundStateImage;
    static Image s_highEnergyStateImage;
    static Image s_middleEnergyStateImage;

    static BufferedImage groundImg;
    static BufferedImage highImg;
    static BufferedImage middleImg;

    // Load the images for atoms and scale them to the correct size
    static {
        ResourceLoader loader = new ResourceLoader();
        ResourceLoader.LoadedImageDescriptor imageDescriptor = loader.loadImage( s_imageName );
        s_particleImage = imageDescriptor.getImage();
        imageDescriptor = loader.loadImage( s_groundStateImageName );
        s_groundStateImage = imageDescriptor.getImage();
        imageDescriptor = loader.loadImage( s_highEnergyStateImageName );
        s_highEnergyStateImage = imageDescriptor.getImage();
        imageDescriptor = loader.loadImage( s_middleEnergyStateImageName );
        s_middleEnergyStateImage = imageDescriptor.getImage();

        groundImg = GraphicsUtil.toBufferedImage( s_groundStateImage );
        AffineTransformOp xformOp;
        AffineTransform xform;
        BufferedImage tempBI;
        tempBI = GraphicsUtil.toBufferedImage( s_highEnergyStateImage );
        xform = AffineTransform.getScaleInstance( 1.2, 1.2 );
        xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        highImg = new BufferedImage( (int)( tempBI.getWidth() * 1.2 ),
                                     (int)( tempBI.getHeight() * 1.2 ),
                                     BufferedImage.TYPE_INT_RGB );
        highImg = xformOp.filter( tempBI, null );

        xform = AffineTransform.getScaleInstance( 1.1, 1.1 );
        xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        middleImg = new BufferedImage( (int)( tempBI.getWidth() * 1.1 ),
                                     (int)( tempBI.getHeight() * 1.1 ),
                                     BufferedImage.TYPE_INT_RGB );
        tempBI = GraphicsUtil.toBufferedImage( s_middleEnergyStateImage );
        middleImg = xformOp.filter( tempBI, null );
    }

}

