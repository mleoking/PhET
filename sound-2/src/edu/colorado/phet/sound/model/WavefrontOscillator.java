/**
 * Class: WavefrontOscillator
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.sound.SoundConfig;
import javasound.SrrOscillatorPlayer;

import java.awt.geom.Point2D;

public class WavefrontOscillator extends SrrOscillatorPlayer implements SimpleObserver {

    private boolean isEnabled = false;
    private double amplitudeInternal;
    private Wavefront wavefront;
    private Listener listener;

    // The point in the wavefront that the oscillator is
    // to generate sound for
    private Point2D.Double refPt = new Point2D.Double();

    // This is a special overide flag so that the two source interference panel works.
    private boolean interferenceOverideEnabled = false;

    /**
     *
     */
    public WavefrontOscillator( Wavefront wavefront, BaseModel model ) {
        this.wavefront = wavefront;
        wavefront.addObserver( this );
        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                WavefrontOscillator.this.update();
            }
        } );
        this.start();
    }

    public void clockTicked( AbstractClock c, double dt ) {
        update();
    }

    /**
     *
     */
    public void setAmplitude( double amplitude ) {
        if( amplitude < 0 ) {
            throw new RuntimeException( "amplitude < 0" );
        }
        amplitude = amplitude / SoundConfig.s_maxAmplitude;
        super.setAmplitude( isEnabled ? (float)amplitude : 0 );
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

        if( listener != null ) {
            refPt = listener.getLocation();
        }
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
        if( isEnabled && amplitude != getAmplitude() && !interferenceOverideEnabled ) {
            setAmplitude( (float)amplitude );
        }
    }

    public void observe( Listener listener ) {
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
