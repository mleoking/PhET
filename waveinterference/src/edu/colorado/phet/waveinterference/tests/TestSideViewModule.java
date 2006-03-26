/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.WaveSideView;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:02:57 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestSideViewModule extends BasicWaveTestModule {
    private WaveSideView waveSideView;

    public TestSideViewModule() {
        super( "Side View" );

        waveSideView = new WaveSideView( getLattice(), new ExplicitCoordinates() );
        waveSideView.setOffset( 0, 300 );
        getPhetPCanvas().addScreenChild( waveSideView );
        BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );

        double distBetweenCells = waveSideView.getDistBetweenCells();
        System.out.println( "distBetweenCells = " + distBetweenCells );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, distBetweenCells );
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

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new TestSideViewModule() );
    }
}
