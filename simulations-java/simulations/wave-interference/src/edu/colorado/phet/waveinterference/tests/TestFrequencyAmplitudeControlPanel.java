/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.OscillatorControlPanel;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:42:07 AM
 */

public class TestFrequencyAmplitudeControlPanel extends TestTopView {
    public TestFrequencyAmplitudeControlPanel() {
        super( "Freq & Amp" );
        OscillatorControlPanel oscillatorControlPanel = new OscillatorControlPanel( getOscillator() );
        getControlPanel().addControl( oscillatorControlPanel );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestFrequencyAmplitudeControlPanel() );
    }
}
