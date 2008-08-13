package edu.colorado.phet.common.timeseries.ui;

import java.util.Hashtable;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:44 PM
 */
public class TimeSpeedSlider extends VerticalLayoutPanel {
    private ConstantDtClock energySkateParkClock;
    private LinearValueControl linearSlider;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock ) {
        linearSlider = new LinearValueControl( min, max, "", textFieldPattern, "" );
        this.energySkateParkClock = defaultClock;
        linearSlider.setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new JLabel( TimeseriesResources.getString( "Common.time.slow" ) ) );
        table.put( new Double( max ), new JLabel( TimeseriesResources.getString( "Common.time.fast" ) ) );
        final JLabel value = new JLabel( TimeseriesResources.getString( "Common.sim.speed" ) );
        value.setFont( new PhetFont( Font.ITALIC, PhetFont.getDefaultFontSize()) );
        table.put( new Double( ( max + min ) / 2 ), value );
        linearSlider.setTickLabels( table );
        defaultClock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                update( defaultClock );
            }
        } );
        update( defaultClock );
//        setBorder( BorderFactory.createTitledBorder( "sim speed" ) );
//        add( new JLabel( "simulation speed" ) );
        add( linearSlider );
    }

    public void addChangeListener( ChangeListener ch ) {
        linearSlider.addChangeListener( ch );
    }

    private void update( ConstantDtClock defaultClock ) {
        linearSlider.setValue( defaultClock.getTimingStrategy().getSimulationTimeChangeForPausedClock() );
    }

    public double getValue() {
        return linearSlider.getValue();
    }

    public void setValue( double dt ) {
        linearSlider.setValue( dt );
    }
}
