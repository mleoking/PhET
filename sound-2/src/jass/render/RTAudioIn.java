package jass.render;

import javax.sound.sampled.*;

/**
 Utility class to read audio in real-time. Use appropriate constructor to use JavaSound or native sound support
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class RTAudioIn extends Thread {
    private float srate;
    private int bitsPerFrame;
    private int nchannels;
    private boolean signed;
    private static final boolean bigEndian = false;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private int scratchBufsz; // byte buffer size
    private byte[] bbuf;
    private int bufferSize; // JavaSound buffersize
    private String preferredMixer = null;
    private Mixer mixer;

    // for native sound
    private boolean useNative = false; // can use native audio
    private long nativeObjectPointer = 0;

    /** Initialize native sound for RtAudio
     This is a native method and needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param nchannels number of audio channels.
     @param srate sampling rate in Hertz.
     @param bufferSizeJass jass buffersize
     @param numRtAudioBuffers affectiing latency, 0 gives best latency
     @return long representing C++ object pointer
     */
    public native long initNativeSound(int nchannels, int srate, int bufferSizeJass, int numRtAudioBuffers);

    /** Read a buffer of floats using native sound.
     This is a native method and needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param nativeObjectPointer representing C++ object pointer.
     @param buf array of float with sound buffer.
     @param buflen length of buffer.
     */
    public native void readNativeSoundFloat(long nativeObjectPointer, float[] buf, int buflen);


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
                //System.out.println("mixer "+i+"-----------------\n");
                //System.out.println("description:"+mixerinfo[i].getDescription());
                String name = mixerinfo[i].getName();
                //System.out.println("name:"+name);
                if (name.equalsIgnoreCase(preferredMixer)) {
                    mixerIndex = i;
                }
                //System.out.println("vendor:"+mixerinfo[i].getVendor());
                //System.out.println("version:"+mixerinfo[i].getVersion());
                //System.out.println("string:"+mixerinfo[i].toString());
            }
        }

        System.out.println("CHOSEN INPUT MIXER: " + mixerinfo[mixerIndex].getName());
        mixer = AudioSystem.getMixer(mixerinfo[mixerIndex]);
    }

    /* num_fragments must equal bufferSize of JASS patch whern using RtAudio on LINUX, it is what its name
       indicates when using DirectSound
     */
    private void initAudioNative(float srate, int nchannels, int num_fragments) {
        int numRtAudioBuffers = 0;
        initAudioNative(srate, nchannels, num_fragments, numRtAudioBuffers);
    }

    private void initAudioNative(float srate, int nchannels, int buffersizeJass, int numRtAudioBuffers) {
        this.srate = srate;
        this.nchannels = nchannels;
        nativeObjectPointer = initNativeSound(nchannels, (int) srate, buffersizeJass, numRtAudioBuffers);

        //This will ensure that close() gets called before the program exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("The shutdown hook in RTAudioIn is executing");
                close();
                try {	//wait for the DLL to do its destruction before terminating!
                    sleep(100);
                } catch (Exception e) {
                    System.out.print("The sleep function is broken");
                }
            }
        });

    }

    private void initAudio(int bufferSize, float srate, int bitsPerFrame, int nchannels, boolean signed) {
        this.srate = srate;
        this.bufferSize = bufferSize;
        this.bitsPerFrame = bitsPerFrame;
        this.nchannels = nchannels;
        this.signed = signed;
        findMixer();
        audioFormat = new AudioFormat(srate, bitsPerFrame, nchannels, signed, bigEndian);
        DataLine.Info info;
        if (bufferSize == 0) {
            info = new DataLine.Info(TargetDataLine.class, audioFormat);
        } else {
            info = new DataLine.Info(TargetDataLine.class, audioFormat, bufferSize);
        }
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println(getClass().getName() + " : Error gettting targetDataLine: not supported\n");
        }
        // BUG
        if (!mixer.isLineSupported(info)) {
            // this may be a bogus message, e.g. on Tritonus this is not reliable information
            System.out.println(getClass().getName()
                    + " : Error: sourcedataline: not supported (this may be a bogus message under Tritonus)\n");
        }
        try {
            // <BUG>
            // For some reason can't get a line from the mixer, but I can get it from AudioSystem
            //
            //targetDataLine = (TargetDataLine) mixer.getLine(info); // should do this but fails??
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info); // workaround, ignore preferred mixer
            // </BUG>
            if (bufferSize == 0) {
                targetDataLine.open(audioFormat);
            } else {
                targetDataLine.open(audioFormat, bufferSize);
            }
            targetDataLine.start();
        } catch (LineUnavailableException ex) {
            System.out.println(getClass().getName() + " : Error getting line, trying again in 1 sec\n");
            // sometimes it works the second time
            try {
                sleep(1000);
            } catch (Exception e3) {
            }
            try {
                targetDataLine = (TargetDataLine) mixer.getLine(info);
                if (bufferSize == 0) {
                    targetDataLine.open(audioFormat);
                } else {
                    targetDataLine.open(audioFormat, bufferSize);
                }
                targetDataLine.start();
            } catch (LineUnavailableException ex2) {
                System.out.println(getClass().getName() + " : Error getting line 2nd time, giving up\n");
            }
        }
        scratchBufsz = 4;
        bbuf = new byte[scratchBufsz];
    }

    /** Constructor. Uses JavaSound with default mixer
     @param bufferSize Buffer size used by JavaSound.
     @param srate sampling rate in Hertz.
     @param bitsPerFrame number of bits per audio frame.
     @param nchannels number of audio channels.
     @param signed true if signed format, false otherwise.
     */
    public RTAudioIn(int buffersize, float srate, int bitsPerFrame, int nchannels, boolean signed) {
        initAudio(buffersize, srate, bitsPerFrame, nchannels, signed);
    }

    /** Constructor. Uses JavaSound and can set mixer to use (e.g. "Esd Mixer")
     @param bufferSize Buffer size used by JavaSound.
     @param srate sampling rate in Hertz.
     @param bitsPerFrame number of bits per audio frame.
     @param nchannels number of audio channels.
     @param signed true if signed format, false otherwise.
     @param prefMixer preferred mixer as name string
     */
    public RTAudioIn(int buffersize, float srate, int bitsPerFrame, int nchannels, boolean signed, String prefMixer) {
        this.preferredMixer = prefMixer;
        initAudio(buffersize, srate, bitsPerFrame, nchannels, signed);
    }

    /** Constructor. Uses default high latency JavaSound buffersize and default mixer
     @param srate sampling rate in Hertz.
     @param bitsPerFrame number of bits per audio frame.
     @param nchannels number of audio channels.
     @param signed true if signed format, false otherwise.
     */
    public RTAudioIn(float srate, int bitsPerFrame, int nchannels, boolean signed) {
        initAudio(0, srate, bitsPerFrame, nchannels, signed);
    }

    /** Constructor. Uses native sound capture.
     This is a native method and needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param srate sampling rate in Hertz.
     @param nchannels number of audio channels.
     @param bufferSizeJass jass bufsz
     @param numRtAudioBuffers rtaudio parameter related to latency. 0 lowest
     */
    public RTAudioIn(float srate, int nchannels, int bufferSizeJass, int numRtAudioBuffers) {
        useNative = true;
        // load shared library with native sound implementations
        try {
            System.loadLibrary("rtaudio");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Could not load shared library rtaudio: " + e);
        }
        initAudioNative(srate, nchannels, bufferSizeJass, numRtAudioBuffers);
    }

    /** Constructor. Uses native sound capture. Uses default (0) for numRtAudioBuffers
     This is a native method and needs librtaudio.so (LINUX) or rtaudio.dll (Windows)
     @param srate sampling rate in Hertz.
     @param nchannels number of audio channels.
     @param bufferSizeJass jass bufsz
     */
    public RTAudioIn(float srate, int nchannels, int bufferSizeJass) {
        useNative = true;
        int numRtAudioBuffers = 0; // use lowest possible
        // load shared library with native sound implementations
        try {
            System.loadLibrary("rtaudio");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Could not load shared library rtaudio: " + e);
        }
        initAudioNative(srate, nchannels, bufferSizeJass, numRtAudioBuffers);
    }

    /** Read audio buffer from input queue and block if queue is empty.
     @param y buffer to write to.
     @param nsamples number of samples required.
     */
    public void read(float[] y, int nsamples) {
        if (useNative) {
            readNativeSoundFloat(nativeObjectPointer, y, nsamples);
        } else {
            if (2 * nsamples > scratchBufsz) {
                scratchBufsz = 2 * nsamples;
                bbuf = new byte[scratchBufsz];
            }
            targetDataLine.read(bbuf, 0, 2 * nsamples);
            FormatUtils.byteToFloat(y, bbuf, nsamples);
        }
    }

    //** Close resources */
    public void close() {
        if (useNative) {
            if (nativeObjectPointer != 0) {
                closeNativeSound(nativeObjectPointer);
                nativeObjectPointer = 0;
            }
        } else {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }

}
