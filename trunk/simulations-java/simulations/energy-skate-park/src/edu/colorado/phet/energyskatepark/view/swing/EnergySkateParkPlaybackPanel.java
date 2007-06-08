package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:34 PM
 */
public class EnergySkateParkPlaybackPanel extends JPanel {
    private MultiStateButton recordButton;
    private Clock clock;
    private TimeSeriesModel timeSeriesModel;
    private JButton rewindButton;
    private MultiStateButton playbackButton;
    private JButton stepButton;

    private static final String KEY_PLAYBACK = "playback";
    private static final String KEY_PAUSE = "pause";

    private static final Object KEY_REC = "rec";
    private static final Object KEY_PAUSE_REC = "pause-rec";

    public EnergySkateParkPlaybackPanel( final EnergySkateParkModule module, final TimeSeriesModel timeSeriesModel, final Clock clock ) {
        this.timeSeriesModel = timeSeriesModel;
        this.clock = clock;
        final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00", (EnergySkateParkClock)clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setTimingStrategy( new TimingStrategy.Constant( timeSpeedSlider.getValue() ) );
            }
        } );
        add( timeSpeedSlider );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesCleared() {
                module.setRecordOrLiveMode();
            }
        } );
        try {

            recordButton = new MultiStateButton( new Object[]{KEY_REC, KEY_PAUSE_REC}, new String[]{"REC", "Pause"},
                                                 new Icon[]{
                                                         new ImageIcon( ImageLoader.loadBufferedImage( "timeseries/images/icons/record24.gif" ) ),
                                                         loadCommonIcon( PhetCommonResources.IMAGE_PAUSE )
                                                 } );
            recordButton.addActionListener( KEY_REC, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    timeSeriesModel.setRecordMode();
                    clock.start();
                }
            } );
            recordButton.addActionListener( KEY_PAUSE_REC, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    clock.pause();
                }
            } );
            add( recordButton );
            timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
                public void modeChanged() {
                    updateRecordButton();
                }
            });
            clock.addClockListener( new ClockAdapter() {
                public void clockPaused( ClockEvent clockEvent ) {
                    updateRecordButton();
                }

                public void clockStarted( ClockEvent clockEvent ) {
                    updateRecordButton();
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        playbackButton = new MultiStateButton( new Object[]{KEY_PLAYBACK, KEY_PAUSE}, new String[]{"Playback", "Pause"}, new Icon[]{loadCommonIcon( PhetCommonResources.IMAGE_PLAY ), loadCommonIcon( PhetCommonResources.IMAGE_PAUSE )} );
        playbackButton.addActionListener( KEY_PLAYBACK, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( timeSeriesModel.isRecording() ) {
                    timeSeriesModel.setPlaybackMode();
                    timeSeriesModel.setPlaybackTime( 0.0 );
                }
                if( timeSeriesModel.getPlaybackTime() == timeSeriesModel.getRecordTime() ) {
                    timeSeriesModel.setPlaybackTime( 0.0 );
                }
                timeSeriesModel.setPlaybackMode();
                clock.start();
            }
        } );
        playbackButton.addActionListener( KEY_PAUSE, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.pause();
            }
        } );
        clock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updatePlaybackButtonMode();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updatePlaybackButtonMode();
            }
        } );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {

            public void modeChanged() {
                updatePlaybackButtonMode();
            }

            public void pauseChanged() {
                updatePlaybackButtonMode();
            }
        } );


        stepButton = new JButton( "Step", loadCommonIcon( PhetCommonResources.IMAGE_STEP_FORWARD ) );
        stepButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.stepClockWhilePaused();
            }
        } );
        add( playbackButton );
        add( stepButton );
        clock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updateStepEnabled();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updateStepEnabled();
            }
        } );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                updateStepEnabled();
            }
        } );

        rewindButton = new JButton( EnergySkateParkStrings.getString( "controls.playback.rewind" ), new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_REWIND ) ) );
        rewindButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
            }
        } );
        add( rewindButton );
        clock.addClockListener( new ClockAdapter() {
            public void clockPaused( ClockEvent clockEvent ) {
                updateRewindButtonEnabled();
            }

            public void clockStarted( ClockEvent clockEvent ) {
                updateRewindButtonEnabled();
            }
        } );
        JButton clearButton = new JButton( "Clear", new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_STOP ) ) );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.clear();
            }
        } );
        add( clearButton );
        updateRecordButton();
    }

    private void updateStepEnabled() {
        boolean endTime = ( timeSeriesModel.getPlaybackTime() == timeSeriesModel.getRecordTime() );
        if( endTime || clock.isRunning() ) {
            stepButton.setEnabled( false );
        }
        else {
            stepButton.setEnabled( true );
        }
    }

    private void updatePlaybackButtonMode() {
        //todo: add check that there is data available to playback before enabling playback button
        if( clock.isPaused() || timeSeriesModel.isRecording() || timeSeriesModel.isLiveMode() ) {
            playbackButton.setMode( "playback" );
        }
        else {
            playbackButton.setMode( "pause" );
        }
    }

    private Icon loadCommonIcon( String commonResource ) {
        return new ImageIcon( PhetCommonResources.getInstance().getImage( commonResource ) );
    }

    private void updateRewindButtonEnabled() {
        rewindButton.setEnabled( clock.isPaused() );
    }

    private void updateRecordButton() {
        //todo: add check that there is space to record before enabling record button
        if( clock.isPaused() || timeSeriesModel.isPlaybackMode() || timeSeriesModel.isLiveMode() ) {
            recordButton.setMode( KEY_REC );
        }
        else {
            recordButton.setMode( KEY_PAUSE_REC );
        }
    }

}
