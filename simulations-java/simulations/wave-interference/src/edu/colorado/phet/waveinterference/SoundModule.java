/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.CompositePotential;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:07:30 PM
 */

public class SoundModule extends WaveInterferenceModule {
    private SoundSimulationPanel soundSimulationPanel;
    private WaveInterferenceModel waveInterferenceModel;
    private SoundControlPanel soundControlPanel;
    private SoundModuleAudio soundModuleAudio;

    public RotationWaveGraphic getRotationWaveGraphic() {
        return soundSimulationPanel.getRotationWaveGraphic();
    }

    public void setAsymmetricFeaturesEnabled( boolean asymmetricFeaturesEnabled ) {
        soundControlPanel.setAsymmetricFeaturesEnabled( asymmetricFeaturesEnabled );
    }

    public WaveInterferenceScreenUnits getScreenUnits() {
        return soundSimulationPanel.getScreenUnits();
    }

    public CompositePotential getWallPotentials() {
        return waveInterferenceModel.getWallPotentials();
    }

    public static class SoundModel extends WaveInterferenceModel {
        public SoundModel() {
            setDistanceUnits( "cm" );
            setPhysicalSize( 100, 100 );
            setTimeUnits( "microsec" );
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
        super( WIStrings.getString( "module.sound" ) );
        waveInterferenceModel = new SoundModel();
        soundModuleAudio = new SoundModuleAudio( waveInterferenceModel );
        soundSimulationPanel = new SoundSimulationPanel( this );
        soundControlPanel = new SoundControlPanel( this );

        addModelElement( waveInterferenceModel );
        addModelElement( soundSimulationPanel );

        setSimulationPanel( soundSimulationPanel );
        setControlPanel( soundControlPanel );

        waveInterferenceModel.setInitialConditions();
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

    public void resetAll() {
        super.resetAll();
        getWaveInterferenceModel().reset();
        soundSimulationPanel.reset();
        soundModuleAudio.reset();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new SoundModule() );
    }
}
