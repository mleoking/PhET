// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;

import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ATMOSPHERES;
import static java.lang.Math.abs;

/**
 * Model for the "pressure" tab
 *
 * @author Sam Reid
 */
public class FluidPressureModel extends FluidPressureAndFlowModel {

    public final Pool squarePool = new Pool();
    public final IPool trapezoidPool = new TrapezoidPool();

    //Pool within which the user can measure the pressure
    public final Property<IPool> pool = new Property<IPool>( squarePool );

    //Flag indicating whether the atmosphere is enabled or not.  it's nice to be able to turn it off and just focus on the water.
    //It also emphasizes that the reason that p is not zero at the top of the water IS the atmosphere.
    public SettableProperty<Boolean> atmosphere = new BooleanProperty( true );

    public FluidPressureModel() {
        super( ATMOSPHERES );

        //Show pressure partly submerged in the water, but at the top of the water
        addPressureSensor( new PressureSensor( this, 0, 0 ) );

        //Show second pressure sensor at a y location that yields a different pressure value
        addPressureSensor( new PressureSensor( this, -4, 2 ) );
    }

    @Override public void addPressureChangeObserver( SimpleObserver updatePressure ) {
        super.addPressureChangeObserver( updatePressure );
        atmosphere.addObserver( updatePressure );
    }

    //Gets the pressure at the specified location.
    @Override public double getPressure( double x, double y ) {

        //TODO: Account for gravity on air pressure
        if ( y < 0 ) {
            return ( atmosphere.get() ? getStandardAirPressure() : 0.0 ) + liquidDensity.get() * gravity.get() * abs( -y );
        }
        else {
            return atmosphere.get() ? super.getPressure( x, y ) : 0.0;
        }
    }
}