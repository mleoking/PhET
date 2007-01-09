package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 12:47:07 PM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

public class TimeSeriesControlPanel extends JPanel {
    private TimeSeriesButton recordButton;
    private TimeSeriesButton pauseButton;
    private TimeSeriesButton playbackButton;
    private TimeSeriesButton slowMotionButton;
    private TimeSeriesButton rewindButton;
    private TimeSeriesButton stepButton;
    private TimeSeriesButton clearButton;
    private TimeSeriesModel timeSeriesModel;

    public TimeSeriesControlPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;
        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );

        recordButton = new TimeSeriesButton( "Record", "Play24.gif" );
        recordButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setRecording();
            }
        } );
        pauseButton = new TimeSeriesButton( "Pause", "Pause24.gif" );
        pauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPaused();
            }
        } );
        playbackButton = new TimeSeriesButton( "Playback", "Play24.gif" );
        playbackButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPlayback();
            }
        } );
        slowMotionButton = new TimeSeriesButton( "Slow Playback", "StepForward24.gif" );
        slowMotionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setSlowMotion();
            }
        } );
        rewindButton = new TimeSeriesButton( "Rewind", "Rewind24.gif" );
        rewindButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
            }
        } );
        stepButton = new TimeSeriesButton( "Step", "StepForward24.gif" );
        stepButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.step();
            }
        } );
        clearButton = new TimeSeriesButton( "Clear", "Stop24.gif" );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.clear();
            }
        } );

        add( recordButton, gridBagConstraints );
        add( pauseButton, gridBagConstraints );
        add( playbackButton, gridBagConstraints );
        add( slowMotionButton, gridBagConstraints );
        add( stepButton, gridBagConstraints );
        add( rewindButton, gridBagConstraints );
        add( clearButton, gridBagConstraints );
        timeSeriesModel.addListener( new TimeSeriesModel.Listener() {
            public void stateChanged() {
                updateButtons();
            }
        } );
        updateButtons();
        setBorder( BorderFactory.createLineBorder( Color.lightGray ) );
    }

    private void updateButtons() {
        recordButton.setEnabled( !timeSeriesModel.isRecording() || timeSeriesModel.isPaused() );
        pauseButton.setEnabled( !timeSeriesModel.isPaused() );
        playbackButton.setEnabled( !timeSeriesModel.isPlayback() || timeSeriesModel.isPaused() );
        slowMotionButton.setEnabled( !timeSeriesModel.isSlowMotion() || timeSeriesModel.isPaused() );
        stepButton.setEnabled( timeSeriesModel.isPaused() );
        rewindButton.setEnabled( true );
        clearButton.setEnabled( true );
    }

    static class TimeSeriesButton extends JButton {
        public TimeSeriesButton( String label, String imageIcon ) {
            super( label );
            try {
                setIcon( new ImageIcon( ImageLoader.loadBufferedImage( "images/icons/java/media/" + imageIcon ) ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
