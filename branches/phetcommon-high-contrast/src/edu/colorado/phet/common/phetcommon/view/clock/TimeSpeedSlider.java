package edu.colorado.phet.common.phetcommon.view.clock;

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
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:44 PM
 */
public class TimeSpeedSlider extends VerticalLayoutPanel {
    private LinearValueControl linearSlider;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock ) {
    	this( min, max, textFieldPattern, defaultClock, PhetCommonResources.getString( "Common.sim.speed" ) );
    }

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final ConstantDtClock defaultClock,
    		String title) {
        linearSlider = new LinearValueControl( min, max, "", textFieldPattern, "" );
        linearSlider.setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new JLabel( PhetCommonResources.getString( "Common.time.slow" ) ) );
        table.put( new Double( max ), new JLabel( PhetCommonResources.getString( "Common.time.fast" ) ) );
        final JLabel value = new JLabel(  title );
        value.setFont( new PhetFont( Font.ITALIC, PhetFont.getDefaultFontSize()) );
        table.put( new Double( ( max + min ) / 2 ), value );
        linearSlider.setTickLabels( table );
        defaultClock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
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

    public void setValue( double dt ) {
        linearSlider.setValue( dt );
    }
    
    public static void main(String[] args) {
		JFrame testFrame = new JFrame();
		testFrame.add(new TimeSpeedSlider(0, 100, "Test Slider", new ConstantDtClock(10, 10)));
		testFrame.pack();
		testFrame.setVisible(true);
	}
}
