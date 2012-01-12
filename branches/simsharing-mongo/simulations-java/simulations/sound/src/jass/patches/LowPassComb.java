// Copyright 2002-2011, University of Colorado
package jass.patches;

import jass.engine.BufferNotAvailableException;
import jass.engine.InOut;
import jass.engine.SinkIsFullException;
import jass.engine.Source;
import jass.generators.Delay;
import jass.generators.Mixer;
import jass.generators.OnePoleLowPass;

/**
 * Delay line with a one pole low-pass. Used in Moorers reverb. See e.g.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 * @book{Steiglitz96, title        = {A Digital Signal Processing Primer with
 * Applications to Digital Audio and Computer Music},
 * author          =  {Ken Steiglitz},
 * publisher	= {Addison-Wesley},
 * address		= {New York},
 * year		= {1996},
 * pages = {290--295}}
 * BUGS: Does not support removal of source.
 */
public class LowPassComb extends InOut {

    protected float srate;
    protected float R = 0; // feedback parameter
    protected float g = 0; // low-pass parameter
    protected float del = 0; // delay in seconds
    protected Mixer mixer;
    protected Delay delay;
    protected OnePoleLowPass lowpass;

    /**
     * Create. For derived classes.
     *
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public LowPassComb( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Create. For derived classes.
     *
     * @param bufferSize Buffer size used for real-time rendering.
     * @param srate      sampling rate in Hz
     */
    public LowPassComb( int bufferSize, float srate ) {
        super( bufferSize );
        this.srate = srate;
        init();
    }

    /**
     * Init and allocate.
     */
    protected void init() {
        mixer = new Mixer( bufferSize, 2 );
        delay = new Delay( bufferSize, srate );
        lowpass = new OnePoleLowPass( bufferSize );
        try {
            mixer.addSource( lowpass );
            lowpass.addSource( delay );
            delay.addSource( mixer );
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " " + e );
        }
        long t = getTime();
        mixer.setTime( t );
        delay.setTime( t );
        lowpass.setTime( t );
    }

    /**
     * Add source to Sink. Override to allow one input only and add to mixer
     *
     * @param s Source to add.
     * @return object representing Source in Sink (may be null).
     */
    public Object addSource( Source s ) throws SinkIsFullException {
        if( getSources().length > 0 ) {
            throw new SinkIsFullException();
        }
        else {
            mixer.addSource( s );
            mixer.setGain( 1, 1f );
            // add to Vector so that other  stuff like getSources will work
            // maybe not necessary??
            return super.addSource( s );
        }
    }

    /**
     * Set delay del. (=L/srate)
     *
     * @param del delay in seconds
     */
    public void setL( float del ) throws IllegalArgumentException {
        this.del = del;
        delay.setRecursiveDelay( del );
    }

    /**
     * Set feedback R.
     *
     * @param R feedback
     */
    public void setR( float R ) {
        this.R = R;
        mixer.setGain( 0, R ); // 0 is the feedback input to the mixer
    }

    /**
     * Set low-pass parameter g.
     *
     * @param g low-pass parameter g.
     */
    public void setG( float g ) {
        this.g = g;
        lowpass.setG( g );
    }

    /**
     * Compute the next buffer and store in member float[] buf.
     */
    protected void computeBuffer() {
        // Time has now been incremented and external Sources
        // have been called, but there are none since this has been
        // routed through the subpatch
        // Call getBuffer on the
        // contained subpatch. It will call getBuffer() on
        // atached Sources, but they are already at this time so will
        // just return their cached buffer. We are wasting only an extra method call.
        try {
            buf = mixer.getBuffer( getTime() );
        }
        catch( BufferNotAvailableException e ) {
            System.out.println( this + " " + e );
        }
    }

}
