/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.WaveRotateControl;
import edu.colorado.phet.waveinterference.view.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:05:17 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestWaveRotateModule extends BasicWaveTestModule {
    private WaveSideView waveSideView;
    private WaveModelGraphic waveModelGraphic;
    private RotationGlyph rotationGlyph;
    public static final int WAVE_GRAPHIC_OFFSET = 75;

    public TestWaveRotateModule() {
        this( "Wave Rotate" );
    }

    protected TestWaveRotateModule( String name ) {
        super( name );

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        waveSideView = new WaveSideViewFull( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
        rotationGlyph = new RotationGlyph();
        RotationWaveGraphic rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( 50, 20 );
        getPhetPCanvas().addScreenChild( rotationWaveGraphic );

        waveSideView.setSpaceBetweenCells( waveModelGraphic.getCellDimensions().width );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveSideView.getDistBetweenCells() );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                waveSideView.setSpaceBetweenCells( dim );
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );

        WaveRotateControl waveRotateControl = new WaveRotateControl( rotationWaveGraphic );
        getControlPanel().addControl( cellDim );
        getControlPanel().addControl( waveRotateControl );
        updateLocations();
        rotationWaveGraphic.setRotation( 0 );
    }

    private void updateLocations() {
        waveSideView.setOffset( waveModelGraphic.getFullBounds().getX(), waveModelGraphic.getFullBounds().getCenterY() );
    }

    protected void step() {
        super.step();
        waveSideView.update();
        waveModelGraphic.update();
        updateLocations();
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "Wave Rotate Test", "", "" );
        phetApplication.addModule( new TestWaveRotateModule() );
        phetApplication.startApplication();
    }
}
