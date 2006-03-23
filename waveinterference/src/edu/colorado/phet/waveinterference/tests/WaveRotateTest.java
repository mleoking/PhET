/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

    class WaveRotateTestModule extends BasicWaveTestModule {
        private WaveSideView waveSideView;
        private SimpleWavefunctionGraphic simpleWavefunctionGraphic;
        private RotationGlyph rotationGlyph;

        public WaveRotateTestModule() {
            super( "Wave Rotate Test" );

            waveSideView = new WaveSideViewFull( getLattice() );
            waveSideView.setOffset( 75, 300 );

            simpleWavefunctionGraphic = new SimpleWavefunctionGraphic( super.getLattice(), 10, 10, new IndexColorMap( super.getLattice() ) );
            simpleWavefunctionGraphic.setOffset( 75, 0 );
            rotationGlyph = new RotationGlyph();
            getPhetPCanvas().addScreenChild( rotationGlyph );
            getPhetPCanvas().addScreenChild( simpleWavefunctionGraphic );
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
            final ModelSlider rotate = new ModelSlider( "View Angle", "radians", 0, Math.PI / 2, 0 );
            rotate.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setRotation( rotate.getValue() );
                }
            } );
            rotate.getSlider().addMouseListener( new MouseListener() {
                public void mouseClicked( MouseEvent e ) {
                }

                public void mouseEntered( MouseEvent e ) {
                }

                public void mouseExited( MouseEvent e ) {
                }

                public void mousePressed( MouseEvent e ) {
                    setGlyphVisible( true );
                }

                public void mouseReleased( MouseEvent e ) {
                    setGlyphVisible( false );
                }
            } );

            controlPanel.addControl( cellDim );
            controlPanel.addControl( rotate );
            setControlPanel( controlPanel );
            updateLocations();
            setRotation( 0 );
        }

        void setGlyphVisible( boolean vis ) {
            if( vis ) {
                rotationGlyph.setPrimaryHeight( simpleWavefunctionGraphic.getFullBounds().getHeight() );
                rotationGlyph.setPrimaryWidth( simpleWavefunctionGraphic.getFullBounds().getWidth() );
                rotationGlyph.setOffset( 75, simpleWavefunctionGraphic.getFullBounds().getCenterY() - rotationGlyph.getFullBounds().getHeight() );
            }
            rotationGlyph.setVisible( vis );
//            waveSideView.setVisible( !vis );
//            simpleWavefunctionGraphic.setVisible( !vis );
        }

        private void setRotation( double value ) {
            setGlyphVisible( true );
            rotationGlyph.setAngle( value );
            rotationGlyph.setOffset( 75, simpleWavefunctionGraphic.getFullBounds().getCenterY() - rotationGlyph.getSurfaceHeight() );
            if( value == 0 ) {
                waveSideView.setVisible( false );
                simpleWavefunctionGraphic.setVisible( true );
            }
            else if( value >= Math.PI / 2 - 0.02 ) {
                setGlyphVisible( false );
                waveSideView.setVisible( true );
                simpleWavefunctionGraphic.setVisible( false );
            }
            else {
                waveSideView.setVisible( false );
                simpleWavefunctionGraphic.setVisible( false );
            }
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
