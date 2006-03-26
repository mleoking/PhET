/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

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
        waveSideView = new WaveSideView( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
//        waveSideView.setOffset( 100, waveModelGraphic.getFullBounds().getCenterY() );
//        waveSideView.setSpaceBetweenCells( waveModelGraphic.getCellDimensions().width );
        getPhetPCanvas().addScreenChild( waveSideView );
        waveModelGraphic.setOffset( 300, 100 );
        faucetGraphic = new FaucetGraphic( getWaveModel(), getOscillator(), waveModelGraphic.getLatticeScreenCoordinates() );

        getPhetPCanvas().addScreenChild( faucetGraphic );
        setOscillatorRadius( 2 );

        final ModelSlider dropHeight = new ModelSlider( "Drop Height", "pixels", 0, 500, faucetGraphic.getDropHeight() );
        dropHeight.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                faucetGraphic.setDropHeight( dropHeight.getValue() );
            }
        } );
        getControlPanel().addControl( dropHeight );

        final ModelSlider dropSpeed = new ModelSlider( "Drop Speed", "pixels/sec", 0, 500, faucetGraphic.getDropSpeed() );
        dropSpeed.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                faucetGraphic.setDropSpeed( dropSpeed.getValue() );
            }
        } );
        getControlPanel().addControl( dropSpeed );
    }

    protected void step() {
        super.step();
        faucetGraphic.step();
        waveModelGraphic.update();
        waveSideView.update();
    }

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new TestFaucetModule() );
    }
}
