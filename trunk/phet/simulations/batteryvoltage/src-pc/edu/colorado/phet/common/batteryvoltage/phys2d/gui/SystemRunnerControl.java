package edu.colorado.phet.common.batteryvoltage.phys2d.gui;

import edu.colorado.phet.common.batteryvoltage.view.util.SimStrings;
import edu.colorado.phet.common.batteryvoltage.phys2d.SystemRunner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SystemRunnerControl {
    JPanel panel;
    SystemRunner sr;
    JSlider dtSlider;
    JTextField dtField;
    JTextField waitField;
    JSlider waitTimeSlider;
    Range dtRange;//target
    Range waitRange;

    Range waitSliderRange;
    Range dtSliderRange;

    public JPanel getJPanel() {
        return panel;
    }

    public SystemRunnerControl( Range dtRange, double dt, Range waitRange, int wait, SystemRunner sr ) {
        dtField = new JTextField( 10 );
        waitField = new JTextField( 10 );
        dtField.setEditable( false );
        waitField.setEditable( false );

        this.sr = sr;
        this.dtRange = dtRange;
        this.waitRange = waitRange;
        this.panel = new JPanel();
        this.dtSlider = new JSlider( 0, 100, 50 );
        this.dtSliderRange = new Range( 0, 100 );
        dtSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSystem();
            }
        } );
        dtSlider.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "SystemRunnerControl.TimeIncrementSlider" ) ) );
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );

        this.waitTimeSlider = new JSlider( 0, 100, 50 );
        this.waitSliderRange = new Range( 0, 100 );
        waitTimeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSystem();
            }
        } );
        waitTimeSlider.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "SystemRunnerControl.WaitTimeSlider" ) ) );
        panel.add( dtSlider );
        panel.add( dtField );
        panel.add( waitTimeSlider );
        panel.add( waitField );

        double tReadout = dtRange.convertTo( dtSliderRange, dt );
        double waitReadout = waitRange.convertTo( waitSliderRange, wait );

        dtSlider.setValue( (int)tReadout );
        waitTimeSlider.setValue( (int)waitReadout );
        updateSystem();
    }

    private void updateSystem() {
        int dtReadout = dtSlider.getValue();
        double dt = dtSliderRange.convertTo( dtRange, dtReadout );
        dtField.setText( "dt=" + dt );
        sr.setDt( dt );

        int waitReadout = waitTimeSlider.getValue();
        double wait = waitSliderRange.convertTo( waitRange, waitReadout );
        waitField.setText( SimStrings.get( "SystemRunnerControl.WaitTimeText" ) + "=" + ( (int)wait ) );
        sr.setWaitTime( (int)wait );
    }
}
