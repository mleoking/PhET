/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AtomGraphic extends PhetImageGraphic implements SimpleObserver {

    static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;
    static String s_groundStateImageName = LaserConfig.GROUND_STATE_IMAGE_FILE;
    static String s_highEnergyStateImageName = LaserConfig.HIGH_ENERGY_STATE_IMAGE_FILE;
    static String s_middleEnergyStateImageName = LaserConfig.MIDDLE_ENERGY_STATE_IMAGE_FILE;
    static Image s_particleImage;
    static Image s_groundStateImage;
    static Image s_highEnergyStateImage;
    static Image s_middleEnergyStateImage;
    static double s_middleEnergyMag = 1.2;
    static double s_highEnergyMag = 1.4;

    static BufferedImage groundImg;
    static BufferedImage highImg;
    static BufferedImage middleImg;

    // Load the images for atoms and scale them to the correct size
    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            s_groundStateImage = ImageLoader.loadBufferedImage( s_groundStateImageName );
            s_highEnergyStateImage = ImageLoader.loadBufferedImage( s_highEnergyStateImageName );
            s_middleEnergyStateImage = ImageLoader.loadBufferedImage( s_middleEnergyStateImageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        groundImg = GraphicsUtil.toBufferedImage( s_groundStateImage );
        AffineTransformOp xformOp;
        AffineTransform xform;
        BufferedImage tempBI;
        tempBI = GraphicsUtil.toBufferedImage( s_highEnergyStateImage );
        xform = AffineTransform.getScaleInstance( 1.2, 1.2 );
        xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        highImg = new BufferedImage( (int)( tempBI.getWidth() * s_highEnergyMag ),
                                     (int)( tempBI.getHeight() * s_highEnergyMag ),
                                     BufferedImage.TYPE_INT_RGB );
        highImg = xformOp.filter( tempBI, null );

        xform = AffineTransform.getScaleInstance( 1.1, 1.1 );
        xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        middleImg = new BufferedImage( (int)( tempBI.getWidth() * s_middleEnergyMag ),
                                       (int)( tempBI.getHeight() * s_middleEnergyMag ),
                                       BufferedImage.TYPE_INT_RGB );
        tempBI = GraphicsUtil.toBufferedImage( s_middleEnergyStateImage );
        middleImg = xformOp.filter( tempBI, null );
    }


    private Atom atom;

    public AtomGraphic( Component component, Atom atom ) {
        super( component, s_groundStateImageName );
        this.atom = atom;
        atom.addObserver( this );
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
        AtomicState state = atom.getState();
        if( true || getImage() != middleImg ) {
            if( state instanceof GroundState ) {
                super.setImage( groundImg );
            }
            if( state instanceof HighEnergyState ) {
                super.setImage( highImg );
            }
            if( state instanceof MiddleEnergyState ) {
                super.setImage( middleImg );
            }
            setPosition( (int)( atom.getPosition().getX() - atom.getRadius() ),
                         (int)( atom.getPosition().getY() - atom.getRadius() ) );
            setBoundsDirty();
            repaint();
        }
    }
}

