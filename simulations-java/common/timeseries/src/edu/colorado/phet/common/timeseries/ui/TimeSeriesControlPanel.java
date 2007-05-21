package edu.colorado.phet.common.timeseries.ui;

import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 12:47:07 PM
 */

public class TimeSeriesControlPanel extends JPanel {
    private TwoModeButton recordButton;
    private TwoModeButton playbackButton;
    private TwoModeButton slowMotionButton;
    private TimeSeriesButton rewindButton;
    private TimeSeriesButton stepButton;
    private TimeSeriesButton clearButton;
    private TimeSeriesModel timeSeriesModel;

    public TimeSeriesControlPanel( final TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;
        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        ButtonMode recordMode = new ButtonMode( TimeseriesResources.getString( "record" ), loadIcon( "Play24.gif" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startRecording();
            }
        } );
        ButtonMode pauseRecordMode = new ButtonMode( TimeseriesResources.getString( "pause.recording" ), loadIcon( "Pause24.gif" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPaused( true );
            }
        } );
        recordButton = new TwoModeButton( recordMode, pauseRecordMode );

        ButtonMode playMode = new ButtonMode( TimeseriesResources.getString( "playback" ), loadIcon( "Play24.gif" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( 1.0 );
            }
        } );
        ButtonMode pausePlayback = new ButtonMode( TimeseriesResources.getString( "pause.playback" ), loadIcon( "Pause24.gif" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPaused( true );
            }
        } );
        playbackButton = new TwoModeButton( playMode, pausePlayback );

        ButtonMode slowMode = new ButtonMode( TimeseriesResources.getString( "slow.playback" ), loadIcon( "StepForward24.gif" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.startPlaybackMode( 0.5 );
            }
        } );
        ButtonMode pauseSlow = new ButtonMode( TimeseriesResources.getString( "pause.playback" ), loadIcon( "Pause24.gif" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPaused( true );
            }
        } );

        slowMotionButton = new TwoModeButton( slowMode, pauseSlow );

        rewindButton = new TimeSeriesButton( TimeseriesResources.getString( "rewind" ), "Rewind24.gif" );
        rewindButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
            }
        } );
        stepButton = new TimeSeriesButton( TimeseriesResources.getString( "step" ), "StepForward24.gif" );
        stepButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPlaybackMode();
                timeSeriesModel.stepMode();
            }
        } );
        clearButton = new TimeSeriesButton( TimeseriesResources.getString( "clear" ), "Stop24.gif" );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.clear();
            }
        } );

        add( recordButton, gridBagConstraints );
        add( playbackButton, gridBagConstraints );
        add( slowMotionButton, gridBagConstraints );
        add( stepButton, gridBagConstraints );
        add( rewindButton, gridBagConstraints );
        add( clearButton, gridBagConstraints );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataAdded() {
                updateButtons();
            }

            public void pauseChanged() {
                updateButtons();
            }
        } );
        updateButtons();
        setBorder( BorderFactory.createLineBorder( Color.lightGray ) );
    }

    private ImageIcon loadIcon( String s ) {
        return new ImageIcon( TimeseriesResources.loadBufferedImage( "icons/java/media/" + s ) );
    }

    private void updateButtons() {
        if( timeSeriesModel.isRecording() && !timeSeriesModel.isPaused() ) {
            recordButton.setPauseMode();
        }
        else {
            recordButton.setGoMode();
        }
        if( timeSeriesModel.isPlaybackMode() && !timeSeriesModel.isPaused() ) {
            playbackButton.setPauseMode();
        }
        else {
            playbackButton.setGoMode();
        }
        if( timeSeriesModel.isPlaybackMode() && timeSeriesModel.getPlaybackMode().getPlaybackSpeed() == 0.5 && !timeSeriesModel.isPaused() ) {
            slowMotionButton.setPauseMode();
        }
        else {
            slowMotionButton.setGoMode();
        }
//        stepButton.setEnabled( timeSeriesModel.getClosestPlaybackIndex() < timeSeriesModel.numPlaybackStates() );
//        rewindButton.setEnabled( timeSeriesModel.getClosestPlaybackIndex() > 0 && timeSeriesModel.numPlaybackStates() > 0 );
        clearButton.setEnabled( timeSeriesModel.numPlaybackStates() > 0 );
    }

    private static class ButtonMode {
        private String label;
        private Icon icon;
        private ActionListener actionListener;

        public ButtonMode( String label, Icon icon, ActionListener actionListener ) {
            this.label = label;
            this.icon = icon;
            this.actionListener = actionListener;
        }

        public void actionPerformed( ActionEvent e ) {
            actionListener.actionPerformed( e );
        }

        public String getLabel() {
            return label;
        }

        public Icon getIcon() {
            return icon;
        }
    }

    static class TwoModeButton extends JButton {
        ButtonMode goMode;
        ButtonMode pauseMode;

        ButtonMode currentMode;

        public TwoModeButton( ButtonMode goMode, ButtonMode pauseMode ) {
            this.goMode = goMode;
            this.pauseMode = pauseMode;
            setMode( goMode );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    currentMode.actionPerformed( e );
                }
            } );
        }

        private void setMode( ButtonMode mode ) {
            currentMode = mode;
            setText( mode.getLabel() );
            setIcon( mode.getIcon() );
        }

        public void setPauseMode() {
            setMode( pauseMode );
        }

        public void setGoMode() {
            setMode( goMode );
        }
    }

    static class TimeSeriesButton extends JButton {

        public TimeSeriesButton( String label, String imageIcon ) {
            super( label, new ImageIcon( TimeseriesResources.loadBufferedImage( "icons/java/media/" + imageIcon ) ) );
        }

    }
}
