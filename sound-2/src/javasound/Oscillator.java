/*
 *	Oscillator.java
 *
 *	This file is part of the Java Sound Examples.
 */

/*
 *  Copyright (c) 1999 - 2001 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package javasound;

import	java.io.ByteArrayInputStream;
import	java.io.IOException;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.LineUnavailableException;
import	javax.sound.sampled.SourceDataLine;



public class Oscillator
	extends AudioInputStream
{
	private static final boolean	DEBUG = false;

	public static final int		WAVEFORM_SINE = 0;
	public static final int		WAVEFORM_SQUARE = 1;
	public static final int		WAVEFORM_TRIANGLE = 2;
	public static final int		WAVEFORM_SAWTOOTH = 3;

	private byte[]			m_abData;
	private int			m_nBufferPosition;
	private long			m_lRemainingFrames;


	public Oscillator(int nWaveformType,
			  float fSignalFrequency,
			  float fAmplitude,
			  AudioFormat audioFormat,
			  long lLength)
	{
		super(new ByteArrayInputStream(new byte[0]),
		      new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				      audioFormat.getSampleRate(),
				      16,
				      2,
				      4,
				      audioFormat.getFrameRate(),
				      audioFormat.isBigEndian()),
		      lLength);
        setParams(  nWaveformType,
			  fSignalFrequency,
			  fAmplitude,
			  audioFormat,
			  lLength);
    }

    public synchronized void setParams( int nWaveformType,
			  float fSignalFrequency,
			  float fAmplitude,
			  AudioFormat audioFormat,
			  long lLength)
	{
		if (DEBUG) { System.out.println("Oscillator.<initLayout>(): begin"); }
		m_lRemainingFrames = lLength;
		fAmplitude = (float) (fAmplitude * Math.pow(2, getFormat().getSampleSizeInBits() - 1));

        int nPeriodLengthInFrames = Math.round(audioFormat.getFrameRate() / fSignalFrequency);
		int nBufferLength = nPeriodLengthInFrames * getFormat().getFrameSize();


        try {
            m_abData = new byte[nBufferLength];
        } catch( Exception e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }


        for (int nFrame = 0; nFrame < nPeriodLengthInFrames; nFrame++)
		{
			/**	The relative position inside the period
				of the waveform. 0.0 = beginning, 1.0 = end
			*/
			float	fPeriodPosition = (float) nFrame / (float) nPeriodLengthInFrames;
			float	fValue = 0;
			switch (nWaveformType)
			{
			case WAVEFORM_SINE:
				fValue = (float) Math.sin(fPeriodPosition * 2.0 * Math.PI);
				break;

			case WAVEFORM_SQUARE:
				fValue = (fPeriodPosition < 0.5F) ? 1.0F : -1.0F;
				break;

			case WAVEFORM_TRIANGLE:
				if (fPeriodPosition < 0.25F)
				{
					fValue = 4.0F * fPeriodPosition;
				}
				else if (fPeriodPosition < 0.75F)
				{
					fValue = -4.0F * (fPeriodPosition - 0.5F);
				}
				else
				{
					fValue = 4.0F * (fPeriodPosition - 1.0F);
				}
				break;

			case WAVEFORM_SAWTOOTH:
				if (fPeriodPosition < 0.5F)
				{
					fValue = 2.0F * fPeriodPosition;
				}
				else
				{
					fValue = 2.0F * (fPeriodPosition - 1.0F);
				}
				break;
			}
			int	nValue = Math.round(fValue * fAmplitude);
			int nBaseAddr = (nFrame) * getFormat().getFrameSize();
			// this is for 16 bit stereo, little endian
			m_abData[nBaseAddr + 0] = (byte) (nValue & 0xFF);
			m_abData[nBaseAddr + 1] = (byte) ((nValue >>> 8) & 0xFF);
			m_abData[nBaseAddr + 2] = (byte) (nValue & 0xFF);
			m_abData[nBaseAddr + 3] = (byte) ((nValue >>> 8) & 0xFF);
		}
		m_nBufferPosition = 0;
		if (DEBUG) { System.out.println("Oscillator.<initLayout>(): end"); }
	}


	/**	Returns the number of bytes that can be read without blocking.
		Since there is no blocking possible here, we simply try to
		return the number of bytes available at all. In case the
		length of the stream is indefinite, we return the highest
		number that can be represented in an integer. If the length
		if finite, this length is returned, clipped by the maximum
		that can be represented.
	*/
	public int available()
	{
		int	nAvailable = 0;
		if (m_lRemainingFrames == AudioSystem.NOT_SPECIFIED)
		{
			nAvailable = Integer.MAX_VALUE;
		}
		else
		{
			long	lBytesAvailable = m_lRemainingFrames * getFormat().getFrameSize();
			nAvailable = (int) Math.min(lBytesAvailable, (long) Integer.MAX_VALUE);
		}
		return nAvailable;
	}



	/*
	  this method should throw an IOException if the frame size is not 1.
	  Since we currently always use 16 bit samples, the frame size is
	  always greater than 1. So we always throw an exception.
	*/
	public int read()
		throws IOException
	{
		if (DEBUG) { System.out.println("Oscillator.read(): begin"); }
		throw new IOException("cannot use this method currently");
	}



	public synchronized int read(byte[] abData, int nOffset, int nLength)
		throws IOException
	{
		if (DEBUG) { System.out.println("Oscillator.read(): begin"); }
		if (nLength % getFormat().getFrameSize() != 0)
		{
			throw new IOException("length must be an integer multiple of frame size");
		}
		int	nConstrainedLength = Math.min(available(), nLength);
		int	nRemainingLength = nConstrainedLength;
		while (nRemainingLength > 0)
		{
			int	nNumBytesToCopyNow = m_abData.length - m_nBufferPosition;
			nNumBytesToCopyNow = Math.min(nNumBytesToCopyNow, nRemainingLength);
			System.arraycopy(m_abData, m_nBufferPosition, abData, nOffset, nNumBytesToCopyNow);
			nRemainingLength -= nNumBytesToCopyNow;
			nOffset += nNumBytesToCopyNow;

            if( m_abData.length == 0 ) {
                System.out.println( "FOO" );
            }
			m_nBufferPosition = (m_nBufferPosition + nNumBytesToCopyNow) % m_abData.length;
		}
		int	nFramesRead = nConstrainedLength / getFormat().getFrameSize();
		if (m_lRemainingFrames != AudioSystem.NOT_SPECIFIED)
		{
			m_lRemainingFrames -= nFramesRead;
		}
		int	nReturn = nConstrainedLength;
		if (m_lRemainingFrames == 0)
		{
			nReturn = -1;
		}
		if (DEBUG) { System.out.println("Oscillator.read(): end"); }
		return nReturn;
	}
}



/*** Oscillator.java ***/
