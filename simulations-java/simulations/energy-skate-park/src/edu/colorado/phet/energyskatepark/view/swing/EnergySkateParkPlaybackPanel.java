package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:34 PM
 */
public class EnergySkateParkPlaybackPanel extends DefaultTimeModelControlPanel {

    public EnergySkateParkPlaybackPanel( final EnergySkateParkModule module, final TimeSeriesModel timeSeriesModel, final Clock clock ) {
        super( clock, timeSeriesModel );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesCleared() {
                module.setRecordOrLiveMode();
            }
        } );
    }

}
