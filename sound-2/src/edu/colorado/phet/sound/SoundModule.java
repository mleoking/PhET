/**
 * Class: SoundModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.model.SineWaveFunction;
import edu.colorado.phet.sound.model.WavefrontOscillator;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;

public class SoundModule extends Module {
    protected boolean audioEnabled = false;

    public SoundModule( String name ) {
        super( name );
    }

    protected SoundModel getSoundModel() {
        return (SoundModel)getModel();
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        //        PhetMainPanel mainPanel = getSoundApplication().getPhetMainPanel();
        //        if( mainPanel != null ) {
        //            SoundControlPanel soundControlPanel = (SoundControlPanel)mainPanel.getControlPanel();
        //            soundControlPanel.setAudioControlVisible( true );
        //            soundControlPanel.setAudioEnabled( this.audioEnabledOnActivation );
        //        }
        //        this.setWavefrontType( getSoundApplication().getSoundSystem().getWavefrontType() );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        getSoundModel().setAudioEnabled( false );
        //        PhetMainPanel mainPanel = getSoundApplication().getPhetMainPanel();
        //        if( mainPanel != null ) {
        //            SoundControlPanel soundControlPanel = (SoundControlPanel)mainPanel.getControlPanel();
        //            this.audioEnabledOnActivation = soundControlPanel.getAudioEnabled();
        //        }
    }

    public void setAudioEnabled( boolean enabled ) {
        getSoundModel().setAudioEnabled( enabled );
    }

    protected void initModel() {

        // Set up the primary wavefront
        Wavefront primaryWavefront = new Wavefront( getSoundModel() );
        primaryWavefront.setWaveFunction( new SineWaveFunction( primaryWavefront ) );
//        primaryWavefront.setConePosition( 0, 0 );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( primaryWavefront );
        getSoundModel().setPrimaryWavefront( primaryWavefront );
        primaryWavefront.setEnabled( true );
        getSoundModel().setPrimaryOscillator( new WavefrontOscillator( primaryWavefront ) );

        Wavefront octaveWavefront = new Wavefront( getSoundModel() );
        octaveWavefront.setWaveFunction( new SineWaveFunction( octaveWavefront ) );
        octaveWavefront.setMaxAmplitude( 0 );
        //        octaveWavefront.setConePosition( 0, 0 );
        octaveWavefront.setEnabled( false );

        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( octaveWavefront );
        getSoundModel().setOctaveWavefront( octaveWavefront );

        getSoundModel().setOctaveOscillator( new WavefrontOscillator( octaveWavefront ) );

        setAudioEnabled( audioEnabled );
        setAudioSource( SingleSourceApparatusPanel.SPEAKER_SOURCE );
        getSoundModel().setFrequency( SoundConfig.s_defaultFrequency );
    }

    /**
     * Determines whether the source for audio is at the speaker or the listener
     * @param source
     */
    public void setAudioSource( int source ) {
        getSoundModel().setAudioSource( source );
    }
}
