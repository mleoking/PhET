/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.physics.atom.*;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.io.IOException;

public class AtomGraphic extends PhetImageGraphic implements SimpleObserver {
//public class AtomGraphic extends ImageGraphic {

    private Image rep;
    private Atom atom;

    public AtomGraphic( Component component, Atom atom ) {
        super( component, null );
        this.atom = atom;
//        super( s_particleImage, 0, 0 );
        update();
    }


//    public static BufferedImage getImage() {
////    protected Image getImage() {
//        if( s_particleImage == null ) {
////            ResourceLoader loader = new ResourceLoader();
////            ResourceLoader.LoadedImageDescriptor imageDescriptor = loader.loadImage( s_imageName );
////            s_particleImage = imageDescriptor.getImage();
//            try {
//                s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
//            }
//            catch( IOException e ) {
//                e.printStackTrace();
//            }
//            this.s_radius = s_particleImage.getWidth( null ) / 2;
////            this.s_radius = imageDescriptor.getWidth() / 2;
//        }
//        return s_particleImage;
//    }

    public void update() {
//    public void update( Observable observable, Object o ) {
//        super.update( observable, o );
//        setPosition( (edu.colorado.phet.physics.body.Particle)observable );
        setPosition( (int)( atom.getPosition().getX() - atom.getRadius()),
                     (int)(atom.getPosition().getY() - atom.getRadius() ));
        AtomicState state = atom.getState();
        if( state instanceof GroundState ) {
//            this.setRep( s_groundStateImage );
            rep = groundImg;
        }
        if( state instanceof SpontaneouslyEmittingState ) {
//            BufferedImage img = GraphicsUtil.toBufferedImage( s_highEnergyStateImage );
            rep = highImg;
//            this.setRep( s_highEnergyStateImage );
        }
        if( state instanceof MiddleEnergyState ) {
//            BufferedImage img = GraphicsUtil.toBufferedImage( s_middleEnergyStateImage );
            rep = middleImg;
//            this.setRep( s_middleEnergyStateImage );
        }
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
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            s_groundStateImage = ImageLoader.loadBufferedImage( s_groundStateImageName );
            s_highEnergyStateImage = ImageLoader.loadBufferedImage( s_groundStateImageName );
            s_middleEnergyStateImage = ImageLoader.loadBufferedImage( s_groundStateImageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

//        ResourceLoader loader = new ResourceLoader();
//        ResourceLoader.LoadedImageDescriptor imageDescriptor = loader.loadImage( s_imageName );
//        s_particleImage = imageDescriptor.getImage();
//        imageDescriptor = loader.loadImage( s_groundStateImageName );
//        s_groundStateImage = imageDescriptor.getImage();
//        imageDescriptor = loader.loadImage( s_highEnergyStateImageName );
//        s_highEnergyStateImage = imageDescriptor.getImage();
//        imageDescriptor = loader.loadImage( s_middleEnergyStateImageName );
//        s_middleEnergyStateImage = imageDescriptor.getImage();
//
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

