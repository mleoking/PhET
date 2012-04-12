// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;

/**
 * Immutable model object for a single mass.
 *
 * @author Sam Reid
 */
public class Mass {

    //For resetting
    public final Shape initialShape;

    //Shape of the mass
    public final Shape shape;

    //True if the user is dragging it
    public final boolean dragging;

    //Velocity for animated falling
    public final double velocity;

    //How heavy in KG
    public final double mass;

    //Image to show for this mass
    public final BufferedImage image;
    public final IUserComponent component;

    public Mass( IUserComponent component, final Shape shape, final boolean dragging, final double velocity, final double mass, final BufferedImage image ) {
        this( component, shape, shape, dragging, velocity, mass, image );
    }

    public Mass( IUserComponent component, final Shape initialShape, final Shape shape, final boolean dragging, final double velocity, final double mass, BufferedImage image ) {
        this.initialShape = initialShape;
        this.shape = shape;
        this.dragging = dragging;
        this.velocity = velocity;
        this.mass = mass;
        this.image = image;
        this.component = component;
    }

    public Mass withDragging( final boolean b ) { return new Mass( component, initialShape, shape, b, velocity, mass, image ); }

    public Mass translate( Dimension2D dim ) {
        return new Mass( component, initialShape, AffineTransform.getTranslateInstance( dim.getWidth(), dim.getHeight() ).createTransformedShape( shape ), dragging, velocity, mass, image );
    }

    public double getMinY() {
        return shape.getBounds2D().getMinY();
    }

    public Mass withMinY( final double minY ) {
        return translate( new Dimension2DDouble( 0, minY - getMinY() ) );
    }

    public Mass withVelocity( final double newVelocity ) {
        return new Mass( component, initialShape, shape, dragging, newVelocity, mass, image );
    }

    public Mass withCenterX( final double centerX ) {
        return translate( new Dimension2DDouble( centerX - shape.getBounds2D().getCenterX(), 0 ) );
    }

    public double getMaxY() {
        return shape.getBounds2D().getMaxY();
    }

    public Mass withShape( final Shape initialShape ) {
        return new Mass( component, this.initialShape, initialShape, dragging, velocity, mass, image );
    }
}