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
//        this.waveProcessor = new DefaultWaveProcessor();
        this.waveProcessor = new FourierChop();

        IWavePacketSolver solver = new RichardsonSolver( wavePacket );
//        IWavePacketSolver solver = new SplitOperatorSolver( wavePacket );
//        IWavePacketSolver solver = new RK4Solver( wavePacket );

        wavePacket.setSolver( solver );
        wavePacket.setTotalEnergy( new TotalEnergy( 0.8 ) );
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
        FastPlotter newPhiPlot = new FastPlotter( "Modified Phi" );
        FastPlotter origPhiPlot = new FastPlotter( "Original Phi" );

        public FourierChop() {
            newPhiPlot.setVisible( true );
            origPhiPlot.setVisible( true );
        }

        //could do this during split operator solver to save time.
        public void process( WavePacket wavePacket ) {
            damping.damp( wavePacket.getWaveFunctionValues() );
            LightweightComplex[] phi = SplitOperatorSolver.forwardFFT( wavePacket.getWaveFunctionValues() );
            origPhiPlot.setData( toData( phi ) );
            for( int i = 0; i < phi.length; i++ ) {
                LightweightComplex lightweightComplex = phi[i];

//            if (i<50||i>phi.length-50){
//            }else{
//                lightweightComplex.setValue( 0,0);
//            }
                if( i > phi.length / 8 ) {
                    lightweightComplex.setValue( 0, 0 );
                }
            }
            newPhiPlot.setData( toData( phi ) );
//        waveFilter.filter( phi);
            LightweightComplex[]psi2 = SplitOperatorSolver.inverseFFT( phi );
            for( int i = 0; i < psi2.length; i++ ) {
                wavePacket.getWaveFunctionValues()[i].setValue( psi2[i].getReal(), psi2[i].getImaginary() );
            }
//        for( int i = psi2.length-100; i < psi2.length; i++ ) {
//            wavePacket.getWaveFunctionValues()[i].setValue( psi2[i].getReal(), psi2[i].getImaginary() );
//        }
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
            meanFilter.filter( wavePacket.getWaveFunctionValues(), 10 );
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
