/**
 * Class: WavefrontOscillator
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.geom.Point2D;

import javasound.SrrOscillatorPlayer;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common_sound.model.clock.AbstractClock;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.Listener;

public class WavefrontOscillator extends SrrOscillatorPlayer implements SimpleObserver {

    private boolean isEnabled = false;
    private double amplitude;
    // The model element that we are providing a view of
    private Listener listener;
    // Whatever multiplication factor is applied frequency the listener in the model reports
    // to us
    private double harmonicFactor = 1;

    // The point in the wavefront that the oscillator is
    // to generate sound for
    private Point2D.Double refPt = new Point2D.Double();

    // This is a special overide flag so that the two source interference panel works.
    private boolean interferenceOverideEnabled = false;


    public void run() {
        super.run();
    }

    public void clockTicked( AbstractClock c, double dt ) {
        update();
    }

    public void setHarmonicFactor( double harmonicFactor ) {
        this.harmonicFactor = harmonicFactor;
    }

    public void setAmplitude( double amplitude ) {
        if( amplitude < 0 ) {
            throw new RuntimeException( "amplitude < 0" );
        }
        amplitude = amplitude / SoundConfig.s_maxAmplitude;
        super.setAmplitude( isEnabled ? (float)amplitude : 0 );
        this.amplitude = amplitude;
    }

    public void setEnabled( boolean enabled ) {
        isEnabled = enabled;

        // Note: If we don't do this messing around with the amplitude, the
        // audio is not right when we toggle from enabled to disabled and back
        // to enabled.
        if( isEnabled ) {
            super.setAmplitude( (float)amplitude );
        }
        else {
            super.setAmplitude( 0 );
        }
        update();
    }

    public void setReferencePoint( float x, float y ) {
        refPt.setLocation( x, y );
        this.update();
    }

    public void setReferencePoint( Point2D.Double location ) {
        setReferencePoint( (float)location.getX(), (float)location.getY() );
    }

    public void update() {

        if( listener != null ) {
            refPt = listener.getLocation();
        }
        double frequency = listener.getFrequencyHeard() * harmonicFactor;
        double amplitude = listener.getAmplitudeHeard();
        if( amplitude < -1 ) {
            throw new RuntimeException( "amplitude < -1" );
        }

        // We never set the frequency to 0, because otherwise the oscillator chokes. We
        // need to make this assignment so that the following if() will test false when
        // frequency == 0.
        // Note that that frequencyDisplayFactor must be used here, because the model uses
        // a value for frequency that corresponds to what will appear on the screen. It would
        // be better if the frequency in the model were accurate for the pitch of the sound, but
        // I haven't figured out how to make that work yet.
        frequency = frequency == 0 ? 0.1f : frequency * SoundConfig.s_frequencyDisplayFactor;
        if( frequency != getFrequency() ) {
            setFrequency( (float)frequency );
        }
        setAmplitude( amplitude );
    }

    public void setListener( Listener listener ) {
        if( this.listener != null ) {
            this.listener.removeObserver( this );
        }
        this.listener = listener;
        listener.addObserver( this );
        update();
    }

    public boolean isInterferenceOverideEnabled() {
        return interferenceOverideEnabled;
    }

    public void setInterferenceOverideEnabled( boolean interferenceOverideEnabled ) {
        this.interferenceOverideEnabled = interferenceOverideEnabled;
        update();
    }

    public boolean getInterferenceOverideEnabled() {
        return this.interferenceOverideEnabled;
    }
}
