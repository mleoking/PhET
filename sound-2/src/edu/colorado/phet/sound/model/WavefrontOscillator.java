/**
 * Class: WavefrontOscillator
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.model;

import javasound.SrrOscillatorPlayer;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.sound.SoundConfig;

public class WavefrontOscillator extends SrrOscillatorPlayer implements SimpleObserver {

    private boolean isEnabled = false;
    private double amplitudeInternal;
    private Wavefront wavefront;

    // The point in the wavefront that the oscillator is
    // to generate sound for
    private Point2D.Float refPt = new Point2D.Float();

    /**
     *
     */
    public WavefrontOscillator( Wavefront wavefront ) {
        this.wavefront = wavefront;
        wavefront.addObserver( this );
        this.start();
    }

    /**
     *
     */
    public void setAmplitude( float amplitude ) {
        if( amplitude < 0 ) {
            throw new RuntimeException( "amplitude < 0" );
        }
        amplitude = amplitude / SoundConfig.s_maxAmplitude;
        super.setAmplitude( isEnabled ? amplitude : 0 );
        amplitudeInternal = amplitude;
    }

    /**
     *
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        isEnabled = enabled;
        if( isEnabled ) {
            super.setAmplitude( (float)amplitudeInternal );
        }
        else {
            super.setAmplitude( 0 );
        }
        update();
    }

    /**
     *
     */
    public void setReferencePoint( float x, float y ) {
        refPt.setLocation( x, y );
        this.update();
        wavefront.setListenerLocation( (int)refPt.getX() );
    }

    /**
     *
     */
    public void setReferencePoint( Point2D.Double location ) {
        setReferencePoint( (float)location.getX(), (float)location.getY() );
    }

    /**
     *
     */
    public void update() {

        double distFromSource = refPt.distance( 0, 0 );
        double frequency = wavefront.getFrequencyAtTime( (int)distFromSource );
        double amplitude = wavefront.getMaxAmplitudeAtTime( (int)distFromSource );

        if( amplitude < -1 ) {
            throw new RuntimeException( "amplitude < -1" );
        }

        // Remember, we never set the frequency to 0, because otherwise it chokes. We
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
//        amplitudeInternal = amplitude;
//        if( isEnabled && amplitude != getAmplitude() ) {
            setAmplitude( (float)amplitude );
//        }
    }
}
