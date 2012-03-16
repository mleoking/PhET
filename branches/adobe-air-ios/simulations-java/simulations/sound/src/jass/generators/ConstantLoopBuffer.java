// Copyright 2002-2011, University of Colorado
package jass.generators;

import jass.engine.Out;

import java.net.URL;

/**
 * Loop through a buffer, loaded from an audio
 * file or provided by caller. No speed or volume control is provided.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ConstantLoopBuffer extends Out {
    /**
     * Buffer to loop
     */
    protected float[] loopBuffer;

    /**
     * Buffer length
     */
    protected int loopBufferLength;

    /**
     * Current integer position of pointer in buffer.
     */
    protected int ix = 0;

    /**
     * Sampling rate in Hertz of Out.
     */
    public float srate;

    /**
     * Construct loop force from named file.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param fn         Audio file name.
     */
    public ConstantLoopBuffer( float srate, int bufferSize, String fn ) {
        super( bufferSize ); // this is the internal buffer size
        AudioFileBuffer afBuffer = new AudioFileBuffer( fn );
        loopBuffer = afBuffer.buf;
        loopBufferLength = loopBuffer.length;
        this.srate = srate;
    }

    /**
     * Construct loop force from url.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param url        Audio file url.
     */
    public ConstantLoopBuffer( float srate, int bufferSize, URL url ) {
        super( bufferSize ); // this is the internal buffer size
        AudioFileBuffer afBuffer = new AudioFileBuffer( url );
        loopBuffer = afBuffer.buf;
        loopBufferLength = loopBuffer.length;
        this.srate = srate;
    }

    /**
     * Get loopBuffer
     *
     * @return loop buffer as float array
     */
    public float[] getLoopBuffer() {
        return loopBuffer;
    }

    /**
     * Get sampling rate
     *
     * @return sampling rate in Hertz
     */
    public float getSamplingRate() {
        return srate;
    }

    /**
     * Construct loop force and provide buffer
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param loopBuffer looping buffer.
     */
    public ConstantLoopBuffer( float srate, int bufferSize, float[] loopBuffer ) {
        super( bufferSize ); // this is the internal buffer size
        this.loopBuffer = loopBuffer;
        this.srate = srate;
        loopBufferLength = loopBuffer.length;
    }

    /**
     * Compute the next buffer.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        for( int k = 0; k < bufsz; k++ ) {
            buf[k] = loopBuffer[ix];
            ix++;
            if( ix == loopBufferLength ) {
                ix = 0;
            }
        }
    }

}


