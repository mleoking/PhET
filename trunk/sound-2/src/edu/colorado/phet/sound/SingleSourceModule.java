/**
 * Class: SingleSourceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.sound.model.SineWaveFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.model.WavefrontOscillator;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;

public abstract class SingleSourceModule extends Module {

    protected SingleSourceModule( ApplicationModel appModel, String name ) {
        super( name );

        this.setModel( new SoundModel( appModel.getClock() ) );
        this.setApparatusPanel( new SingleSourceApparatusPanel( (SoundModel)getModel() ) );

        initApparatusPanel();
        initControlPanel();
    }

    private void initControlPanel() {
        this.setControlPanel( new SoundControlPanel( this ) );
    }

    private SoundModel getSoundModel() {
        return (SoundModel)getModel();
    }

    private void initApparatusPanel() {
        //        WaveMedium waveMedium = getSoundModel().getWaveMedium();
        //        new AddParticleCmd( waveMedium ).doItLater();

        // Set up the primary wavefront and graphic
        Wavefront primaryWavefront = new Wavefront( getSoundModel() );
        primaryWavefront.setWaveFunction( new SineWaveFunction( primaryWavefront ) );
        //        primaryWavefront.setPosition( 0, 0 );
        // todo: these lines should be collapsed into one call
        getSoundModel().setPrimaryWavefront( primaryWavefront );
        getSoundModel().addWaveFront( primaryWavefront );
        primaryWavefront.setEnabled( true );

        Wavefront octaveWavefront = new Wavefront( getSoundModel() );
        octaveWavefront.setWaveFunction( new SineWaveFunction( octaveWavefront ) );
        octaveWavefront.setMaxAmplitude( 0 );
        //        octaveWavefront.setPosition( 0, 0 );
        octaveWavefront.setEnabled( false );

        // todo: these lines should be collapsed into one call
        getSoundModel().setOctaveWavefront( octaveWavefront );
        getSoundModel().addWaveFront( octaveWavefront );
        getSoundModel().setOctaveWavefront( octaveWavefront );

        getSoundModel().setPrimaryOscillator( new WavefrontOscillator( primaryWavefront ) );
        getSoundModel().setOctaveOscillator( new WavefrontOscillator( octaveWavefront ) );

        getSoundModel().setAudioEnabled( false );
        getSoundModel().setFrequency( SoundConfig.s_defaultFrequency );

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
        //        PhetMainPanel mainPanel = getSoundApplication().getPhetMainPanel();
        //        if( mainPanel != null ) {
        //            SoundControlPanel soundControlPanel = (SoundControlPanel)mainPanel.getControlPanel();
        //            this.audioEnabledOnActivation = soundControlPanel.getAudioEnabled();
        //        }
    }
}
