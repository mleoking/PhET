/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
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
    private final int WAVE_GRAPHIC_OFFSET = 75;

    public TestWaveRotateModule() {
        this( "Wave Rotate" );
    }

    protected TestWaveRotateModule( String name ) {
        super( name );


        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        waveModelGraphic.setOffset( WAVE_GRAPHIC_OFFSET, 0 );

        waveSideView = new WaveSideViewFull( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
        waveSideView.setOffset( WAVE_GRAPHIC_OFFSET, 300 );

        rotationGlyph = new RotationGlyph();
        getPhetPCanvas().addScreenChild( rotationGlyph );
        getPhetPCanvas().addScreenChild( waveModelGraphic );
        getPhetPCanvas().addScreenChild( waveSideView );

        waveSideView.setSpaceBetweenCells( waveModelGraphic.getCellDimensions().width );
        BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveSideView.getDistBetweenCells() );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                waveSideView.setSpaceBetweenCells( dim );
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        final ModelSlider rotate = new ModelSlider( "View Angle", "radians", 0, Math.PI / 2, 0 );
        rotate.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setRotation( rotate.getValue() );
            }
        } );
        controlPanel.addControl( cellDim );
        controlPanel.addControl( rotate );
        setControlPanel( controlPanel );
        updateLocations();
        setRotation( 0 );
    }

    private void setRotation( double value ) {
        updateRotationGlyph( value );
        if( value == 0 ) {
            rotationGlyph.setVisible( false );
            waveSideView.setVisible( false );
            waveModelGraphic.setVisible( true );
        }
        else if( value >= Math.PI / 2 - 0.02 ) {
            rotationGlyph.setVisible( false );
            waveSideView.setVisible( true );
            waveModelGraphic.setVisible( false );
        }
        else {
            rotationGlyph.setVisible( true );
            waveSideView.setVisible( false );
            waveModelGraphic.setVisible( false );
        }
    }

    private void updateRotationGlyph( double value ) {
        rotationGlyph.setPrimaryHeight( waveModelGraphic.getFullBounds().getHeight() );
        rotationGlyph.setPrimaryWidth( waveModelGraphic.getFullBounds().getWidth() );
        rotationGlyph.setAngle( value );
        rotationGlyph.setOffset( WAVE_GRAPHIC_OFFSET, waveModelGraphic.getFullBounds().getCenterY() - rotationGlyph.getSurfaceHeight() );
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
