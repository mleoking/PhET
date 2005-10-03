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
 * FourierSoundPlayer
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierSoundPlayer implements Runnable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DATA_BUFFER_SIZE = 16000;
    private static final int OUTPUT_DEVICE_BUFFER_SIZE = 16000;
    
    private static final float SAMPLE_RATE = 44100.0F;
    private static final int SAMPLE_SIZE = 16; // bits
    private static final int CHANNELS = 2; // 2==stereo
    private static final int BITS_PER_BYTE = 8;
    private static final int FRAME_SIZE = SAMPLE_SIZE * CHANNELS / BITS_PER_BYTE; // bytes
    private static final float FRAME_RATE = SAMPLE_RATE;
    private static final  float FREQUENCY = 440.0F;
    private static final float AMPLITUDE = 0.7F;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierOscillator _oscillator;
    private SourceDataLine _sourceDataLine;
    private boolean _soundEnabled;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FourierSoundPlayer( FourierSeries fourierSeries ) throws LineUnavailableException {
        
        _soundEnabled = false;
        _listenerList = new EventListenerList();
        
        // Set up the source data line.
        AudioFormat audioFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, FRAME_SIZE, FRAME_RATE, false );
        _oscillator = new FourierOscillator( fourierSeries, FREQUENCY, AMPLITUDE, audioFormat, AudioSystem.NOT_SPECIFIED );
        DataLine.Info info = new DataLine.Info( SourceDataLine.class, audioFormat );
        _sourceDataLine = (SourceDataLine) AudioSystem.getLine( info );
        _sourceDataLine.open( audioFormat, OUTPUT_DEVICE_BUFFER_SIZE );
    }
    
    public void cleanup() {
        _oscillator.cleanup();
        _oscillator = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
    
    //----------------------------------------------------------------------------
    // SoundErrorEvent handling
    //----------------------------------------------------------------------------
    
    public void addSoundErrorListener( SoundErrorListener listener ) {
        _listenerList.add( SoundErrorListener.class, listener );
    }
    
    public void removeSoundErrorListener( SoundErrorListener listener ) {
        _listenerList.remove( SoundErrorListener.class, listener );
    }
    
    private void notifySoundErrorListeners( Exception e, String message ) {
        SoundErrorEvent event = new SoundErrorEvent( this, e, message );
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
    
    public void run() {
        System.out.println( "FourierSoundPlayer.run begins" );//XXX
        byte[] buffer = new byte[DATA_BUFFER_SIZE];
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
        System.out.println( "FourierSoundPlayer.run ends" );//XXX
    }
    
}
