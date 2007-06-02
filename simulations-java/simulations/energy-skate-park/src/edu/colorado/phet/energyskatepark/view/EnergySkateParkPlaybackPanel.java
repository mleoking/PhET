package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:34 PM
 */
public class EnergySkateParkPlaybackPanel extends JPanel {

    public EnergySkateParkPlaybackPanel( final TimeSeriesModel timeSeriesModel, final Clock clock ) {
        final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00",(EnergySkateParkClock)clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setTimingStrategy( new TimingStrategy.Constant( timeSpeedSlider.getValue()) );
            }
        } );
        add( timeSpeedSlider );
        add( new ClockControlPanel( clock ) );
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
