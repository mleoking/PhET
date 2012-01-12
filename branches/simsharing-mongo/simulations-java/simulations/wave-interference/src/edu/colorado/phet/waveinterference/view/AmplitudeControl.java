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
 * Time: 8:23:10 PM
 */

public class AmplitudeControl extends VerticalLayoutPanel {
    public AmplitudeControl( final Oscillator oscillator ) {

//        final ModelSlider amplitudeSlider = new ModelSlider( WIStrings.getString( "readout.amplitude" ), "cm", 0, 2, oscillator.getAmplitude() );
        final LinearValueControl amplitudeSlider = new LinearValueControl( 0, 2, WIStrings.getString( "readout.amplitude" ), "0.00", null, new ModelSliderLayoutStrategy() );
        amplitudeSlider.setValue( oscillator.getAmplitude() );
        amplitudeSlider.setBorder( null );
        amplitudeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                oscillator.setAmplitude( amplitudeSlider.getValue() );
            }
        } );
        oscillator.addListener( new Oscillator.Adapter() {
            public void amplitudeChanged() {
                amplitudeSlider.setValue( oscillator.getAmplitude() );
            }
        } );
        amplitudeSlider.setTextFieldVisible( false );
        amplitudeSlider.getSlider().setPaintLabels( false );
        amplitudeSlider.getSlider().setPaintTicks( false );
        add( amplitudeSlider );
    }
}
