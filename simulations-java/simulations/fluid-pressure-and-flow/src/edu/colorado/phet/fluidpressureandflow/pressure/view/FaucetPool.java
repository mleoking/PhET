// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;

/**
 * @author Sam Reid
 */
public interface FaucetPool extends IPool {
    double getWaterOutputCenterX();

    ObservableProperty<Double> getWaterVolume();

    ObservableProperty<Boolean> getDrainFaucetEnabled();

    Property<Double> getDrainFlowRate();

    Property<Double> getInputFlowRatePercentage();

    ObservableProperty<Boolean> getInputFaucetEnabled();

    double getInputFaucetX();

    double getWaterHeight();
}