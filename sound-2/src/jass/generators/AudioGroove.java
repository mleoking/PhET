package jass.generators;

import jass.engine.Out;

import java.net.URL;

/**
 Position  based playback  of audio  date (gramophone  model). Wavfile
 (mono) is indexed  with position of needle on  record.  Every call to
 getBuffer this UG  polls for position of needle  in seconds, and uses
 this with the previous saved value to index a wav file and compute an
 audio buffer for the corresponding segment. Method to obtain position
 of needle is abstract.
 Loads entire clip in memory.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public abstract class AudioGroove extends Out {

    /** Buffer with music (groove) */
    protected float[] grooveBuffer;

    /** Buffer length */
    protected int grooveBufferLength;

    /** Sampling rate in Hertz of Out. */
    public float srate;

    /** Sampling rate ratio, srateLoopBuffer/srate */
    protected float srateRatio = 1;

    /** Sampling rate in Hertz of loaded buffer. */
    public float srateGrooveBuffer;

    /** Name of buffer */
    protected String name = "x";

    /** Current needle position in seconds  */
    protected double posNeedle = 0;

    /** Past needle position in seconds */
    protected double posNeedlePast = 0;

    /**
     For derived classes
     @param bufferSize buffer size
     */
    public AudioGroove(int bufferSize) {
        super(bufferSize); // this is the internal buffer size
    }

    /** Construct Groove from named file.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out
     @param fn Audio file name.
     */
    public AudioGroove(float srate, int bufferSize, String fn) {
        super(bufferSize); // this is the internal buffer size
        AudioFileBuffer afBuffer = new AudioFileBuffer(fn);
        grooveBuffer = afBuffer.buf;
        grooveBufferLength = grooveBuffer.length;
        srateGrooveBuffer = afBuffer.srate;
        this.srate = srate;
        srateRatio = srateGrooveBuffer / srate;
        this.name = fn;
    }

    /** Construct Groove from named URL.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out
     @param url Audio file url name.
     */
    public AudioGroove(float srate, int bufferSize, URL url) {
        super(bufferSize); // this is the internal buffer size
        AudioFileBuffer afBuffer = new AudioFileBuffer(url);
        grooveBuffer = afBuffer.buf;
        grooveBufferLength = grooveBuffer.length;
        srateGrooveBuffer = afBuffer.srate;
        this.srate = srate;
        srateRatio = srateGrooveBuffer / srate;
        this.name = url.toString();
    }

    /** Construct Groove and provide buffer at same sampling rate.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out.
     @param grooveBuffer groove buffer.
     */
    public AudioGroove(float srate, int bufferSize, float[] grooveBuffer) {
        super(bufferSize); // this is the internal buffer size
        this.grooveBuffer = grooveBuffer;
        srateGrooveBuffer = this.srate = srate;
        grooveBufferLength = grooveBuffer.length;
    }

    /** Get the groove buffer as array.
     @return The containing loopbuffer
     */
    public float[] getGrooveBuffer() {
        return grooveBuffer;
    }

    /**
     @return the position (in seconds) of the needle into the audio data
     */
    public abstract double getPositionOfNeedle();

    /** Compute the next buffer.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        posNeedlePast = posNeedle;
        posNeedle = getPositionOfNeedle();

        double ireal; // (fractional) index into grooveBuffer
        // ireal = a + b * k (k integer index in buffer to compute)a
        double a = posNeedlePast * srateGrooveBuffer;
        double b = (posNeedle * srateGrooveBuffer - a) / (bufsz);
        //System.out.println("p= "+posNeedle+"ppast= "+posNeedlePast);
        for (int k = 0; k < bufsz; k++) {
            ireal = a + b * (k + 1);
            if (ireal >= grooveBufferLength - 1 || ireal < 0) {
                buf[k] = 0;
            } else {
                int i = (int) ireal; // integer part
                double ifrac = ireal - i; // fractional part
                buf[k] = (float) ((1 - ifrac) * grooveBuffer[i] + ifrac * grooveBuffer[i + 1]);
            }
        }
    }

}


