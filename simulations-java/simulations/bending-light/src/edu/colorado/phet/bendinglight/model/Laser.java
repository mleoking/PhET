// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * @author Sam Reid
 */
public class Laser {
    public final Property<Double> distanceFromOrigin;
    public final Property<Double> angle;
    public final Property<Boolean> on = new Property<Boolean>( false );
    public final Property<LaserColor> color = new Property<LaserColor>( new LaserColor.OneColor( WAVELENGTH_RED ) );
    public final Property<Boolean> wave = new Property<Boolean>( false );

    public static final double MAX_ANGLE_IN_WAVE_MODE = 3.0194002144959584;//so the refracted wave mode doesn't get too big

    public Laser( final double distFromOrigin, final double angle, final boolean topLeftQuadrant ) {
        this.distanceFromOrigin = new Property<Double>( distFromOrigin );
        this.angle = new Property<Double>( angle );

        //Prevent laser from going to 90 degrees when in wave mode, should go until laser bumps into edge.
        final SimpleObserver clampAngle = new SimpleObserver() {
            public void update() {
                if ( wave.getValue() && Laser.this.angle.getValue() > MAX_ANGLE_IN_WAVE_MODE && topLeftQuadrant ) {
                    Laser.this.angle.setValue( MAX_ANGLE_IN_WAVE_MODE );
                }
            }
        };
        this.angle.addObserver( clampAngle );
        wave.addObserver( clampAngle );
    }

    public ImmutableVector2D getEmissionPoint() {
        return parseAngleAndMagnitude( distanceFromOrigin.getValue(), angle.getValue() );
    }

    public void resetAll() {
        distanceFromOrigin.reset();
        angle.reset();
        on.reset();
        color.reset();
        wave.reset();
    }
}
