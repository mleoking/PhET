package edu.colorado.phet.bernoulli.common.animation;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.GraphicsUtilities;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.physics2d.Force;
import edu.colorado.phet.coreadditions.physics2d.Particle2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 12:14:33 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class AnimationTest {

    public Image[] createAnimation() {
        ModelViewTransform2d transform = new ModelViewTransform2d( new Rectangle2D.Double( 0, 0, 1, 1 ), new Rectangle( 0, 0, 16, 16 ) );
        final Particle2d particle = new Particle2d( .6, .5, .1, .1 );
        final Particle2d particle2 = new Particle2d( .4, .5, -.15, -.4 );
        int numFrames = 100;
        Image[] frames = new Image[numFrames];
        int width = 16;
        int height = 16;
        Color purple = new Color( 220, 140, 250 );
        for( int i = 0; i < numFrames; i++ ) {
            BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
            Graphics2D g = bi.createGraphics();
            g.setColor( purple );
            g.fillRect( 0, 0, width, height );
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g.setColor( Color.blue );
            Point view = transform.modelToView( particle.getX(), particle.getY() );
            int pointWidth = 4;
            g.fillOval( view.x - pointWidth / 2, view.y - pointWidth / 2, pointWidth, pointWidth );
            g.setColor( Color.red );
            view = transform.modelToView( particle2.getX(), particle2.getY() );
            g.fillOval( view.x - pointWidth / 2, view.y - pointWidth / 2, pointWidth, pointWidth );
            frames[i] = bi;
            double dt = 1;
            final double k = 104;
            particle.addForce( new Force() {
                public Point2D.Double getForce() {
                    return AnimationTest.getForce( particle2, particle, k );
                }
            } );
            particle2.addForce( new Force() {
                public Point2D.Double getForce() {
                    return AnimationTest.getForce( particle, particle2, k );
                }
            } );
            particle.stepInTime( dt );
            particle2.stepInTime( dt );
            double max = .24;
            ensureBounds( particle, dt, max, transform );
            ensureBounds( particle2, dt, max, transform );
        }
        return frames;
    }

    private static Point2D.Double getForce( Particle2d causer, Particle2d particle, double k ) {
        PhetVector pv = new PhetVector( particle.getX() - causer.getX(), particle.getY() - causer.getY() );
        double f = pv.getMagnitude() * k;
        f = Math.abs( f );
        double maxf = .001;
        if( f > maxf ) {
            f = maxf;
        }
//        if (f < .0000001)
//            f = 0;
        PhetVector force = pv.getScaledInstance( -f );
        return new Point2D.Double( force.getX(), force.getY() );
    }

    public void ensureBounds( Particle2d particle, double dt, double maxVel, ModelViewTransform2d transform ) {
        if( particle.getVelocityVector().getMagnitude() > maxVel ) {
            PhetVector vel = particle.getVelocityVector().getNormalizedInstance().getScaledInstance( maxVel );
            particle.setVelocity( vel.getX(), vel.getY() );
        }
        if( particle.getX() > transform.getModelBounds().width ) {
            particle.setVelocityX( -particle.getVelocityVector().getX() );
            particle.setX( transform.getModelBounds().width - .1 );
        }
        if( particle.getX() < 0 ) {
            particle.setVelocityX( -particle.getVelocityVector().getX() );
            particle.setX( .1 );
        }
        if( particle.getY() > transform.getModelBounds().height ) {
            particle.setVelocityY( -particle.getVelocityVector().getY() );
            particle.setY( transform.getModelBounds().height - .1 );
        }
        if( particle.getY() < 0 ) {
            particle.setVelocityY( -particle.getVelocityVector().getY() );
            particle.setY( .1 );
        }
    }

    public Image[] createAnimationTest() {
        BufferedImage bi = new ImageLoader().loadBufferedImage( "images/Phet-logo-16x16.gif" );
        ArrayList animation = new ArrayList();
        int numSteps = 20;
        double dtheta = Math.PI * 2.0 / numSteps;

        for( double theta = 0; theta < Math.PI * 2; theta += dtheta ) {
            AffineTransform transform = GraphicsUtilities.getImageTransform( bi, theta, bi.getWidth() / 2, bi.getHeight() / 2 );
            AffineTransformOp op = new AffineTransformOp( transform, AffineTransformOp.TYPE_BILINEAR );
            BufferedImage out = op.filter( bi, op.createCompatibleDestImage( bi, bi.getColorModel() ) );
            animation.add( out );
        }
        Image[] images = (Image[])animation.toArray( new Image[0] );
        return images;
    }
}
