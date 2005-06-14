/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class GaussianWave implements InitialWavefunction {
    private int XMESH;
    private int YMESH;
    private Point center;
    private Vector2D momentum;
    private double a;

    public GaussianWave( int XMESH, int YMESH, Point center, Vector2D momentum, double a ) {
        this.XMESH = XMESH;
        this.YMESH = YMESH;
        this.center = center;
        this.momentum = momentum;
        this.a = a;
    }

    public void initialize( Complex[][] wavefunction ) {
        initGaussian( wavefunction );
    }

    void initGaussian( Complex[][] w ) {
        int XMESH = w.length - 1;
        int YMESH = w[0].length - 1;
        for( int i = 0; i <= XMESH; i++ ) {
            for( int j = 0; j <= YMESH; j++ ) {
                w[i][j] = new Complex();
                init( w, w[i][j], i, j );
            }
        }
    }

    private void init( Complex[][] w, Complex complex, int i, int j ) {
        double space = getSpaceTerm( i, j );
        Complex mom = getMomentumTerm( i, j );
        double norm = getNormalizeTerm();

        Complex c = mom.times( space ).times( norm );
        complex.setValue( c );
    }

    private Complex getMomentumTerm( int i, int j ) {
        Point loc = new Point( i, j );
        Vector2D v2 = new Vector2D.Double( center, loc );
        double dot = v2.dot( momentum );
        Complex c = new Complex( Math.cos( dot ), Math.sin( dot ) );
        return c;
    }

    private double getSpaceTerm( int i, int j ) {
        Point loc = new Point( i, j );
        Vector2D v2 = new Vector2D.Double( center, loc );
        double mag = v2.getMagnitudeSq();
        double pow = -a * mag;
        return Math.exp( pow );
    }

    private double getNormalizeTerm() {
        return 1.0;
    }

//    public void waveSim() {
//        for( int x = 0; x < XMESH; x++ ) {
//            double r, xval;
//            xval = xmin + dx * x;
//            xpts[x] = (int)( xscale * ( xval - xmin ) );
//            r = Math.exp( -( ( xval - x0 ) / width ) * ( ( xval - x0 ) / width ) );
//            psi[x] = new McComplex( r * Math.cos( mass * vx * xval / hbar ),
//                                    r * Math.sin( mass * vx * xval / hbar ) );
//            r = v( xval ) * dt / hbar;
//            EtoV[x] = new McComplex( Math.cos( r ), -Math.sin( r ) );
//        }
//    }
}
