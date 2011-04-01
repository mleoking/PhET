// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.clock;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.SliderOnlyLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Simulation speed slider.
 *
 * @author Sam Reid
 */
public class TimeSpeedSlider extends JPanel {
    private final LinearValueControl linearSlider;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock ) {
    	this( min, max, textFieldPattern, defaultClock, PhetCommonResources.getString( "Common.sim.speed" ) );
    }

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock, String title) {
        this( min, max, textFieldPattern, defaultClock, title, Color.BLACK );
    }

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock,
    		String title, final Color textColor ) {

        // title
        final JLabel titleLabel = new TimeSpeederLabel( title, textColor );
        titleLabel.setFont( new PhetFont( Font.ITALIC, PhetFont.getDefaultFontSize()) );

        // slider
        linearSlider = new LinearValueControl( min, max, "", textFieldPattern, "", new SliderOnlyLayoutStrategy() );
        linearSlider.setTextFieldVisible( false );
        Hashtable<Double,JLabel> table = new Hashtable<Double,JLabel>();
        table.put( new Double( min ), new TimeSpeederLabel( PhetCommonResources.getString( "Common.time.slow" ), textColor ) );
        table.put( new Double( max ), new TimeSpeederLabel( PhetCommonResources.getString( "Common.time.fast" ), textColor ) );
        linearSlider.setTickLabels( table );
        defaultClock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            @Override
            public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                update( defaultClock );
            }
        } );
        update( defaultClock );

        // layout, title centered above slider
        setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.CENTER;
        add( titleLabel, c );
        add( linearSlider, c );
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

    public double getMin(){
        return linearSlider.getMinimum();
    }

    public double getMax(){
        return linearSlider.getMaximum();
    }

    public void setValue( double dt ) {
        linearSlider.setValue( dt );
    }

    private static class TimeSpeederLabel extends JLabel{
        public TimeSpeederLabel( String text, Color textColor ) {
            super( text );
            setForeground( textColor );
        }
    }

    public static void main(String[] args) {
        JFrame testFrame = new JFrame();
        testFrame.add(new TimeSpeedSlider(0, 100, "Test Slider", new ConstantDtClock(10, 10)));
        testFrame.pack();
        testFrame.setVisible(true);
    }
}
