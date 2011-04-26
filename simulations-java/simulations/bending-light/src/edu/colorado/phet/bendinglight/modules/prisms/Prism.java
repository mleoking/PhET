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
    public Prism( ImmutableVector2D... points ) {
        this( new Polygon( points ) );
    }

    public Prism( IShape shape ) {
        this.shape = new Property<IShape>( shape );
    }

    public void translate( double dx, double dy ) {
        shape.setValue( shape.getValue().getTranslatedInstance( dx, dy ) );
    }

    //Compute the intersections of the specified ray with this polygon's edges
    public ArrayList<Intersection> getIntersections( Ray incidentRay ) {
        return shape.getValue().getIntersections( incidentRay );
    }

    public boolean contains( ImmutableVector2D point ) {
        return shape.getValue().containsPoint( point );
    }

    public Prism copy() {
        return new Prism( shape.getValue() );
    }

    public Rectangle2D getBounds() {
        return shape.getValue().getBounds();
    }

    public void translate( Dimension2D delta ) {
        translate( delta.getWidth(), delta.getHeight() );
    }

    public void rotate( double deltaAngle ) {
        shape.setValue( shape.getValue().getRotatedInstance( deltaAngle ) );
    }
}
