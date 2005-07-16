///* Copyright 2004, Sam Reid */
//package edu.colorado.phet.qm.tests;
//
//import edu.colorado.phet.common.math.ImmutableVector2D;
//import edu.colorado.phet.common.math.Vector2D;
//import edu.colorado.phet.qm.model.operators.ProbabilityValue;
//import edu.colorado.phet.qm.model.InitialWavefunction;
//import edu.colorado.phet.qm.model.Wavefunction;
//import edu.colorado.phet.qm.model.Complex;
//
//import java.awt.*;
//
///**
// * User: Sam Reid
// * Date: Jun 10, 2005
// * Time: 8:16:39 AM
// * Copyright (c) Jun 10, 2005 by Sam Reid
// */
//
//public class GaussianWaveORIG implements InitialWavefunction {
//
//    private Point center;
//    private Vector2D momentum;
//    private double dxLattice;
//
//    public GaussianWaveORIG( Point center, Vector2D momentum, double dxLattice ) {
//        this.center = center;
//        this.momentum = momentum;
//        this.dxLattice = dxLattice;
//    }
//
//    public void initialize( Wavefunction wavefunction ) {
//        initGaussian( wavefunction );
//        System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );
//        Wavefunction.normalize( wavefunction );
//        System.out.println( "GaussianWave.initialize" );
//    }
//
//    private void initGaussian( Complex[][] w ) {
//        System.out.println( "GaussianWave.initGaussian" );
//        System.out.println( "momentum = " + momentum );
//
//        for( int i = 0; i < w.length; i++ ) {
//            for( int j = 0; j < w[0].length; j++ ) {
//                w[i][j] = new Complex();
//                init( w, w[i][j], i, j );
//            }
//        }
//    }
//
//    private void init( Complex[][] w, Complex complex, int i, int j ) {
//        boolean debug = false;
//        if( Math.abs( i - center.x ) < 3 && Math.abs( j - center.y ) < 3 ) {
//            debug = true;
//        }
////        if( i == 50 && j == 50 ) {
////            System.out.println( "GaussianWave.init" );
////        }
//
//        debug( debug, "i=" + i + ", j=" + j );
//        debug( debug, "momentum vector=" + momentum );
//        Vector2D dx = new Vector2D.Double( center, new Point( i, j ) );
//        debug( debug, "dx=" + dx );
//        double magnitudeSq = dx.getMagnitudeSq();
//        debug( debug, "magSq=" + magnitudeSq );
//
//        double space = Math.exp( -magnitudeSq / ( 2 * dxLattice * dxLattice ) );
//        debug( debug, "spaceTerm=" + space );
//        double theta = momentum.dot( new ImmutableVector2D.Double( i, j ) );
//        debug( debug, "theta=" + theta );
//        Complex mom = Complex.eulerize( theta );
//        debug( debug, "momTerm=" + mom );
//        double norm = getNormalizeTerm( 2 );
//        debug( debug, "norm=" + norm );
//
//        Complex c = mom.times( space * norm );
//        debug( debug, "val=" + c );
//        complex.setValue( c );
//    }
//
//    private void debug( boolean debug, String s ) {
//        if( debug ) {
//            System.out.println( s );
//        }
//    }
//
//    private double getNormalizeTerm( int dim ) {
//        return Math.pow( Math.PI * 2 * dxLattice * dxLattice, -dim / 2 );
//    }
//
//}
