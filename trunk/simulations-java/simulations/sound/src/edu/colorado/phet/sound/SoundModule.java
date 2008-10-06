/**
 * Class: SoundModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common_sound.application.Module;
import edu.colorado.phet.common_sound.application.PhetApplication;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SineWaveFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.view.RgbReporter;
import edu.colorado.phet.sound.view.WavefrontOscillator;

/**
 * Base module for the Sound simulations
 */
public class SoundModule extends Module implements RgbReporter {

    //------------------------------------------------------------------------------
    // Class fields and methods
    //------------------------------------------------------------------------------
    private static WavefrontOscillator primaryOscillator = new WavefrontOscillator();
    private static WavefrontOscillator octaveOscillator = new WavefrontOscillator();
    private Listener speakerListener;
    protected final static Random randomGenerator = new Random();

    static {
        primaryOscillator.run();
        octaveOscillator.setHarmonicFactor( 2 );
        //        octaveOscillator.run();
    }

    //------------------------------------------------------------------------------
    // Instance fielda and methods
    //------------------------------------------------------------------------------
    private boolean audioEnabled = false;
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private Listener currentListener;
    private Boolean saveAudioEnabledState;
    private boolean isActive;
    private IClock clock;

    /**
     * @param application
     * @param name
     */
    public SoundModule( SoundApplication application, String name ) {
//    public SoundModule( ApplicationModel appModel, String name ) {
        super( name );
        clock = application.getClock();
//        clock = appModel.getClock();
        this.setModel( new SoundModel( clock ) );
        initModel();
        speakerListener = new Listener( (SoundModel)getModel(),
                                        new Point2D.Double() );
        setListener( speakerListener );

        // Add a listener to the clock that will turn the audio off and on when the clock
        // is paused and unpaused
        clock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                stateChanged( clockEvent.getClock().isPaused() );
            }
            public void clockPaused( ClockEvent clockEvent ) {
                stateChanged( clockEvent.getClock().isPaused() );
            }
            private void stateChanged( boolean isPaused ) {
                if( isActive ) {
                    if( isPaused ) {
                        saveAudioEnabledState = new Boolean( audioEnabled );
                        primaryOscillator.setEnabled( false );
                        octaveOscillator.setEnabled( false );
                    }
                    else {
                        setAudioEnabled( audioEnabled );
                        if( saveAudioEnabledState != null ) {
                            setAudioEnabled( saveAudioEnabledState.booleanValue() );
                        }
                        saveAudioEnabledState = null;
                    }
                }
                else {
                    if( saveAudioEnabledState != null ) {
                        audioEnabled = saveAudioEnabledState.booleanValue();
                    }
                }
            }
        } );
    }

    protected SoundModel getSoundModel() {
        return (SoundModel)getModel();
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        if( !clock.isPaused() ) {
            setAudioEnabled( audioEnabled );
        }
        if( currentListener != null ) {
            setListener( currentListener );
        }
        isActive = true;
    }

    public void deactivate( PhetApplication phetApplication ) {
        super.deactivate( phetApplication );
        isActive = false;
    }

    public void setAudioEnabled( boolean enabled ) {
        if( clock.isPaused() ) {
            saveAudioEnabledState = new Boolean( enabled );
        }
        else {
            audioEnabled = enabled;
            primaryOscillator.setEnabled( enabled );
            octaveOscillator.setEnabled( enabled && getSoundModel().isOctaveEnabled() );
        }
    }

    private void initModel() {
        // Set up the primary wavefront
        primaryWavefront = new Wavefront();
        primaryWavefront.setWaveFunction( new SineWaveFunction( primaryWavefront ) );
        primaryWavefront.setEnabled( true );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( primaryWavefront );
        getSoundModel().setPrimaryWavefront( primaryWavefront );

        // Set up the octave wavefront
        octaveWavefront = new Wavefront();
        octaveWavefront.setWaveFunction( new SineWaveFunction( octaveWavefront ) );
        octaveWavefront.setMaxAmplitude( 0 );
        octaveWavefront.setEnabled( false );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( octaveWavefront );
        getSoundModel().setOctaveWavefront( octaveWavefront );

        getSoundModel().setFrequency( SoundConfig.s_defaultFrequency );
    }

    /**
     * Determines whether the source for audio is at the speaker or the listener
     *
     * @param source
     */
    public void setAudioSource( int source ) {
        // NOP  This should probably be an abstract method, or the classes refactored so this doesn't
        // need to be here at all.
    }

    public WavefrontOscillator getPrimaryOscillator() {
        return primaryOscillator;
    }

    public WavefrontOscillator getOctaveOscillator() {
        return octaveOscillator;
    }

    public void setListener( Listener listener ) {
        currentListener = listener;
        primaryOscillator.setListener( listener );
        octaveOscillator.setListener( listener );
    }

    public Listener getCurrentListener() {
        return currentListener;
    }

    public void setOscillatorDopplerFrequency( double dopplerFrequency ) {
        getPrimaryOscillator().setFrequency( (float)dopplerFrequency );
        getOctaveOscillator().setFrequency( (float)dopplerFrequency * 2 );
    }

    /**
     * This method is provided so subclass modules can get the background color at
     * a particular pixel. It was introduced so we could write the module that
     * evacuates air from a box. Default behavior is to return middle gray.
     *
     * @param x
     * @param y
     * @return
     */
    public int rgbAt( int x, int y ) {
        return 128;
    }

    public boolean getAudioEnabled() {
        return this.audioEnabled;
    }
}
