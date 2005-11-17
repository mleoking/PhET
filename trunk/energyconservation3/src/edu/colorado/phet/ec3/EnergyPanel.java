/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
    private final JCheckBox showBackgroundCheckbox;
    private PlanetButton[] planetButtons;

    public EnergyPanel( final EC3Module module ) {
        super( module );
        super.removeTitle();
        this.module = module;
        getControlPane().setAnchor( GridBagConstraints.WEST );
        getControlPane().setFillNone();
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        addControl( reset );

        JButton resetSkater = new JButton( "Return Skater" );
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

        getControlPane().setAnchor( GridBagConstraints.CENTER );
        getControlPane().setFillNone();

        final JPanel piePanel = new PieChartPanel( module, this );
        addControlFullWidth( piePanel );

        final VerticalLayoutPanel chartPanel = new VerticalLayoutPanel();
        chartPanel.setFillNone();
        chartPanel.setAnchor( GridBagConstraints.WEST );
        chartPanel.setBorder( BorderFactory.createTitledBorder( "Plot" ) );

        final JButton showChart = new JButton( "Energy vs. Time" );
        showChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPlotVisible( true );
            }
        } );
        chartPanel.add( showChart );

        final JButton showEnergyPositionPlot = new JButton( "Energy vs. Position" );
        showEnergyPositionPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyPositionPlotVisible( true );
            }
        } );
        chartPanel.add( showEnergyPositionPlot );


        final JButton showBarChart = new JButton( "Bar Graph" );
        showBarChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setBarChartVisible( true );
            }
        } );
        chartPanel.add( showBarChart );

        addControlFullWidth( chartPanel );

        final ModelSlider modelSlider = new ModelSlider( "Coefficient of Friction", "", 0, 0.04, 0.0, new DecimalFormat( "0.000" ), new DecimalFormat( "0.000" ) );
//        final ModelSlider modelSlider = new ModelSlider( "Coefficient of Friction", "", 0, 1.0, 0.0, new DecimalFormat( "0.000" ), new DecimalFormat( "0.000" ) );
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

        final ModelSlider mass = new ModelSlider( "Mass", "kg", 0, 200, 75 );
        mass.setModelTicks( new double[]{0, 75, 200} );
        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                EnergyConservationModel model = module.getEnergyConservationModel();
                for( int i = 0; i < model.numBodies(); i++ ) {
                    Body b = model.bodyAt( i );
                    b.setMass( mass.getValue() );
                }
            }
        } );
        addControl( mass );

        ButtonGroup location = new ButtonGroup();


        PlanetButton space = new PlanetButton( module, new Planet.Space(), false );
        PlanetButton moon = new PlanetButton( module, new Planet.Moon(), false );
        PlanetButton earth = new PlanetButton( module, new Planet.Earth(), true );
        PlanetButton jupiter = new PlanetButton( module, new Planet.Jupiter(), false );
        planetButtons = new PlanetButton[]{space, moon, earth, jupiter};
        location.add( space );
        location.add( moon );
        location.add( earth );
        location.add( jupiter );
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        verticalLayoutPanel.setBorder( BorderFactory.createTitledBorder( "Background" ) );
        verticalLayoutPanel.setFillHorizontal();
        showBackgroundCheckbox = new JCheckBox( "Show Background", false );
        verticalLayoutPanel.add( showBackgroundCheckbox );
        verticalLayoutPanel.add( space );
        verticalLayoutPanel.add( moon );
        verticalLayoutPanel.add( earth );
        verticalLayoutPanel.add( jupiter );
        addControlFullWidth( verticalLayoutPanel );

        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                synchronizePlanet();
            }

        } );
        synchronizePlanet();
        new Planet.Earth().apply( module );
    }

//    Planet[] planets = new Planet[]{new Planet.Earth(), new Planet.Moon(), new Planet.Jupiter()};

    private void synchronizePlanet() {
        module.getEnergyConservationCanvas().getRootNode().getBackground().setVisible( showBackgroundCheckbox.isSelected() );
        boolean matched = false;
        for( int i = 0; i < planetButtons.length; i++ ) {
            Planet planet = planetButtons[i].getPlanet();
            if( module.getEnergyConservationModel().getGravity() == planet.getGravity() ) {
                planet.apply( module );
                matched = true;

            }
            planetButtons[i].setSelected( module.getEnergyConservationModel().getGravity() == planet.getGravity() );
        }
        if( !matched ) {
            module.getEnergyConservationCanvas().getRootNode().clearBackground();
        }
    }

    private void resetSkater() {
        module.resetSkater();
    }

    private void reset() {
        module.reset();
    }
}
