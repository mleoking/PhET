package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.model.TimeModelClock;
import edu.colorado.phet.common.timeseries.ui.DefaultTimeModelControlPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:34 PM
 */
public class EnergySkateParkPlaybackPanel extends DefaultTimeModelControlPanel {

    public EnergySkateParkPlaybackPanel( final EnergySkateParkModule module, TimeSeriesModel timeSeriesModel, TimeModelClock clock ) {
        super( clock, timeSeriesModel, EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0,EnergySkateParkApplication.SIMULATION_TIME_DT );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesCleared() {
                module.setRecordOrLiveMode();
            }
        } );
    }

}
