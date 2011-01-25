// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.clock;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:44 PM
 */
public class TimeSpeedSlider extends VerticalLayoutPanel {
    private final LinearValueControl linearSlider;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock ) {
    	this( min, max, textFieldPattern, defaultClock, PhetCommonResources.getString( "Common.sim.speed" ) );
    }

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock,
            String title) {
        this( min, max, textFieldPattern, defaultClock, title, Color.BLACK );
    }

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock,
    		String title, final Color textColor ) {
        linearSlider = new LinearValueControl( min, max, "", textFieldPattern, "" );
        linearSlider.setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new TimeSpeederLabel( PhetCommonResources.getString( "Common.time.slow" ), textColor ) );
        table.put( new Double( max ), new TimeSpeederLabel( PhetCommonResources.getString( "Common.time.fast" ), textColor ) );
        final JLabel value = new TimeSpeederLabel( title, textColor );
        value.setFont( new PhetFont( Font.ITALIC, PhetFont.getDefaultFontSize()) );
        table.put( new Double( ( max + min ) / 2 ), value );
        linearSlider.setTickLabels( table );
        defaultClock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            @Override
            public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                update( defaultClock );
            }
        } );
        update( defaultClock );
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

    public double getMin(){
        return linearSlider.getMinimum();
    }

    public double getMax(){
        return linearSlider.getMaximum();
    }

    public void setValue( double dt ) {
        linearSlider.setValue( dt );
    }

    public static void main(String[] args) {
		JFrame testFrame = new JFrame();
		testFrame.add(new TimeSpeedSlider(0, 100, "Test Slider", new ConstantDtClock(10, 10)));
		testFrame.pack();
		testFrame.setVisible(true);
	}

    private static class TimeSpeederLabel extends JLabel{
        public TimeSpeederLabel( String text, Color textColor ) {
            super( text );
            setForeground( textColor );
        }
    }
}
