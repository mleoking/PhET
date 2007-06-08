package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.model.clock.*;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.TimeControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
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
    private JButton recordButton;
    private Clock clock;
    private ClockControlPanel energySkateParkCCP;
    private TimeSeriesModel timeSeriesModel;
    private JButton rewindButton;

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
            recordButton = new JButton( EnergySkateParkStrings.getString( "controls.playback.record" ),
                                        new ImageIcon( ImageLoader.loadBufferedImage( "timeseries/images/icons/record24.gif" ) ) );
            recordButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( recordButton.getText().equals( EnergySkateParkStrings.getString( "controls.playback.pause" ) ) ) {
                        clock.pause();
                    }
                    else {
                        timeSeriesModel.setRecordMode();
                        clock.start();
                    }
                }
            } );
            add( recordButton );
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
        try {
            recordButton.setPreferredSize( ( SwingUtils.getMaxDimension(
                    recordButton, EnergySkateParkStrings.getString( "controls.playback.record" ),
                    new ImageIcon( ImageLoader.loadBufferedImage( "timeseries/images/icons/record24.gif" ) ),
                    EnergySkateParkStrings.getString( "controls.playback.pause" ),
                    new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_PAUSE ) ) ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                updateModeButtons();
            }
        } );
        clock.addClockListener( new ClockAdapter() {

            public void clockStarted( ClockEvent clockEvent ) {
                updateModeButtons();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updateModeButtons();
            }
        } );

        energySkateParkCCP = new ClockControlPanel( clock );
        energySkateParkCCP.setPlayString( EnergySkateParkStrings.getString( "controls.playback.playback" ) );
        energySkateParkCCP.addTimeControlListener( new TimeControlPanel.TimeControlAdapter() {
            public void playPressed() {
                actionPerformed();
            }

            public void pausePressed() {
                actionPerformed();
            }

            public void actionPerformed() {
//                System.out.println( "timeSeriesModel.getPlaybackTime() = " + timeSeriesModel.getPlaybackTime() +", recTime="+timeSeriesModel.getRecordTime());
                //todo: this depends on correct sequence of dispatches to listeners in superclass
                if( timeSeriesModel.getPlaybackTime() == timeSeriesModel.getRecordTime() ) {
                    timeSeriesModel.setPlaybackTime( 0.0 );
                }
                timeSeriesModel.setPlaybackMode();
            }
        } );
//        energySkateParkCCP.addPlayPauseActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
////                System.out.println( "timeSeriesModel.getPlaybackTime() = " + timeSeriesModel.getPlaybackTime() +", recTime="+timeSeriesModel.getRecordTime());
//                //todo: this depends on correct sequence of dispatches to listeners in superclass
//                if( timeSeriesModel.getPlaybackTime() == timeSeriesModel.getRecordTime() ) {
//                    timeSeriesModel.setPlaybackTime( 0.0 );
//                }
//                timeSeriesModel.setPlaybackMode();
//            }
//        } );

        add( energySkateParkCCP );
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
        updateModeButtons();
    }

    private void updateRewindButtonEnabled() {
        rewindButton.setEnabled( clock.isPaused() );
    }

    private void updateModeButtons() {
        SwingUtilities.invokeLater( new Runnable() {
            //hack to make sure this happens last (after other state update events)
            //todo: listening to different objects for state changes should allow us to avoid this hack.
            public void run() {
                if( clock.isPaused() ) {
                    recordButton.setEnabled( true );
                }
                else {
                    recordButton.setEnabled( timeSeriesModel.isRecording() );
                }
            }
        } );
    }

    private void updateRecordButton() {
        recordButton.setText( clock.isPaused() ? EnergySkateParkStrings.getString( "controls.playback.record" ) : EnergySkateParkStrings.getString( "controls.playback.pause" ) );
        try {
            recordButton.setIcon( clock.isPaused() ?
                                  new ImageIcon( ImageLoader.loadBufferedImage( "timeseries/images/icons/record24.gif" ) ) :
                                  new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_PAUSE ) )
            );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

}
