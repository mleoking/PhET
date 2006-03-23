/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.waveinterference.model.ClassicalWavePropagator;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.Potential;
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

    public static class TestWaveModule extends PiccoloModule {
        private Lattice2D lattice2D;
        private ClassicalWavePropagator classicalWavePropagator;
        private WaveSideView waveSideView;
        private double period = 2;

        public TestWaveModule() {
            super( "Test Side View", new SwingClock( 30, 0.1 ) );
            PhetPCanvas panel = new PhetPCanvas();
            setSimulationPanel( panel );
            lattice2D = new Lattice2D( 80, 80 );

            waveSideView = new WaveSideView( lattice2D );
            waveSideView.setOffset( 300, 300 );
            panel.addScreenChild( waveSideView );
            classicalWavePropagator = new ClassicalWavePropagator( new Potential() );
            addModelElement( new ModelElement() {
                public void stepInTime( double dt ) {
                    step();
                }
            } );
            setControlPanel( new TestSideView.TestWaveModule.TestWaveControlPanel( this ) );
        }

        static class TestWaveControlPanel extends ControlPanel {
            private TestSideView.TestWaveModule testWaveModule;

            public TestWaveControlPanel( final TestSideView.TestWaveModule testWaveModule ) {
                this.testWaveModule = testWaveModule;
                final ModelSlider modelSlider = new ModelSlider( "Period", "sec", 0.01, 10, testWaveModule.period );
                modelSlider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        testWaveModule.period = modelSlider.getValue();
                    }
                } );
                addControl( modelSlider );

                final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, testWaveModule.waveSideView.getDistBetweenCells() );
                cellDim.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        int dim = (int)cellDim.getValue();
                        testWaveModule.waveSideView.setSpaceBetweenCells( dim );
                    }
                } );
                addControl( cellDim );

                final ModelSlider latticeWidth = new ModelSlider( "Lattice Width", "cells", 10, 300, testWaveModule.lattice2D.getWidth() );
                latticeWidth.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        int width = (int)latticeWidth.getValue();
                        testWaveModule.setLatticeSize( width, testWaveModule.lattice2D.getHeight() );
                    }
                } );
                addControl( latticeWidth );

                final ModelSlider latticeHeight = new ModelSlider( "Lattice Height", "cells", 10, 300, testWaveModule.lattice2D.getWidth() );
                latticeHeight.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        int height = (int)latticeHeight.getValue();
                        testWaveModule.setLatticeSize( testWaveModule.lattice2D.getWidth(), height );
                    }
                } );
                addControl( latticeHeight );
            }
        }

        private void setLatticeSize( int width, int height ) {
            lattice2D.setSize( width, height );
            classicalWavePropagator.setSize( width, height );
        }

        private void step() {
            double t = System.currentTimeMillis() / 1000.0;
            double frequency = 1 / period;
            for( int i = 20; i <= 30; i++ ) {
                for( int j = 20; j <= 30; j++ ) {
                    if( Math.sqrt( ( i - 25 ) * ( i - 25 ) + ( j - 25 ) * ( j - 25 ) ) < 5 ) {
                        double value = Math.cos( 2 * Math.PI * frequency * t );
                        lattice2D.setValue( i, j, (float)value );
                        classicalWavePropagator.setBoundaryCondition( i, j, (float)value );
                    }
                }
            }
            classicalWavePropagator.propagate( lattice2D );
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
