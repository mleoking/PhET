package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

/**
 * Plays a rising pitch whose delay and final pitch is proportional to the value of the corresponding fraction.
 *
 * @author Sam Reid
 */
public class MatchingGameAudio {

    //I followed this example: http://www.java2s.com/Code/Java/Development-Class/AnexampleofloadingandplayingasoundusingaClip.htm
    public static void playAudioClip( final double value ) {//play a sound
        try {

            //Buffer it to avoid mark/reset not supported error
            //See http://stackoverflow.com/questions/5529754/java-io-ioexception-mark-reset-not-supported
            AudioInputStream sound = AudioSystem.getAudioInputStream( new BufferedInputStream( Thread.currentThread().getContextClassLoader().getResourceAsStream( "fractions/audio/pitchup.wav" ) ) );
            // load the sound into memory (a Clip)
            DataLine.Info info = new DataLine.Info( Clip.class, sound.getFormat() );
            final Clip clip = (Clip) AudioSystem.getLine( info );
            clip.open( sound );

            // play the sound clip
            clip.start();

            //Halt prematurely based on the value so the sound will go higher pitched (and longer) for higher values
            new Timer( 1000, new ActionListener() {
                @Override public void actionPerformed( ActionEvent e ) {
                    clip.stop();
                }
            } ) {{
                setRepeats( false );

                //Add some constant to the delay so low sounds don't end too fast
                final int delay = (int) ( 1000 * value ) + 200;
//                            System.out.println( "delay = " + delay );
                setInitialDelay( delay );
            }}.start();
//                        clip.stop();
        }
        catch ( UnsupportedAudioFileException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
        }
    }

}