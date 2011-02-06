// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Laser {
    public final Property<Double> distanceFromOrigin;
    public final Property<Double> angle = new Property<Double>( Math.PI * 3 / 4 );
    public final Property<Boolean> on = new Property<Boolean>( false );
    public final Property<Color> color = new Property<Color>( Color.red );

    public Laser( double distFromOrigin ) {
        this.distanceFromOrigin = new Property<Double>( distFromOrigin );
    }

    public ImmutableVector2D getEmissionPoint() {
        return ImmutableVector2D.parseAngleAndMagnitude( distanceFromOrigin.getValue(), angle.getValue() );
    }
}
