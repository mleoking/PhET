package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Mar 3, 2007
 * Time: 3:30:21 AM
 * Copyright (c) Mar 3, 2007 by Sam Reid
 */

public class ParticleImageNode extends PNode {
    private Particle particle;
    private PImage imageNode;
    private double w;
    private double h;
    private double scale;

    public ParticleImageNode( final Particle particle ) {
        this.particle = particle;
        imageNode = PImageFactory.create( "images/skater3.png" );
        w = imageNode.getFullBounds().getWidth();
        h = imageNode.getFullBounds().getHeight();
        scale = 0.004*1.4;
        imageNode.scale( scale );
        addChild( imageNode );
        particle.addListener( new Particle.Listener() {
            public void particleChanged() {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                particle.setPosition( event.getPositionRelativeTo( ParticleImageNode.this ) );
                particle.setUserUpdateStrategy();
                particle.setVelocity( 0, 0 );
            }

            public void mouseReleased( PInputEvent event ) {
                particle.setFreeFall();
            }
        } );
        update();
    }

    public void update() {
//        imageNode.setRotation( 0 );
//        imageNode.rotateAboutPoint( particle.getAngle(), imageNode.getFullBounds().getWidth() / 2.0, imageNode.getFullBounds().getHeight() / 2.0 );

//        imageNode.setOffset( particle.getX() - imageNode.getFullBounds().getWidth() / 2, particle.getY() - imageNode.getFullBounds().getHeight() / 2 );
        imageNode.setOffset( particle.getX(), particle.getY() );
        imageNode.setRotation( particle.getAngle() + Math.PI / 2 );
//        imageNode.translate( -imageNode.getFullBounds().getWidth() / 2.0, imageNode.getFullBounds().getHeight() / 2.0 );
//        imageNode.translate( -w / 2.0, -h / 2.0 );
        imageNode.translate( -w / 2.0, -h  );
//        imageNode.rotate( particle.getAngle()+Math.PI/2);
//        imageNode.setRotation( particle.getAngle()+Math.PI/2);
    }
}
