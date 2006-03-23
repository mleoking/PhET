/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.WaveSideView;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:57:27 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class TestSideView extends PhetApplication {

    public static class TestWaveModule extends BasicWaveTestModule {
        private WaveSideView waveSideView;

        public TestWaveModule() {
            super( "Test Side View" );

            waveSideView = new WaveSideView( getLattice() );
            waveSideView.setOffset( 0, 300 );
            getPhetPCanvas().addScreenChild( waveSideView );
            BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );

            final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveSideView.getDistBetweenCells() );
            cellDim.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int dim = (int)cellDim.getValue();
                    waveSideView.setSpaceBetweenCells( dim );
                }
            } );
            controlPanel.addControl( cellDim );
            setControlPanel( controlPanel );
        }

        protected void step() {
            super.step();
            waveSideView.update();
        }
    }

    public TestSideView( String[]args ) {
        super( args, "Feasibility Test", "Test", "0.01" );
        addModule( new TestSideView.TestWaveModule() );
    }

    public static void main( String[] args ) {
        new TestSideView( args ).startApplication();
    }

}
