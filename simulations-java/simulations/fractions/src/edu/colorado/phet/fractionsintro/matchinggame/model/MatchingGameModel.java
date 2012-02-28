// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

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

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;

/**
 * The model contains a sequence of immutable states.
 *
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final Property<MatchingGameState> state = new Property<MatchingGameState>( initialState() ) {{
        addObserver( new ChangeObserver<MatchingGameState>() {
            @Override public void update( final MatchingGameState newValue, final MatchingGameState oldValue ) {
                if ( oldValue.getLeftScaleValue() == 0.0 && newValue.getLeftScaleValue() > 0 ) {
                    playAudioClip( newValue.getLeftScaleValue() );
                }
                if ( oldValue.getRightScaleValue() == 0.0 && newValue.getRightScaleValue() > 0 ) {
                    playAudioClip( newValue.getRightScaleValue() );
                }
            }


            //I followed this example: http://www.java2s.com/Code/Java/Development-Class/AnexampleofloadingandplayingasoundusingaClip.htm
            private void playAudioClip( final double value ) {//play a sound
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
                        final int delay = (int) ( 1000 * value );
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
        } );
    }};
    public Clock clock = new ConstantDtClock( 60 ) {{
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                state.set( state.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
            }
        } );
    }};
}