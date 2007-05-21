package edu.colorado.phet.common.timeseries.ui;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

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
//            if( lowRes() ) {
//                suffix = "16";
//            }
            String iconLoc = "phetcommon/images/clock/" + iconName + suffix + ".gif";
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
            button.setFont( new Font( "Lucida Sans", Font.BOLD, 10 ) );

        }
        return button;
    }

    private boolean lowRes() {
        return Toolkit.getDefaultToolkit().getScreenSize().width <= 1024;
    }


    public TimeSeriesPlaybackPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;

        live = createButton( TimeseriesResources.getString( "go" ), "Play" );
        live.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startLiveMode();
            }
        } );
        record = createButton( TimeseriesResources.getString( "record" ), "Play" );
        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startRecording();
            }
        } );

        pause = createButton( TimeseriesResources.getString( "pause" ), "Pause" );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //pausing from playback leaves it alone
                timeSeriesModel.setPaused( true );
            }
        } );

        step = createButton( TimeseriesResources.getString( "step" ), "StepForward" );
        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.stepMode();
            }
        } );

        play = createButton( TimeseriesResources.getString( "playback" ), "Play" );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( PLAYBACK_FULL );
            }
        } );

        rewind = createButton( TimeseriesResources.getString( "rewind" ), "Rewind" );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
                timeSeriesModel.setPaused( true );
            }
        } );

        slowMotion = createButton( TimeseriesResources.getString( "slow.motion" ), "StepForward" );
//        slowMotion = createButton( "<html>Slow<br>Motion</html>", "StepForward" );
        slowMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( PLAYBACK_SLOW );
            }
        } );

//        clear = createButton( "Clear", "energy-skate-park/images/icons/java/media/Stop24.gif" );
        clear = createButton( TimeseriesResources.getString( "clear" ), "Stop" );
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
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataAdded() {
                updateButtons();
            }
        } );
        updateButtons();
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
        live.setEnabled( !timeSeriesModel.isLiveMode() || timeSeriesModel.isPaused() );
        record.setEnabled( !timeSeriesModel.isRecording() );
        play.setEnabled( ( timeSeriesModel.isThereRecordedData() && !timeSeriesModel.isPlaybackMode( PLAYBACK_FULL ) ) || ( timeSeriesModel.isPlaybackMode() && timeSeriesModel.isPaused() ) );
        slowMotion.setEnabled( ( timeSeriesModel.isThereRecordedData() && !timeSeriesModel.isPlaybackMode( PLAYBACK_SLOW ) ) || ( timeSeriesModel.isPlaybackMode() && timeSeriesModel.isPaused() ) );
        pause.setEnabled( !timeSeriesModel.isPaused() );
        rewind.setEnabled( timeSeriesModel.isThereRecordedData() && !timeSeriesModel.isFirstPlaybackPoint() );
        step.setEnabled( timeSeriesModel.isPaused() );
        clear.setEnabled( timeSeriesModel.isThereRecordedData() );
    }


}
