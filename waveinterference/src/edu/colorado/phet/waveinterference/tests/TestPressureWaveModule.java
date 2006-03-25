/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.PressureWaveGraphic;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:00:25 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestPressureWaveModule extends BasicWaveTestModule {
    private PressureWaveGraphic pressureWaveGraphic;
    private WaveModelGraphic waveModelGraphic;

    public TestPressureWaveModule() {
        super( "Test Pressure View" );
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        waveModelGraphic.setVisible( false );
        super.getPhetPCanvas().addScreenChild( waveModelGraphic );

        pressureWaveGraphic = new PressureWaveGraphic( getLattice() );
        pressureWaveGraphic.setOffset( 0, 0 );
        getPhetPCanvas().addScreenChild( pressureWaveGraphic );
        BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );

        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, pressureWaveGraphic.getDistBetweenCells() );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                pressureWaveGraphic.setSpaceBetweenCells( dim );
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        final JCheckBox showWave = new JCheckBox( "Show Lattice", waveModelGraphic.getVisible() );
        showWave.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                waveModelGraphic.setVisible( showWave.isSelected() );
            }
        } );
        final ModelSlider imageSize = new ModelSlider( "Particle Size", "pixels", 1, 36, pressureWaveGraphic.getImageSize() );
        imageSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setImageSize( (int)imageSize.getValue() );
            }
        } );
        final ModelSlider acceleration = new ModelSlider( "Particle Acceleration", "", 0, 10, pressureWaveGraphic.getParticleAcceleration() );
        acceleration.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setParticleAcceleration( acceleration.getValue() );
            }
        } );
        final ModelSlider maxVelocity = new ModelSlider( "Particle Max Velocity", "pixels/sec", 0, 30, pressureWaveGraphic.getMaxVelocity() );
        maxVelocity.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setMaxVelocity( maxVelocity.getValue() );
            }
        } );
        final ModelSlider friction = new ModelSlider( "Friction", "scale", 0, 1, pressureWaveGraphic.getFriction() );
        friction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setFriction( friction.getValue() );
            }
        } );

        controlPanel.addControl( cellDim );
        controlPanel.addControl( showWave );
        controlPanel.addControl( imageSize );
        controlPanel.addControl( acceleration );
        controlPanel.addControl( maxVelocity );
        controlPanel.addControl( friction );
        setControlPanel( controlPanel );
    }

    protected void step() {
        super.step();
        waveModelGraphic.update();
        pressureWaveGraphic.update();
    }

    public static void main( String[] args ) {
        PhetApplication app = new PhetApplication( args, "Feasibility Test", "Test", "0.01" );
        app.addModule( new TestPressureWaveModule() );
        app.startApplication();
    }

}
