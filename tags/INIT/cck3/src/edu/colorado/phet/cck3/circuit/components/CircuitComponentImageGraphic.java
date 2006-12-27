/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintImageGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class CircuitComponentImageGraphic extends FastPaintImageGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private Rectangle2D.Double src;

    public CircuitComponentImageGraphic( BufferedImage image, Component parent, CircuitComponent component, ModelViewTransform2D transform ) {
        super( image, parent );

        this.component = component;
        this.transform = transform;
        component.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
        src = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        } );
        changed();
    }

    private void changed() {
        AffineTransform at = createTransform();
        super.setTransform( at );
    }

    private AffineTransform createTransform() {
        Point2D srcpt = transform.toAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.toAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double dist = component.getStartJunction().getPosition().distance( component.getEndJunction().getPosition() );
        double length = component.getLength();
        double diff = Math.abs( length - dist );

        if( diff > Double.parseDouble( "1E-10" ) ) {
            throw new RuntimeException( "Components moved to a weird place, dist=" + dist + ", length=" + length + ", diff=" + diff );
        }

        double newHeight = transform.modelToViewDifferentialY( component.getHeight() );
        AffineTransform at = createTransform( getBufferedImage().getWidth(), getBufferedImage().getHeight(),
                                              srcpt, dstpt, newHeight );
        return at;
    }

    public static AffineTransform createTransform( int initWidth, int initHeight,
                                                   Point2D src, Point2D dst, double newHeight ) {
        double dist = src.distance( dst );
        double newLength = dist;
        double angle = new ImmutableVector2D.Double( src, dst ).getAngle();
        AffineTransform trf = new AffineTransform();
        trf.rotate( angle, src.getX(), src.getY() );
        trf.translate( 0, -newHeight / 2 );
        trf.translate( src.getX(), src.getY() );
//        System.out.println( "newLength = " + newLength + ", newHeight=" + newHeight );
        if( newLength == 0 || newHeight == 0 ) {
            throw new RuntimeException( "Length or height is zero." );
        }
        trf.scale( newLength / initWidth, newHeight / initHeight );
        return trf;
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getComponent() {
        return component;
    }
}
