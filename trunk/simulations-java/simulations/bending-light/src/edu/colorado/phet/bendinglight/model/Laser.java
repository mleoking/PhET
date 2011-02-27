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
    public final Property<Double> angle;
    public final Property<ImmutableVector2D> emissionPoint;

    public final double distanceFromPivot;
    public final Property<ImmutableVector2D> pivot = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );

    public final Property<Boolean> on = new Property<Boolean>( false );
    public final Property<LaserColor> color = new Property<LaserColor>( new LaserColor.OneColor( WAVELENGTH_RED ) );
    public final Property<Boolean> wave = new Property<Boolean>( false );

    public static final double MAX_ANGLE_IN_WAVE_MODE = 3.0194002144959584;//so the refracted wave mode doesn't get too big

    public Laser( final double distanceFromPivot, final double angle, final boolean topLeftQuadrant ) {
        this.distanceFromPivot = distanceFromPivot;
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

        emissionPoint = new Property<ImmutableVector2D>( parseAngleAndMagnitude( distanceFromPivot, this.angle.getValue() ) );
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                emissionPoint.setValue( parseAngleAndMagnitude( distanceFromPivot, Laser.this.angle.getValue() ).getAddedInstance( pivot.getValue() ) );
            }
        };
        this.angle.addObserver( observer );
        pivot.addObserver( observer );
    }

    public void resetAll() {
        angle.reset();
        on.reset();
        color.reset();
        wave.reset();
    }

    public void translate( double dx, double dy ) {
        emissionPoint.setValue( emissionPoint.getValue().getAddedInstance( dx, dy ) );
        pivot.setValue( pivot.getValue().getAddedInstance( dx, dy ) );
    }

    public ImmutableVector2D getDirectionUnitVector() {
        return ImmutableVector2D.parseAngleAndMagnitude( 1, angle.getValue() + Math.PI );//TODO: why is this flipped by 180 degrees?
    }
}
