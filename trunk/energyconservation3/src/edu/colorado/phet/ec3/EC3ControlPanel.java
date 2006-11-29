/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.common.IconComponent;
import edu.colorado.phet.ec3.controls.PieChartControlPanel;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergySkateParkModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:37:21 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class EC3ControlPanel extends ControlPanel {
    private EnergySkateParkModule module;

    public EC3ControlPanel( final EnergySkateParkModule module ) {
        this.module = module;
        JButton reset = new JButton( EnergySkateParkStrings.getString( "reset" ) );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        addControl( reset );

        JButton resetSkater = new JButton( EnergySkateParkStrings.getString( "return.skater" ) );
        resetSkater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetSkater();
            }
        } );
        addControl( resetSkater );

        JPanel pathPanel = new JPanel( new BorderLayout() );
        pathPanel.setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "path" ) ) );
        final JCheckBox recordPath = new JCheckBox( EnergySkateParkStrings.getString( "record" ), module.getEnergySkateParkModel().isRecordPath() );
        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( recordPath.isSelected() );
            }
        } );
        final JButton clearHistory = new JButton( EnergySkateParkStrings.getString( "clear" ) );
        clearHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPaths();
            }
        } );
        pathPanel.add( recordPath, BorderLayout.WEST );
        pathPanel.add( clearHistory, BorderLayout.EAST );
        addControlFullWidth( pathPanel );

        BufferedImage measuringTapeIcon = null;
        try {
            measuringTapeIcon = ImageLoader.loadBufferedImage( "images/ruler-thumb.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        final JCheckBox measuringTape = new JCheckBox( EnergySkateParkStrings.getString( "measuring.tape" ), module.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        addControlFullWidth( new IconComponent( measuringTape, measuringTapeIcon ) );

        final JCheckBox zeroPointPotential = new JCheckBox( EnergySkateParkStrings.getString( "potential.energy.reference" ) );
        zeroPointPotential.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getEnergyConservationCanvas().setZeroPointVisible( zeroPointPotential.isSelected() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                zeroPointPotential.setSelected( module.getEnergyConservationCanvas().isZeroPointVisible() );
            }
        } );

        BufferedImage potentialIcon = null;
        try {
            potentialIcon = ImageLoader.loadBufferedImage( "images/peicon.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addControlFullWidth( new IconComponent( zeroPointPotential, potentialIcon ) );

        addControlFullWidth( new GridLinesCheckBox( module ) );

//        getControlPane().setAnchor( GridBagConstraints.CENTER );
//        getControlPane().setFillNone();

        final JPanel piePanel = new PieChartControlPanel( module, this );
        addControlFullWidth( piePanel );

        final VerticalLayoutPanel chartPanel = new VerticalLayoutPanel();
        chartPanel.setFillNone();
        chartPanel.setAnchor( GridBagConstraints.WEST );
        chartPanel.setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "plot" ) ) );

        final JButton showChart = new JButton( EnergySkateParkStrings.getString( "energy.vs.time" ) );
        showChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyTimePlotVisible( true );
            }
        } );
        chartPanel.add( showChart );

        final JButton showEnergyPositionPlot = new JButton( EnergySkateParkStrings.getString( "energy.vs.position" ) );
        showEnergyPositionPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyPositionPlotVisible( true );
            }
        } );
        chartPanel.add( showEnergyPositionPlot );


        final JButton showBarChart = new JButton( EnergySkateParkStrings.getString( "bar.graph" ) );
        showBarChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setBarChartVisible( true );
            }
        } );
        chartPanel.add( showBarChart );
        addControlFullWidth( chartPanel );
//        addControl( chartPanel );
        addControlFullWidth( getLocationPanel( module ) );

        final FrictionControl frictionControl = new FrictionControl( module );
        final JComponent clearHeatButton = new ClearHeatButton( module );

        AdvancedPanel frictionPanel = new AdvancedPanel( EnergySkateParkStrings.getString( "friction" ), EnergySkateParkStrings.getString( "hide.friction" ) );
        frictionPanel.addControl( frictionControl );
        frictionControl.getModelSlider().setBorder( null );
        frictionPanel.addControl( clearHeatButton );
//        addControl( frictionControl );
//        addControl( clearHeatButton );
        addControl( frictionPanel );

        AdvancedPanel editSkaterPanel = new AdvancedPanel( EnergySkateParkStrings.getString( "edit.skater" ), EnergySkateParkStrings.getString( "hide.skater.properties" ) );
//        final ModelSlider restitution = new ModelSlider( "Coeff. of Restitution", "", 0, 1.0, 1.0 );
        final ModelSlider restitution = new ModelSlider( EnergySkateParkStrings.getString( "bounciness" ), "", 0, 1.0, 1.0 );
        restitution.setModelTicks( new double[]{0, 0.5, 1} );
        restitution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double rest = restitution.getValue();
                module.setCoefficientOfRestitution( rest );
            }
        } );
//        module.getClock().addClockListener( new ClockAdapter() {
//            public void clockTicked( ClockEvent event ) {
//                double rest = restitution.getValue();
//                module.setCoefficientOfRestitution( rest );
//            }
//        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().numBodies() > 0 ) {
                    restitution.setValue( module.getEnergySkateParkModel().bodyAt( 0 ).getCoefficientOfRestitution() );
                }
            }
        } );

        editSkaterPanel.addControl( restitution );

        final ModelSlider mass = new ModelSlider( EnergySkateParkStrings.getString( "mass" ), EnergySkateParkStrings.getString( "kg" ), 1, 200, 75 );
        mass.setModelTicks( new double[]{1, 75, 200} );
        mass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                EnergySkateParkModel model = module.getEnergySkateParkModel();
                for( int i = 0; i < model.numBodies(); i++ ) {
                    Body b = model.bodyAt( i );
                    b.setMass( mass.getValue() );
                }
            }
        } );
//        module.getClock().addClockListener( new ClockAdapter() {
//            public void clockTicked( ClockEvent event ) {
//                EnergyConservationModel model = module.getEnergyConservationModel();
//                for( int i = 0; i < model.numBodies(); i++ ) {
//                    Body b = model.bodyAt( i );
//                    b.setMass( mass.getValue() );
//                }
//            }
//        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().numBodies() > 0 ) {
                    mass.setValue( module.getEnergySkateParkModel().bodyAt( 0 ).getMass() );
                }
            }
        } );

        editSkaterPanel.addControl( mass );
        addControl( editSkaterPanel );


    }

    private VerticalLayoutPanel getLocationPanel( EnergySkateParkModule module ) {
//        ButtonGroup location = new ButtonGroup();
        return new LocationControlPanel( module );
    }

//    Planet[] planets = new Planet[]{new Planet.Earth(), new Planet.Moon(), new Planet.Jupiter()};


    private void resetSkater() {
        module.resetSkater();
    }

    private void reset() {
        module.confirmAndReset();
    }
}
