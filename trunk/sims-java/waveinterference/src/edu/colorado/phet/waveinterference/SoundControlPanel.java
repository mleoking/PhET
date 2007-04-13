/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.util.WIStrings;
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
        addControl( new DetectorSetControlPanel( WIStrings.getString( "pressure" ), soundModule.getIntensityReaderSet(), soundModule.getSoundSimulationPanel(), soundModule.getWaveModel(), soundModule.getLatticeScreenCoordinates(), soundModule.getClock() ) );

        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );
        addControl( new ResetModuleControl( soundModule ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new WaveRotateControl3D( soundModule.getWaveInterferenceModel(), soundModule.getRotationWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

//        addControl( new SoundWaveGraphicRadioControl( soundModule.getSoundWaveGraphic() ) );
//        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new ShowMarkersControl( soundModule.getSoundWaveGraphic(), soundModule.getSoundWaveGraphic().getPressureWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        multiOscillatorControlPanel = new MultiOscillatorControlPanel( soundModule.getMultiOscillator(), WIStrings.getString( "speaker" ), soundModule.getScreenUnits() );
        addControl( multiOscillatorControlPanel );
        addControl( new SoundAudioControlPanel( soundModule.getAudioSubsystem() ) );
        addVerticalSpace();

        slitControlPanel = new SlitControlPanel( soundModule.getSlitPotential(), soundModule.getScreenUnits() );
        addControl( slitControlPanel );


    }

    public void setAsymmetricFeaturesEnabled( boolean b ) {
        multiOscillatorControlPanel.setEnabled( b );
        slitControlPanel.setEnabled( b );
    }
}
