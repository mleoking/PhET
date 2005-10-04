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

import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.event.SoundErrorEvent;
import edu.colorado.phet.fourier.event.SoundErrorListener;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * FourierSoundPlayer handles playing the sound that 
 * corresponds to a Fourier series.
 * <p>
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
    private static final float FREQUENCY = 440.0F; // Hz
    private static final float DEFAULT_VOLUME = 0.5F; // 0=off, 1=fully on
    private static final int STREAM_LENGTH = AudioSystem.NOT_SPECIFIED;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierOscillator _oscillator;  // audio data producer (input stream)
    private SourceDataLine _sourceDataLine; // audio data consumer (output device)
    private boolean _soundEnabled;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.  Creates a sound player that responds to changes
     * in a specified Fourier series.  Sound I/O is handled in a separate 
     * thread, which is running only when sound is "on".
     * 
     * @param fourierSeries
     */
    public FourierSoundPlayer( FourierSeries fourierSeries ) throws LineUnavailableException {
        
        _soundEnabled = false;
        _listenerList = new EventListenerList();
        
        // Set up the source data line.
        AudioFormat audioFormat = new AudioFormat( ENCODING, SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, FRAME_SIZE, FRAME_RATE, false );
        _oscillator = new FourierOscillator( fourierSeries, FREQUENCY, DEFAULT_VOLUME, audioFormat, STREAM_LENGTH );
        DataLine.Info info = new DataLine.Info( SourceDataLine.class, audioFormat );
        _sourceDataLine = (SourceDataLine) AudioSystem.getLine( info );
        _sourceDataLine.open( audioFormat, DEVICE_BUFFER_SIZE );
    }
    
    /**
     * Call this method before releasing all references to an instance of this object.
     */
    public void cleanup() {
        _oscillator.cleanup();
        _oscillator = null;
        _listenerList = new EventListenerList(); // lazy way to clear the list
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Turns the sound thread on and off.
     * 
     * @param true or false
     */
    public void setSoundEnabled( boolean enabled ) {
        _soundEnabled = enabled; // false stops the sound thread
        if ( enabled ) {
            Thread soundThread = new Thread( this );
            soundThread.start();
            _sourceDataLine.start();
        }
        else { 
            _sourceDataLine.stop();
            _sourceDataLine.flush();
        }
        _oscillator.setEnabled( enabled );
    }
    
    /**
     * Determines whether sound is on or off.
     * 
     * @return true or false
     */
    public boolean isEnabled() {
        return _oscillator.isEnabled();
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
    // SoundErrorEvent handling
    //----------------------------------------------------------------------------
    
    /**
     * Adds a SoundErrorListener.
     * 
     * @param listener
     */
    public void addSoundErrorListener( SoundErrorListener listener ) {
        _listenerList.add( SoundErrorListener.class, listener );
    }
    
    /**
     * Removes a SoundErrorListener.
     * 
     * @param listener
     */
    public void removeSoundErrorListener( SoundErrorListener listener ) {
        _listenerList.remove( SoundErrorListener.class, listener );
    }
    
    /**
     * Notifies all SoundErrorListeners that a sound error has occurred.
     * 
     * @param exception
     * @param message
     */
    private void notifySoundErrorListeners( Exception exception, String message ) {
        SoundErrorEvent event = new SoundErrorEvent( this, exception, message );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == SoundErrorListener.class ) {
                ((SoundErrorListener) listeners[ i + 1 ] ).soundErrorOccurred( event );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------------
    
    /**
     * Reads from the Fourier oscillator and writes to the output device.
     * The thread exits when _soundEnabled is set to false;
     */
    public void run() {
//        System.out.println( "FourierSoundPlayer.run begins" );//XXX
        byte[] buffer = new byte[TRANSFER_BUFFER_SIZE];
        while ( _soundEnabled ) {
            try {
                int nRead = _oscillator.read( buffer );
                int nWritten = _sourceDataLine.write( buffer, 0, nRead );
            }
            catch ( IOException ioe ) {
                _soundEnabled = false; // cause the sound thread to exit
                String message = SimStrings.get( "sound.error.io" );
                notifySoundErrorListeners( ioe, message );
            }
        }
//        System.out.println( "FourierSoundPlayer.run ends" );//XXX
    }
}
