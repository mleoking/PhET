/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.TheRampStrings;

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
public class TimeSeriesPlaybackPanel extends JPanel {
    private JButton record;
    private JButton play;
    private JButton pause;
    private JButton rewind;
    private JButton slowMotion;
    private JButton clear;
    private TimeSeriesModel timeSeriesModel;

    public TimeSeriesPlaybackPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;

        record = new JButton( TheRampStrings.getString( "record" ) );
        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setRecordMode();
                timeSeriesModel.setPaused( false );
            }
        } );

        ImageIcon pauseIcon = null;
        try {
            pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        pause = new JButton( TheRampStrings.getString( "pause" ), pauseIcon );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //pausing from playback leaves it alone
                timeSeriesModel.setPaused( true );
            }
        } );
        ImageIcon playIcon = null;
        try {
            playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        play = new JButton( TheRampStrings.getString( "playback" ), playIcon );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( 1.0 );
            }
        } );

        ImageIcon rewindIcon = null;
        try {
            rewindIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Rewind24.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        rewind = new JButton( TheRampStrings.getString( "rewind" ), rewindIcon );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
                timeSeriesModel.setPaused( true );
            }
        } );

        ImageIcon slowIcon = null;
        try {
            slowIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/StepForward24.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        slowMotion = new JButton( TheRampStrings.getString( "slow.motion" ), slowIcon );
        slowMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( .4 );
            }
        } );

        clear = new JButton( TheRampStrings.getString( "clear" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.confirmAndApplyReset();
            }
        } );
//        add( record );
        add( play );
        add( slowMotion );
        add( pause );
        add( rewind );
        add( clear );

        if( timeSeriesModel instanceof HasAudio ) {
            final JCheckBox audio = new JCheckBox( TheRampStrings.getString( "sound" ), true );
            audio.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (HasAudio)timeSeriesModel ).setAudioEnabled( audio.isSelected() );
                }
            } );
            add( new JSeparator() );
            add( audio );
        }

        TimeSeriesModelListener timeListener = new TimeSeriesModelListener() {
            public void recordingStarted() {
                setButtons( false, false, false, false );
            }

            public void recordingPaused() {
                setButtons( true, true, false, false );
            }

            public void recordingFinished() {
                setButtons( true, true, false, false );
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
        timeSeriesModel.addListener( timeListener );
    }

    private void setButtons( boolean playBtn, boolean slowBtn, boolean pauseBtn, boolean rewindBtn ) {
        play.setEnabled( playBtn );
        slowMotion.setEnabled( slowBtn );
        pause.setEnabled( true );
        rewind.setEnabled( rewindBtn );
    }

}
