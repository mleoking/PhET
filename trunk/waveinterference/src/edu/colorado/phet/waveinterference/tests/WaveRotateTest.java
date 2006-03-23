/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.SimpleWavefunctionGraphic;
import edu.colorado.phet.waveinterference.view.WaveSideView;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 9:25:10 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class WaveRotateTest extends PhetApplication {

    public WaveRotateTest( String[] args ) {
        super( args, "Wave Rotate Test", "", "" );
        addModule( new WaveRotateTestModule() );
    }

    static class WaveRotateTestModule extends BasicWaveTestModule {
        private WaveSideView waveSideView;
        private SimpleWavefunctionGraphic simpleWavefunctionGraphic;

        public WaveRotateTestModule() {
            super( "Wave Rotate Test" );

            waveSideView = new WaveSideView( getLattice() );
            waveSideView.setOffset( 0, 300 );

            simpleWavefunctionGraphic = new SimpleWavefunctionGraphic( super.getLattice(), 10, 10, new IndexColorMap( super.getLattice() ) );

            super.getPhetPCanvas().addScreenChild( simpleWavefunctionGraphic );
            getPhetPCanvas().addScreenChild( waveSideView );
            waveSideView.setSpaceBetweenCells( simpleWavefunctionGraphic.getCellDimensions().width );

            BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );
            final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveSideView.getDistBetweenCells() );
            cellDim.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int dim = (int)cellDim.getValue();
                    waveSideView.setSpaceBetweenCells( dim );
                    simpleWavefunctionGraphic.setCellDimensions( dim, dim );
                }
            } );
            controlPanel.addControl( cellDim );
            setControlPanel( controlPanel );
            updateLocations();
        }

        private void updateLocations() {
            waveSideView.setOffset( simpleWavefunctionGraphic.getFullBounds().getX(), simpleWavefunctionGraphic.getFullBounds().getCenterY() );
        }

        protected void step() {
            super.step();
            waveSideView.update();
            simpleWavefunctionGraphic.update();
            updateLocations();
        }
    }

    public static void main( String[] args ) {
        new WaveRotateTest( args ).startApplication();
    }
}
