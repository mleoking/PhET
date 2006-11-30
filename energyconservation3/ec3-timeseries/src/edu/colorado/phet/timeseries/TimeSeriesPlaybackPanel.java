/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.common.LucidaSansFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
    private JButton step;
    private JButton rewind;
    private JButton slowMotion;
    private JButton clear;
    private TimeSeriesModel timeSeriesModel;
    private JButton live;
    private double PLAYBACK_SLOW = 0.4;
    private double PLAYBACK_FULL = 1.0;

    private JButton createButton( String name, String iconName ) {
        JButton button = null;
        if( iconName != null ) {
            String suffix = "24";
            if( lowRes() ) {
                suffix = "16";
            }
            String iconLoc = "images/icons/java/media/" + iconName + suffix + ".gif";
            button = new JButton( name, new ImageIcon( loadImage( iconLoc ) ) );

            if( lowRes() ) {
                button.setVerticalTextPosition( AbstractButton.BOTTOM );
                button.setHorizontalTextPosition( AbstractButton.CENTER );
            }
        }
        else {
            button = new JButton( name );
        }
        if( lowRes() ) {
//            System.out.println( "button.getFont().getSize() = " + button.getFont().getSize() );
            button.setFont( new LucidaSansFont( 10, true ) );

        }
        return button;
    }

    private boolean lowRes() {
        return Toolkit.getDefaultToolkit().getScreenSize().width <= 1024;
    }


    public TimeSeriesPlaybackPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;

        live = createButton( EnergySkateParkStrings.getString( "go" ), "Play" );
        live.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startLiveMode();
            }
        } );
        record = createButton( EnergySkateParkStrings.getString( "record" ), "Movie" );
        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startRecording();
            }
        } );

        pause = createButton( EnergySkateParkStrings.getString( "pause" ), "Pause" );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //pausing from playback leaves it alone
                timeSeriesModel.setPaused( true );
            }
        } );

        step = createButton( EnergySkateParkStrings.getString( "step" ), "StepForward" );
        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.stepMode();
            }
        } );

        play = createButton( EnergySkateParkStrings.getString( "playback" ), "Forward" );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( PLAYBACK_FULL );
            }
        } );

        rewind = createButton( EnergySkateParkStrings.getString( "rewind" ), "Rewind" );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
                timeSeriesModel.setPaused( true );
            }
        } );

        slowMotion = createButton( EnergySkateParkStrings.getString( "slow.motion" ), "StepForward" );
//        slowMotion = createButton( "<html>Slow<br>Motion</html>", "StepForward" );
        slowMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( PLAYBACK_SLOW );
            }
        } );

//        clear = createButton( "Clear", "images/icons/java/media/Stop24.gif" );
        clear = createButton( EnergySkateParkStrings.getString( "clear" ), "Stop" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.confirmAndApplyReset();
            }
        } );
        add( live );
        add( record );
        add( play );
        add( slowMotion );
        add( pause );
        add( step );
        add( rewind );
        add( clear );

        TimeSeriesModelListener timeListener = new TimeSeriesModelListener() {
            public void liveModeStarted() {
                updateButtons();
            }

            public void recordingStarted() {
                updateButtons();
            }

            public void recordingPaused() {
                updateButtons();
            }

            public void recordingFinished() {
                updateButtons();
            }

            public void playbackFinished() {
                updateButtons();
            }

            public void playbackStarted() {
                updateButtons();
            }

            public void playbackPaused() {
                updateButtons();
            }

            public void reset() {
                updateButtons();
            }

            public void rewind() {
                updateButtons();
            }

            public void liveModePaused() {
                updateButtons();
            }
        };
        timeSeriesModel.addListener( timeListener );
        timeListener.liveModeStarted();
    }

    private BufferedImage loadImage( String s ) {
        BufferedImage image = null;
        try {
            image = new ImageLoader().loadImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return image;
    }

    private void updateButtons() {
        live.setEnabled( !timeSeriesModel.isLiveMode() );
        record.setEnabled( !timeSeriesModel.isRecording() );
        play.setEnabled( ( timeSeriesModel.isThereRecordedData() && !timeSeriesModel.isPlaybackMode( PLAYBACK_FULL ) ) || ( timeSeriesModel.isPlaybackMode() && timeSeriesModel.isPaused() ) );
        slowMotion.setEnabled( ( timeSeriesModel.isThereRecordedData() && !timeSeriesModel.isPlaybackMode( PLAYBACK_SLOW ) ) || ( timeSeriesModel.isPlaybackMode() && timeSeriesModel.isPaused() ) );
        pause.setEnabled( !timeSeriesModel.isPaused() );
        rewind.setEnabled( timeSeriesModel.isThereRecordedData() && !timeSeriesModel.isFirstPlaybackPoint() );
        step.setEnabled( timeSeriesModel.isPaused() );
    }


}
