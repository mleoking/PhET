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
//    private PNode topIndicator;

    public ParticleNode( final Particle particle ) {
        this.particle = particle;
        Color color = Color.yellow;
        phetPPath = new PhetPPath( toTransparentColor( color, 128 ), new BasicStroke( 0.01f ), Color.red );
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
                particle.setPosition( event.getPositionRelativeTo( ParticleNode.this ) );
                particle.setUserUpdateStrategy();
                particle.setVelocity( 0, 0 );
            }

            public void mouseReleased( PInputEvent event ) {
                particle.setFreeFall();
            }
        } );

//        topIndicator = new PNode();
//        addChild( topIndicator );
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
            return particle.isBelowSplineZero()?Color.black : Color.green;
        }else{
            return particle.isBelowSpline1D()?Color.black:Color.green;
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
