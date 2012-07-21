// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the protractor angle and position
 *
 * @author Sam Reid
 */
public class ProtractorModel {
    public final Property<Vector2D> position;//Position of the center
    public final Property<Double> angle = new Property<Double>( 0.0 );

    public ProtractorModel( double x, double y ) {
        position = new Property<Vector2D>( new Vector2D( x, y ) );
    }

    public void translate( double x, double y ) {
        position.set( new Vector2D( position.get().getX() + x, position.get().getY() + y ) );
    }

    public void translate( Vector2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    public void reset() {
        position.reset();
        angle.reset();
    }
}
