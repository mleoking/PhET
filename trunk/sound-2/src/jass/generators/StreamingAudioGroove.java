package jass.generators;

import jass.engine.UnsupportedAudioFileFormatException;
import jass.render.FormatUtils;

import java.net.URL;

/**
 Position  based playback  of audio  date (gramophone  model). Wavfile
 (mono) is indexed  with position of needle on  record.  Every call to
 getBuffer this UG  polls for position of needle  in seconds, and uses
 this with the previous saved value to index a wav file and compute an
 audio buffer for the corresponding segment. Method to obtain position
 of needle is abstract.
 Streams audio off source
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public abstract class StreamingAudioGroove extends AudioGroove {

    private float[] tempFloatBuffer = null;
    private byte[] tempByteBuffer = null;

    private StreamingAudioFileBuffer afBuffer;

    /**
     For derived classes
     @param bufferSize buffer size
     */
    public StreamingAudioGroove(int bufferSize) {
        super(bufferSize); // this is the internal buffer size
    }

    /** Construct Groove from named file.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out
     @param fn Audio file name.
     */
    public StreamingAudioGroove(float srate, int bufferSize, String fn) throws UnsupportedAudioFileFormatException {
        super(bufferSize);
        afBuffer = new StreamingAudioFileBuffer(fn);
        grooveBufferLength = afBuffer.bufsz;
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
    public StreamingAudioGroove(float srate, int bufferSize, URL url) throws UnsupportedAudioFileFormatException {
        super(bufferSize); // this is the internal buffer size
        afBuffer = new StreamingAudioFileBuffer(url);
        grooveBufferLength = afBuffer.bufsz;
        srateGrooveBuffer = afBuffer.srate;
        this.srate = srate;
        srateRatio = srateGrooveBuffer / srate;
        this.name = url.toString();
    }

    /** Get the groove buffer as array, which is not possible. So return null
     @return null
     */
    public float[] getGrooveBuffer() {
        return null;
    }

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

        // determine which segment of the audiogroove we need to access and stream it into temp buffer
        // done rather inelegantly by making a dummy pass through the buffer...
        int i_min = grooveBufferLength;
        int i_max = -1;
        for (int k = 0; k < bufsz; k++) {
            ireal = a + b * (k + 1);
            if (ireal >= grooveBufferLength - 1 || ireal < 0) {
                // no data
            } else {
                int i = (int) ireal; // integer part
                if (i < i_min) {
                    i_min = i;
                }
                if (i + 1 > i_max) {
                    i_max = i + 1;
                }
            }
        }

        // have to pre-load this many float samples:
        int tempFloatBufferLength = i_max - i_min + 1;
        int tempByteBufferLength = 2 * tempFloatBufferLength;
        if (tempFloatBuffer == null || tempFloatBuffer.length < tempFloatBufferLength) {
            tempFloatBuffer = new float[tempFloatBufferLength];
        }
        if (tempByteBuffer == null || tempByteBuffer.length < tempByteBufferLength) {
            tempByteBuffer = new byte[tempByteBufferLength];
        }
        int offset = 0;
        try {
            /*
            afBuffer.randomAccessFile.seek(2*i_min); // double for bytes
            afBuffer.randomAccessFile.readFully(tempByteBuffer,offset,tempByteBufferLength);
            */
            // do seek() emulation:
            afBuffer.audioInputStream.reset();
            afBuffer.audioInputStream.skip(2 * i_min); // double for bytes
            afBuffer.audioInputStream.read(tempByteBuffer, offset, tempByteBufferLength);
        } catch (Exception e) {
        }
        FormatUtils.byteToFloat(tempFloatBuffer, tempByteBuffer, tempFloatBufferLength);
        // grooveBuffer[i] now corresponds to tempFloatBuffer[i-i_min]

        for (int k = 0; k < bufsz; k++) {
            ireal = a + b * (k + 1);
            if (ireal >= grooveBufferLength - 1 || ireal < 0) {
                buf[k] = 0;
            } else {
                int i = (int) ireal; // integer part
                double ifrac = ireal - i; // fractional part
                buf[k] = (float) ((1 - ifrac) * tempFloatBuffer[i - i_min] + ifrac * tempFloatBuffer[i - i_min + 1]);
            }
        }

    }

}


