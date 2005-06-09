/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

import edu.colorado.phet.theramp.common.qm.Complex;

import java.io.File;

/**
 * *****************************************************
 */

//#include <iostream.h>
//#include <math.h>
//#include <stdio.h>
//#include "complex.h"
///* The complex operation packet */
//
//#define XMESH 500    /* # of X intevals from 0-1 */
//#define YMESH 500    /* # of Y intevals from 0-1 */
//#define TAU  2E-6    /* Time inteval             */
//#define PI   3.14159265457

public class Schrodinger {
    double K, Time;
    double LAMBDA;
    long Steps;
    int XMESH = 500;
    int YMESH = 500;
    double TAU = 2E-6;
    private static final double PI = Math.PI;
/*
** Initialize the wave pack.
*/
    void InitWave( Complex[][] w ) {
        int i, j;
        for( i = 0; i <= XMESH; i++ ) {
            for( j = 0; j <= YMESH; j++ ) {
                w[i][j] = new Complex( cos( K * i / XMESH ), sin( K * i / XMESH ) );
            }
        }
    }

    private double sin( double v ) {
        return Math.sin( v );
    }

    private double cos( double v ) {
        return Math.cos( v );
    }

/*
** Potential function
*/
    double V( int i, int j ) {
        if( Time < 300 * TAU ) {
            return ( 0 );
        }
        if( ( i - 250 ) * ( i - 250 ) + ( j - 250 ) * ( j - 250 ) < 2500 ) {
            return ( 1E4 );
        }
        else {
            return ( 0. );
        }
    }

/*
** Cayley propagator.
*/
    void Cayley( Complex[][] w ) {
        Complex[] alpha = new Complex[XMESH + YMESH];
        Complex[] beta = new Complex[XMESH + YMESH];
        Complex[] gamma = new Complex[XMESH + YMESH];
        Complex XAP = new Complex( 0, -0.5 * XMESH * XMESH * TAU );
        Complex XA0 = new Complex();
        Complex XA00 = new Complex( 1, XMESH * XMESH * TAU );
        Complex XA0V = new Complex( 0, 0.5 * TAU );
        Complex YAP = new Complex( 0, -0.5 * YMESH * YMESH * TAU );
        Complex YA0 = new Complex();
        Complex YA00 = new Complex( 1, YMESH * YMESH * TAU );
        Complex YA0V = new Complex( 0, 0.5 * TAU );
        Complex bi, bj;

        int i, j, N;

//        for( j = 1; j < YMESH; j++ ) {
//            N = XMESH;
//            alpha[N - 1].zero();
//            beta[N - 1] = new Complex( cos( K - K * K * Time ), sin( K - K * K * Time ) );
//
//            for( i = N - 1; i >= 1; i-- ) {
//                XA0 = XA00 + XA0V * V( i, j ) / 2.;
//                bi = ( 2 - XA0 ) * w[i][j] - XAP * ( w[i - 1][j] + w[i + 1][j] );
//                gamma[i] = -1 / ( XA0 + XAP * alpha[i] );
//                alpha[i - 1] = gamma[i] * XAP;
//                beta[i - 1] = gamma[i] * ( XAP * beta[i] - bi );
//            }
//
//            w[0][j] = Complex( cos( K * K * Time ), -sin( K * K * Time ) );
//            for( i = 0; i <= N - 1; i++ ) {
//                w[i + 1][j] = alpha[i] * w[i][j] + beta[i];
//            }
//        }
//
//        for( i = 1; i < XMESH; i++ ) {
//            N = YMESH;
//            alpha[N - 1] = 0;
//            beta[N - 1] = Complex( cos( K * i / XMESH - K * K * Time ), sin( K * i / XMESH - K * K * Time ) );
//
//            for( j = N - 1; j >= 1; j-- ) {
//                YA0 = YA00 + YA0V * V( i, j ) / 2.;
//                bj = ( 2 - YA0 ) * w[i][j] - YAP * ( w[i][j - 1] + w[i][j + 1] );
//                gamma[j] = -1 / ( YA0 + YAP * alpha[j] );
//                alpha[j - 1] = gamma[j] * YAP;
//                beta[j - 1] = gamma[j] * ( YAP * beta[j] - bj );
//            }
//
//            w[i][0] = Complex( cos( K * i / XMESH - K * K * Time ), sin( K * i / XMESH - K * K * Time ) );
//            for( j = 0; j <= N - 1; j++ ) {
//                w[i][j + 1] = alpha[j] * w[i][j] + beta[j];
//            }
//        }
    }

    public void main() {

        System.out.println( "Hello" );
        File output;
//        FILE * OUTPUT1;
        int Shot, i, ii, j;
        String filename = "";
        Complex[][] w = new Complex[XMESH + 1][YMESH + 1];
        /* w[][] is where the wave function is kept. */

        Steps = 1500;
        Shot = 30;
        Time = 0.;

        K = 10 * PI;
        LAMBDA = (int)2 * PI / K * XMESH;

        InitWave( w );

        for( i = 0; i <= Steps; i++ ) {
            printf( "Running to Step %d\n", i );
            if( i % Shot == 0 ) {
                j = i / Shot;
//                sprintf( filename, "a%d.out", j );
//                OUTPUT1 = fopen( filename, "w" );

                for( j = 0; j <= YMESH; j += 5 ) {
                    for( ii = 0; ii <= XMESH; ii += 5 ) {
//                        fprintf( OUTPUT1, "%f ", w[ii][j].Abs() );
                        System.out.println( "w[ii][j]=" + w[ii][j] );
                    }
//                    fprintf( OUTPUT1, "\n" );
                    System.out.println( "" );
                }
//                fclose( OUTPUT1 );
            }
            Time += TAU;
            Cayley( w );
        }

    }

    public static void main( String[] args ) {
        new Schrodinger().main();

    }

    private void printf( String s, long value ) {
        System.out.println( s + ": " + value );
    }
}