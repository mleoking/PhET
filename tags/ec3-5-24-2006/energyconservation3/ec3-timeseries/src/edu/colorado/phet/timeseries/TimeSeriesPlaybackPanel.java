/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
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
    private JButton rewind;
    private JButton slowMotion;
    private JButton clear;
    private TimeSeriesModel timeSeriesModel;
    private JButton live;

    private JButton createButton( String name, String iconName ) {
        JButton button = null;
        if( iconName != null ) {
            String suffix = "24";
            if( lowRes() ) {
                suffix = "16";
            }
            String iconLoc = "images/icons/java/media/" + iconName + suffix + ".gif";
            button = new JButton( SimStrings.get( name ), new ImageIcon( loadImage( iconLoc ) ) );

            if( lowRes() ) {
                button.setVerticalTextPosition( AbstractButton.BOTTOM );
                button.setHorizontalTextPosition( AbstractButton.CENTER );
            }
        }
        else {
            button = new JButton( SimStrings.get( name ) );
        }
        if( lowRes() ) {
            System.out.println( "button.getFont().getSize() = " + button.getFont().getSize() );
            button.setFont( new LucidaSansFont( 10, true ) );

        }
        return button;
    }

    private boolean lowRes() {
        return Toolkit.getDefaultToolkit().getScreenSize().width <= 1024;
    }


    public TimeSeriesPlaybackPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;

        live = createButton( "Go", "Play" );
        live.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startLiveMode();
            }
        } );
        record = createButton( "Record", "Movie" );
        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startRecording();
            }
        } );

        pause = createButton( "Pause", "Pause" );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //pausing from playback leaves it alone
                timeSeriesModel.setPaused( true );
            }
        } );
        play = createButton( "Playback", "Forward" );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( 1.0 );
            }
        } );

        rewind = createButton( "Rewind", "Rewind" );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
                timeSeriesModel.setPaused( true );
            }
        } );

        slowMotion = createButton( "Slow Motion", "StepForward" );
//        slowMotion = createButton( "<html>Slow<br>Motion</html>", "StepForward" );
        slowMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( .4 );
            }
        } );

//        clear = createButton( "Clear", "images/icons/java/media/Stop24.gif" );
        clear = createButton( "Clear", "Stop" );
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
        add( rewind );
        add( clear );

        TimeSeriesModelListener timeListener = new TimeSeriesModelListener() {
            public void liveModeStarted() {
                setButtons( false, true, false, false, true, false );
            }

            public void recordingStarted() {
                setButtons( false, false, false, false, true, false );
            }

            public void recordingPaused() {
                setButtons( true, true, true, true, false, true );
            }

            public void recordingFinished() {
                setButtons( true, false, true, true, false, true );
            }

            public void playbackFinished() {
                setButtons( true, true, false, false, false, true );
            }

            public void playbackStarted() {
                setButtons( false, false, false, false, true, true );
            }

            public void playbackPaused() {
                setButtons( true, true, true, true, false, true );
            }

            public void reset() {
                setButtons( true, true, false, false, false, false );
            }

            public void rewind() {
                setButtons( true, true, true, true, false, false );
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
//        if (lowRes()){
//            image= BufferedImageUtils.rescaleYMaintainAspectRatio( image, 14);
//        }
        return image;
    }

    private void setButtons( boolean liveBtn, boolean recordBtn, boolean playBtn, boolean slowBtn, boolean pauseBtn, boolean rewindBtn ) {
        live.setEnabled( liveBtn );
        record.setEnabled( recordBtn );
        play.setEnabled( playBtn );
        slowMotion.setEnabled( slowBtn );
        pause.setEnabled( pauseBtn );
        rewind.setEnabled( rewindBtn );
    }

}
