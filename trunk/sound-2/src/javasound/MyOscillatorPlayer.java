/*
 *	OscillatorPlayer.java
 *
 *	This file is part of the Java Sound Examples.
 */

/*
 *  Copyright (c) 1999 -2001 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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
 *
 */
package javasound;

import java.io.IOException;
import java.util.Observer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


/*	+DocBookXML
	<title>Playing waveforms</title>

	<formalpara><title>Purpose</title>
	<para>
	Plays waveforms (sine, square, ...).
	</para></formalpara>

	<formalpara><title>Level</title>
	<para>Advanced</para></formalpara>

	<formalpara><title>Usage</title>
	<para>
	<cmdsynopsis>
	<command>java OscillatorPlayer</command>
	<arg><option>-t <replaceable>waveformtype</replaceable></option></arg>
	<arg><option>-f <replaceable>signalfrequency</replaceable></option></arg>
	<arg><option>-r <replaceable>samplerate</replaceable></option></arg>
	<arg><option>-a <replaceable>amplitude</replaceable></option></arg>
	</cmdsynopsis>
	</para>
	</formalpara>

	<formalpara><title>Parameters</title>
	<variablelist>
	<varlistentry>
	<term><option>-t <replaceable>waveformtype</replaceable></option></term>
	<listitem><para>the waveform to play. One of sine, sqaure, triangle and sawtooth. Default: sine.</para></listitem>
	</varlistentry>
	<varlistentry>
	<term><option>-f <replaceable>signalfrequency</replaceable></option></term>
	<listitem><para>the frequency of the signal to create. Default: 1000 Hz.</para></listitem>
	</varlistentry>
	<varlistentry>
	<term><option>-r <replaceable>samplerate</replaceable></option></term>
	<listitem><para>the sample rate to use. Default: 44.1 kHz.</para></listitem>
	</varlistentry>
	<varlistentry>
	<term><option>-a <replaceable>amplitude</replaceable></option></term>
	<listitem><para>the amplitude of the generated signal. May range from 0.0 to 1.0. 1.0 means a full-scale wave. Default: 0.7.</para></listitem>
	</varlistentry>
	</variablelist>
	</formalpara>

	<formalpara><title>Bugs, limitations</title>
	<para>
	Full-scale waves can lead to clipping. It is currently not known
	which component is responsible for this.
	</para></formalpara>

	<formalpara><title>Source code</title>
	<para>
	<ulink url="OscillatorPlayer.java.html">OscillatorPlayer.java</ulink>,
	<ulink url="Oscillator.java.html">Oscillator.java</ulink>,
	<ulink url="http://www.urbanophile.com/arenn/hacking/download.html">gnu.getopt.Getopt</ulink>
	</para></formalpara>

-DocBookXML
*/

public class MyOscillatorPlayer extends Thread {

//    private static final int BUFFER_SIZE = 128000;
//    private static final int BUFFER_SIZE = 16000;
    private static final int BUFFER_SIZE = 32000;
    private static boolean DEBUG = false;
    private Oscillator oscillator;
    private float fAmplitude;
    private float fSignalFrequency;
    private float fSampleRate;
    private SourceDataLine line;
    private Object lineMonitor = new Object();
    private AudioFormat audioFormat;

    private boolean enabled = false;

    public MyOscillatorPlayer() {

        fSampleRate = 44100.0F;
//        fSampleRate = 22050.0F;
        fSignalFrequency = 1.0F; // Oscillator class doesn't like a frequency of 0;
        fAmplitude = 0.0F;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    /**
     * Runs the oscillator player
     */
    public void run() {

        // TODO: make the priority setable from the outside
        this.setPriority( Thread.MAX_PRIORITY );

        try {
            setup( (float)fSignalFrequency, (float)fAmplitude, (float)fSampleRate );
            byte[] abData;
            while( true ) {

                if( enabled ) {
                    synchronized( lineMonitor ) {
                        if( line != null ) {

                            // Note: we must allocate a new buffer every time. I tried reusing the buffer and
                            // had all sorts of problems
                            abData = new byte[BUFFER_SIZE];
                            if( DEBUG ) {
                                out( "OscillatorPlayer.main(): trying to read (bytes): " + abData.length );
                            }

                            // Here is where we get the bytes that are to be played. It looks like we get somewhere
                            // between 1/2 and 1 sec of sound at a time.
                            int nRead = oscillator.read( abData );
                            if( DEBUG ) {
                                out( "OscillatorPlayer.main(): in loop, read (bytes): " + nRead );
                            }

                            int nWritten = line.write( abData, 0, nRead );
                            if( DEBUG ) {
                                out( "OscillatorPlayer.main(): written: " + nWritten );
                            }
                        }
                    }
                }

                try {
                    Thread.sleep( /* 50 */ 100 );
                } catch( InterruptedException e ) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
            }

        } catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    private static void out( String strMessage ) {
        System.out.println( strMessage );
    }

    public synchronized float getFrequency() {
        return fSignalFrequency;
    }

    public synchronized void setFrequency( float frequency ) {

        // Oscillator chokes if given a frequency of 0
        frequency = Math.max( frequency, 1 );

        fSignalFrequency = (float)frequency;
        try {
            this.setup( (float)fSignalFrequency, (float)fAmplitude, (float)fSampleRate );
        } catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public synchronized float getAmplitude() {
        return fAmplitude;
    }

    public synchronized void setAmplitude( float amplitude ) {

        // Oscillator clips if you give it an amplitude of 1
        amplitude = (float)Math.min( amplitude, 0.95 );
        fAmplitude = (float)amplitude;
        try {
            this.setup( (float)fSignalFrequency, (float)fAmplitude, (float)fSampleRate );
        } catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    /**
     * Creates an oscillator and sets it up for frequency and amplitude.
     * @param frequency
     * @param amplitude
     * @param samplerate
     * @throws IOException
     */
    public synchronized void setup( float frequency, float amplitude, float samplerate )
            throws IOException {

        audioFormat = null;
        int nWaveformType = Oscillator.WAVEFORM_SINE;

        if( audioFormat == null ) {
            try {
                audioFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
                                               fSampleRate, 16, 2, 4, fSampleRate, false );
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }

        if( true ) {
//        if( oscillator == null ) {
            oscillator = new Oscillator(
                    nWaveformType,
                    fSignalFrequency,
                    fAmplitude,
                    audioFormat,
                    AudioSystem.NOT_SPECIFIED );
        } else {
            oscillator.setParams(
                    nWaveformType,
                    fSignalFrequency,
                    fAmplitude,
                    audioFormat,
                    AudioSystem.NOT_SPECIFIED );
        }

        synchronized( lineMonitor ) {
        if( line != null ) {
            line.stop();
        }

        line = null;
        DataLine.Info info = new DataLine.Info(
                SourceDataLine.class,
                audioFormat );

        try {
            line = (SourceDataLine)AudioSystem.getLine( info );
            line.open( audioFormat );
        } catch( LineUnavailableException e ) {
            e.printStackTrace();
        } catch( Exception e ) {
            e.printStackTrace();
        }
        line.start();
    }
    }

}