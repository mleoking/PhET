package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:15:44 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */
class ParticleNode extends PNode {
    private Particle particle;
    private PhetPPath phetPPath;

    public ParticleNode( final Particle particle ) {
        this.particle = particle;
        Color y = Color.yellow;
        phetPPath = new PhetPPath( new Color( y.getRed(), y.getGreen(), y.getBlue(), 128 ), new BasicStroke( 1 ), Color.red );
        double w = 30;
        phetPPath.setPathTo( new Ellipse2D.Double( 0, 0, w, w ) );
        addChild( phetPPath );
        particle.addListener( new Particle.Listener() {
            public void particleChanged() {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                particle.setPosition( event.getPositionRelativeTo( ParticleNode.this ) );
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
        phetPPath.setOffset( particle.getX() - phetPPath.getWidth() / 2, particle.getY() - phetPPath.getHeight() / 2 );
    }
}
