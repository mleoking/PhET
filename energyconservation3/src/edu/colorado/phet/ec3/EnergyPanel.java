/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:37:21 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class EnergyPanel extends ControlPanel {
    private EC3Module module;

    public EnergyPanel( final EC3Module module ) {
        super( module );
        this.module = module;
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        addControl( reset );

        final JCheckBox recordPath = new JCheckBox( "Record Path", module.getEnergyConservationModel().isRecordPath() );
        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( recordPath.isSelected() );
            }
        } );
        addControl( recordPath );

        final JButton clearHistory = new JButton( "Clear Path" );
        clearHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPaths();
            }
        } );
        addControl( clearHistory );

        final JCheckBox measuringTape = new JCheckBox( "Measuring Tape",
                                                       module.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        addControl( measuringTape );

        final JCheckBox pieChart = new JCheckBox( "Pie Chart", module.isPieChartVisible() );
        pieChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPieChartVisible( pieChart.isSelected() );
            }
        } );
        addControl( pieChart );
    }

    private void reset() {
        module.reset();
    }
}
