/**
 * Class: SoundModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.sound.model.*;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;

import java.awt.geom.Point2D;

public class SoundModule extends Module {
    protected boolean audioEnabled = false;
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private WavefrontOscillator primaryOscillator;
    private WavefrontOscillator octaveOscillator;
//    private Listener speakerListener = new Listener();
//    private Listener headListener = new Listener();
    private Listener currentListener;

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
        primaryWavefront = new Wavefront( getSoundModel() );
        primaryWavefront.setWaveFunction( new SineWaveFunction( primaryWavefront ) );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( primaryWavefront );
        getSoundModel().setPrimaryWavefront( primaryWavefront );
        primaryWavefront.setEnabled( true );
        primaryOscillator = new WavefrontOscillator( primaryWavefront, getModel() );
        getSoundModel().setPrimaryOscillator( primaryOscillator );

        // Set up the octave wavefront
        octaveWavefront = new Wavefront( getSoundModel() );
        octaveWavefront.setWaveFunction( new SineWaveFunction( octaveWavefront ) );
        octaveWavefront.setMaxAmplitude( 0 );
        octaveWavefront.setEnabled( false );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( octaveWavefront );
        getSoundModel().setOctaveWavefront( octaveWavefront );
        octaveOscillator = new WavefrontOscillator( octaveWavefront, getModel() );
        getSoundModel().setOctaveOscillator( octaveOscillator );

        setAudioEnabled( audioEnabled );
        getSoundModel().setFrequency( SoundConfig.s_defaultFrequency );
    }

    /**
     * Determines whether the source for audio is at the speaker or the listener
     * @param source
     */
    public void setAudioSource( int source ) {
        getSoundModel().setAudioSource( source );
    }

    public WavefrontOscillator getPrimaryOscillator() {
        return primaryOscillator;
    }

    public WavefrontOscillator getOctaveOscillator() {
        return octaveOscillator;
    }

    public void setListener( Listener listener ) {
        currentListener = listener;
        getPrimaryOscillator().observe( listener );
        getOctaveOscillator().observe( listener );
    }

}
