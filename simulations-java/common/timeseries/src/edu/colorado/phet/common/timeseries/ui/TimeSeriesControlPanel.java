package edu.colorado.phet.common.timeseries.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

/**
 * Author: Sam Reid
 * Jun 27, 2007, 10:05:59 AM
 */
public class TimeSeriesControlPanel extends JPanel {
    protected MultiStateButton recordButton;
    protected ConstantDtClock clock;
    protected TimeSeriesModel timeSeriesModel;
    protected JButton rewindButton;
    protected MultiStateButton playbackButton;
    protected JButton stepButton;
    protected static final String KEY_PLAYBACK = "playback";
    protected static final String KEY_PAUSE = "pause";
    protected static final Object KEY_REC = "rec";
    protected static final Object KEY_PAUSE_REC = "pause-rec";
    private TimeSpeedSlider timeSpeedSlider;

    public TimeSeriesControlPanel( final TimeSeriesModel timeSeriesModel, double minDT, double maxDT ) {
        this.clock = timeSeriesModel.getTimeModelClock();
        this.timeSeriesModel = timeSeriesModel;

        timeSpeedSlider = new TimeSpeedSlider( minDT, maxDT, "0.00", clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setDt( timeSpeedSlider.getValue() );
            }
        } );
        clock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                timeSpeedSlider.setValue( clock.getDt() );
            }
        } );

        add( timeSpeedSlider );

        recordButton = new MultiStateButton( new Object[]{KEY_REC, KEY_PAUSE_REC}, new String[]{PhetCommonResources.getString( "chart-time-control.go" ), PhetCommonResources.getString( "Common.StopwatchPanel.stop" )},
                                             new Icon[]{
                                                     new ImageIcon( TimeseriesResources.loadBufferedImage( "icons/go.png" ) ),
                                                     new ImageIcon( TimeseriesResources.loadBufferedImage( "icons/stop.png" ) )
                                             } );
        recordButton.addActionListener( KEY_REC, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setRecordMode();
                clock.start();
                notifyRecordButtonPressed();
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
        } );
        clock.addClockListener( new ClockAdapter() {
            public void clockPaused( ClockEvent clockEvent ) {
                updateRecordButton();
            }

            public void clockStarted( ClockEvent clockEvent ) {
                updateRecordButton();
            }
        } );

        playbackButton = new MultiStateButton( new Object[]{KEY_PLAYBACK, KEY_PAUSE}, new String[]{PhetCommonResources.getString( "timeseries.control.panel.playback" ), PhetCommonResources.getString( "Common.ClockControlPanel.Pause" )}, new Icon[]{loadCommonIcon( PhetCommonResources.IMAGE_PLAY ), loadCommonIcon( PhetCommonResources.IMAGE_PAUSE )} );
        playbackButton.addActionListener( KEY_PLAYBACK, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( timeSeriesModel.isRecording() ) {
                    timeSeriesModel.setPlaybackMode();
                    timeSeriesModel.setPlaybackTime( 0.0 );
                }
                if ( timeSeriesModel.getPlaybackTime() == timeSeriesModel.getRecordTime() ) {
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

        stepButton = new JButton( PhetCommonResources.getString( "Common.ClockControlPanel.Step" ), loadCommonIcon( PhetCommonResources.IMAGE_STEP_FORWARD ) );
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

        rewindButton = new JButton( TimeseriesResources.getString( "Common.rewind" ), new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_REWIND ) ) );
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
        JButton clearButton = new JButton( PhetCommonResources.getString( "Common.clear" ), new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_STOP ) ) );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( confirmClear() ) {
                    timeSeriesModel.clear();
                }
            }
        } );
        add( clearButton );
        updateRecordButton();
    }

    private boolean confirmClear() {
        return JOptionPane.showConfirmDialog( this, TimeseriesResources.getString("Common.confirm.clear.graphs" ) )== JOptionPane.YES_OPTION;
    }

    public void setSpeedControlVisible( boolean b ) {
        timeSpeedSlider.setVisible( b );
    }

    public static interface Listener {
        void recordButtonPressed();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyRecordButtonPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).recordButtonPressed();
        }
    }

    protected void updateStepEnabled() {
        boolean endTime = ( timeSeriesModel.getPlaybackTime() == timeSeriesModel.getRecordTime() );
        if ( endTime || clock.isRunning() ) {
            stepButton.setEnabled( false );
        }
        else {
            stepButton.setEnabled( true );
        }
    }

    protected void updatePlaybackButtonMode() {
        //todo: add check that there is data available to playback before enabling playback button
        if ( clock.isPaused() || timeSeriesModel.isRecording() || timeSeriesModel.isLiveMode() ) {
            playbackButton.setMode( "playback" );
        }
        else {
            playbackButton.setMode( "pause" );
        }
    }

    protected Icon loadCommonIcon( String commonResource ) {
        return new ImageIcon( PhetCommonResources.getInstance().getImage( commonResource ) );
    }

    protected void updateRewindButtonEnabled() {
        rewindButton.setEnabled( clock.isPaused() );
    }

    protected void updateRecordButton() {
        //todo: add check that there is space to record before enabling record button
        if ( clock.isPaused() || timeSeriesModel.isPlaybackMode() || timeSeriesModel.isLiveMode() ) {
            recordButton.setMode( KEY_REC );
        }
        else {
            recordButton.setMode( KEY_PAUSE_REC );
        }
    }
}
