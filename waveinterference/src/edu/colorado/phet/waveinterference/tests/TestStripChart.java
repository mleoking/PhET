/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.IntensityReader;
import edu.colorado.phet.waveinterference.view.SimpleLatticeGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 1:32:18 PM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

public class TestStripChart extends PhetApplication {
    public static class TestStripChartModule extends BasicWaveTestModule {
        private SimpleLatticeGraphic simpleLatticeGraphic;
        private IntensityReader intensityReader;

        public TestStripChartModule() {
            super( "Test Strip Chart" );

            simpleLatticeGraphic = new SimpleLatticeGraphic( super.getLattice(), 10, 10, new IndexColorMap( super.getLattice() ) );
            super.getPhetPCanvas().addScreenChild( simpleLatticeGraphic );
            intensityReader = new IntensityReader( simpleLatticeGraphic );
            super.getPhetPCanvas().addScreenChild( intensityReader );

            BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );
            final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, simpleLatticeGraphic.getCellDimensions().width );
            cellDim.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int dim = (int)cellDim.getValue();
                    simpleLatticeGraphic.setCellDimensions( dim, dim );
                }
            } );
            controlPanel.addControl( cellDim );
            setControlPanel( controlPanel );
        }

        protected void step() {
            super.step();
            simpleLatticeGraphic.update();
//            intensityReader.
        }

    }

    public TestStripChart( String[]args ) {
        super( args, "Feasibility Test", "Test", "0.01" );
        addModule( new TestStripChartModule() );
    }


    public static void main( String[] args ) {
        new TestStripChart( args ).startApplication();
    }
}
