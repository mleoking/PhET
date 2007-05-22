/*  */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 8:23:10 PM
 *
 */

public class AmplitudeControl extends VerticalLayoutPanel {
    public AmplitudeControl( final Oscillator oscillator ) {

        final ModelSlider amplitudeSlider = new ModelSlider( WIStrings.getString( "readout.amplitude" ), "cm", 0, 2, oscillator.getAmplitude() );
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
        amplitudeSlider.setPaintLabels( false );
        amplitudeSlider.setPaintTicks( false );
        add( amplitudeSlider );
    }
}
