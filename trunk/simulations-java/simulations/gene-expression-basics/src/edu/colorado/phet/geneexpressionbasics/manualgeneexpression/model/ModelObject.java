// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * An basic object in the model.  The main thing that this model object does
 * is to define the shape of the object.  Note that the shape also defines the
 * position, and no separate position information exists.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public abstract class ModelObject {
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
    public ModelObject( Shape shape ) {
        this.shapeProperty = new Property<Shape>( shape );
        // Set the position handle, which in this sim is always the center
        // bottom of the shape.
        positionHandle.setLocation( shape.getBounds2D().getCenterX(), shape.getBounds2D().getMinY() );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

    public Property<Shape> getShapeProperty() {
        return shapeProperty;
    }

    protected void setShapeProperty( Shape newShape ) {
        shapeProperty.set( newShape );
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
