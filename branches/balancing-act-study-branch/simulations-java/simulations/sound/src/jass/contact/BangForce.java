// Copyright 2002-2011, University of Colorado
package jass.contact;

import jass.generators.OneShotBuffer;

/**
 * A force model based on 1 period of some waveform (cos for example)
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class BangForce extends OneShotBuffer {
    /**
     * Duration in seconds of impact in buffer.
     */
    private float durBang;

    /**
     * Construct impact force from named file. (For example cos20ms.wav.)
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param fn         Audio file name. (For example cos20ms.wav.)
     */
    public BangForce( float srate, int bufferSize, String fn ) {
        super( srate, bufferSize, fn );
        durBang = loopBufferLength / srateLoopBuffer;
    }

    /**
     * Construct loop force and provide buffer.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param loopBuffer looping buffer.
     */
    public BangForce( float srate, int bufferSize, float[] loopBuffer ) {
        super( srate, bufferSize, loopBuffer );
    }

    /**
     * Generate impact force.
     *
     * @param force magnitude.
     * @param dur   duration in seconds of impact.
     */
    public void bang( float force, float dur ) {
        if( dur < 2 / srate ) {
            dur = 2 / srate;
        }
        setVolume( force );
        setSpeed( durBang / dur );
        hit();
    }

}


