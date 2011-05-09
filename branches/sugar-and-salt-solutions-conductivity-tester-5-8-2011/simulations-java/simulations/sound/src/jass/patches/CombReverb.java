// Copyright 2002-2011, University of Colorado
package jass.patches;

import jass.engine.BufferNotAvailableException;
import jass.engine.InOut;
import jass.engine.SinkIsFullException;
import jass.engine.Source;
import jass.generators.Mixer;

/**
 * CombReverb as Moorers reverb. See e.g.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 * @book{Steiglitz96, title        = {A Digital Signal Processing Primer with
 * Applications to Digital Audio and Computer Music},
 * author          =  {Ken Steiglitz},
 * publisher	= {Addison-Wesley},
 * address		= {New York},
 * year		= {1996},
 * pages = {290--295}}
 * <p/>
 * Must have delays greater than the bufferSize.
 * Defaults are for 25Khz sampling rate from Steiglitz book.
 * BUGS: Does not support removal of source.
 */
public class CombReverb extends InOut {

    protected float srate;
    protected int nCombs = 6;
    protected float[] combDelays = {.05f, .056f, .061f, .068f, .072f, .078f}; // delays in seonds
    protected float allpassDelay = .006f; // delay in seonds
    protected float a = .7f; // allpass parameter
    protected float[] R = {.4897f, .6142f, .5976f, .5893f, .581f, .5644f}; // feedback comb parameters
    protected float[] g = {.24f, .26f, .28f, .29f, .3f, .32f}; // low-pass comb parameters
    protected float dryToWet = .9f; // 1 is dry only
    protected LowPassComb[] lpCombs;
    protected AllPass allPass;
    protected Mixer mixer; // mixes combs
    protected Mixer endMixer; // mixes dry and wet

    /**
     * Create. For derived classes.
     *
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public CombReverb( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Create. For derived classes.
     *
     * @param bufferSize Buffer size used for real-time rendering.
     * @param srate      sampling rate in Hz
     * @param nCombs     number of comb filters
     */
    public CombReverb( int bufferSize, float srate, int nCombs ) {
        super( bufferSize );
        this.srate = srate;
        this.nCombs = nCombs;
        init();
    }

    /**
     * Init and allocate. Defaults are usable.
     */
    protected void init() {
        mixer = new Mixer( bufferSize, nCombs );
        endMixer = new Mixer( bufferSize, 2 );
        allPass = new AllPass( bufferSize, srate );
        lpCombs = new LowPassComb[nCombs];
        for( int i = 0; i < nCombs; i++ ) {
            lpCombs[i] = new LowPassComb( bufferSize, srate );
        }
        try {
            endMixer.addSource( allPass );
            allPass.addSource( mixer );
            for( int i = 0; i < nCombs; i++ ) {
                mixer.addSource( lpCombs[i] );
                mixer.setGain( i, 1f ); // constant, not settable
            }
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " " + e );
        }
        long t = getTime();
        endMixer.setTime( t );
        mixer.setTime( t );
        allPass.setTime( t );
        for( int i = 0; i < nCombs; i++ ) {
            lpCombs[i].setTime( t );
        }
        setAllParameters();
    }

    /**
     * Set all filter parameters
     */
    public void setAllParameters() {
        endMixer.setGain( 0, 1 - dryToWet );
        endMixer.setGain( 1, dryToWet );
        allPass.setA( a );
        allPass.setM( allpassDelay );
        for( int i = 0; i < nCombs; i++ ) {
            lpCombs[i].setL( combDelays[i] );
            lpCombs[i].setG( g[i] );
            lpCombs[i].setR( R[i] );
        }
    }

    /**
     * Set feedback of filter k
     *
     * @param r feedback
     * @param k index of comb filter
     */
    public void setR( float r, int k ) {
        R[k] = r;
        lpCombs[k].setR( R[k] );
    }

    /**
     * Set one pole lowpass coefficient g; H(z) = 1/(1-g/z))
     *
     * @param g lowpass filter coefficient
     * @param k index of comb filter
     */
    public void setG( float g, int k ) {
        this.g[k] = g;
        lpCombs[k].setG( this.g[k] );
    }

    /**
     * Set dryToWet ratio
     *
     * @param d dry to wet. 1 is dry only
     */
    public void setDryToWet( float d ) {
        dryToWet = d;
        endMixer.setGain( 0, 1 - dryToWet );
        endMixer.setGain( 1, dryToWet );
    }

    /**
     * Set allpass coeff. a: H(z) = (z^_{m} + a)/(1 + a*z^{-m})
     *
     * @param a allpass coeff
     */
    public void setA( float a ) {
        this.a = a;
        allPass.setA( a );
    }

    /**
     * Set allpass delay: H(z) = (z^_{m} + a)/(1 + a*z^{-m})
     * Make sure is not snaller than buffersize
     *
     * @param del allpass delay in seconds
     */
    public void setM( float del ) {
        allpassDelay = del;
        allPass.setM( del );
    }

    /**
     * Set comb delay. Make sure is not snaller than buffersize
     *
     * @param del allpass delay in seconds
     * @param k   index of comb filter
     */
    public void setL( float del, int k ) {
        combDelays[k] = del;
        lpCombs[k].setL( combDelays[k] );
    }

    /**
     * Add source to Sink. Override to allow one input only and add to mixer with
     * gain coefficient a and to delay 2.
     * This will be called after init() so mixer will already have 2 inputs
     *
     * @param s Source to add.
     * @return object representing Source in Sink (may be null).
     */
    public Object addSource( Source s ) throws SinkIsFullException {
        if( getSources().length > 0 ) {
            throw new SinkIsFullException();
        }
        else {
            endMixer.addSource( s );
            for( int i = 0; i < nCombs; i++ ) {
                lpCombs[i].addSource( s );
            }
            // add to the superclass. THis is for administrative reasons only,
            // the source cache is not used here.
            return super.addSource( s );
        }
    }

    /**
     * Compute the next buffer and store in member float[] buf.
     */
    protected void computeBuffer() {
        // Time has now been incremented and external Sources
        // have been called, the result of which is in the cache.
        // But we won't use this and just call getBuffer on the
        // contained subpatch. It will call getBuffer() on
        // atached Sources but they are already at this time so will
        // just return their cached buffer. We are wasting only an extra method call.
        try {
            buf = endMixer.getBuffer( getTime() );
        }
        catch( BufferNotAvailableException e ) {
            System.out.println( this + " " + e );
        }
    }

}
