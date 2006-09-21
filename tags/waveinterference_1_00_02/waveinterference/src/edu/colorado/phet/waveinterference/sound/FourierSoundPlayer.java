/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.waveinterference.sound;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.io.IOException;


/**
 * FourierSoundPlayer handles playing the sound that
 * corresponds to a Fourier series.
 * <p/>
 * FourierOscillator produces the audio data, while FourierSoundPlayer
 * handles initialization of the Java sound system and data I/O.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierSoundPlayer implements Runnable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /*
     * A NOTE ABOUT AUDIO BUFFER SIZE:  In choosing a buffer size, you should
     * seek to minimize latency, minimize the risk of underflow, and avoid
     * unnecessary consumption of CPU resources.  Smaller buffer sizes result
     * in less latency, but increase the risk of underflow and increase the
     * use of CPU resources. Underflow manifests itself as pops and clicks,
     * caused by discontinuities in the audio output -- this occurs when
     * the output device is gobbling up audio data faster than you can
     * write it to the device.
     */

    // Size of the buffer used to transfer data from the input stream to the output device.
    private static final int TRANSFER_BUFFER_SIZE = 16000; // bytes

    // Size of the buffer internal to the output device.
    private static final int DEVICE_BUFFER_SIZE = 16000; // bytes

    // Audio format parameters
    private static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    private static final float SAMPLE_RATE = 44100.0F; // samples per second
    private static final int SAMPLE_SIZE = 16; // bits
    private static final int CHANNELS = 2; // 2==stereo
    private static final int BITS_PER_BYTE = 8;
    private static final int FRAME_SIZE = SAMPLE_SIZE * CHANNELS / BITS_PER_BYTE; // bytes
    private static final float FRAME_RATE = SAMPLE_RATE; // frames per second

    // Audio input stream parameters
    private static final float DEFAULT_VOLUME = 0.5F; // 0=off, 1=fully on
    private static final int STREAM_LENGTH = AudioSystem.NOT_SPECIFIED;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierOscillator _oscillator;  // audio data producer (input stream)
    private SourceDataLine _sourceDataLine; // audio data consumer (output device)
    private boolean _soundEnabled;
    private EventListenerList _listenerList;
    private Thread soundThread;
    private boolean active;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.  Creates a sound player that responds to changes
     * in a specified Fourier series.  Sound I/O is handled in a separate
     * thread, which is running only when sound is "on".
     * <p/>
     * //     * @param fourierSeries
     *
     * @throws LineUnavailableException if there is an error configuring the audio system
     */
    public FourierSoundPlayer() throws LineUnavailableException {

        _soundEnabled = false;
        _listenerList = new EventListenerList();

        // Set up the source data line.
        AudioFormat audioFormat = new AudioFormat( ENCODING, SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, FRAME_SIZE, FRAME_RATE, false );
        _oscillator = new FourierOscillator( DEFAULT_VOLUME, audioFormat, STREAM_LENGTH );
        DataLine.Info info = new DataLine.Info( SourceDataLine.class, audioFormat );
        _sourceDataLine = (SourceDataLine)AudioSystem.getLine( info );
        _sourceDataLine.open( audioFormat, DEVICE_BUFFER_SIZE );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Turns the sound thread on and off.
     * <p/>
     * //     * @param true or false
     */
    public void setSoundEnabled( boolean enabled ) {
        _soundEnabled = enabled; // false stops the sound thread
        _oscillator.setEnabled( enabled );

        //in the original way of doing this, after manually changing frequency, there would be 15+ threads
        //alive that were not yet exited from processing, and performance degraded substantially.
        if( enabled ) {
            if( soundThread == null ) {
                soundThread = new Thread( this );
                soundThread.start();
            }
            //I have it set to max because if my app is > 60% and thread has normal priority, the audio is choppy.

            _sourceDataLine.start();
//            soundThread.setPriority( Thread.MAX_PRIORITY );//todo thread priority.
        }
        else {
            _sourceDataLine.stop();
            _sourceDataLine.flush();
        }
    }

    /**
     * Determines whether sound is on or off.
     *
     * @return true or false
     */
    public boolean isEnabled() {
        return _soundEnabled && active;
    }

    /**
     * Sets the volume of the sound.  The volume is relative to
     * the volume that the user has set for the output device,
     * tpyically done in some platform-specific control panel.
     *
     * @param volume 0.0 (full off) to 1.0 (full on)
     */
    public void setVolume( float volume ) {
        _oscillator.setVolume( volume );
    }

    /**
     * Gets the current volume setting.
     *
     * @return volume, 0.0 (full off) to 1.0 (full on)
     */
    public float getVolume() {
        return _oscillator.getVolume();
    }

    //----------------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------------

    /**
     * This is the thread that reads from the Fourier oscillator and
     * writes to the output device. The thread exits when _soundEnabled
     * is set to false.  The thread is started/stopped in setSoundEnabled.
     * If an audio system error occurs, all registered SoundErrorListeners
     * are notified.
     */
    public void run() {
//        System.out.println( "FourierSoundPlayer.run begins" );//XXX
        byte[] buffer = new byte[TRANSFER_BUFFER_SIZE];
//        System.out.println( "TRANSFER_BUFFER_SIZE = " + TRANSFER_BUFFER_SIZE );
        while( true ) {
            if( _soundEnabled && active ) {
                try {
                    int nRead = _oscillator.read( buffer );
                    int nWritten = _sourceDataLine.write( buffer, 0, nRead );
//                System.out.println( "nWritten = " + nWritten );
                }
                catch( IOException ioe ) {
                    _soundEnabled = false; // cause the sound thread to exit
                    String message = SimStrings.get( "sound.error.io" );
                    notifySoundErrorListeners( ioe, message );
                }
            }
            else {
                try {
                    Thread.sleep( 30 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifySoundErrorListeners( IOException ioe, String message ) {
        System.out.println( "ioe = " + ioe );
        System.out.println( "message = " + message );
    }

    public void setFrequency( double value ) {
        _oscillator.setFrequency( value );
    }

    private double getFrequency() {
        return _oscillator.getFrequency();
    }

    public void setActive( boolean active ) {
        this.active = active;
    }

    public static void main( String[] args ) throws LineUnavailableException {
        JFrame control = new JFrame( "Test Audio" );
        JPanel contentPane = new JPanel();
        final FourierSoundPlayer fourierSoundPlayer = new FourierSoundPlayer();
        fourierSoundPlayer.setSoundEnabled( true );
        final ModelSlider modelSlider = new ModelSlider( "Frequency", "Hz", 0, 1200, fourierSoundPlayer.getFrequency() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fourierSoundPlayer.setFrequency( modelSlider.getValue() );
            }
        } );
        contentPane.add( modelSlider );
        control.setContentPane( contentPane );
        control.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        control.pack();
        control.setVisible( true );
        Thread stress = new Thread( new Runnable() {
            public void run() {
                while( true ) {
                    String str = "aaeou".toUpperCase();
                    if( str.equals( "hello" ) ) {
                        System.out.println( "str = " + str );
                    }
                }
            }
        } );
//        stress.setPriority( Thread.MAX_PRIORITY );
        stress.start();
    }

}
