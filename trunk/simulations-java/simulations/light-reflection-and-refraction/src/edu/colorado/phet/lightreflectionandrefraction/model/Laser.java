// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.lightreflectionandrefraction.view.LaserColor;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.lightreflectionandrefraction.model.LRRModel.WAVELENGTH_RED;

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
