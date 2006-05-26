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
import edu.colorado.phet.ec3.controls.GravitySlider;
import edu.colorado.phet.ec3.controls.PieChartControlPanel;
import edu.colorado.phet.ec3.controls.PlanetButton;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;

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
    private EC3Module module;
    private JCheckBox showBackgroundCheckbox;
    private PlanetButton[] planetButtons;

    public EC3ControlPanel( final EC3Module module ) {
        this.module = module;
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

        JPanel pathPanel = new JPanel( new BorderLayout() );
        pathPanel.setBorder( BorderFactory.createTitledBorder( "Path" ) );
        final JCheckBox recordPath = new JCheckBox( "Record", module.getEnergyConservationModel().isRecordPath() );
        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( recordPath.isSelected() );
            }
        } );
        final JButton clearHistory = new JButton( "Clear" );
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

        final JCheckBox measuringTape = new JCheckBox( "Measuring Tape", module.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        addControlFullWidth( new IconComponent( measuringTape, measuringTapeIcon ) );

        final JCheckBox zeroPointPotential = new JCheckBox( "Potential Energy Reference" );
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

//        getControlPane().setAnchor( GridBagConstraints.CENTER );
//        getControlPane().setFillNone();

        final JPanel piePanel = new PieChartControlPanel( module, this );
        addControlFullWidth( piePanel );

        final VerticalLayoutPanel chartPanel = new VerticalLayoutPanel();
        chartPanel.setFillNone();
        chartPanel.setAnchor( GridBagConstraints.WEST );
        chartPanel.setBorder( BorderFactory.createTitledBorder( "Plot" ) );

        final JButton showChart = new JButton( "Energy vs. Time" );
        showChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyTimePlotVisible( true );
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
//        addControl( chartPanel );
        addControlFullWidth( getLocationPanel( module ) );

        final FrictionControl frictionControl = new FrictionControl( module );
        final JComponent clearHeatButton = new ClearHeatButton( module );

        AdvancedPanel frictionPanel = new AdvancedPanel( "Friction >>", "Hide Friction<<" );
        frictionPanel.addControl( frictionControl );
        frictionControl.getModelSlider().setBorder( null );
        frictionPanel.addControl( clearHeatButton );
//        addControl( frictionControl );
//        addControl( clearHeatButton );
        addControl( frictionPanel );

        AdvancedPanel editSkaterPanel = new AdvancedPanel( "Edit Skater >>", "Hide Skater Properties<<" );
//        final ModelSlider restitution = new ModelSlider( "Coeff. of Restitution", "", 0, 1.0, 1.0 );
        final ModelSlider restitution = new ModelSlider( "Bounciness", "", 0, 1.0, 1.0 );
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
                if( module.getEnergyConservationModel().numBodies() > 0 ) {
                    restitution.setValue( module.getEnergyConservationModel().bodyAt( 0 ).getCoefficientOfRestitution() );
                }
            }
        } );

        editSkaterPanel.addControl( restitution );

        final ModelSlider mass = new ModelSlider( "Mass", "kg", 0, 200, 75 );
        mass.setModelTicks( new double[]{0, 75, 200} );
        mass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                EnergyConservationModel model = module.getEnergyConservationModel();
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
                if( module.getEnergyConservationModel().numBodies() > 0 ) {
                    mass.setValue( module.getEnergyConservationModel().bodyAt( 0 ).getMass() );
                }
            }
        } );

        editSkaterPanel.addControl( mass );
        addControl( editSkaterPanel );


    }

    private VerticalLayoutPanel getLocationPanel( EC3Module module ) {
//        ButtonGroup location = new ButtonGroup();
        PlanetButton space = new PlanetButton( module, new Planet.Space(), false );
        PlanetButton moon = new PlanetButton( module, new Planet.Moon(), false );
        PlanetButton earth = new PlanetButton( module, new Planet.Earth(), true );
        PlanetButton jupiter = new PlanetButton( module, new Planet.Jupiter(), false );
        planetButtons = new PlanetButton[]{space, moon, earth, jupiter};
//        location.add( space );
//        location.add( moon );
//        location.add( earth );
//        location.add( jupiter );
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        verticalLayoutPanel.setBorder( BorderFactory.createTitledBorder( "Location" ) );
        verticalLayoutPanel.setFillHorizontal();
        showBackgroundCheckbox = new JCheckBox( "Show Background", true );
        verticalLayoutPanel.add( showBackgroundCheckbox );
        verticalLayoutPanel.add( space );
        verticalLayoutPanel.add( moon );
        verticalLayoutPanel.add( earth );
        verticalLayoutPanel.add( jupiter );
        final JComponent gravitySlider = new GravitySlider( module );
        verticalLayoutPanel.addFullWidth( gravitySlider );

        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                synchronizePlanet();
            }

        } );
        synchronizePlanet();
        new Planet.Earth().apply( module );
        return verticalLayoutPanel;
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
//            planetButtons[i].setSelected( module.getEnergyConservationModel().getGravity() == planet.getGravity() );
            boolean match = module.getEnergyConservationModel().getGravity() == planet.getGravity();
            planetButtons[i].setSelected( match );
        }
        if( !matched ) {
            module.getEnergyConservationCanvas().getRootNode().clearBackground();
        }
    }

    private void resetSkater() {
        module.resetSkater();
    }

    private void reset() {
        module.confirmAndReset();
    }
}
