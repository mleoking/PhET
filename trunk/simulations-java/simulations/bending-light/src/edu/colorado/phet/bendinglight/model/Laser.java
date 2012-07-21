// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;
import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.createPolar;

/**
 * Model for the laser, which emits LightRays.
 *
 * @author Sam Reid
 */
public class Laser {
    //Independent variables
    public final Property<Vector2D> emissionPoint;//where the light comes from
    public final Property<Vector2D> pivot = new Property<Vector2D>( new Vector2D( 0, 0 ) );//point to be pivoted about, and at which the laser points

    public final Property<Boolean> on = new Property<Boolean>( false );//True if the laser is activated and emitting light
    public final Property<LaserColor> color = new Property<LaserColor>( new LaserColor.OneColor( WAVELENGTH_RED ) );
    public final Property<Boolean> wave = new Property<Boolean>( false );

    public static final double MAX_ANGLE_IN_WAVE_MODE = 3.0194;//so the refracted wave mode doesn't get too big because at angle = PI it would become infinite.  This value was determined by printing out actual angle values at runtime and sampling a good value.

    public Laser( final double distanceFromPivot, final double angle, final boolean topLeftQuadrant ) {
        //Prevent laser from going to 90 degrees when in wave mode, should go until laser bumps into edge.
        final SimpleObserver clampAngle = new SimpleObserver() {
            public void update() {
                if ( wave.get() && getAngle() > MAX_ANGLE_IN_WAVE_MODE && topLeftQuadrant ) {
                    setAngle( MAX_ANGLE_IN_WAVE_MODE );
                }
            }
        };
        wave.addObserver( clampAngle );

        //Model the point where light comes out of the laser
        emissionPoint = new Property<Vector2D>( createPolar( distanceFromPivot, angle ) );
        emissionPoint.addObserver( clampAngle );
    }

    //Reset all parts of the laser
    public void resetAll() {
        on.reset();
        color.reset();
        wave.reset();
        resetLocation();
    }

    //Called if the laser is dropped out of bounds
    public void resetLocation() {
        emissionPoint.reset();
        pivot.reset();
    }

    public void translate( Dimension2D delta ) {
        translate( delta.getWidth(), delta.getHeight() );
    }

    public void translate( double dx, double dy ) {
        emissionPoint.set( emissionPoint.get().plus( dx, dy ) );
        pivot.set( pivot.get().plus( dx, dy ) );
    }

    public Vector2D getDirectionUnitVector() {
        return pivot.get().minus( emissionPoint.get() ).getNormalizedInstance();
    }

    //Rotate about the fixed pivot
    public void setAngle( double angle ) {
        double distFromPivot = pivot.get().distance( emissionPoint.get() );
        emissionPoint.set( Vector2D.createPolar( distFromPivot, angle ).plus( pivot.get() ) );
    }

    public double getAngle() {
        return getDirectionUnitVector().getAngle() + Math.PI;//TODO: why is this backwards by 180 degrees?
    }

    public double getDistanceFromPivot() {
        return emissionPoint.get().minus( pivot.get() ).getMagnitude();
    }

    public double getWavelength() {
        return color.get().getWavelength();
    }

    public double getFrequency() {
        return BendingLightModel.SPEED_OF_LIGHT / getWavelength();
    }
}
