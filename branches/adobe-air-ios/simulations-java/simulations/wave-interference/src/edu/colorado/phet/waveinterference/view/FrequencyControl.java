// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ModelSliderLayoutStrategy;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 8:24:25 PM
 */

public class FrequencyControl extends VerticalLayoutPanel {
    public FrequencyControl( final Oscillator oscillator ) {
//        final LinearValueControl frequencySlider = new LinearValueControl( 0, 3, WIStrings.getString( "controls.frequency" ), "0.00", WIStrings.getString( "units.frequency" ) );
        final LinearValueControl frequencySlider = new LinearValueControl( 0, 3, WIStrings.getString( "controls.frequency" ), "0.00",
                                                                           null,//no units visible
                                                                           new ModelSliderLayoutStrategy() );//center the title
        frequencySlider.setValue( oscillator.getFrequency() );
        frequencySlider.setBorder( null );
        frequencySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                oscillator.setFrequency( frequencySlider.getValue() );
            }
        } );
        frequencySlider.setTextFieldVisible( false );
        frequencySlider.getSlider().setPaintLabels( false );
        frequencySlider.getSlider().setPaintTicks( false );
        add( frequencySlider );
        oscillator.addListener( new Oscillator.Adapter() {
            public void frequencyChanged() {
                frequencySlider.setValue( oscillator.getFrequency() );
            }
        } );
    }
}
