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
    private JLabel label;
    private DoubleSeries doubleSeries = new DoubleSeries( 10 );

    public RotationModuleProfiler( final RotationApplication application, final RotationModule module ) {
        this.application = application;
        this.module = module;
        this.frame = new JDialog( application.getPhetFrame(), "Profiler", false );
        this.module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                doubleSeries.add( module.getRotationClock().getLastFrameRate() );
                updateLabel();
            }
        } );
        label = new JLabel( "Frame Rate= ??" );
        label.setOpaque( true );
        JPanel contentPane = new VerticalLayoutPanel();
        contentPane.add( label );

        final LinearValueControl linearValueControl = new LinearValueControl( 5, 100, "Clock Delay", "0.0", "ms" );
        linearValueControl.setValue( getConstantDTClock().getDelay() );
        linearValueControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getConstantDTClock().setDelay( (int)linearValueControl.getValue() );
            }
        } );

        contentPane.add( linearValueControl );

        frame.setContentPane( contentPane );
        frame.pack();
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                frame.dispose();
            }
        } );
    }

    private RotationClock getConstantDTClock() {
        return module.getRotationClock();
    }

    private void updateLabel() {
        label.setText( "Frame Rate=" + new DecimalFormat( "0.0" ).format( doubleSeries.average() ) );
        label.paintImmediately( 0, 0, label.getWidth(), label.getHeight() );//paint immediately in case app is consuming too many resources to do it itself
    }

    public void start() {
        frame.setVisible( true );
    }
}
