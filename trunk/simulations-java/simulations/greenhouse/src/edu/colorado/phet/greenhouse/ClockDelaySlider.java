// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.greenhouse;

import java.awt.Font;
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
 * Slider that controls the amount of delay between clock ticks for the given
 * clock.  This is intended for use in the clock control panel.
 * 
 * Author: Sam Reid, John Blanco
 */
public class ClockDelaySlider extends VerticalLayoutPanel {
	
    private LinearValueControl linearSlider;
    private int maxDelay;

    public ClockDelaySlider( int maxDelay, int minDelay, String textFieldPattern, final ConstantDtClock clock,
    		String title) {
    	this.maxDelay = maxDelay;
    	double minSliderValue = 1;
    	double maxSliderValue = (double)maxDelay / (double)minDelay;
        linearSlider = new LinearValueControl( minSliderValue, maxSliderValue, "", textFieldPattern, "" );
        linearSlider.setTextFieldVisible( false );
        Hashtable<Double, JLabel> table = new Hashtable<Double, JLabel>();
        table.put( new Double( minSliderValue ), new JLabel( PhetCommonResources.getString( "Common.time.slow" ) ) );
        table.put( new Double( maxSliderValue ), new JLabel( PhetCommonResources.getString( "Common.time.fast" ) ) );
        if (title != null){
        	final JLabel value = new JLabel(  title );
        	value.setFont( new PhetFont( Font.ITALIC, PhetFont.getDefaultFontSize()) );
        	table.put( new Double( ( minSliderValue + maxSliderValue ) / 2 ), value );
        }
        linearSlider.setTickLabels( table );
        clock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            public void delayChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                update( clock );
            }
        } );
        update( clock );
        add( linearSlider );
    }

    public void addChangeListener( ChangeListener ch ) {
        linearSlider.addChangeListener( ch );
    }
    
    public void update(ConstantDtClock clock){
        linearSlider.setValue( mapDelayValueToSliderValue( clock.getDelay() ) );
    }
    
    private double mapDelayValueToSliderValue(int delay){
    	return (double)maxDelay / (double)delay;
    }
    
    public double getValue() {
        return linearSlider.getValue();
    }

    public void setValue( double dt ) {
        linearSlider.setValue( dt );
    }
    
    public static void main(String[] args) {
		JFrame testFrame = new JFrame();
		testFrame.add(new ClockDelaySlider(100, 30, "Test Slider", new ConstantDtClock(10, 10), "Test Clock"));
		testFrame.pack();
		testFrame.setVisible(true);
	}
}
