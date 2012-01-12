// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Wrapper around a shape with convenience methods for computing intersections, etc.
 *
 * @author Sam Reid
 */
public class Prism {
    public final Property<IShape> shape;

    //Create a prism with the specified corner points
    public Prism( int referencePointIndex, ImmutableVector2D... points ) {
        this( new Polygon( points, referencePointIndex ) );
    }

    public Prism( IShape shape ) {
        this.shape = new Property<IShape>( shape );
    }

    public void translate( double dx, double dy ) {
        shape.set( shape.get().getTranslatedInstance( dx, dy ) );
    }

    //Compute the intersections of the specified ray with this polygon's edges
    public ArrayList<Intersection> getIntersections( Ray incidentRay ) {
        return shape.get().getIntersections( incidentRay );
    }

    public boolean contains( ImmutableVector2D point ) {
        return shape.get().containsPoint( point );
    }

    public Prism copy() {
        return new Prism( shape.get() );
    }

    public Rectangle2D getBounds() {
        return shape.get().getBounds();
    }

    public void translate( Dimension2D delta ) {
        translate( delta.getWidth(), delta.getHeight() );
    }

    public void rotate( double deltaAngle ) {
        shape.set( shape.get().getRotatedInstance( deltaAngle, shape.get().getRotationCenter() ) );
    }
}
