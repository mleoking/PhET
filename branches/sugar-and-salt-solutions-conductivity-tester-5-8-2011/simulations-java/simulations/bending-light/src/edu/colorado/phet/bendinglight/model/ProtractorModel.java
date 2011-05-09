// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the protractor angle and position
 *
 * @author Sam Reid
 */
public class ProtractorModel {
    public final Property<ImmutableVector2D> position;//Position of the center
    public final Property<Double> angle = new Property<Double>( 0.0 );

    public ProtractorModel( double x, double y ) {
        position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
    }

    public void translate( double x, double y ) {
        position.setValue( new ImmutableVector2D( position.getValue().getX() + x, position.getValue().getY() + y ) );
    }

    public void translate( ImmutableVector2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    public void reset() {
        position.reset();
        angle.reset();
    }
}
