// ParticleGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.graphics.ImageGraphic;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.physics.collision.CollidableBody;

import java.awt.*;
import java.util.Observable;

/**
 * The graphic representation of a physical particle
 */
public class ParticleGraphic extends ImageGraphic {

    public ParticleGraphic() {
        super( s_particleImage, 0, 0 );
        init();
    }

    public ParticleGraphic( Particle particle ) {
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
//            this.radius = imageDescriptor.getWidth() / 2;
        }
        return s_particleImage;
    }

    public void update( Observable observable, Object o ) {
        super.update( observable, o );
        setPosition( (Particle)observable );
    }

    protected void setPosition( CollidableBody body ) {
        Particle particle = (Particle)body;

        // Need to subtract half the width and height of the image to locate it
        // porperly. The particle's location is its center, but the location of
        // the graphic is the upper-left corner of the bounding box.
        // TODO: coordinate the size of the particle and the image
        // TODO: the + and - signs are dependent on the transform from world to screen coords. They should not be hard-coded
        float x = (float)( particle.getPosition().getX() - particle.getRadius() );
        float y = (float)( particle.getPosition().getY() - particle.getRadius() );
        setPosition( x, y );
    }

    //
    // Static fields and methods
    //
    static String s_imageName = IdealGasConfig.PARTICLE_IMAGE_FILE;
    static Image s_particleImage;
}
