// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static java.lang.Math.pow;

/**
 * Main model for bending light application.  Rays are recomputed whenever laser parameters changed.
 * Each ray oscillates in time, as shown in the wave view.  There are model representations for several tools as well as their visibility.
 *
 * @author Sam Reid
 */
public class BendingLightModel implements ResetModel {
    public static final double DEFAULT_DIST_FROM_PIVOT = 8.125E-6;
    public static double DIAMOND_INDEX_OF_REFRACTION_FOR_RED_LIGHT = 2.419;
    public static final MediumState AIR = new MediumState( "Air", 1.000293 );
    public static final MediumState WATER = new MediumState( "Water", 1.333 );
    public static final MediumState GLASS = new MediumState( "Glass", 1.5 );
    public static final MediumState DIAMOND = new MediumState( "Diamond", DIAMOND_INDEX_OF_REFRACTION_FOR_RED_LIGHT );
    public static final MediumState MYSTERY_A = new MediumState( "Mystery A", DIAMOND_INDEX_OF_REFRACTION_FOR_RED_LIGHT, true );
    public static final MediumState MYSTERY_B = new MediumState( "Mystery B", 1.4, true );

    public static final double SPEED_OF_LIGHT = 2.99792458E8;
    public static final double WAVELENGTH_RED = 650E-9;
    public static final double RED_LIGHT_FREQUENCY = SPEED_OF_LIGHT / WAVELENGTH_RED;//To come up with a good time scale dt, use lambda = v/f.  For lambda = RED_WAVELENGTH and C=SPEED_OF_LIGHT, we have f=4.612E14
    public static final double DT = 1.0 / RED_LIGHT_FREQUENCY / 30 * 2.5;//thirty frames per cycle, sped up by a factor of 2.5 because default wave view was moving too slow
    public static final double CHARACTERISTIC_LENGTH = WAVELENGTH_RED;//A good size for the units being used in the sim; used to determine the dimensions of various model objects

    protected final List<LightRay> rays = new LinkedList<LightRay>();
    private ConstantDtClock clock;
    public final Property<LaserView> laserView = new Property<LaserView>( LaserView.RAY );

    final double modelWidth = CHARACTERISTIC_LENGTH * 62;
    final double modelHeight = modelWidth * 0.7;

    private ArrayList<VoidFunction1<LightRay>> rayAddedListeners = new ArrayList<VoidFunction1<LightRay>>();
    protected final Laser laser;
    protected final IntensityMeter intensityMeter = new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 );
    private final ArrayList<VoidFunction0> modelUpdateListeners = new ArrayList<VoidFunction0>();
    public final Property<Double> wavelengthProperty;
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    public BendingLightModel( double laserAngle, boolean topLeftQuadrant, final double laserDistanceFromPivot ) {
        laser = new Laser( laserDistanceFromPivot, laserAngle, topLeftQuadrant );
        this.clock = new ConstantDtClock( 20, DT ) {{
            addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    for ( LightRay ray : rays ) {
                        ray.setTime( getSimulationTime() );
                    }
                }
            } );
        }};
        new RichSimpleObserver() {
            public void update() {
                updateModel();
            }
        }.observe( laser.on, laser.pivot, laser.emissionPoint, intensityMeter.sensorPosition, intensityMeter.enabled, laser.color, laserView );

        wavelengthProperty = new Property<Double>( BendingLightModel.WAVELENGTH_RED ) {{
            addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    laser.color.setValue( new LaserColor.OneColor( value ) );
                }
            } );
        }};
        laserView.addObserver( new SimpleObserver() {
            public void update() {
                updateModel();//TODO: Maybe it would be better just to regenerate view, but now we just do this by telling the model to recompute and repopulate
                getLaser().wave.setValue( laserView.getValue() == LaserView.WAVE );// synchronize view and model representations of whether it is wave or not
            }
        } );
    }

    protected void addRay( LightRay ray ) {
        rays.add( ray );
        for ( VoidFunction1<LightRay> rayAddedListener : rayAddedListeners ) {
            rayAddedListener.apply( ray );
        }
    }

    public double getWidth() {
        return modelWidth;
    }

    public double getHeight() {
        return modelHeight;
    }

    public void addRayAddedListener( VoidFunction1<LightRay> listener ) {
        rayAddedListeners.add( listener );
    }

    public Laser getLaser() {
        return laser;
    }

    public ConstantDtClock getClock() {
        return clock;
    }

    public Iterable<? extends LightRay> getRays() {
        return rays;
    }

    public IntensityMeter getIntensityMeter() {
        return intensityMeter;
    }

    protected void clearModel() {
        for ( LightRay ray : rays ) {
            ray.remove();
        }
        rays.clear();
        intensityMeter.clearRayReadings();
    }

    public void updateModel() {
        clearModel();
        propagateRays();
        for ( VoidFunction0 modelUpdateListener : modelUpdateListeners ) {
            modelUpdateListener.apply();
        }
    }

    protected void propagateRays() {
    }

    public static double getTransmittedPower( double n1, double n2, double cosTheta1, double cosTheta2 ) {
        return 4 * n1 * n2 * cosTheta1 * cosTheta2 / ( pow( n1 * cosTheta1 + n2 * cosTheta2, 2 ) );
    }

    public static double getReflectedPower( double n1, double n2, double cosTheta1, double cosTheta2 ) {
        return pow( ( n1 * cosTheta1 - n2 * cosTheta2 ) / ( n1 * cosTheta1 + n2 * cosTheta2 ), 2 );
    }

    public void addModelUpdateListener( VoidFunction0 listener ) {
        modelUpdateListeners.add( listener );
    }

    public void addResetListener( VoidFunction0 listener ) {
        resetListeners.add( listener );
    }

    public void resetAll() {
        laser.resetAll();
        intensityMeter.resetAll();
        laserView.reset();
        for ( VoidFunction0 listener : resetListeners ) {
            listener.apply();
        }
    }
}