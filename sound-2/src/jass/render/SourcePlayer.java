package jass.render;

import jass.engine.BufferNotAvailableException;
import jass.engine.In;
import jass.engine.SinkIsFullException;
import jass.engine.Source;
import jass.generators.AudioIn;

import java.io.*;

/**
 Renders Sources to audio card using JavaSound or native methods, or out to file.
 To write to file manually call advanceTime(), see renderToFile example.
 Can't add rendered objects after starting it.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class SourcePlayer extends In {

    private int bufferSize;
    private int bufferSizeJavaSound;
    private float srate;
    private float maxSignal = 0;
    private boolean agc = true;
    private long t = 0; // frame time (count of buffers)
    private double real_time = 0; // 0 is begin of frame 0
    private float[] tempBuf; // mix in here
    private byte[] byteBuf; // to dump to output
    private FileOutputStream outStream;
    private PrintStream printStream;
    private boolean stopFlag = false; // set to true to stop
    private String preferredMixer = null; // use this Mixer if possible
    private boolean muted = false; // to mute/unmute
    private boolean useNativeSound = false;
    private boolean useASIO = false;
    private int numRtAudioBuffers = 0; // for RtAudio. 0 will give minimum
    private float counter = 0;
    private boolean ASIOconnected = false;
    private String audioAPI = "";
    private int outputchannelNum = 2;
    private int inputchannelNum = 0;
    private AudioIn myInput = null;
    private RTPlay pb;
    private int nchannels = 1;

    /** Initialize the ASIO libraries this is done in JassASIO.dll
     The system will begin running once the initialization has occurred
     The buffers will be retrieved from the getNextBuffer(short[]) method

     @param srate is the smapling rate
     @param bufferSize is the size of the output buffer to be used in ASIO
     @param outputchannelNum selects the output channels
     @param inputchannelNum select the input channel (if desired)
     @param myInput is used to give access to an input device if ASIO input-output is desired
     */
    public native int initAsioJass(int srate, int bufferSize, int outputchannelNum, int inputchannelNum, AudioIn myInput);

    /** Shut down the ASIO libraries.
     This call will disconnect the ASIO and stop its execution.
     */
    public native void cleanupASIO();

    /**	Set this AudioIn object so that we can use ASIO input.
     Input must be handled here if using ASIO as it provides only one ASIO conncection.
     @param commingIn audio input through ASIO
     */
    public void setASIOInput(AudioIn commingIn) {
        this.myInput = commingIn;
    }


    /** Determine the use of Native Methods, either  ASIO (needs JassASIO.dll) or something else (will be chosen
     later at run time depending on which shared library is found:  rtaudio.dll (ASIO or DX version)
     or librtaudio.so (OSS or ALSA version).
     @param value - true to use native methods, false otherwise
     @param audioAPI - either "something", or "ASIO".
     */
    public void setUseNativeSound(boolean value, String audioAPI) {
        this.useNativeSound = value;
        if ((audioAPI.equals("ASIO")) && value == true) {
            this.audioAPI = audioAPI;
        }
    }

    /** Set method to make it use/not use RtAudio based  native sound.
     Needs rtaudio.dll or librtaudio.so and works on Windows/LINUX only.
     @param value value
     */
    public void setUseNativeSound(boolean value) {
        this.useNativeSound = value;
        if (this.useNativeSound == true) {
            this.audioAPI = "notASIO";
        }
    }

    /** Get method for output channel number.
     This selects the output channel number.  Refer to sound card driver documentation
     to decide which channel to output through.  Once ASIO has started the selected channel
     and the next greatest one will be used for output. On the SoundBlaster Audigy
     the left speaker chennel is number 6 therefore the output will be on channel 6 and 7
     (7 is the right channel).  This convention assumes that the two sides of a stereo
     convention will be consecutive channels in the driver.
     @return channel number
     */
    public int getOutputChannelNum() {
        return this.outputchannelNum;
    }

    /** Set method for output channel Number
     @param channel - the selected channel.  Output will be done through the selected
     channel and the chennel at the next largest location.  This will create matched
     stereo sound.
     @param channel the channel to use
     */
    public void setOutputChannelNum(int channel) {
        this.outputchannelNum = channel;
    }

    /** Get method for returning the selected output channel for ASIO
     @return int input channel number
     */
    public int getInputChannelNum() {
        return this.inputchannelNum;
    }

    /** Set method for Input channel Number
     @param channel - the desired Input channel.  This must be set before the InitASIO method is called
     */
    public void setInputChannelNum(int channel) {
        this.inputchannelNum = channel;
    }


    /** Get method for useNativeSound flag
     @return true is using native sound
     */
    public boolean getUseNativeSound() {
        return this.useNativeSound;
    }

    /** Get method for audioAPI string
     @return string indicating which native audio API used
     */
    public String getAudioAPI() {
        return this.audioAPI;
    }

    /** Set RtAudio latency parameter if using  native sound.
     @param  numRtAudioBuffer rtaudio parameter influences latency. 0 will choose minimum
     */
    public void setNumRtAudioBuffersNative(int numRtAudioBuffers) {
        this.numRtAudioBuffers = numRtAudioBuffers;
    }

    /** Get RtAudio latency parameter if using  native sound.
     @return  rtaudio parameter influences latency. 0 will choose minimum
     */
    public int getNumRtAudioBuffersNative() {
        return numRtAudioBuffers;
    }

    /** set nchannels 1 for mone 2 for stereo
     @param n # channels
     */
    public void setNChannels(int n) {
        nchannels = n;
    }

    /** Set mute/unmute
     @param muted false if unmute, true if mute
     */
    public void setMute(boolean muted) {
        this.muted = muted;
    }

    /** Get Mute/unmute state
     @return muted false if unmuted, true if muted
     */
    public boolean getMute() {
        return muted;
    }

    /** Get value used by AGC.
     @return maximum signal value used by AGC
     */
    public float getAGC() {
        return maxSignal;
    }

    /** Set AGC maximum signal seen.
     @param maxSignal maximum value of signal seen.
     */
    public void setAGC(float maxSignal) {
        this.maxSignal = maxSignal;
    }

    /** Reset AGC state */
    public void resetAGC() {
        maxSignal = 0;
    }

    /** Enable AGC */
    public void AGCOn() {
        agc = true;
    }

    /** Disable AGC */
    public void AGCOff() {
        agc = false;
    }

    /** Create Source Player intended to real-time audio output.
     @param bufferSize Buffersize used for blocks in filter graph
     @param bufferSizeJavaSound buffer size used by Java Sound. Use 0 for default.
     @param srate sampling rate in Hz
     */
    public SourcePlayer(int bufferSize, int bufferSizeJavaSound, float srate) {
        this.bufferSize = bufferSize;
        this.bufferSizeJavaSound = bufferSizeJavaSound;
        this.srate = srate;
        tempBuf = new float[bufferSize];

    }


    /** Create Source Player intended to real-time audio output. Will use default for JavaSound buffersize.
     @param bufferSize Buffersize used for blocks in filter graph
     @param srate sampling rate in Hz
     */
    public SourcePlayer(int bufferSize, float srate) {
        this.bufferSize = bufferSize;
        this.bufferSizeJavaSound = 0;
        this.srate = srate;
        tempBuf = new float[bufferSize];

    }


    /** Create Source Player intended to real-time audio output.
     Chose a preferred Mixer. If not found will get line from AudioSystem.
     @param bufferSize Buffersize used for blocks in filter graph
     @param bufferSizeJavaSound buffer size used by Java Sound. Use 0 for default.
     @param srate sampling rate in Hz
     @param preferredMixer preferred Mixer (e.g., "Esd Mixer").
     */
    public SourcePlayer(int bufferSize, int bufferSizeJavaSound, float srate, String preferredMixer) {
        this.bufferSize = bufferSize;
        this.bufferSizeJavaSound = bufferSizeJavaSound;
        this.srate = srate;
        tempBuf = new float[bufferSize];
        this.preferredMixer = preferredMixer;
    }


    /** Create Source Player intended to real-time audio output.
     @param bufferSize Buffersize used for blocks in filter graph
     @param bufferSizeJavaSound buffer size used by Java Sound. Use 0 for default.
     @param srate sampling rate in Hz
     @param src Source to play.
     */
    public SourcePlayer(int bufferSize, int bufferSizeJavaSound,
                        float srate, Source src) throws SinkIsFullException {
        this.bufferSize = bufferSize;
        this.bufferSizeJavaSound = bufferSizeJavaSound;
        this.srate = srate;
        tempBuf = new float[bufferSize];
        addSource(src);
    }

    /** Create Source Player intended for audio-file output. Will create raw audio file,
     or ascii data depending on which advanceTime() call is made.
     @param bufferSize Buffersize used for blocks in filter graph
     @param srate sampling rate in Hz
     @param fn file name for raw audio data
     */
    public SourcePlayer(int bufferSize, float srate, String fn) {
        this.bufferSize = bufferSize;
        this.bufferSizeJavaSound = bufferSizeJavaSound;
        this.srate = srate;
        tempBuf = new float[bufferSize];
        byteBuf = new byte[2 * bufferSize];
        try {
            outStream = new FileOutputStream(new File(fn));
            printStream = new PrintStream(outStream);
        } catch (FileNotFoundException e) {
            System.out.println(this + " " + e);
        }
    }

    /** Advance time and process audio till then. This writes raw data suitable for playback
     as raw audio (16-bit mono ).
     @param realTime real-time in seconds.
     */
    public void advanceTime(double realTime) throws BufferNotAvailableException, IOException {
        // while realTime later than end of next to-be-computed buffer...
        while (realTime > (bufferSize / srate) * (t + 2)) {
            float[] y = getNextBuffer();
            FormatUtils.floatToByte(byteBuf, y);
            outStream.write(byteBuf);
        }
    }

    /** Advance time and process audio till then. This writes ascii data, one sample per line.
     @param realTime real-time in seconds.
     */
    public void advanceTime(double realTime, boolean ascii)
            throws BufferNotAvailableException, IOException {
        // while realTime later than end of next to-be-computed buffer...
        while (realTime > (bufferSize / srate) * (t + 2)) {
            float[] y = getNextBuffer();
            for (int i = 0; i < bufferSize; i++) {
                printStream.println(y[i]);
            }
        }
    }

    /** Retrieve the next available audio buffer.
     @param myData an array to be filled with the data elements
     */
    public void getNextBuffer(short[] myData) throws BufferNotAvailableException {
        //counter++;
        int nsources = sourceContainer.size();
        for (int k = 0; k < bufferSize; k++) {
            tempBuf[k] = 0;
        }

        // Mixdown all sources
        for (int is = 0; is < nsources; is++) {
            float[] y = ((Source) sourceContainer.elementAt(is)).getBuffer(t);
            if (!muted) {
                for (int k = 0; k < bufferSize; k++) {
                    tempBuf[k] += y[k];
                }
            }
        }
        if (agc) {
            for (int i = 0; i < bufferSize; i++) {
                float tmpAbs = Math.abs(tempBuf[i]);
                if (tmpAbs > maxSignal) {
                    maxSignal = tmpAbs;
                }
            }
            if (maxSignal != 0) {
                for (int i = 0; i < bufferSize; i++) {
                    tempBuf[i] /= maxSignal;
                }
            }
        }

        t++;

        FormatUtils.floatToShort(myData, tempBuf);

    }


    /** Retrieve the next available audio buffer.
     @return float array of data filled with the requested buffer
     */
    private float[] getNextBuffer() throws BufferNotAvailableException {
        int nsources = sourceContainer.size();
        for (int k = 0; k < bufferSize; k++) {
            tempBuf[k] = 0;
        }

        // Mixdown all sources
        for (int is = 0; is < nsources; is++) {
            float[] y = ((Source) sourceContainer.elementAt(is)).getBuffer(t);
            if (!muted) {
                for (int k = 0; k < bufferSize; k++) {
                    tempBuf[k] += y[k];
                }
            }
        }
        if (agc) {
            for (int i = 0; i < bufferSize; i++) {
                float tmpAbs = Math.abs(tempBuf[i]);
                if (tmpAbs > maxSignal) {
                    maxSignal = tmpAbs;
                }
            }
            if (maxSignal != 0) {
                for (int i = 0; i < bufferSize; i++) {
                    tempBuf[i] /= maxSignal;
                }
            }
        }

        t++;
        return tempBuf;
    }


    /**
     Call to stop player
     */
    public void stopPlaying() {
        stopFlag = true;
    }

    /** Start thread which obtains buffers form sources and writes to audio out.
     */
    public void run() {
        int nsources = sourceContainer.size();
        if (nsources == 0) {
            System.out.println("no sources to play, nsources=" + nsources);
            return;
        }
        if (useNativeSound && (audioAPI.equals("ASIO"))) {
            initASIO();
        } else {

            if (useNativeSound) {	//Must now be RTAudio
                pb = new RTPlay(srate, nchannels, bufferSize, numRtAudioBuffers);
                try {
                    sleep(100); // allow native stuff some time
                } catch (Exception e) {
                }

            } else {
                pb = new RTPlay(bufferSizeJavaSound, srate, 16, nchannels, true, preferredMixer);
            }
            t = 0;
            for (int is = 0; is < nsources; is++) {
                ((Source) sourceContainer.elementAt(is)).setTime(t);
            }
            while (!stopFlag) {
                try {
                    pb.write(getNextBuffer());
                } catch (BufferNotAvailableException ee) {
                    System.out.println(ee + " Sourceplayer could not get play buffer");
                    System.exit(0);
                }
            }
            System.out.println("The shutdown hook in RTPlay is executing");
            pb.close();
        }
    }

    /** Initialize the ASIO system and start it up.
     This must be done through JassAsio.dll.
     Once this method is called the ASIO streaming proccess will begin and the getNextBuffer(short[])
     method will be called when new buffers are required for output.
     */
    public void initASIO() {
        //load the asioJass.dll library

        try {
            System.loadLibrary("JassAsio");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Could not load shared library JassAsio");
        }

        try {	//wait for the native stuff to get going
            sleep(500);
        } catch (Exception e) {
            System.out.print("The sleep function is broken");
        }

        //This will ensure that the cleanupASIO() gets called before the program exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("The shutdown hook is executing");
                if (audioAPI.equals("ASIO"))
                    cleanupASIO();
                try {	//wait for the DLL to do its destruction before terminating!
                    sleep(1500);
                } catch (Exception e) {
                    System.out.print("The sleep function is broken");
                }
            }
        });	//end of ASIO destruction code


        //initialize the ASIO system and it will begin running and getting
        //its buffers from getNextBuffer()
        if (initAsioJass((int) srate, bufferSize, outputchannelNum, inputchannelNum, myInput) == 0) {
            ASIOconnected = true;
            System.out.println("ASIO has returned and finished.");
        } else {
            ASIOconnected = false;
            System.out.println("The ASIO system Could not be initialized.");
        }
        if (myInput != null) {
            myInput.close();
        }
    }

    /** Stop the ASIO streaming process and clean up.
     This must be done through JassAsio.dll.
     Call this method asynchronously to stop the ASIO streaming process.  Once the
     streaming has stopped the ASIO connection will be destructed. To reconnect
     call initASIO().
     */
    public void closeASIO() {
        Thread endASIO = new Thread(new Runnable() {
            public void run() {
                cleanupASIO();
            }
        });
        endASIO.run();

    }


}


