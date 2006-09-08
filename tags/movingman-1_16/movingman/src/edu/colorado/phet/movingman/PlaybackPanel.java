/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.model.TimeListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 6, 2004
 * Time: 3:21:19 PM
 * Copyright (c) Nov 6, 2004 by Sam Reid
 */
public class PlaybackPanel extends JPanel {
    private JButton play;
    private JButton pause;
    private JButton rewind;
    private JButton slowMotion;
    private JButton clear;
    private MovingManModule module;

    public PlaybackPanel( final MovingManModule module ) throws IOException {
        this.module = module;
        ImageIcon pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );

        pause = new JButton( SimStrings.get( "PlaybackPanel.PauseButton" ), pauseIcon );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //pausing from playback leaves it alone
                module.setPaused( true );
            }
        } );
        ImageIcon playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
        play = new JButton( SimStrings.get( "PlaybackPanel.PlaybackButton" ), playIcon );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.startPlaybackMode( 1.0 );
            }
        } );

        ImageIcon rewindIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Rewind24.gif" ) );
        rewind = new JButton( SimStrings.get( "PlaybackPanel.RewindButton" ), rewindIcon );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.rewind();
                module.setPaused( true );
            }
        } );

        ImageIcon slowIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/StepForward24.gif" ) );
        slowMotion = new JButton( SimStrings.get( "PlaybackPanel.ShowPlaybackButton" ), slowIcon );
        slowMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.startPlaybackMode( .4 );
            }
        } );

        clear = new JButton( SimStrings.get( "PlaybackPanel.Clear" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.confirmAndApplyReset();
            }
        } );

        add( play );
        add( slowMotion );
        add( pause );
        add( rewind );
        add( clear );

        JPanel arrowPanel = new ArrowPanel( module );
        add( arrowPanel );

        final JCheckBox audio = new JCheckBox( "Sound", true );
        audio.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSoundEnabled( audio.isSelected() );
            }
        } );
        add( new JSeparator() );
        add( audio );

        TimeListener timeListener = new TimeListener() {
            public void recordingStarted() {
                setButtons( false, false, false, false );
            }

            public void recordingPaused() {
                setButtons( true, true, false, true );
            }

            public void recordingFinished() {
                setButtons( true, true, false, true );
            }

            public void playbackFinished() {
                setButtons( false, false, false, true );
            }

            public void playbackStarted() {
                setButtons( false, false, true, true );
            }

            public void playbackPaused() {
                setButtons( true, true, false, true );
            }

            public void modeChanged() {
            }

            public void reset() {
                setButtons( false, false, false, false );
            }

            public void rewind() {
                setButtons( true, true, false, false );
            }
        };
        module.addListener( timeListener );
    }

    private void setButtons( boolean playBtn, boolean slowBtn, boolean pauseBtn, boolean rewindBtn ) {
        play.setEnabled( playBtn );
        slowMotion.setEnabled( slowBtn );
        pause.setEnabled( pauseBtn );
        rewind.setEnabled( rewindBtn );
    }

}
