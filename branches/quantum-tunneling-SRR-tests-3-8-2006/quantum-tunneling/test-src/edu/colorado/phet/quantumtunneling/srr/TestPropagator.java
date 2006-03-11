/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.ConstantPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.model.RichardsonSolver;
import edu.colorado.phet.quantumtunneling.model.rungekutta.RK4Solver;
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
    private WaveFilter waveFilter;
    private Damping damping = new Damping();
    private MyMeanFilter meanFilter = new MyMeanFilter();

    public TestPropagator() {
        final FastPlotter basicPlotter = new FastPlotter();
        basicPlotter.setVisible( true );
        phiPlotter.setVisible( true );
        final WavePacket wavePacket = new WavePacket();
        wavePacket.setSolver( new RichardsonSolver( wavePacket ) );
//        wavePacket.setSolver( new SplitOperatorSolver( wavePacket ) );
//        wavePacket.setSolver( new RK4Solver( wavePacket ) );
        wavePacket.setTotalEnergy( new TotalEnergy( 0.8 ) );
        wavePacket.setPotentialEnergy( new ConstantPotential( 0 ) );
        wavePacket.setEnabled( true );
        wavePacket.update( null, null );
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
                postProcess( wavePacket );
//                fourierChop( wavePacket );
            }


        } );
        wavePacket.update( null, null );
        int n = 1000;
        wavePacket.setDx( QTConstants.POSITION_RANGE.getLength() / n );
        waveFilter = new WaveFilter();
    }

    public static void main( String[] args ) {
        new TestPropagator().start();
    }

    private void start() {
        timer.start();
    }

    FastPlotter phiPlotter = new FastPlotter();

    private void fourierChop( WavePacket wavePacket ) {
        damping.damp( wavePacket.getWaveFunctionValues() );
        LightweightComplex[] phi = SplitOperatorSolver.forwardFFT( wavePacket.getWaveFunctionValues() );
        for( int i = 0; i < phi.length; i++ ) {
            LightweightComplex lightweightComplex = phi[i];

//            if (i<50||i>phi.length-50){
//            }else{
//                lightweightComplex.setValue( 0,0);
//            }
            if (i>phi.length/8){
                lightweightComplex.setValue( 0,0);
            }
        }
        ArrayList data = new ArrayList();
        for( int j = 0; j < phi.length; j++ ) {
//                    data.add( new Point2D.Double( j, lightweightComplex.getReal() ) );
            data.add( new Point2D.Double( j, phi[j].getAbs() ) );
        }
        phiPlotter.setData( (Point2D.Double[])data.toArray( new Point2D.Double[0] ) );
//        waveFilter.filter( phi);
        LightweightComplex[]psi2=SplitOperatorSolver.inverseFFT( phi );
        for( int i = 0; i < psi2.length; i++ ) {
            wavePacket.getWaveFunctionValues()[i].setValue( psi2[i].getReal(), psi2[i].getImaginary() );
        }
//        for( int i = psi2.length-100; i < psi2.length; i++ ) {
//            wavePacket.getWaveFunctionValues()[i].setValue( psi2[i].getReal(), psi2[i].getImaginary() );
//        }
    }

    private void postProcess( WavePacket wavePacket ) {
        damping.damp( wavePacket.getWaveFunctionValues() );
        meanFilter.filter( wavePacket.getWaveFunctionValues(), 10 );
        waveFilter.filter( wavePacket.getWaveFunctionValues() );
    }
}
