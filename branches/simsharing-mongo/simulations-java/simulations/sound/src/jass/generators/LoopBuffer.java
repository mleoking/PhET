// Copyright 2002-2011, University of Colorado
package jass.generators;

import jass.engine.Out;

import java.net.URL;

/**
 * A force model based on looping through a buffer, loaded from an audio
 * file/url or provided by caller.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class LoopBuffer extends Out {
    /**
     * Buffer to loop
     */
    protected float[] loopBuffer;

    /**
     * Buffer length
     */
    protected int loopBufferLength;

    /**
     * Playback volume.
     */
    protected float volume = 1;

    /**
     * Loop speed through buffer in seconds per second.
     */
    protected float speed = 1;

    /**
     * Current fractional position [0 1] of pointer in buffer.
     */
    protected float x = 0;

    /**
     * Current integer position of pointer in buffer.
     */
    protected int ix = 0;

    /**
     * Current  fractional speed [0  1]  of pointer  in  buffer  per sample.
     */
    protected float dx;

    /**
     * Current  integer speed of pointer  in  buffer  per sample.
     */
    protected int dix;

    /**
     * Sampling rate in Hertz of Out.
     */
    public float srate;

    /**
     * Sampling rate ratio, srateLoopBuffer/srate
     */
    private float srateRatio = 1;

    /**
     * Sampling rate in Hertz of loaded buffer.
     */
    public float srateLoopBuffer;

    private String name = "x";

    /**
     * For derived classes
     *
     * @param bufferSize biffer size
     */
    public LoopBuffer( int bufferSize ) {
        super( bufferSize ); // this is the internal buffer size
    }

    /**
     * Construct loop force from named file.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param fn         Audio file name.
     */
    public LoopBuffer( float srate, int bufferSize, String fn ) {
        super( bufferSize ); // this is the internal buffer size
        AudioFileBuffer afBuffer = new AudioFileBuffer( fn );
        loopBuffer = afBuffer.buf;
        loopBufferLength = loopBuffer.length;
        srateLoopBuffer = afBuffer.srate;
        this.srate = srate;
        srateRatio = srateLoopBuffer / srate;
        setSpeed( speed );
        this.name = fn;
        /*
          System.out.println(name);
          for(int i=0;i<loopBuffer.length;i++) {
          System.out.println(loopBuffer[i]);
          }
        */
    }


    /**
     * Construct loop force from named URL.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param url        Audio file url name.
     */
    public LoopBuffer( float srate, int bufferSize, URL url ) {
        super( bufferSize ); // this is the internal buffer size
        AudioFileBuffer afBuffer = new AudioFileBuffer( url );
        loopBuffer = afBuffer.buf;
        loopBufferLength = loopBuffer.length;
        srateLoopBuffer = afBuffer.srate;
        this.srate = srate;
        srateRatio = srateLoopBuffer / srate;
        setSpeed( speed );
        if( url == null ) {
            this.name = "No URL";
        }
        else {
            this.name = url.toString();
        }
    }

    /**
     * Construct loop force and provide buffer at same sampling rate.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param loopBuffer looping buffer.
     */
    public LoopBuffer( float srate, int bufferSize, float[] loopBuffer ) {
        super( bufferSize ); // this is the internal buffer size
        this.loopBuffer = loopBuffer;
        srateLoopBuffer = this.srate = srate;
        loopBufferLength = loopBuffer.length;
        setSpeed( speed );
    }

    /**
     * Get the loopbuffer as array.
     *
     * @return The containing loopbuffer
     */
    public float[] getLoopBuffer() {
        return loopBuffer;
    }

    /**
     * Set force magnitude.
     *
     * @param val Volume.
     */
    public void setVolume( float val ) {
        //System.out.println(name+" volume= "+val);
        volume = val;
    }

    /**
     * Set loopspeed.
     *
     * @param speed Loop speed., 1 corresponding to original recorded speed.
     */
    public void setSpeed( float speed ) {
        this.speed = speed;
        float tmp = speed * srateRatio;
        dix = (int)tmp;
        dx = tmp - dix;
    }

    /**
     * Get next sample value, interpolating in between sample points.
     */
    protected float getNextSample() {
        ix += dix;
        x += dx;
        if( x > 1.f ) {
            x -= 1.f;
            ix++;
        }
        while( ix >= loopBufferLength ) {
            ix -= loopBufferLength;
        }
        int next_index;
        if( ix == loopBufferLength - 1 ) {
            next_index = 0;
        }
        else {
            next_index = ix + 1;
        }
        float val = ( 1.f - dx ) * loopBuffer[ix] + dx * loopBuffer[next_index];
        return (float)( val * volume );
    }

    /**
     * Compute the next buffer.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        for( int k = 0; k < bufsz; k++ ) {
            buf[k] = getNextSample();
        }
    }

}


