/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.PressureWaveGraphic;

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

        public TestPressureWaveModule() {
            super( "Test Pressure View" );

            pressureWaveGraphic = new PressureWaveGraphic( getLattice() );
            pressureWaveGraphic.setOffset( 0, 0 );
            getPhetPCanvas().addScreenChild( pressureWaveGraphic );
            BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );

            final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, pressureWaveGraphic.getDistBetweenCells() );
            cellDim.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int dim = (int)cellDim.getValue();
                    pressureWaveGraphic.setSpaceBetweenCells( dim );
                }
            } );
            controlPanel.addControl( cellDim );
            setControlPanel( controlPanel );
        }

        protected void step() {
            super.step();
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
