// Copyright 2002-2011, University of Colorado
package jass.generators;

import jass.engine.UnsupportedAudioFileFormatException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.net.URL;

/**
 * A buffer from an audio file or URL, streamed off the source.
 * Assumes 16 bit audio format.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class StreamingAudioFileBuffer {
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
     * Length of buffer as floats, should be numBytes/2.
     */
    public int bufsz;

    // to get audio data
    public AudioInputStream audioInputStream = null;

    // temp buffer
    private byte[] audioBytes = null;

    /**
     * Construct buffer from named file.
     *
     * @param fn Audio file name.
     */
    public StreamingAudioFileBuffer( String fn ) throws UnsupportedAudioFileFormatException {
        loadAudio( fn );
    }

    /**
     * Construct buffer from  url.
     *
     * @param url Audiofile url.
     */
    public StreamingAudioFileBuffer( URL url ) throws UnsupportedAudioFileFormatException {
        loadAudio( url );
    }

    /**
     * Close stream
     */
    public void close() {
        try {
            audioInputStream.close();
        }
        catch( Exception e ) {
            System.out.println( e );
        }
        audioInputStream = null;
    }

    /**
     * Open and read audio file properties and prepare for streaming
     *
     * @param fn Audio file name.
     */
    protected void loadAudio( String fn ) throws UnsupportedAudioFileFormatException {
        File fileIn = new File( fn );
        try {
            audioInputStream = AudioSystem.getAudioInputStream( fileIn );
        }
        catch( Exception e ) {
            System.out.println( "Error reading audio file " + fn );
        }
        doRestOfLoad();
    }

    /**
     * Open and read audio URL properties and prepare for streaming
     *
     * @param url Audiofile url.
     */
    protected void loadAudio( URL url ) throws UnsupportedAudioFileFormatException {
        try {
            audioInputStream = AudioSystem.getAudioInputStream( url );
        }
        catch( Exception e ) {
            System.out.println( e + " Error reading audio url " + url );
        }
        doRestOfLoad();
    }

    private void doRestOfLoad() throws UnsupportedAudioFileFormatException {
        bytesPerFrame = audioInputStream.getFormat().getFrameSize();
        System.out.println( "marksupported=" + audioInputStream.markSupported() );
        bitsPerSample = audioInputStream.getFormat().getSampleSizeInBits();
        if( bitsPerSample != 16 ) {
            throw new UnsupportedAudioFileFormatException( "not 16 bits audio" );
        }
        nChannels = audioInputStream.getFormat().getChannels();
        if( nChannels != 1 ) {
            throw new UnsupportedAudioFileFormatException( "not 1 channel audio" );
        }
        srate = (float)audioInputStream.getFormat().getSampleRate();
        nFrames = audioInputStream.getFrameLength();
        numBytes = (int)nFrames * bytesPerFrame;
        bufsz = numBytes / 2;
        int readLimit = numBytes;
        audioInputStream.mark( readLimit );
    }

}


