/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.model.splitoperator.SplitOperatorSolver;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 2:52:04 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class TestPropagator {
    private Timer timer;

    private WaveProcessor waveProcessor;
    private WavePacket wavePacket = new WavePacket();

    public TestPropagator() {
//        this.waveProcessor = new DampingOnly();
        this.waveProcessor = new DefaultWaveProcessor();
//        this.waveProcessor = new FourierChop();

        IWavePacketSolver solver = new RichardsonSolver( wavePacket );
//        IWavePacketSolver solver = new SplitOperatorSolver( wavePacket );
//        IWavePacketSolver solver = new RK4Solver( wavePacket );

        wavePacket.setSolver( solver );
        wavePacket.setTotalEnergy( new TotalEnergy( 0.8 * 10 ) );
        wavePacket.setPotentialEnergy( new ConstantPotential( 0 ) );
        wavePacket.setEnabled( true );

        int n = 1000;
        wavePacket.setDx( QTConstants.POSITION_RANGE.getLength() / n );
        wavePacket.update( null, null );
        final FastPlotter basicPlotter = new FastPlotter( "Wavefunction" );
        basicPlotter.setVisible( true );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                LightweightComplex[] psi = wavePacket.getWaveFunctionValues();
                ArrayList data = new ArrayList();
                for( int j = 0; j < psi.length; j++ ) {
                    LightweightComplex lightweightComplex = psi[j];
//                    data.add( new Point2D.Double( j, lightweightComplex.getReal() ) );
                    data.add( new Point2D.Double( j, lightweightComplex.getAbs() ) );
                }
                basicPlotter.setData( (Point2D.Double[])data.toArray( new Point2D.Double[0] ) );
                wavePacket.propagate();
                waveProcessor.process( wavePacket );
            }
        } );

    }

    public static void main( String[] args ) {
        new TestPropagator().start();
    }

    private void start() {
        timer.start();
    }


    static interface WaveProcessor {
        void process( WavePacket wavePacket );
    }

    public static class FourierChop implements WaveProcessor {
        private Damping damping = new Damping();
        FastPlotter origPhiPlot = new FastPlotter( "Phi" );
        FastPlotter phiLeftPlot = new FastPlotter( "PhiLeft" );
        FastPlotter phiRightPlot = new FastPlotter( "PhiRight" );
        FastPlotter psiLeftPlot = new FastPlotter( "Psi * Left" );
        FastPlotter psiRightPlot = new FastPlotter( "Psi * Right" );

        public FourierChop() {
            origPhiPlot.setVisible( true );
            phiLeftPlot.setVisible( true );
            phiRightPlot.setVisible( true );
            psiLeftPlot.setVisible( true );
            psiRightPlot.setVisible( true );
        }

        //could do this during split operator solver to save time.
        public void process( WavePacket wavePacket ) {
            LightweightComplex[] psi = wavePacket.getWaveFunctionValues();
//            damping.damp( psi );
            LightweightComplex[] phi = SplitOperatorSolver.forwardFFT( psi );
            LightweightComplex[]phiLeft = copy( phi );
            LightweightComplex[]phiRight = copy( phi );
            origPhiPlot.setData( toData( phi ) );
            for( int i = phiRight.length / 2; i < phiRight.length; i++ ) {
                phiRight[i].setValue( 0, 0 );
            }
            for( int i = 0; i < phiLeft.length / 2; i++ ) {
                phiLeft[i].setValue( 0, 0 );
            }
            phiLeftPlot.setData( toData( phiLeft ) );
            phiRightPlot.setData( toData( phiRight ) );
            LightweightComplex[] psiLeft = SplitOperatorSolver.inverseFFT( phiLeft );
            psiLeftPlot.setData( toData( psiLeft ) );
            LightweightComplex[] psiRight = SplitOperatorSolver.inverseFFT( phiRight );
            psiRightPlot.setData( toData( psiRight ) );
            int windowSize = 50;
            for( int i = 0; i < windowSize; i++ ) {
                psi[i].setValue( psiLeft[i].getReal(), psiLeft[i].getImaginary() );
                psi[psi.length - 1 - i].setValue( psiRight[psiRight.length - 1 - i].getReal(), psiRight[psiRight.length - 1 - i].getImaginary() );
            }
        }

        private LightweightComplex[] copy( LightweightComplex[] phi ) {
            LightweightComplex[]lwc = new LightweightComplex[phi.length];
            for( int i = 0; i < lwc.length; i++ ) {
                lwc[i] = new LightweightComplex( phi[i].getReal(), phi[i].getImaginary() );
            }
            return lwc;
        }

        private Point2D.Double[] toData( LightweightComplex[] phi ) {
            ArrayList data = new ArrayList();
            for( int j = 0; j < phi.length; j++ ) {
                //                    data.add( new Point2D.Double( j, lightweightComplex.getReal() ) );
                data.add( new Point2D.Double( j, phi[j].getAbs() ) );
            }
            return (Point2D.Double[])data.toArray( new Point2D.Double[0] );
        }
    }

    static class DefaultWaveProcessor implements WaveProcessor {
        private Damping damping = new Damping();
        private MyMeanFilter meanFilter = new MyMeanFilter();
        private WaveFilter waveFilter = new WaveFilter();

        public void process( WavePacket wavePacket ) {
            damping.damp( wavePacket.getWaveFunctionValues() );
            meanFilter.filter( wavePacket.getWaveFunctionValues(), 1 );
//            meanFilter.filter( wavePacket.getWaveFunctionValues(), 2 );
//            meanFilter.filter( wavePacket.getWaveFunctionValues(), 3 );
//            meanFilter.filter( wavePacket.getWaveFunctionValues(), 1 );
            waveFilter.filter( wavePacket.getWaveFunctionValues() );
        }
    }

    static class DampingOnly implements WaveProcessor {
        Damping damping = new Damping();

        public void process( WavePacket wavePacket ) {
            damping.damp( wavePacket.getWaveFunctionValues() );
        }
    }
}
