/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

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
        super.removeTitle();
        this.module = module;
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        addControl( reset );

        JButton resetSkater = new JButton( "Reset Skater" );
        resetSkater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetSkater();
            }
        } );
        addControl( resetSkater );

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

        final JPanel piePanel = new PieChartPanel( module, this );
        addControlFullWidth( piePanel );

        final JButton showChart = new JButton( "Show Plot" );
        showChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPlotVisible( true );
            }
        } );
        addControl( showChart );

        final JButton showBarChart = new JButton( "Show Bar Graph" );
        showBarChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setBarChartVisible( true );
            }
        } );
        addControl( showBarChart );

        final ModelSlider modelSlider = new ModelSlider( "Coefficient of Friction", "", 0, 0.04, 0.0, new DecimalFormat( "0.000" ), new DecimalFormat( "0.000" ) );
        modelSlider.setModelTicks( new double[]{0, 0.02, 0.04} );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setCoefficientOfFriction( modelSlider.getValue() );
            }
        } );
        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                if( module.getEnergyConservationModel().numBodies() > 0 ) {
                    modelSlider.setValue( module.getEnergyConservationModel().bodyAt( 0 ).getFrictionCoefficient() );
                }
            }
        } );
        addControl( modelSlider );

        final JButton clearHeat = new JButton( "Clear Heat" );
        clearHeat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearHeat();
            }
        } );
        addControl( clearHeat );

        final JButton showEnergyPositionPlot = new JButton( "Show Energy vs. Position" );
        showEnergyPositionPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyPositionPlotVisible( true );
            }
        } );
        addControl( showEnergyPositionPlot );

        final JComponent gravitySlider = new GravitySlider( module );
        addControl( gravitySlider );

        final ModelSlider restitution = new ModelSlider( "Coeff. of Restitution", "", 0, 1.0, 1.0 );
        restitution.setModelTicks( new double[]{0, 0.5, 1} );
        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                double rest = restitution.getValue();
                module.setCoefficientOfRestitution( rest );
            }
        } );
        addControl( restitution );
    }

    private void resetSkater() {
        module.resetSkater();
    }

    private void reset() {
        module.reset();
    }
}
