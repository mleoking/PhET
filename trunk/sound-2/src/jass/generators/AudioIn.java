package jass.generators;

import jass.engine.Out;
import jass.render.FormatUtils;
import jass.render.RTAudioIn;


/**
 Obtain buffers by reading audio input. To use native audio input use appropriate contructor.
 Mono input only.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 (ASIO interface by Richard Corbett.)
 */
public class AudioIn extends Out {
    /** Audio input port. */
    private RTAudioIn pai;
    private boolean useNativeSound;
    private String nativeString;
    private boolean useASIO = false;
    private short[] dataCommingIn;
    private boolean writing = true;	// a safety flag to ensure that reading and writing don't occur at the same time

    /** Constructor. Default mixer, uses JavaSound API
     @param srate Sampling rate in Hertz.
     @param bufferSize Buffer size of Out object.
     @param bufferSizeJavaSound Buffer size used by JavaSound. Set to 0 to get default.
     */
    public AudioIn(float srate, int bufferSize, int bufferSizeJavaSound) {
        super(bufferSize);
        useASIO = false;
        pai = new RTAudioIn(bufferSizeJavaSound, srate, 16, 1, true);
    }

    /** Constructor. Allows selection of audio API, JavaSound mixer. Has all the options.
     @param srate Sampling rate in Hertz.
     @param bufferSize Buffer size of Out object.
     @param bufferSizeJavaSound Buffer size used by JavaSound. Set to 0 to get default. Not used if using native input.
     @param preferredMixer mixer name for JavaSound, for example "Esd Mixer". Use anything to get default. Ignoted if using native input
     @param nativesource "RtAudio" or "ASIO" or anything else to use JavaSound
     @param numRtAudioBuffers latency parameter for  rtaudio 0 is lowest latency, ignored for JavaSound or ASIO
     */
    public AudioIn(float srate, int bufferSize, int bufferSizeJavaSound, String preferredMixer, String nativesource, int numRtAudioBuffers) {
        super(bufferSize);
        if (nativesource.equalsIgnoreCase("RtAudio")) {
            useASIO = false;
            int nchannels = 1;
            pai = new RTAudioIn(srate, nchannels, bufferSize, numRtAudioBuffers);
        } else if (nativesource.equalsIgnoreCase("ASIO")) {
            this.nativeString = nativesource;
            useASIO = true;
            //data array for ASIO input. This works without using RTAudioIn
            dataCommingIn = new short[bufferSize];
        } else {
            useASIO = false;
            int nchannels = 1;
            int bitsperframe = 16;
            pai = new RTAudioIn(bufferSizeJavaSound, srate, bitsperframe, nchannels, true, preferredMixer);
        }

    }


    /** This is where we will take the data from ASIO.  This function should not be explicitly
     called by the user. ASIO is calling this function.
     */
    public void takeASIOData(short[] myData) {
        writing = true;
        for (int i = 0; i < myData.length; i++) {
            dataCommingIn[i] = myData[i];
        }
        writing = false;
    }


    /** Method for reading data into buf
     */
    protected void computeBuffer() {
        if (useASIO == false) {
            pai.read(buf, getBufferSize());
        } else {
            getInputBuffer(buf, bufferSize);
        }

    }

    /** Close resources
     If ASIO mode is running, the closing will be taken care of automatically.
     */
    public void close() {
        if (useASIO == false) {
            pai.close();
        }
    }

    /** A method to extract the ASIO data.
     */
    private void getInputBuffer(float[] getBuf, int bufferSize) {
        if (!writing) {
            FormatUtils.shortToFloat(getBuf, dataCommingIn, bufferSize);
        }
    }


}



