// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * All units in SI unless otherwise noted
 */
public abstract class PlateModel {
    public final VoidNotifier modelChanged = new VoidNotifier();

    // grid used mainly for the (x,z) terrain and elevation in general
    public final Grid3D grid;

    // full bounds of the simulated model
    public final Bounds3D bounds;

    protected PlateModel( Grid3D grid ) {
        this.grid = grid;
        this.bounds = grid.getBounds();
    }

    public abstract double getElevation( double x, double z );

    public abstract double getDensity( double x, double y ); // z = 0 (cross section plate)

    public abstract double getTemperature( double x, double y ); // z = 0 (cross section plate)

    public void update( double timeElapsed ) {

    }

    /*---------------------------------------------------------------------------*
    * common temperature models
    *----------------------------------------------------------------------------*/

    public static double ZERO_CELSIUS = 293.15;

    // mean air temperature
    public static double getAirTemperature( double y ) {
        // simplified model! see http://www.engineeringtoolbox.com/air-altitude-temperature-d_461.html

        assert y >= 0; // water/land below 0
        return ZERO_CELSIUS + 15 - 15 * y / 2500; // approximately zero celsius at 2500m (8000ft)
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

    public static final float EARTH_RADIUS = 6371000;

    // TODO: doc, but basically handles the roundness of the earth in the X direction
    public static Vector3f convertToRadial( Vector3f planar ) {
        float radius = planar.getY() + EARTH_RADIUS; // add in radius of the earth

        float theta = FastMath.PI / 2 - planar.getX() / EARTH_RADIUS; // dividing by the radius actually gets us the correct thing
        float phi = FastMath.PI / 2 - planar.getZ() / EARTH_RADIUS;

        float sinPhi = FastMath.sin( phi );
        return new Vector3f( radius * FastMath.cos( theta ) * sinPhi, radius * FastMath.sin( theta ) * sinPhi - EARTH_RADIUS, radius * FastMath.cos( phi ) );
    }
}
