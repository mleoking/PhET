package edu.colorado.phet.common.timeseries.ui;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

import javax.swing.*;
import java.util.Hashtable;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:44 PM
 */
public class TimeSpeedSlider extends LinearValueControl {
    private ConstantDtClock energySkateParkClock;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock ) {
        super( min, max, "", textFieldPattern, "" );
        this.energySkateParkClock = defaultClock;
        setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new JLabel( TimeseriesResources.getString( "time.slow" ) ) );
        table.put( new Double( max ), new JLabel( TimeseriesResources.getString( "time.normal" ) ) );
        setTickLabels( table );
        setValue( max );
        defaultClock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                update( defaultClock );
            }
        } );
        update( defaultClock );
    }

    private void update( ConstantDtClock defaultClock ) {
        setValue( defaultClock.getTimingStrategy().getSimulationTimeChangeForPausedClock() );
    }
}
