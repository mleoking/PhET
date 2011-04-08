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
    public final Property<Polygon> shape;

    public Prism( ImmutableVector2D... vectors ) {
        this( new Polygon( vectors ) );
    }

    public Prism( Polygon polygon ) {
        this.shape = new Property<Polygon>( polygon );
    }

    public void translate( double dx, double dy ) {
        shape.setValue( shape.getValue().getTranslatedInstance( dx, dy ) );
    }

    public ArrayList<Intersection> getIntersections( Ray incidentRay ) {
        return shape.getValue().getIntersections( incidentRay );
    }

    public boolean contains( ImmutableVector2D emissionPoint ) {
        return shape.getValue().toShape().contains( emissionPoint.toPoint2D() );
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
