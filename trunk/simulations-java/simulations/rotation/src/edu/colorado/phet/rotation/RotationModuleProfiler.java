package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.rotation.controls.DoubleSeries;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:48:36 AM
 */
public class RotationModuleProfiler {
    private JDialog frame;
    private RotationApplication application;
    private RotationModule module;
    private JLabel frameRate;
    private int NUM_SAMPLES = 20;
    private DoubleSeries frameRateSeries = new DoubleSeries( NUM_SAMPLES );
    private DoubleSeries paintTimeSeries = new DoubleSeries( NUM_SAMPLES );
    private DoubleSeries evalTimeSeries = new DoubleSeries( NUM_SAMPLES );
    private DoubleSeries delaySeries = new DoubleSeries( NUM_SAMPLES );
    private JLabel breakdown;

    public RotationModuleProfiler( final RotationApplication application, final RotationModule module ) {
        this.application = application;
        this.module = module;
        this.frame = new JDialog( application.getPhetFrame(), "Profiler", false );
        this.module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                updateSeries( module );
                updateLabels();
            }
        } );
        frameRate = new JLabel( "Frame Rate= ??" );
        frameRate.setOpaque( true );

        breakdown = new JLabel( "breakdown desciption filler" );
        breakdown.setOpaque( true );

        JPanel contentPane = new VerticalLayoutPanel();
        contentPane.add( frameRate );
        contentPane.add( breakdown );

        final LinearValueControl linearValueControl = new LinearValueControl( 5, 100, "Clock Delay", "0.0", "ms" );
        linearValueControl.setValue( getConstantDTClock().getDelay() );
        linearValueControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getConstantDTClock().setDelay( (int)linearValueControl.getValue() );
            }
        } );

        contentPane.add( linearValueControl );

        frame.setContentPane( contentPane );
        updateSeries( module );
        updateLabels();
        frame.pack();
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                frame.dispose();
            }
        } );

        frame.setSize( frame.getWidth() + 100, frame.getHeight() );
    }

    private void updateSeries( RotationModule module ) {
        frameRateSeries.add( module.getRotationClock().getLastFrameRate() );
        paintTimeSeries.add( module.getRotationSimulationPanel().getLastPaintTime() );
        evalTimeSeries.add( module.getRotationClock().getLastEvalTime() );
        delaySeries.add( module.getRotationClock().getLastActualDelay() );
    }

    private RotationClock getConstantDTClock() {
        return module.getRotationClock();
    }

    private void updateLabels() {
        frameRate.setText( "Frame Rate=" + format( frameRateSeries.average() ) );
//        breakdown.setText( "Delay=" + format( delaySeries.average() ) + " (ms), " +
//                           "Paint=" + format( paintTimeSeries.average() ) + ", " +
//                           "Model=" + format( evalTimeSeries.average() - paintTimeSeries.average() ) + ", " +
//                           "total=" + format( delaySeries.average() + evalTimeSeries.average() ) );
        frameRate.paintImmediately( 0, 0, frameRate.getWidth(), frameRate.getHeight() );//paint immediately in case app is consuming too many resources to do it itself
//        breakdown.paintImmediately( 0, 0, breakdown.getWidth(), breakdown.getHeight() );
    }

    private String format( double v ) {
        return new DecimalFormat( "0.0" ).format( v );
    }

    public void start() {
        frame.setVisible( true );
    }
}
