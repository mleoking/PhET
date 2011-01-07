// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.And;
import edu.colorado.phet.common.phetcommon.model.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.ValueEquals;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowModule<T extends FluidPressureAndFlowModel> extends Module {
    private final T model;
    private final Property<Boolean> fluidDensityControlVisible = new Property<Boolean>( false );
    private final Property<Boolean> gravityControlVisible = new Property<Boolean>( false );
    private final Property<Boolean> rulerVisibleProperty = new Property<Boolean>( false );
    private final ObservableProperty<Boolean> meterStickVisibleProperty;
    private final ObservableProperty<Boolean> yardStickVisibleProperty;

    protected FluidPressureAndFlowModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;
        meterStickVisibleProperty = new And( rulerVisibleProperty, new ValueEquals<Units.Unit>( model.getDistanceUnitProperty(), Units.METERS ) );
        yardStickVisibleProperty = new And( rulerVisibleProperty, new ValueEquals<Units.Unit>( model.getDistanceUnitProperty(), Units.FEET ) );

        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }

    public void reset() {
        rulerVisibleProperty.reset();
        fluidDensityControlVisible.reset();
        gravityControlVisible.reset();
        model.reset();
    }

    public T getFluidPressureAndFlowModel() {
        return model;
    }

    public Property<Boolean> getRulerVisibleProperty() {
        return rulerVisibleProperty;
    }

    public Property<Boolean> getFluidDensityControlVisible() {
        return fluidDensityControlVisible;
    }

    public Property<Boolean> getGravityControlVisible() {
        return gravityControlVisible;
    }

    public ObservableProperty<Boolean> getMeterStickVisibleProperty() {
        return meterStickVisibleProperty;
    }

    public ObservableProperty<Boolean> getYardStickVisibleProperty() {
        return yardStickVisibleProperty;
    }
}
