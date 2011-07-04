// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Conductivity tester that can be dipped in the water to light a light bulb.
 *
 * @author Sam Reid
 */
public class ConductivityTester implements IConductivityTester {

    private final Point2D.Double negativeProbeLocation;
    private final Point2D.Double positiveProbeLocation;
    private final Point2D.Double location;
    private final double negativeProbeX;
    private final double positiveProbeX;
    final double defaultProbeY;

    public ConductivityTester( double beakerWidth, double beakerHeight ) {
        //Locations are in model coordinates (meters).
        //Note that in the typical usage scenario (dragged out of a toolbox), these values are overriden with other values in SugarAndSaltSolutionsConductivityTesterNode
        defaultProbeY = beakerHeight;
        negativeProbeX = -beakerWidth / 3;
        positiveProbeX = +beakerWidth / 3;

        //Position of the probes, in model coordinates
        negativeProbeLocation = new Point2D.Double( negativeProbeX, defaultProbeY );
        positiveProbeLocation = new Point2D.Double( positiveProbeX, defaultProbeY );

        //Set the initial position
        location = new Point2D.Double( 0, defaultProbeY );
    }

    //Listeners
    private final ArrayList<ConductivityTesterChangeListener> conductivityTesterListeners = new ArrayList<ConductivityTesterChangeListener>();

    //True if the user has selected to use the conductivity tester
    public final Property<Boolean> visible = new Property<Boolean>( false );

    //Brightness value (between 0 and 1)
    public final Property<Double> brightness = new Property<Double>( 0.0 ) {{

        //When brightness changes, forward change events to ConductivityTesterChangeListeners
        addObserver( new SimpleObserver() {
            public void update() {
                for ( ConductivityTesterChangeListener conductivityTesterListener : conductivityTesterListeners ) {
                    conductivityTesterListener.brightnessChanged();
                }
            }
        } );
    }};

    public final Property<Boolean> shortCircuited = new Property<Boolean>( false );

    //Model shapes corresponding to where the battery and bulb are
    private Shape batteryRegion;
    private Shape bulbRegion;

    //Determine if the conductivity tester is visible
    public boolean isVisible() {
        return visible.get();
    }

    //Add a listener
    public void addConductivityTesterChangeListener( ConductivityTesterChangeListener conductivityTesterChangeListener ) {
        conductivityTesterListeners.add( conductivityTesterChangeListener );
    }

    //Determine the size of the probes in meters
    public PDimension getProbeSizeReference() {
        return new PDimension( 0.0125, 0.025 );
    }

    //Returns the region in space occupied by the positive probe, used for hit detection with the entire probe region
    public ImmutableRectangle2D getPositiveProbeRegion() {
        return new ImmutableRectangle2D( positiveProbeLocation.getX() - getProbeSizeReference().getWidth() / 2, positiveProbeLocation.getY(), getProbeSizeReference().getWidth(), getProbeSizeReference().getHeight() );
    }

    //Returns the region in space occupied by the negative probe, used for hit detection with the entire probe region
    public ImmutableRectangle2D getNegativeProbeRegion() {
        return new ImmutableRectangle2D( negativeProbeLocation.getX() - getProbeSizeReference().getWidth() / 2, negativeProbeLocation.getY(), getProbeSizeReference().getWidth(), getProbeSizeReference().getHeight() );
    }

    //Determine the location of the positive probe
    public Point2D getPositiveProbeLocationReference() {
        return positiveProbeLocation;
    }

    //Determine the location of the bulb/battery unit.
    public Point2D getLocationReference() {
        return location;
    }

    //Set the location of the positive probe and notify observers
    public void setPositiveProbeLocation( double x, double y ) {
        positiveProbeLocation.setLocation( x, y );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.positiveProbeLocationChanged();
        }
    }

    //Get the location of the negative probe
    public Point2D getNegativeProbeLocationReference() {
        return negativeProbeLocation;
    }

    //Set the location of the negative probe and notify observers
    public void setNegativeProbeLocation( double x, double y ) {
        negativeProbeLocation.setLocation( x, y );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.negativeProbeLocationChanged();
        }
    }

    //Get the bulb brightness, a function of the conductivity of the liquid
    public double getBrightness() {
        return brightness.get();
    }

    public void reset() {
        visible.reset();
        brightness.reset();

        //Reset the location of the probes
        setNegativeProbeLocation( negativeProbeX, defaultProbeY );
        setPositiveProbeLocation( positiveProbeX, defaultProbeY );
    }

    //Sets the location of the unit (battery + bulb) and notifies listeners
    public void setLocation( double x, double y ) {
        location.setLocation( x, y );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.locationChanged();
        }
    }


    //Setters and getters for the battery region, set by the view since bulb and battery are primarily view components. Used to determine if the circuit should short out.
    public void setBatteryRegion( Shape shape ) {
        this.batteryRegion = shape;
    }

    public Shape getBatteryRegion() {
        return batteryRegion;
    }

    //Setters and getters for the bulb region, set by the view since bulb and battery are primarily view components.  Used to determine if the circuit should short out.
    public void setBulbRegion( Shape shape ) {
        this.bulbRegion = shape;
    }

    public Shape getBulbRegion() {
        return bulbRegion;
    }
}
