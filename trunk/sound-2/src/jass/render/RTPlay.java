package jass.render;

import javax.sound.sampled.*;

/** Utility class for real-time audio play.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class RTPlay extends Thread {
    private float srate;
    private int bitsPerFrame;
    private int nchannels;
    private boolean signed;
    private static final boolean bigEndian = false;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private Mixer mixer;
    private static final int DEFAULT_BUFFERSIZE = 1024; //in bytes
    private int scratchBufsz; // Buffersize for writing audio
    private byte[] bbuf;
    private String preferredMixer = null;

    // for native sound
    private boolean useNative = false; // can use native audio out
    private long nativeObjectPointer = 0;

    /** Initialize native sound using RtAudio, setting buffersize and an internal RtAudio buffersize
     @param nchannels number of audio channels.
     @param srate sampling rate in Hertz.
     @param buffersizeJASS buffers will be rendered in these chunks
     @param nRtaudioBuffers internal buffers, 0 = lowest possible
     @return long representing C++ object pointer
     */
    public native long initNativeSound(int nchannels, int srate, int buffersizeJASS, int nRtaudioBuffers);

    /** write a buffer of floats to native sound.
     This is a native method and needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param nativeObjectPointer representing C++ object pointer.
     @param buf array of floats with sound buffer.
     @param buflen length of buffer.
     */
    public native void writeNativeSoundFloat(long nativeObjectPointer, float[] buf, int buflen);

    /** Close native sound
     This is a native method and needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param nativeObjectPointer representing C++ object pointer
     */
    public native void closeNativeSound(long nativeObjectPointer);

    private void findMixer() {
        Mixer.Info[] mixerinfo = AudioSystem.getMixerInfo();
        int mixerIndex = 0;
        if (preferredMixer != null) {
            for (int i = 0; i < mixerinfo.length; i++) {
                // list the mixers
                System.out.println("mixer " + i + "-----------------\n");
                //System.out.println("description:"+mixerinfo[i].getDescription());
                String name = mixerinfo[i].getName();
                System.out.println("name:" + name);
                if (name.equalsIgnoreCase(preferredMixer)) {
                    mixerIndex = i;
                }
                //System.out.println("vendor:"+mixerinfo[i].getVendor());
                //System.out.println("version:"+mixerinfo[i].getVersion());
                //System.out.println("string:"+mixerinfo[i].toString());
            }
        }
        System.out.println("CHOSEN MIXER: " + mixerinfo[mixerIndex].getName());
        mixer = AudioSystem.getMixer(mixerinfo[mixerIndex]);
    }

    private void initAudio(int buffersize, float srate, int bitsPerFrame, int nchannels, boolean signed) {
        this.srate = srate;
        this.bitsPerFrame = bitsPerFrame;
        this.nchannels = nchannels;
        this.signed = signed;
        findMixer();
        audioFormat = new AudioFormat((float) srate, bitsPerFrame, nchannels, signed, bigEndian);
        DataLine.Info info;
        if (buffersize == 0) {
            info = new DataLine.Info(SourceDataLine.class, audioFormat);
        } else {
            info = new DataLine.Info(SourceDataLine.class, audioFormat, buffersize);
        }
        //System.out.println("minBufsz="+info.getMinBufferSize()+"maxBufsz="+info.getMaxBufferSize());
        if (!mixer.isLineSupported(info)) {
            // this may be a bogus message, e.g. on Tritonus this is not reliable information
            System.out.println(getClass().getName()
                    + " : Error: sourcedataline: not supported (this may be a bogus message under Tritonus)\n");
        }
        try {
            sourceDataLine = (SourceDataLine) mixer.getLine(info);
            if (buffersize == 0) {
                sourceDataLine.open(audioFormat);
            } else {
                sourceDataLine.open(audioFormat, buffersize);
            }
            sourceDataLine.start();
        } catch (LineUnavailableException ex) {
            System.out.println(getClass().getName() + " : Error getting line\n");
        }
        scratchBufsz = 4; // some small value. Will be increased when needed
        bbuf = new byte[scratchBufsz];
    }

    private void initAudioNative(float srate, int nchannels, int num_fragments) {
        int numRtAudioBuffers = 0;
        initAudioNative(srate, nchannels, num_fragments, numRtAudioBuffers);
    }

    private void initAudioNative(float srate, int nchannels, int num_fragments, int numRtAudioBuffers) {
        this.srate = srate;
        this.bitsPerFrame = bitsPerFrame;
        this.nchannels = nchannels;
        this.signed = signed;
        scratchBufsz = 4 * 2;
        // get pointer to C++ object
        nativeObjectPointer = initNativeSound(nchannels, (int) srate, num_fragments, numRtAudioBuffers);

        //This will ensure that close() gets called before the program exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("The shutdown hook in RTPlay is executing");
                close();
                try {
                    sleep(100);
                } catch (Exception e) {
                    System.out.print("The sleep function is broken");
                }
            }
        });

    }

    /** Constructor.
     @param bufferSize Buffer size used by JavaSound.
     @param srate sampling rate in Hertz.
     @param bitsPerFrame number of bits per audio frame.
     @param nchannels number of audio channels.
     @param signed true if signed format, false otherwise.
     */
    public RTPlay(int buffersize, float srate, int bitsPerFrame, int nchannels, boolean signed) {
        initAudio(buffersize, srate, bitsPerFrame, nchannels, signed);
    }


    /** Constructor. Chose preferred mixer by name
     @param bufferSize Buffer size used by JavaSound.
     @param srate sampling rate in Hertz.
     @param bitsPerFrame number of bits per audio frame.
     @param nchannels number of audio channels.
     @param signed true if signed format, false otherwise.
     @param preferredMixer preferred mixer as a name string
     */
    public RTPlay(int buffersize, float srate, int bitsPerFrame, int nchannels,
                  boolean signed, String preferredMixer) {
        this.preferredMixer = preferredMixer;
        initAudio(buffersize, srate, bitsPerFrame, nchannels, signed);
    }

    /** Constructor. Uses default high latency JavaSound buffer size.
     @param srate sampling rate in Hertz.
     @param bitsPerFrame number of bits per audio frame.
     @param nchannels number of audio channels.
     @param signed true if signed format, false otherwise.
     */
    public RTPlay(float srate, int bitsPerFrame, int nchannels, boolean signed) {
        initAudio(0, srate, bitsPerFrame, nchannels, signed);
    }

    /** Constructor. Uses native audio write.
     Needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param srate sampling rate in Hertz.
     @param nchannels number of audio channels.
     @param buffersizeJass this will be the buffersize of RtAudio
     */
    public RTPlay(float srate, int nchannels, int buffersizeJass) {
        useNative = true;
        // load shared library with native sound implementations
        try {
            System.loadLibrary("rtaudio");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Could not load shared library rtaudio: " + e);
        }
        initAudioNative(srate, nchannels, buffersizeJass);
    }


    /** Constructor. Uses native audio write. Also specify RtAudio tweak parameter numberofbuffers used
     Needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param srate sampling rate in Hertz.
     @param nchannels number of audio channels.
     @param buffersizeJass jass buffersize (TODO fix name)
     @param numRtAudioBuffers number of rtaudio buffers (0 is lowest)
     */
    public RTPlay(float srate, int nchannels, int buffersizeJass, int numRtAudioBuffers) {
        useNative = true;
        // load shared library with native sound implementations
        try {
            System.loadLibrary("rtaudio");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Could not load shared library rtaudio: " + e);
        }
        initAudioNative(srate, nchannels, buffersizeJass, numRtAudioBuffers);
    }


    /** Write audio buffer to output queue and block if queue is full.
     @param y output buffer.
     */
    public void write(float[] y) {
        if (useNative) {
            writeNativeSoundFloat(nativeObjectPointer, y, y.length);
        } else {
            if (2 * y.length > scratchBufsz) {
                scratchBufsz = 2 * y.length;
                bbuf = new byte[scratchBufsz];
            }
            FormatUtils.floatToByte(bbuf, y);
            sourceDataLine.write(bbuf, 0, 2 * y.length);
        }
    }

    /** Close line or native sound
     */
    public void close() {
        if (useNative) {
            System.out.println("RTPlay.close() will call closeNativeSound: nativeObjectPointer= " + nativeObjectPointer);
            if (nativeObjectPointer != 0) {
                closeNativeSound(nativeObjectPointer);
                nativeObjectPointer = 0;
            }
            try {
                sleep(100);
            } catch (Exception e) {
            }
        } else {
            sourceDataLine.stop();
            sourceDataLine.close();
        }
    }

}





