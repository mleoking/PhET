// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * An element in the model for the torque sim that has a shape.  Most model
 * elements do have a shape, but some are defined instead by images.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class ShapeModelElement {
    private final Property<Shape> shapeProperty;
    public final BooleanProperty userControlled = new BooleanProperty( false );

    // The position handle is the point that is used as a reference when
    // setting the position of this object.  For the purposes this simulation,
    // we always assume that the position handle is the center bottom of the
    // shape.  TODO: This could be generalized by having a constructor where
    // the position handle is explicitly specified, but this was not deemed
    // necessary at initial design time.  Decide at some point whether it is
    // worthwhile.
    protected final Point2D positionHandle = new Point2D.Double();

    /**
     * Constructor.
     *
     * @param shape The shape of the model object.
     */
    public ShapeModelElement( Shape shape ) {
        this.shapeProperty = new Property<Shape>( shape );
        // Set the position handle, which in this sim is always the center
        // bottom of the shape.
        positionHandle.setLocation( shape.getBounds2D().getCenterX(), shape.getBounds2D().getMinY() );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

//    public Property<Shape> getShapeProperty() {
//        return shapeProperty;
//    }

    /**
     * Set a new shape value.  This should only be done by subclasses.
     *
     * @param newShape
     */
    protected void setShape( Shape newShape ) {
        shapeProperty.set( newShape );
    }

    public void addShapeObserver( VoidFunction1<Shape> observer ) {
        shapeProperty.addObserver( observer );
    }

    public void addShapeObserver( SimpleObserver observer ) {
        shapeProperty.addObserver( observer );
    }

    public void removeShapeObserver( VoidFunction1<Shape> observer ) {
        shapeProperty.removeObserver( observer );
    }

    public void removeShapeObserver( SimpleObserver observer ) {
        shapeProperty.removeObserver( observer );
    }

    // TODO: Probably need to make this abstract, but it has a default implementation for now.
    public void translate( ImmutableVector2D modelDelta ) {
        // Does nothing by default.
    }

    public void setPosition( Point2D newPosition ) {
        setPosition( newPosition.getX(), newPosition.getY() );
    }

    // TODO: Probably need to make this abstract, but it has a default implementation for now.
    public void setPosition( double x, double y ) {
        // Does nothing by default.
    }

    /**
     * Get the current position, which is defined by the 'position handle'.
     */
    public Point2D getPosition() {
        return new Point2D.Double( positionHandle.getX(), positionHandle.getY() );
    }
}
