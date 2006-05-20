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
    private MultiOscillatorControlPanel multiOscillatorControlPanel;
    private SlitControlPanel slitControlPanel;

    public SoundControlPanel( SoundModule soundModule ) {
        this.soundModule = soundModule;
//        addControl( new ParticleSizeSliderControl( soundModule.getSoundWaveGraphic() ) );
        addControl( new MeasurementControlPanel( soundModule.getMeasurementToolSet() ) );
        addControl( new DetectorSetControlPanel( "Pressure", soundModule.getIntensityReaderSet(), soundModule.getSoundSimulationPanel(), soundModule.getWaveModel(), soundModule.getLatticeScreenCoordinates(), soundModule.getClock() ) );
        addControl( new VerticalSeparator() );
        addControl( new ClearWaveControl( this.soundModule.getWaveModel() ) );

        addControlFullWidth( new VerticalSeparator() );
        addControl( new WaveRotateControl3D( soundModule.getWaveInterferenceModel(), soundModule.getRotationWaveGraphic() ) );

        addControlFullWidth( new VerticalSeparator() );
        addControl( new SoundWaveGraphicRadioControl( soundModule.getSoundWaveGraphic() ) );

        addControlFullWidth( new VerticalSeparator() );
        slitControlPanel = new SlitControlPanel( soundModule.getSlitPotential() );
        addControl( slitControlPanel );
        addControlFullWidth( new VerticalSeparator() );

        multiOscillatorControlPanel = new MultiOscillatorControlPanel( soundModule.getMultiOscillator(), "Speaker" );
        addControl( multiOscillatorControlPanel );
        addControl( new SoundAudioControlPanel( soundModule.getAudioSubsystem() ) );
    }

    public void setAsymmetricFeaturesEnabled( boolean b ) {
        multiOscillatorControlPanel.setEnabled( b );
        slitControlPanel.setEnabled( b );
    }
}
