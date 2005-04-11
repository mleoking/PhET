/**
 * Class: WavefrontOscillator
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.common.util.SimpleObserver;

import java.util.Observer;
import java.util.Observable;
import java.awt.geom.Point2D;

import javasound.SrrOscillatorPlayer;

public class WavefrontOscillator extends /*MyOscillatorPlayer*/ SrrOscillatorPlayer implements SimpleObserver {

    private boolean isEnabled = false;
    private float amplitudeInternal;
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
            System.out.println( "$$$" );
        }

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
            super.setAmplitude( amplitudeInternal );
        }
        else {
            super.setAmplitude( 0 );
        }
    }

    /**
     *
     */
    public void setReferencePoint( float x, float y ) {
        refPt.setLocation( x, y );
        this.update();
//        this.update( this.wavefront, null );
        wavefront.setListenerLocation( (int)refPt.getX() );
    }

    /**
     *
     */
    public void setReferencePoint( Point2D.Float location ) {
        setReferencePoint( (float)location.getX(), (float)location.getY() );
    }

    /**
     *
     */
    public void update() {
//    public void update( Observable o, Object arg ) {

//        Wavefront wavefront = (Wavefront)o;
//        float distFromSource = refPt.distance( wavefront.getPosition().getX(),
//                                                wavefront.getPosition().getY() );
        double distFromSource = refPt.distance( 0, 0 );

        double frequency = wavefront.getFrequencyAtTime( (int)distFromSource );
        double amplitude = wavefront.getMaxAmplitudeAtTime( (int)distFromSource );

        if( amplitude < -1 ) {
            System.out.println( "###" );
        }
//        float frequency = wavefront.getFrequencyAtTime( (int)refPt.getX() );
//        float amplitude = wavefront.getMaxAmplitudeAtTime( (int)refPt.getX() );

        // Remember, we never set the frequency to 0, because otherwise it chokes. We
        // need to make this assignment so that the following if() will test false when
        // frequency == 0.
        frequency = frequency == 0 ? 0.1f : frequency;
        if( frequency * 10 != getFrequency() ) {
            setFrequency( (float)frequency * 10 );
        }
        if( isEnabled && amplitude != getAmplitude() ) {
            setAmplitude( (float)amplitude );
        }
    }

    /**
     *
     */
//    public void update() {
//        update( this.wavefront, null );
//    }
}
