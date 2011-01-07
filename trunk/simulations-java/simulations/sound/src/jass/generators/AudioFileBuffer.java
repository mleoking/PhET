// Copyright 2002-2011, University of Colorado
package jass.generators;

import jass.render.FormatUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.net.URL;

/**
 * A buffer loaded from an audio file or URL.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class AudioFileBuffer {
    /**
     * Sampling rate  in Hertz.
     */
    public float srate;

    /**
     * Bytes per frame (2 for mono, 4 stereo, etc.)
     */
    public int bytesPerFrame;

    /**
     * # frames
     */
    public long nFrames;

    /**
     * bytes
     */
    public int numBytes;

    /**
     * Bits per sample
     */
    public int bitsPerSample;

    /**
     * Number of channels
     */
    public int nChannels;

    /**
     * Length of buffer as floats.
     */
    public int bufsz;

    /**
     * Buffer.
     */
    public float[] buf;

    /**
     * Construct buffer from named file.
     *
     * @param fn Audio file name.
     */
    public AudioFileBuffer( String fn ) {
        loadAudio( fn );
    }


    /**
     * Construct buffer from  url.
     *
     * @param url Audiofile url.
     */
    public AudioFileBuffer( URL url ) {
        loadAudio( url );
    }

    /**
     * Load audio file.
     *
     * @param fn Audio file name.
     */
    public void loadAudio( String fn ) {
        int totalFramesRead = 0;
        File fileIn = new File( fn );
        byte[] audioBytes = null;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( fileIn );
            bytesPerFrame = audioInputStream.getFormat().getFrameSize();
            bitsPerSample = audioInputStream.getFormat().getSampleSizeInBits();
            nChannels = audioInputStream.getFormat().getChannels();
            srate = (float)audioInputStream.getFormat().getSampleRate();
            nFrames = audioInputStream.getFrameLength();
            numBytes = (int)nFrames * bytesPerFrame;
            audioBytes = new byte[numBytes];
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                while( ( numBytesRead = audioInputStream.read( audioBytes ) ) != -1 ) {
                    ;
                }
                audioInputStream.close();
            }
            catch( Exception ex ) {
                System.out.println( "Error loading audio file " + fn );
            }
        }
        catch( Exception e ) {
            System.out.println( "Error reading audio file " + fn );
        }

        bufsz = numBytes / 2;
        buf = new float[bufsz];
        FormatUtils.byteToFloat( buf, audioBytes, bufsz );
    }


    /**
     * Load audio url.
     *
     * @param url Audiofile url.
     */
    public void loadAudio( URL url ) {
        int totalFramesRead = 0;
        byte[] audioBytes = null;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( url );
            bytesPerFrame = audioInputStream.getFormat().getFrameSize();
            bitsPerSample = audioInputStream.getFormat().getSampleSizeInBits();
            nChannels = audioInputStream.getFormat().getChannels();
            srate = (float)audioInputStream.getFormat().getSampleRate();
            nFrames = audioInputStream.getFrameLength();
            numBytes = (int)nFrames * bytesPerFrame;
            audioBytes = new byte[numBytes];
            try {
                int totalBytesRead = 0;
                int bytesRead;
                int offset = 0;
                int bytesToRead = 0;
                do {
                    bytesToRead = audioInputStream.available();
                    bytesRead = audioInputStream.read( audioBytes, offset, bytesToRead );
                    offset += bytesRead;
                    totalBytesRead += bytesRead;
                } while( totalBytesRead < numBytes );
                audioInputStream.close();
            }
            catch( Exception ex ) {
                System.out.println( "Error loading audio url " + url );
            }
        }
        catch( Exception e ) {
            System.out.println( e + " Error reading audio url " + url );
        }
        bufsz = numBytes / 2;
        buf = new float[bufsz];
        FormatUtils.byteToFloat( buf, audioBytes, bufsz );
    }

}


