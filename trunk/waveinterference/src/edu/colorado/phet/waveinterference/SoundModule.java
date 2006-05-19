/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.ModuleApplication;
import edu.colorado.phet.waveinterference.view.IntensityReaderSet;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.MeasurementToolSet;
import edu.colorado.phet.waveinterference.view.MultiOscillator;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:07:30 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class SoundModule extends WaveInterferenceModule {
    private SoundSimulationPanel soundSimulationPanel;
    private WaveInterferenceModel waveInterferenceModel;
    private SoundControlPanel soundControlPanel;
    private SoundModuleAudio soundModuleAudio;

    public static class SoundModel extends WaveInterferenceModel {
        public SoundModel() {
            setDistanceUnits( "cm" );
            setPhysicalSize( 100, 100 );
        }
    }

    public void activate() {
        super.activate();
        soundModuleAudio.setActive( true );
    }

    public void deactivate() {
        super.deactivate();
        soundModuleAudio.setActive( false );
    }

    public SoundModule() {
        super( "Sound" );
        waveInterferenceModel = new SoundModel();
        soundModuleAudio = new SoundModuleAudio( waveInterferenceModel );
        soundSimulationPanel = new SoundSimulationPanel( this );
        soundControlPanel = new SoundControlPanel( this );

        addModelElement( waveInterferenceModel );
        addModelElement( soundSimulationPanel );

        setSimulationPanel( soundSimulationPanel );
        setControlPanel( soundControlPanel );
    }

    public WaveModel getWaveModel() {
        return waveInterferenceModel.getWaveModel();
    }

    public SlitPotential getSlitPotential() {
        return waveInterferenceModel.getSlitPotential();
    }

    public Oscillator getSecondaryOscillator() {
        return waveInterferenceModel.getSecondaryOscillator();
    }

    public Oscillator getPrimaryOscillator() {
        return waveInterferenceModel.getPrimaryOscillator();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return soundSimulationPanel.getIntensityReaderSet();
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return soundSimulationPanel.getMeasurementToolSet();
    }

    public SoundSimulationPanel getSoundSimulationPanel() {
        return soundSimulationPanel;
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return soundSimulationPanel.getLatticeScreenCoordinates();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new SoundModule() );
    }

    public SoundWaveGraphic getSoundWaveGraphic() {
        return soundSimulationPanel.getSoundWaveGraphic();
    }

    public MultiOscillator getMultiOscillator() {
        return soundSimulationPanel.getMultiOscillator();
    }

    public SoundModuleAudio getAudioSubsystem() {
        return soundModuleAudio;
    }

    public WaveInterferenceModel getWaveInterferenceModel() {
        return waveInterferenceModel;
    }
}
