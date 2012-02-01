// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionPatch;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Bounds3D;

/**
 * All units in SI unless otherwise noted
 */
public abstract class PlateModel {
    // event notification
    public final VoidNotifier modelChanged = new VoidNotifier();
    public final Notifier<Terrain> terrainAdded = new Notifier<Terrain>();
    public final Notifier<Region> regionAdded = new Notifier<Region>();
    public final Notifier<Region> regionRemoved = new Notifier<Region>();
    public final Notifier<CrossSectionPatch> patchAdded = new Notifier<CrossSectionPatch>();
    public final Notifier<CrossSectionPatch> patchRemoved = new Notifier<CrossSectionPatch>();

    // full bounds of the simulated model
    public final Bounds3D bounds;

    // terrains model the surface of the ground (and sea-floor)
    private final List<Terrain> terrains = new ArrayList<Terrain>();

    // regions model the interior of the earth in the z=0 plane
    private final List<Region> regions = new ArrayList<Region>();

    // replacement for regions with improved texturing
    private final List<CrossSectionPatch> patches = new ArrayList<CrossSectionPatch>();

    protected PlateModel( final Bounds3D bounds ) {
        this.bounds = bounds;
    }

    public abstract double getElevation( double x, double z );

    public abstract double getDensity( double x, double y ); // z = 0 (cross section plate)

    public abstract double getTemperature( double x, double y ); // z = 0 (cross section plate), in...

    public void update( double timeElapsed ) {

    }

    public void resetAll() {

    }

    public void addTerrain( Terrain terrain ) {
        terrains.add( terrain );
        terrainAdded.updateListeners( terrain );
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void addRegion( Region region ) {
        regions.add( region );
        regionAdded.updateListeners( region );
    }

    public void removeRegion( Region region ) {
        regions.remove( region );
        regionRemoved.updateListeners( region );
    }

    public List<Region> getRegions() {
        return regions;
    }

    /*---------------------------------------------------------------------------*
    * common temperature models
    *----------------------------------------------------------------------------*/

    public static float ZERO_CELSIUS = 293.15f;

    // mean air temperature
    public static double getAirTemperature( double y ) {
        // simplified model! see http://www.engineeringtoolbox.com/air-altitude-temperature-d_461.html

        assert y >= 0; // water/land below 0
        return ZERO_CELSIUS + 15 - 15 * y / 2500; // approximately zero celsius at 2500m (8000ft)
    }

    public static double getAirDensity( double y ) {
        // calculated from https://en.wikipedia.org/wiki/Density_of_air
        final double p0 = 101.325;
        final double T0 = 288.15;
        final double L = 0.0065;
        final double R = 8.31447;
        final double M = 0.0289644;
        final double g = 9.8;
        final double pressure = p0 * Math.pow( 1 - ( L * y / T0 ), g * M / ( R * L ) );
        return pressure * M / ( R * getAirTemperature( y ) );
    }

    public static double getWaterDensity( double y ) {
        if ( y > -1000 ) {
            return 1025 + 3 * ( y / -1000 );
        }
        else {
            return 1028;
        }
    }

    public static final double DEEP_OCEAN_TEMPERATURE = ZERO_CELSIUS + 4;
    // simplified model! see http://www.windows2universe.org/earth/Water/temp.html
    public static LinearFunction water200to1000 = new LinearFunction( -1000, -200, DEEP_OCEAN_TEMPERATURE, getAirTemperature( 0 ) );

    public static double getWaterTemperature( double y ) {
        assert y <= 0;

        // simplified model! see http://www.windows2universe.org/earth/Water/temp.html
        if ( y > -200 ) {
            // approximately surface temperature
            return getAirTemperature( 0 );
        }
        else if ( y > -1000 ) {
            return water200to1000.evaluate( y );
        }
        else {
            return DEEP_OCEAN_TEMPERATURE;
        }
    }

    public static double getSurfaceTemperature( double y ) {
        if ( y > 0 ) {
            return getAirTemperature( y );
        }
        else {
            return getWaterTemperature( y );
        }
    }

    /*---------------------------------------------------------------------------*
    * earth curvature computations
    *----------------------------------------------------------------------------*/

    public static final float EARTH_RADIUS = 6371000;
    public static final float CENTER_OF_EARTH_Y = -PlateModel.EARTH_RADIUS;
    public static final float MAX_FLAT_X = (float) ( Math.abs( CENTER_OF_EARTH_Y ) * Math.PI );
    public static final ImmutableVector3F EARTH_CENTER = new ImmutableVector3F( 0, -EARTH_RADIUS, 0 );
    public static final ImmutableVector3F RADIAL_Z_0 = new ImmutableVector3F( 1, 1, 0 );

    /**
     * Converts a given "planar" point into a full 3D model point.
     *
     * @param planar "Planar" point, where y is elevation relative to sea level, and x,z are essentially
     *               distance around the circumference of the earth (assumed to be a sphere, not actually
     *               the geoid shape) in the x and z directions. Basically, think of this "planar" point
     *               as spherical coordinates.
     * @return A point in the cartesian coordinate frame in 3D
     */
    public static ImmutableVector3F convertToRadial( ImmutableVector3F planar ) {
        return convertToRadial( getXRadialVector( planar.x ), getZRadialVector( planar.z ), planar.y );
    }

    /**
     * Decomposed performance shortcut for convertToRadial( ImmutableVector3F planar ).
     *
     * @param xRadialVector result of getXRadialVector( x )
     * @param zRadialVector result of getZRadialVector( z )
     * @param y             Same as in the simple version
     * @return A point in the cartesian coordinate frame in 3D
     */
    public static ImmutableVector3F convertToRadial( ImmutableVector3F xRadialVector, ImmutableVector3F zRadialVector, float y ) {
        float radius = y + EARTH_RADIUS; // add in the radius of the earth, since y is relative to mean sea level
        return xRadialVector.componentTimes( zRadialVector ).times( radius ).plus( EARTH_CENTER );
    }

    // improved performance version for z=0 plane
    public static ImmutableVector3F convertToRadial( float x, float y ) {
        return convertToRadial( getXRadialVector( x ), y );
    }

    // improved performance version for z=0 plane
    public static ImmutableVector3F convertToRadial( ImmutableVector3F xRadialVector, float y ) {
        return convertToRadial( xRadialVector, RADIAL_Z_0, y );
    }

    public static ImmutableVector3F getXRadialVector( float x ) {
        float theta = (float) Math.PI / 2 - x / EARTH_RADIUS; // dividing by the radius actually gets us the correct angle
        return new ImmutableVector3F( (float) Math.cos( theta ), (float) Math.sin( theta ), 1 );
    }

    public static ImmutableVector3F getZRadialVector( float z ) {
        float phi = (float) Math.PI / 2 - z / EARTH_RADIUS; // dividing by the radius actually gets us the correct angle
        float sinPhi = (float) Math.sin( phi );
        return new ImmutableVector3F( sinPhi, sinPhi, (float) Math.cos( phi ) );
    }
}
