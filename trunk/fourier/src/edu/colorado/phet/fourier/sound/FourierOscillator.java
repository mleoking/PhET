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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;


public class FourierOscillator extends AudioInputStream implements SimpleObserver {

    private static final boolean DEBUG = true;

    private FourierSeries _fourierSeries;
    private byte[] m_abData;
    private int m_nBufferPosition;
    private long m_lRemainingFrames;
    private float m_fSignalFrequency;
    private float m_fAmplitude;
    private long m_lLength;
    private int m_nPeriodLengthInFrames;
    private int m_nBufferLength;
    private boolean _enabled;

    public FourierOscillator( FourierSeries fourierSeries, float fSignalFrequency, float fAmplitude, AudioFormat audioFormat, long lLength ) {
        super( new ByteArrayInputStream( new byte[0] ), new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, 2, 4, audioFormat.getFrameRate(), audioFormat.isBigEndian() ), lLength );

        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        m_fSignalFrequency = fSignalFrequency;
        m_fAmplitude = fAmplitude;
        m_lLength = lLength;

        m_nPeriodLengthInFrames = Math.round( getFormat().getFrameRate() / m_fSignalFrequency );
        m_nBufferLength = m_nPeriodLengthInFrames * getFormat().getFrameSize();
        m_abData = new byte[m_nBufferLength];
        
        generateData();
        
        m_lRemainingFrames = m_lLength;
        m_nBufferPosition = 0;
        _enabled = true;
    }
    
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    public void update() {
        if ( _enabled ) {
            debug( "FourierOscillator.update" );
            generateData();
        }
    }
    
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
        if ( enabled ) {
            update();
        }
    }
    
    private void generateData() {
        debug( "FourierOscillator.generateData" );
        float fAmplitude = (float) ( m_fAmplitude * Math.pow( 2, getFormat().getSampleSizeInBits() - 1 ) );
        byte[] abData = new byte[m_nBufferLength];
        for ( int nFrame = 0; nFrame < m_nPeriodLengthInFrames; nFrame++ ) {
            // The relative position inside the period of the waveform. 0.0 = beginning, 1.0 = end
            float fPeriodPosition = (float) nFrame / (float) m_nPeriodLengthInFrames;
            float fValue = getFourierSum( fPeriodPosition );
            int nValue = Math.round( fValue * fAmplitude );
            int nBaseAddr = ( nFrame ) * getFormat().getFrameSize();
            // this is for 16 bit stereo, little endian
            abData[nBaseAddr + 0] = (byte) ( nValue & 0xFF );
            abData[nBaseAddr + 1] = (byte) ( ( nValue >>> 8 ) & 0xFF );
            abData[nBaseAddr + 2] = (byte) ( nValue & 0xFF );
            abData[nBaseAddr + 3] = (byte) ( ( nValue >>> 8 ) & 0xFF );
        }
        setData( abData );
    }

    private float getFourierSum( float fPeriodPosition ) {
        float fSum = 0;
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            Harmonic harmonic = _fourierSeries.getHarmonic( i );
            double amplitude = harmonic.getAmplitude();
            if ( amplitude != 0 ) {
                double radiansPerPeriod = ( i + 1 ) * 2.0 * Math.PI;
                double angle = fPeriodPosition * radiansPerPeriod;
                fSum += (float) ( amplitude * Math.sin( angle ) );
            }
        }
        return fSum;
    }
    
    private synchronized void setData( byte[] abData ) {
        System.arraycopy( abData, 0, m_abData, 0, m_nBufferLength );
    }
    
    /**     Returns the number of bytes that can be read without blocking.
     Since there is no blocking possible here, we simply try to
     return the number of bytes available at all. In case the
     length of the stream is indefinite, we return the highest
     number that can be represented in an integer. If the length
     if finite, this length is returned, clipped by the maximum
     that can be represented.
     */
    public int available() {
        int nAvailable = 0;
        if ( m_lRemainingFrames == AudioSystem.NOT_SPECIFIED ) {
            nAvailable = Integer.MAX_VALUE;
        }
        else {
            long lBytesAvailable = m_lRemainingFrames * getFormat().getFrameSize();
            nAvailable = (int) Math.min( lBytesAvailable, (long) Integer.MAX_VALUE );
        }
        return nAvailable;
    }


    /*
     this method should throw an IOException if the frame size is not 1.
     Since we currently always use 16 bit samples, the frame size is
     always greater than 1. So we always throw an exception.
     */
    public int read() throws IOException {
        throw new IOException( "cannot use this method currently" );
    }


    public synchronized int read( byte[] abData, int nOffset, int nLength ) throws IOException {
        if ( nLength % getFormat().getFrameSize() != 0 ) {
            throw new IOException( "length must be an integer multiple of frame size" );
        }
        int nConstrainedLength = Math.min( available(), nLength );
        int nRemainingLength = nConstrainedLength;
        while ( nRemainingLength > 0 ) {
            int nNumBytesToCopyNow = m_abData.length - m_nBufferPosition;
            nNumBytesToCopyNow = Math.min( nNumBytesToCopyNow, nRemainingLength );
            System.arraycopy( m_abData, m_nBufferPosition, abData, nOffset, nNumBytesToCopyNow );
            nRemainingLength -= nNumBytesToCopyNow;
            nOffset += nNumBytesToCopyNow;
            m_nBufferPosition = ( m_nBufferPosition + nNumBytesToCopyNow ) % m_abData.length;
        }
        int nFramesRead = nConstrainedLength / getFormat().getFrameSize();
        if ( m_lRemainingFrames != AudioSystem.NOT_SPECIFIED ) {
            m_lRemainingFrames -= nFramesRead;
        }
        int nReturn = nConstrainedLength;
        if ( m_lRemainingFrames == 0 ) {
            nReturn = -1;
        }
        return nReturn;
    }


    private static void debug( String strMessage ) {
        if ( DEBUG ) {
            System.out.println( strMessage );
        }
    }
}


/*** Oscillator.java ***/
