package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowModule<T extends FluidPressureAndFlowModel> extends Module {
    private T model;
    private Property<Boolean> rulerVisibleProperty = new Property<Boolean>( false );

    protected FluidPressureAndFlowModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;

        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }

    public void resetAll() {
    }

    public T getFluidPressureAndFlowModel() {
        return model;
    }

    public Property<Boolean> getRulerVisibleProperty() {
        return rulerVisibleProperty;
    }
}
