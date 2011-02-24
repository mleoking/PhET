// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

import static edu.colorado.phet.bendinglight.model.LRRModel.WAVELENGTH_RED;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * @author Sam Reid
 */
public class Laser {
    public final Property<Double> distanceFromOrigin;
    public final Property<Double> angle;
    public final Property<Boolean> on = new Property<Boolean>( false );
    public final Property<LaserColor> color = new Property<LaserColor>( new LaserColor.OneColor( WAVELENGTH_RED ) );

    public Laser( double distFromOrigin, double angle ) {
        this.distanceFromOrigin = new Property<Double>( distFromOrigin );
        this.angle = new Property<Double>( angle );
    }

    public ImmutableVector2D getEmissionPoint() {
        return parseAngleAndMagnitude( distanceFromOrigin.getValue(), angle.getValue() );
    }

    public void resetAll() {
        distanceFromOrigin.reset();
        angle.reset();
        on.reset();
        color.reset();
    }
}
