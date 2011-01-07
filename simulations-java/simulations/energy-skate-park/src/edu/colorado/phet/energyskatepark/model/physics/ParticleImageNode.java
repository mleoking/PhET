// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Mar 3, 2007
 * Time: 3:30:21 AM
 */

public class ParticleImageNode extends PNode {
    private Particle particle;
    private PImage imageNode;
    private double w;
    private double h;
    private double scale;

    private boolean centered = false;

    public ParticleImageNode( final Particle particle ) {
        this.particle = particle;
        imageNode = PImageFactory.create( "energy-skate-park/images/skater3.png" );
        w = imageNode.getFullBounds().getWidth();
        h = imageNode.getFullBounds().getHeight();
        scale = 0.004 * 1.4;
        imageNode.scale( scale );
        addChild( imageNode );
        particle.addListener( new Particle.Listener() {
            public void particleChanged() {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                particle.setPosition( new SerializablePoint2D( event.getPositionRelativeTo( ParticleImageNode.this ) ) );
                particle.setUserControlled( true );
                particle.setVelocity( 0, 0 );
            }

            public void mouseReleased( PInputEvent event ) {
                particle.setUserControlled( false );
                particle.setFreeFall();
            }
        } );
        update();
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered( boolean centered ) {
        this.centered = centered;
        update();
    }

    public void update() {
        imageNode.setOffset( particle.getX(), particle.getY() );
        imageNode.setRotation( particle.getAngle() + Math.PI / 2 );
        imageNode.translate( -w / 2.0, centered ? -h / 2.0 : -h );
//        imageNode.translate( -w / 2.0, -h/2.0  );
    }
}
