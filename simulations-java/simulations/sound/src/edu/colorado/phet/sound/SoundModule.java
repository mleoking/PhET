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
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.sound.model.SineWaveFunction;
import edu.colorado.phet.sound.model.SoundListener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.view.RgbReporter;
import edu.colorado.phet.sound.view.WavefrontOscillator;

/**
 * Base module for the Sound simulations
 */
public abstract class SoundModule extends PhetGraphicsModule implements RgbReporter {

    //------------------------------------------------------------------------------
    // Class fields and methods
    //------------------------------------------------------------------------------
    private static WavefrontOscillator primaryOscillator = new WavefrontOscillator();
    private static WavefrontOscillator octaveOscillator = new WavefrontOscillator();
    private SoundListener speakerListener;
    protected final static Random randomGenerator = new Random();

    static {
        primaryOscillator.run();
        octaveOscillator.setHarmonicFactor( 2 );
        //        octaveOscillator.run();
    }

    //------------------------------------------------------------------------------
    // Instance fields and methods
    //------------------------------------------------------------------------------
    
    private boolean audioEnabled = false;
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private SoundListener currentListener;
    private Boolean saveAudioEnabledState;
    private final IClock clock;
    private final SoundModel model;

    /**
     * @param application
     * @param name
     */
    public SoundModule( String name ) {
        super( name, new ConstantDtClock( SoundConfig.s_waitTime, SoundConfig.s_timeStep ) );
        clock = getClock();
        model = new SoundModel();
        initModel();
        setModel( model );
        speakerListener = new SoundListener( model, new Point2D.Double() );
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
                if( isActive() ) {
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

    public SoundModel getSoundModel() {
        return model;
    }

    public void activate() {
        super.activate();
        if( !clock.isPaused() ) {
            setAudioEnabled( audioEnabled );
        }
        if( currentListener != null ) {
            setListener( currentListener );
        }
    }

    public void deactivate() {
        super.deactivate();
        setAudioEnabled( false );
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

    public void setListener( SoundListener listener ) {
        currentListener = listener;
        primaryOscillator.setListener( listener );
        octaveOscillator.setListener( listener );
    }

    public SoundListener getCurrentListener() {
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
