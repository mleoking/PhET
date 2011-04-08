// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * Model for the laser, which emits LightRays.
 *
 * @author Sam Reid
 */
public class Laser {
    //Independent variables
    public final Property<ImmutableVector2D> emissionPoint;//where the light comes from
    public final Property<ImmutableVector2D> pivot = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );//point to be pivoted about, and at which the laser points

    public final Property<Boolean> on = new Property<Boolean>( false );
    public final Property<LaserColor> color = new Property<LaserColor>( new LaserColor.OneColor( WAVELENGTH_RED ) );
    public final Property<Boolean> wave = new Property<Boolean>( false );

    public static final double MAX_ANGLE_IN_WAVE_MODE = 3.0194002144959584;//so the refracted wave mode doesn't get too big

    public Laser( final double distanceFromPivot, final double angle, final boolean topLeftQuadrant ) {
        //Prevent laser from going to 90 degrees when in wave mode, should go until laser bumps into edge.
        final SimpleObserver clampAngle = new SimpleObserver() {
            public void update() {
                if ( wave.getValue() && getAngle() > MAX_ANGLE_IN_WAVE_MODE && topLeftQuadrant ) {
                    setAngle( MAX_ANGLE_IN_WAVE_MODE );
                }
            }
        };
        wave.addObserver( clampAngle );
        emissionPoint = new Property<ImmutableVector2D>( parseAngleAndMagnitude( distanceFromPivot, angle ) );
        emissionPoint.addObserver( clampAngle );
    }

    public void resetAll() {
        on.reset();
        color.reset();
        wave.reset();
    }

    public void translate( Dimension2D delta ) {
        translate( delta.getWidth(), delta.getHeight() );
    }

    public void translate( double dx, double dy ) {
        emissionPoint.setValue( emissionPoint.getValue().plus( dx, dy ) );
        pivot.setValue( pivot.getValue().plus( dx, dy ) );
    }

    public ImmutableVector2D getDirectionUnitVector() {
        return pivot.getValue().minus( emissionPoint.getValue() ).getNormalizedInstance();//TODO: why is this flipped by 180 degrees?
    }

    //Rotate about the fixed pivot
    public void setAngle( double angle ) {
        double distFromPivot = pivot.getValue().getDistance( emissionPoint.getValue() );
        emissionPoint.setValue( ImmutableVector2D.parseAngleAndMagnitude( distFromPivot, angle ).plus( pivot.getValue() ) );
    }

    public double getAngle() {
        return getDirectionUnitVector().getAngle() + Math.PI;//TODO: why is this backwards by 180 degrees?
    }

    public double getDistanceFromPivot() {
        return emissionPoint.getValue().minus( pivot.getValue() ).getMagnitude();
    }

    public double getWavelength() {
        return color.getValue().getWavelength();
    }

    public double getFrequency() {
        return BendingLightModel.SPEED_OF_LIGHT / getWavelength();
    }
}
