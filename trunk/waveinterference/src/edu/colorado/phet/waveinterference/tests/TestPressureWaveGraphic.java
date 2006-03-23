/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.PressureWaveGraphic;
import edu.colorado.phet.waveinterference.view.SimpleWavefunctionGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:57:27 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class TestPressureWaveGraphic extends PhetApplication {

    public static class TestPressureWaveModule extends BasicWaveTestModule {
        private PressureWaveGraphic pressureWaveGraphic;
        private SimpleWavefunctionGraphic simpleWavefunctionGraphic;

        public TestPressureWaveModule() {
            super( "Test Pressure View" );
            simpleWavefunctionGraphic = new SimpleWavefunctionGraphic( super.getLattice(), 10, 10, new IndexColorMap( super.getLattice() ) );
            simpleWavefunctionGraphic.setVisible( false );
            super.getPhetPCanvas().addScreenChild( simpleWavefunctionGraphic );

            pressureWaveGraphic = new PressureWaveGraphic( getLattice() );
            pressureWaveGraphic.setOffset( 0, 0 );
            getPhetPCanvas().addScreenChild( pressureWaveGraphic );
            BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );

            final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, pressureWaveGraphic.getDistBetweenCells() );
            cellDim.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int dim = (int)cellDim.getValue();
                    pressureWaveGraphic.setSpaceBetweenCells( dim );
                    simpleWavefunctionGraphic.setCellDimensions( dim, dim );
                }
            } );
            final JCheckBox showWave = new JCheckBox( "Show Lattice", simpleWavefunctionGraphic.getVisible() );
            showWave.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    simpleWavefunctionGraphic.setVisible( showWave.isSelected() );
                }
            } );
            final ModelSlider imageSize = new ModelSlider( "Particle Size", "pixels", 1, 36, pressureWaveGraphic.getImageSize() );
            imageSize.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    pressureWaveGraphic.setImageSize( (int)imageSize.getValue() );
                }
            } );
            controlPanel.addControl( cellDim );
            controlPanel.addControl( showWave );
            controlPanel.addControl( imageSize );
            setControlPanel( controlPanel );
        }

        protected void step() {
            super.step();
            simpleWavefunctionGraphic.update();
            pressureWaveGraphic.update();
        }
    }

    public TestPressureWaveGraphic( String[]args ) {
        super( args, "Feasibility Test", "Test", "0.01" );
        addModule( new TestPressureWaveGraphic.TestPressureWaveModule() );
    }

    public static void main( String[] args ) {
        new TestPressureWaveGraphic( args ).startApplication();
    }

}
