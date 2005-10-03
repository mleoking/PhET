/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

/*
 * Portions of this code (as noted in the Javadoc):
 * 
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.colorado.phet.fourier.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;

/**
 * FourierOscillator produces the data stream that represents a Fourier series.
 * <p>
 * The implementation of this class is based on the Oscillator.java example
 * available at www.jsresources.org.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierOscillator extends AudioInputStream implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    
    // The audio data buffer
    private byte[] _buffer;
    private int _bufferLength;
    private int _bufferIndex;
    
    private long _remainingFrames;
    private float _signalFrequency;
    private float _amplitude;
    private long _streamLength;
    private int _periodLengthInFrames;

    private boolean _enabled;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param fourierSeries
     * @param signalFrequency
     * @param amplitude
     * @param audioFormat
     * @param streamLength
     */
    public FourierOscillator( FourierSeries fourierSeries, float signalFrequency, float amplitude, AudioFormat audioFormat, long streamLength ) {
        super( new ByteArrayInputStream( new byte[0] ), audioFormat, streamLength );

        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        _signalFrequency = signalFrequency;
        _amplitude = amplitude;
        _streamLength = streamLength;

        _periodLengthInFrames = Math.round( getFormat().getFrameRate() / _signalFrequency );
        _bufferLength = _periodLengthInFrames * getFormat().getFrameSize();
        _buffer = new byte[_bufferLength];
        
        generateData();
        
        _remainingFrames = _streamLength;
        _bufferIndex = 0;
        _enabled = true;
    }
    
    /**
     * Call this method before releasing all references to an instance of this object.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables and disabled the oscillator.
     * While disabled, the oscillator does not respond 
     * to changes in the Fourier series.
     * 
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
        if ( enabled ) {
            update();
        }
    }

    //----------------------------------------------------------------------------
    // Audio data generation
    //----------------------------------------------------------------------------
    
    /*
     * Generates the audio data that correspond to one period of the Fourier
     * series. The data is written to a local buffer, then copies to the read
     * buffer.  This minimizes the amount of snychronization needed, 
     * and the amount of time that read calls are blocked.
     */
    private void generateData() {
        System.out.println( "FourierOscillator.generateData" );//XXX
        float amplitude = (float) ( _amplitude * Math.pow( 2, getFormat().getSampleSizeInBits() - 1 ) );
        byte[] localBuffer = new byte[_bufferLength];
        for ( int frame = 0; frame < _periodLengthInFrames; frame++ ) {
            // The relative position inside the period of the waveform. 0.0 = beginning, 1.0 = end
            float periodPosition = (float) frame / (float) _periodLengthInFrames;
            float fValue = getFourierSum( periodPosition );
            int nValue = Math.round( fValue * amplitude );
            int baseAddr = ( frame ) * getFormat().getFrameSize();
            // this is for 16 bit stereo, little endian
            localBuffer[baseAddr + 0] = (byte) ( nValue & 0xFF );
            localBuffer[baseAddr + 1] = (byte) ( ( nValue >>> 8 ) & 0xFF );
            localBuffer[baseAddr + 2] = (byte) ( nValue & 0xFF );
            localBuffer[baseAddr + 3] = (byte) ( ( nValue >>> 8 ) & 0xFF );
        }
        setData( localBuffer );
    }

    /*
     * Gets the value of the Fourier sum at a specific position 
     * in the Fourier series' cycle.  The position is a value 
     * between 0.0 (beginning) and 1.0 (end) inclusive.
     * 
     * @param fPeriodPosition
     * @return the Fourier sum at the specified position
     */
    private float getFourierSum( float periodPosition ) {
        float sum = 0;
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            Harmonic harmonic = _fourierSeries.getHarmonic( i );
            double amplitude = harmonic.getAmplitude();
            if ( amplitude != 0 ) {
                double radiansPerPeriod = ( i + 1 ) * 2.0 * Math.PI;
                double angle = periodPosition * radiansPerPeriod;
                sum += (float) ( amplitude * Math.sin( angle ) );
            }
        }
        return sum;
    }
    
    /*
     * Copies audio data into the read buffer.
     *  
     * @param audioData the data to copy into the read buffer
     */
    private synchronized void setData( byte[] audioData ) {
        System.arraycopy( audioData, 0, _buffer, 0, _bufferLength );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the audio data when the Fourier series is changed.
     * The data is not updated while the oscillator is disabled.
     */
    public void update() {
        if ( _enabled ) {
            generateData();
        }
    }
    
    //----------------------------------------------------------------------------
    // AudioInputStream overrides
    //----------------------------------------------------------------------------
    
    /**
     * Returns the number of bytes that can be read without blocking.
     * Since there is no blocking possible here, we simply try to
     * return the number of bytes available at all. In case the
     * length of the stream is indefinite, we return the highest
     * number that can be represented in an integer. If the length
     * if finite, this length is returned, clipped by the maximum
     * that can be represented.
     * <p>
     * This method was copied verbatim from jsresource.org's Oscillator.
     * 
     * @return the number of bytes available
     */
    public int available() {
        int nAvailable = 0;
        if ( _remainingFrames == AudioSystem.NOT_SPECIFIED ) {
            nAvailable = Integer.MAX_VALUE;
        }
        else {
            long lBytesAvailable = _remainingFrames * getFormat().getFrameSize();
            nAvailable = (int) Math.min( lBytesAvailable, (long) Integer.MAX_VALUE );
        }
        return nAvailable;
    }

    /**
     * According to the Javadoc for AudioInputStream, this method should 
     * throw an IOException if the frame size is not 1.  Since we currently 
     * always use 16 bit samples, the frame size is always greater than 1.
     * So we always throw an exception.
     * <p>
     * This method was copied verbatim from jsresource.org's Oscillator.
     * 
     * @return never
     * @throws IOException always
     */
    public int read() throws IOException {
        throw new IOException( "not implemented, see javadoc" );
    }

    /**
     * Reads audio data from the "read buffer".  The read buffer contains audio
     * data for one period of the Fourier series.  When we reach the end of the
     * read buffer, we simply wrap around to the beginning.  This presents the
     * single period of audio data as a continuous stream.
     * <p>
     * This method was copied verbatim from jsresource.org's Oscillator.
     * 
     * @param abData buffer into which the data is written
     * @param nOffset offset into the buffer where we'll start writing data
     * @param nLength maximum number of bytes to read
     * @throws IOException if nLength is not an integer multiple of the frame size
     */
    public synchronized int read( byte[] abData, int nOffset, int nLength ) throws IOException {
        if ( nLength % getFormat().getFrameSize() != 0 ) {
            throw new IOException( "length must be an integer multiple of frame size" );
        }
        int nConstrainedLength = Math.min( available(), nLength );
        int nRemainingLength = nConstrainedLength;
        while ( nRemainingLength > 0 ) {
            int nNumBytesToCopyNow = _buffer.length - _bufferIndex;
            nNumBytesToCopyNow = Math.min( nNumBytesToCopyNow, nRemainingLength );
            System.arraycopy( _buffer, _bufferIndex, abData, nOffset, nNumBytesToCopyNow );
            nRemainingLength -= nNumBytesToCopyNow;
            nOffset += nNumBytesToCopyNow;
            _bufferIndex = ( _bufferIndex + nNumBytesToCopyNow ) % _buffer.length;
        }
        int nFramesRead = nConstrainedLength / getFormat().getFrameSize();
        if ( _remainingFrames != AudioSystem.NOT_SPECIFIED ) {
            _remainingFrames -= nFramesRead;
        }
        int nReturn = nConstrainedLength;
        if ( _remainingFrames == 0 ) {
            nReturn = -1;
        }
        return nReturn;
    }
}
