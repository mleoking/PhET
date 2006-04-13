/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.ExplicitCoordinates;
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

        waveSideView = new WaveSideView( getWaveModel(), new ExplicitCoordinates() );
        waveSideView.setOffset( 0, 300 );
        getPhetPCanvas().addScreenChild( waveSideView );

        double distBetweenCells = waveSideView.getDistBetweenCells();
        System.out.println( "distBetweenCells = " + distBetweenCells );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, distBetweenCells );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                waveSideView.setSpaceBetweenCells( cellDim.getValue() );
            }
        } );
        getControlPanel().addControl( cellDim );
    }

    protected void step() {
        super.step();
        waveSideView.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestSideViewModule() );
    }
}
