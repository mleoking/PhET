/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.DetectorSetControlPanel;
import edu.colorado.phet.waveinterference.view.MeasurementControlPanel;
import edu.colorado.phet.waveinterference.view.MultiOscillatorControlPanel;
import edu.colorado.phet.waveinterference.view.SlitControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:15:03 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class SoundControlPanel extends WaveInterferenceControlPanel {
    private SoundModule soundModule;

    public SoundControlPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;
//        addControl( new ParticleSizeSliderControl( soundModule.getSoundWaveGraphic() ) );
        addControl( new MeasurementControlPanel( soundModule.getMeasurementToolSet() ) );
        addControl( new DetectorSetControlPanel( "Pressure", soundModule.getIntensityReaderSet(), soundModule.getSoundSimulationPanel(), soundModule.getWaveModel(), soundModule.getLatticeScreenCoordinates(), soundModule.getClock() ) );
        addControl( new VerticalSeparator() );
        addControl( new ClearWaveControl( this.soundModule.getWaveModel() ) );
        addControlFullWidth( new VerticalSeparator() );

//        addControl( new WaveRotateControl( this.soundModule.getRotationWaveGraphic() ) );
//        addControlFullWidth( new VerticalSeparator() );

        addControl( new SoundWaveGraphicRadioControl( soundModule.getSoundWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator() );
        addControl( new SlitControlPanel( soundModule.getSlitPotential() ) );
        addControlFullWidth( new VerticalSeparator() );

        addControl( new MultiOscillatorControlPanel( soundModule.getMultiOscillator(), "Speaker" ) );
//        addControl( new JSeparator( ));
        addControl( new SoundAudioControlPanel( soundModule.getAudioSubsystem() ) );
    }
}
