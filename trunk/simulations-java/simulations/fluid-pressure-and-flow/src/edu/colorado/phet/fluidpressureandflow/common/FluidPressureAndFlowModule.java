// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;

/**
 * Base class for the different modules for FluidPressureAndFlow, parametrized on its model type.
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowModule<T extends FluidPressureAndFlowModel> extends Module {
    public final T model;

    //User settings
    public final Property<Boolean> fluidDensityControlVisible = new Property<Boolean>( false );
    public final Property<Boolean> gravityControlVisible = new Property<Boolean>( false );
    public final BooleanProperty rulerVisible = new BooleanProperty( false );
    public final BooleanProperty gridVisible = new BooleanProperty( false );
    public final ObservableProperty<Boolean> meterStickVisible;
    public final ObservableProperty<Boolean> yardStickVisible;

    protected FluidPressureAndFlowModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;
        //Show the meter stick if the units are in meters
        meterStickVisible = new And( rulerVisible, new ValueEquals<UnitSet>( model.units, UnitSet.METRIC ) );

        //Show the yard stick if the units are in feet (whether in atm or psi pressure unit)
        yardStickVisible = new And( rulerVisible, new ValueEquals<UnitSet>( model.units, UnitSet.ENGLISH ).or( new ValueEquals<UnitSet>( model.units, UnitSet.ATMOSPHERES ) ) );

        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }

    public ConstantDtClock getConstantDtClock() {
        return model.getClock();
    }

    public void reset() {
        rulerVisible.reset();
        gridVisible.reset();
        fluidDensityControlVisible.reset();
        gravityControlVisible.reset();
        model.reset();
    }
}