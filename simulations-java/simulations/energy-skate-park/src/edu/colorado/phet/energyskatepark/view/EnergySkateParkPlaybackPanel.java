package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.*;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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

    public static class EnergySkateParkCCP extends ClockControlPanel {
        public EnergySkateParkCCP( IClock clock ) {
            super( clock );
            setPlayString( "Playback" );
        }

        public void addPlayPauseActionListener( ActionListener actionListener ) {
            super.addPlayPauseActionListener( actionListener );
        }
    }

    public EnergySkateParkPlaybackPanel( final TimeSeriesModel timeSeriesModel, final Clock clock ) {
        this.clock = clock;
        final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00", (EnergySkateParkClock)clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setTimingStrategy( new TimingStrategy.Constant( timeSpeedSlider.getValue() ) );
            }
        } );
        add( timeSpeedSlider );

        try {
            recordButton = new JButton( "REC", new ImageIcon( ImageLoader.loadBufferedImage( "timeseries/images/icons/record24.gif" ) ) );
            recordButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( recordButton.getText().equals( "Pause" ) ) {
                        clock.pause();
                    }else{
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

        EnergySkateParkCCP clockControlPanel = new EnergySkateParkCCP( clock );
        clockControlPanel.addPlayPauseActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.setPlaybackMode();
            }
        } );

        add( clockControlPanel );
        JButton rewindButton = new JButton( "Rewind", new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_REWIND ) ) );
        rewindButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.rewind();
            }
        } );
        add( rewindButton );
        JButton clearButton = new JButton( "Clear", new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_STOP ) ) );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timeSeriesModel.clear();
            }
        } );
        add( clearButton );
        updateRecordButton();
    }

    private void updateRecordButton() {
        recordButton.setText( clock.isPaused() ? "REC" : "Pause" );
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

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        SwingClock swingClock = new SwingClock( 30, 1 );
        JPanel contentPane = new EnergySkateParkPlaybackPanel( new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), swingClock ), swingClock );
        frame.setContentPane( contentPane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
    }

}
