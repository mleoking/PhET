/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Reason why PhetAudioClip exists:
 * 
 * http://www.javaworld.com/javaworld/javatips/jw-javatip24.html
 */
public class PhetAudioClip {
    private static final int EXTERNAL_BUFFER_SIZE = 4000;

    private final URL url;
    
    private volatile boolean playing;

    public PhetAudioClip( String resourceName ) {
        this.url = Thread.currentThread().getContextClassLoader().getResource( resourceName );
    }

    public boolean isPlaying() {
        return playing;
    }

    public void play() {
        Thread playingThread = new Thread( new Runnable() {
            public void run() {
                try {
                    blockingPlay();
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );

        playingThread.setDaemon( true );

        playingThread.start();
    }

    private void blockingPlay() {
        playing = true;

        try {
            AudioInputStream audioInputStream;
            SourceDataLine line;

            try {
                audioInputStream = AudioSystem.getAudioInputStream( url.openStream() );

                AudioFileFormat aff = AudioSystem.getAudioFileFormat( url );

                AudioFormat audioFormat = aff.getFormat();

                DataLine.Info info = new DataLine.Info( SourceDataLine.class,
                                                        audioFormat );
                line = (SourceDataLine)AudioSystem.getLine( info );

                /*
                  The line is there, but it is not yet ready to
                  receive audio data. We have to open the line.
                */
                line.open( audioFormat );
            }
            catch( Exception e ) {
                e.printStackTrace();

                return;
            }

            /*
              Still not enough. The line now can receive data,
                  but will not pass them on to the audio output device
                  (which means to your sound card). This has to be
                  activated.
                */
            line.start();

            /*
              Ok, finally the line is prepared. Now comes the real
                  job: we have to write data to the line. We do this
                  in a loop. First, we read data from the
                  AudioInputStream to a buffer. Then, we write from
                  this buffer to the Line. This is done until the finish
                  of the file is reached, which is detected by a
                  return value of -1 from the read method of the
                  AudioInputStream.
                */
            int nBytesRead = 0;

            byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
            while( nBytesRead != -1 ) {
                try {
                    nBytesRead = audioInputStream.read( abData, 0, abData.length );
                }
                catch( IOException e ) {
                    break;
                }
                if( nBytesRead >= 0 ) {
                    line.write( abData, 0, nBytesRead );
                }
            }
            line.drain();
            line.close();
        }
        finally {
            playing = false;
        }
    }
}
