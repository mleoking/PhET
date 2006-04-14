/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:22:47 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MeasurementControlPanel extends VerticalLayoutPanel {
    private MeasurementToolSet measurementToolSet;

    public MeasurementControlPanel( final MeasurementToolSet measurementToolSet ) {
        this.measurementToolSet = measurementToolSet;
//        setBorder( BorderFactory.createTitledBorder( "Tools" ) );
        final JCheckBox measuringTape = new JCheckBox( "Measuring Tape", measurementToolSet.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                measurementToolSet.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        add( measuringTape );

        final JCheckBox stopwatch = new JCheckBox( "Stopwatch", measurementToolSet.isStopwatchVisible() );
        stopwatch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                measurementToolSet.setStopwatchVisible( stopwatch.isSelected() );
            }
        } );
        add( stopwatch );
    }
}
