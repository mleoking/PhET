// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:15:44 AM
 */
public class ParticleNode extends PNode {
    private Particle particle;
    private PhetPPath phetPPath;

    public ParticleNode( final Particle particle ) {
        this.particle = particle;
        Color color = Color.yellow;
        phetPPath = new PhetPPath( toTransparentColor( color, 128 ), new BasicStroke( 0.02f ), Color.red );
        double w = 0.30f;
        phetPPath.setPathTo( new Ellipse2D.Double( 0, 0, w, w ) );
        addChild( phetPPath );
        particle.addListener( new Particle.Listener() {
            public void particleChanged() {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                particle.setPosition( new SerializablePoint2D( event.getPositionRelativeTo( ParticleNode.this ) ) );
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

    private Color toTransparentColor( Color color, int alpha ) {
        return new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
    }

    public void update() {
        phetPPath.setOffset( particle.getX() - phetPPath.getWidth() / 2, particle.getY() - phetPPath.getHeight() / 2 );
        phetPPath.setPaint( toTransparentColor( getParticleColor(), 128 ) );
        phetPPath.setStrokePaint( getColorForTop() );
    }

    private Paint getColorForTop() {
        if( particle.isFreeFall() ) {
            return particle.isAboveSplineZero() ? Color.green : Color.black;
        }
        else {
            return particle.isAboveSpline1D() ? Color.green : Color.yellow;
        }
    }

    private Color getParticleColor() {
        if( particle.isFreeFall() ) {
            return Color.blue;
        }
        else {
            return Color.red;
        }
    }
}
