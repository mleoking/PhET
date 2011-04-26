// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.bendinglight.BendingLightStrings;
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
public abstract class BendingLightModel implements ResetModel {
    //Default values
    public static final double DEFAULT_LASER_DISTANCE_FROM_PIVOT = 8.125E-6;
    public static final double DIAMOND_INDEX_OF_REFRACTION_FOR_RED_LIGHT = 2.419;

    //Mediums that can be selected
    public static final MediumState AIR = new MediumState( BendingLightStrings.AIR, 1.000293 );
    public static final MediumState WATER = new MediumState( BendingLightStrings.WATER, 1.333 );
    public static final MediumState GLASS = new MediumState( BendingLightStrings.GLASS, 1.5 );
    public static final MediumState DIAMOND = new MediumState( BendingLightStrings.DIAMOND, DIAMOND_INDEX_OF_REFRACTION_FOR_RED_LIGHT );
    public static final MediumState MYSTERY_A = new MediumState( BendingLightStrings.MYSTERY_A, DIAMOND_INDEX_OF_REFRACTION_FOR_RED_LIGHT, true );
    public static final MediumState MYSTERY_B = new MediumState( BendingLightStrings.MYSTERY_B, 1.4, true );

    //Model parameters
    public static final double SPEED_OF_LIGHT = 2.99792458E8;
    public static final double WAVELENGTH_RED = 650E-9;
    public static final double RED_LIGHT_FREQUENCY = SPEED_OF_LIGHT / WAVELENGTH_RED;//To come up with a good time scale dt, use lambda = v/f.  For lambda = RED_WAVELENGTH and C=SPEED_OF_LIGHT, we have f=4.612E14
    public static final double TIME_SPEEDUP_SCALE = 2.5; //Speed up by a factor of 2.5 because default wave view was moving too slow
    public static final double MAX_DT = 1.0 / RED_LIGHT_FREQUENCY / 30 * TIME_SPEEDUP_SCALE;//thirty frames per cycle times the speedup scale
    public static final double MIN_DT = MAX_DT / 10;
    public static final double DEFAULT_DT = MAX_DT / 4;
    public static final double CHARACTERISTIC_LENGTH = WAVELENGTH_RED;//A good size for the units being used in the sim; used to determine the dimensions of various model objects

    private ConstantDtClock clock;
    protected final List<LightRay> rays = new LinkedList<LightRay>();//List of rays in the model
    public final Property<LaserView> laserView = new Property<LaserView>( LaserView.RAY );//Whether the laser is Ray or Wave mode

    //Dimensions of the model, guaranteed to be shown in entirety on the stage
    final double modelWidth = CHARACTERISTIC_LENGTH * 62;
    final double modelHeight = modelWidth * 0.7;

    //Model components
    protected final Laser laser;
    public final Property<Double> wavelengthProperty;
    protected final IntensityMeter intensityMeter = new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 );

    //support for bounding drags
    public final ModelBounds visibleModelBounds = new ModelBounds();//model coordinates of what is visible on the screen, or None if not yet set (has to be set by canvas after canvas is constructed)

    //Listeners
    private final ArrayList<VoidFunction1<LightRay>> rayAddedListeners = new ArrayList<VoidFunction1<LightRay>>();
    private final ArrayList<VoidFunction0> modelUpdateListeners = new ArrayList<VoidFunction0>();
    private final ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    public BendingLightModel( double laserAngle, boolean topLeftQuadrant, final double laserDistanceFromPivot ) {
        laser = new Laser( laserDistanceFromPivot, laserAngle, topLeftQuadrant );

        //When the clock ticks, notify the rays that the time changed so they can animate in wave mode
        this.clock = new ConstantDtClock( 20, DEFAULT_DT ) {{
            addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    for ( LightRay ray : rays ) {
                        ray.setTime( getSimulationTime() );
                    }
                }
            } );
        }};

        //Update the model when any dependent properties change
        new RichSimpleObserver() {
            public void update() {
                updateModel();
            }
        }.observe( laser.on, laser.pivot, laser.emissionPoint, intensityMeter.sensorPosition, intensityMeter.enabled, laser.color, laserView );

        //Initialize the wavelength property
        wavelengthProperty = new Property<Double>( BendingLightModel.WAVELENGTH_RED ) {{
            addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    laser.color.setValue( new LaserColor.OneColor( value ) );
                }
            } );
        }};

        //When the laser view changes, recompute the model and set the laser wave state to reflect the new mode
        laserView.addObserver( new SimpleObserver() {
            public void update() {
                updateModel();//Maybe it would be better just to regenerate view, but now we just do this by telling the model to recompute and repopulate
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

    //Clear the model in preparation for another ray propagation update phase
    protected void clearModel() {
        for ( LightRay ray : rays ) {
            ray.remove();
        }
        rays.clear();
        intensityMeter.clearRayReadings();//Clear the accumulator in the intensity meter so it can sum up the newly created rays
    }

    //Update the model by clearing the rays, then recreating them
    public void updateModel() {
        clearModel();
        propagateRays();
        for ( VoidFunction0 modelUpdateListener : modelUpdateListeners ) {
            modelUpdateListener.apply();
        }
    }

    //Abstract method for creating all the rays in the model after the model has been cleared
    protected abstract void propagateRays();

    //Get the fraction of power transmitted through the medium
    public static double getTransmittedPower( double n1, double n2, double cosTheta1, double cosTheta2 ) {
        return 4 * n1 * n2 * cosTheta1 * cosTheta2 / ( pow( n1 * cosTheta1 + n2 * cosTheta2, 2 ) );
    }

    //Get the fraction of power reflected from the medium
    public static double getReflectedPower( double n1, double n2, double cosTheta1, double cosTheta2 ) {
        return pow( ( n1 * cosTheta1 - n2 * cosTheta2 ) / ( n1 * cosTheta1 + n2 * cosTheta2 ), 2 );
    }

    //Add a listener that is notified after the model gets updated (by having the ray propagation scheme run again)
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