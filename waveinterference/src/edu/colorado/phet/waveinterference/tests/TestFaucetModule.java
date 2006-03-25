/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.FaucetGraphic;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.colorado.phet.waveinterference.view.WaveSideView;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 12:12:49 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestFaucetModule extends BasicWaveTestModule {
    private FaucetGraphic faucetGraphic;
    private WaveModelGraphic waveModelGraphic;
    private WaveSideView waveSideView;

    public TestFaucetModule() {
        super( "Faucet" );
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( waveModelGraphic );

        BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveModelGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        controlPanel.addControl( cellDim );
        setControlPanel( controlPanel );
        waveSideView = new WaveSideView( getLattice() );
        waveSideView.setOffset( 100, waveModelGraphic.getFullBounds().getCenterY() );
        waveSideView.setSpaceBetweenCells( waveModelGraphic.getCellDimensions().width );
        getPhetPCanvas().addScreenChild( waveSideView );
        waveModelGraphic.setOffset( 100, 0 );
        faucetGraphic = new FaucetGraphic( getPeriod() );
        double faucetHeight = 50;
        faucetGraphic.setOffset( 0, waveModelGraphic.getFullBounds().getCenterY() - faucetGraphic.getFullBounds().getHeight() / 2 - faucetHeight );
        getPhetPCanvas().addScreenChild( faucetGraphic );
        setOscillatorRadius( 2 );
    }

    protected void step() {
        super.step();
        faucetGraphic.step();
        waveModelGraphic.update();
        waveSideView.update();
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "Test Faucet", "", "" );
        phetApplication.addModule( new TestFaucetModule() );
        phetApplication.startApplication();
    }
}
