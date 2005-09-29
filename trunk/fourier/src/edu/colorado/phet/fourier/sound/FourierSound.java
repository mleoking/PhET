/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.*;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.model.FourierSeries;


public class FourierSound extends AudioInputStream implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Audio format parameters
    private static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    private static final float SAMPLE_RATE = 44100.0F; // number of samples per second
    private static final int SAMPLE_SIZE = 16; // bits in each sample
    private static final int CHANNELS = 2; // number of audio channels
    private static final int FRAME_SIZE = 4; // number of bytes in each frame
    private static final float FRAME_RATE = SAMPLE_RATE; // number of frames played per second
    private static final boolean BIG_ENDIAN = false; // is the audio stored big-endian?
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat( ENCODING, SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, FRAME_SIZE, SAMPLE_RATE, BIG_ENDIAN );
    
    private static final float FREQUENCY = 1000.0F; // Hz
    private static final float AMPLITUDE = (float) ( 1.0F * Math.pow( 2, SAMPLE_SIZE - 1 ) );

    private static final int FRAMES_PER_PERIOD = Math.round( FRAME_RATE / FREQUENCY );
    private static final int BUFFER_LENGTH = FRAMES_PER_PERIOD * FRAME_SIZE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Clip _clip;
    private FourierSeries _fourierSeries;
    private byte[] _buffer;
    private int _bufferIndex;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FourierSound( FourierSeries fourierSeries ) {
        super( new ByteArrayInputStream( new byte[0] ), AUDIO_FORMAT, BUFFER_LENGTH );
        
        assert( fourierSeries != null );
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        _buffer = new byte[ BUFFER_LENGTH ];
        _bufferIndex = 0;
        
        fillBuffer();
        
        DataLine.Info info = new DataLine.Info( Clip.class, AUDIO_FORMAT );
        try {
            _clip = (Clip) AudioSystem.getLine( info );
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace(); //XXX
        }
    }
    
    /**
     * Call this method before releasing all references to 
     * an instance of this class.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // Playback control
    //----------------------------------------------------------------------------
    
    public void start() {
        try {
            _clip.open( this );
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace(); //XXX
        }
        catch ( IOException e1 ) {
            e1.printStackTrace(); //XXX
        }
        _clip.loop( Clip.LOOP_CONTINUOUSLY );
        _clip.start();
    }
    
    public void stop() {
        _clip.stop();
        _clip.close();
        _bufferIndex = 0;
    }
    
    //----------------------------------------------------------------------------
    // Data production
    //----------------------------------------------------------------------------
    
    /*
     * Fills the buffer with audio data that corresponds to the audio format
     * and current state of the Fourier series.
     */
    private void fillBuffer() {
        System.out.println( "FourierOscillator.fillBuffer with " + FRAMES_PER_PERIOD + " frames" );//XXX
        for ( int nFrame = 0; nFrame < FRAMES_PER_PERIOD; nFrame++ ) {
            
            // The relative position inside the period of the waveform. 0.0 = beginning, 1.0 = end
            float periodPosition = (float) nFrame / (float) FRAMES_PER_PERIOD;
            
            // XXX sine wave
            int value = Math.round( AMPLITUDE * (float) Math.sin( periodPosition * 2.0 * Math.PI ) );
            int baseAddr = nFrame * FRAME_SIZE;
            
            // This is for 16 bit stereo, little endian.
            assert ( BIG_ENDIAN == false );
            _buffer[baseAddr + 0] = (byte) ( value & 0xFF );
            _buffer[baseAddr + 1] = (byte) ( ( value >>> 8 ) & 0xFF );
            _buffer[baseAddr + 2] = (byte) ( value & 0xFF );
            _buffer[baseAddr + 3] = (byte) ( ( value >>> 8 ) & 0xFF );
        }
        _bufferIndex = 0;
        System.out.println( "fillBuffer done" );//XXX
    }

    //----------------------------------------------------------------------------
    // AudioInputStream overrides
    //----------------------------------------------------------------------------

    /** 
     * Returns the number of bytes that can be read without blocking.
     * Since there is no blocking possible here, we simply return the
     * number of bytes that haven't been read yet. 
     * 
     * @return the number of bytes available
     */
    public int available() {
        return ( _buffer.length - _bufferIndex );
    }

    /**
     * This method should throw an IOException if the frame size is not 1.
     * Since we currently always use 16 bit samples, the frame size is
     * always greater than 1. So we always throw an exception.
     */
    public int read() throws IOException {
        assert( FRAME_SIZE != 1 );
        throw new IOException( "frame size is not 1" );
    }

    /**
     * Reads up to a specified maximum number of bytes of data from the 
     * audio stream, putting them into the given byte array.
     * 
     * @param buffer
     * @param nOffset
     * @param nLength
     * @throws IOException if length is not an integer multiple of frame size
     */
    public int read( byte[] buffer, int nOffset, int nLength ) throws IOException {
        if ( nLength % FRAME_SIZE != 0 ) {
            throw new IOException( "length must be an integer multiple of frame size" );
        }
        int nReturn = -1;
        int nConstrainedLength = Math.min( available(), nLength );
        if ( nConstrainedLength > 0 ) {
            int nNumBytesToCopyNow = _buffer.length - _bufferIndex;
            nNumBytesToCopyNow = Math.min( nNumBytesToCopyNow, nConstrainedLength );
            System.arraycopy( _buffer, _bufferIndex, buffer, nOffset, nNumBytesToCopyNow );
            _bufferIndex += nNumBytesToCopyNow;
            return nConstrainedLength;
        }
        System.out.println( "FourierOscillator.read offset=" + nOffset + " length=" + nLength + " return=" + nReturn );//XXX
        return nReturn;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the data in the audio buffer when the Fourier series changes.
     */
    public void update() {
        fillBuffer();   
    }
}
