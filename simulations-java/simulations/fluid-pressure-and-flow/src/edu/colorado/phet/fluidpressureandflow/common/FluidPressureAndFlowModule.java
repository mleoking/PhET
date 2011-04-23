// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.Units;

/**
 * Base class for the different modules for FluidPressureAndFlow, parameterized on its model type.
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowModule<T extends FluidPressureAndFlowModel> extends Module {
    private final T model;
    public final Property<Boolean> fluidDensityControlVisible = new Property<Boolean>( false );
    public final Property<Boolean> gravityControlVisible = new Property<Boolean>( false );
    public final Property<Boolean> rulerVisible = new Property<Boolean>( false );
    public final ObservableProperty<Boolean> meterStickVisible;
    public final ObservableProperty<Boolean> yardStickVisible;
    public final Property<Boolean> hoseVisible = new Property<Boolean>( false );

    protected FluidPressureAndFlowModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;
        meterStickVisible = new And( rulerVisible, new ValueEquals<Units.Unit>( model.distanceUnit, Units.METERS ) );
        yardStickVisible = new And( rulerVisible, new ValueEquals<Units.Unit>( model.distanceUnit, Units.FEET ) );

        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }

    public void reset() {
        rulerVisible.reset();
        fluidDensityControlVisible.reset();
        gravityControlVisible.reset();
        model.reset();
    }

    public T getFluidPressureAndFlowModel() {
        return model;
    }
}