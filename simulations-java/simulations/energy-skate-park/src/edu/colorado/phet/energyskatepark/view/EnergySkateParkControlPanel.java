/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.IconComponent;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.physics.Particle;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:37:21 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class EnergySkateParkControlPanel extends ControlPanel {
    private EnergySkateParkModule module;
    private PieChartControlPanel piePanel;

    public EnergySkateParkControlPanel( final EnergySkateParkModule module ) {
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


        BufferedImage measuringTapeIcon = null;
        try {
            measuringTapeIcon = ImageLoader.loadBufferedImage( "energy-skate-park/images/ruler-thumb.png" );
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
        getMeasuringTapeNode( module ).addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                measuringTape.setSelected( getMeasuringTapeNode( module ).getVisible() );
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
            potentialIcon = ImageLoader.loadBufferedImage( "energy-skate-park/images/peicon.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addControlFullWidth( new IconComponent( zeroPointPotential, potentialIcon ) );


        try {
            JButton chooseCharacter = new JButton( "Choose Character" );
            chooseCharacter.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new ChooseCharacterDialog( module ).setVisible( true );
                }
            } );
            addControl( new IconComponent( chooseCharacter, BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "energy-skate-park/images/skater3.png" ), chooseCharacter.getPreferredSize().height ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        addControlFullWidth( new GridLinesCheckBox( module ) );
        addControlFullWidth( new PathRecordContol( module ) );
//        getControlPane().setAnchor( GridBagConstraints.CENTER );
//        getControlPane().setFillNone();

        piePanel = new PieChartControlPanel( module, this );
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
                module.setBounciness( restitution.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    restitution.setValue( module.getEnergySkateParkModel().getBody( 0 ).getBounciness() );//todo: refactor to listener pattern.
                }
            }
        } );


        final ModelSlider mass = new ModelSlider( EnergySkateParkStrings.getString( "mass" ), EnergySkateParkStrings.getString( "kg" ), 1, 200, 75 );
        mass.setModelTicks( new double[]{1, 75, 200} );
        mass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setMass( mass.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    mass.setValue( module.getEnergySkateParkModel().getBody( 0 ).getMass() );
                }
            }
        } );

        final ModelSlider stickiness = new ModelSlider( "Stickiness", "", 0.01, 5, Body.DEFAULT_STICKINESS );
        stickiness.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setStickiness( stickiness.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    stickiness.setValue( module.getEnergySkateParkModel().getBody( 0 ).getStickiness() );
                }
            }
        } );

        JButton revertToDefaults = new JButton( "Restore Defaults" );
        revertToDefaults.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMass( module.getSkaterCharacter().getMass() );
                module.setBounciness( Particle.DEFAULT_ELASTICITY );
                setStickiness( Body.DEFAULT_STICKINESS );
            }
        } );

        editSkaterPanel.addControl( restitution );
        editSkaterPanel.addControl( mass );
        editSkaterPanel.addControl( stickiness );
        editSkaterPanel.addControl( revertToDefaults );

        addControl( editSkaterPanel );
    }

    private void setStickiness( double stickinessValue ) {
        Body.staticSticky = stickinessValue;
        for( int i = 0; i < Body.particles.size(); i++ ) {
            Body body = (Body)Body.particles.get( i );
            body.setStickiness( Body.staticSticky );
        }
    }

    private void setMass( double massValue ) {
        EnergySkateParkModel model = module.getEnergySkateParkModel();
        for( int i = 0; i < model.getNumBodies(); i++ ) {
            Body b = model.getBody( i );
            b.setMass( massValue );
        }
    }

    private PNode getMeasuringTapeNode( EnergySkateParkModule module ) {
        return module.getEnergyConservationCanvas().getRootNode().getMeasuringTapeNode();
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

    public void update() {
        piePanel.update();
    }
}
