package edu.colorado.phet.theramp.common.qm;

import edu.colorado.phet.theramp.view.SurfaceGraphic;

import javax.swing.*;
import java.awt.*;

/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */


/**
 * *****************************************************
 */

public class Schrodinger {
    double K, Time;
    double LAMBDA;

    int XMESH;
    int YMESH;
    double TAU;
    private static final double PI = Math.PI;
    private static final Complex TWO = new Complex( 2, 0 );
    private static final Complex MINUS_ONE = new Complex( -1, 0 );

    public Schrodinger( int XMESH, int YMESH, double TAU ) {
        this.XMESH = XMESH;
        this.YMESH = YMESH;
        this.TAU = TAU;
        Time = 0.0;
    }
/*
** Initialize the wave pack.
void InitWave(Complex w[XMESH+1][YMESH+1])
{
  int i,j;
  for (i=0;i<=XMESH;i++)
    for (j=0;j<=YMESH;j++)
      w[i][j] = Complex(cos(K*i/XMESH),sin(K*i/XMESH));
}
*/
    void initPlaneWave( Complex[][] w ) {
        int i, j;
        for( i = 0; i <= XMESH; i++ ) {
            for( j = 0; j <= YMESH; j++ ) {
                w[i][j] = new Complex( Math.cos( K * i / XMESH ), Math.sin( K * i / XMESH ) );
            }
        }
    }

/*
** Potential function

float V(int i,int j)
{
  if (Time < 300*TAU) return(0);
  if ((i-250)*(i-250)+(j-250)*(j-250)<2500)
    return (1E4);
  else return(0.);
}
*/
    double getPotential( int i, int j ) {
//        return 0;
//        if( Time < 300 * TAU ) {
        if( Time < 100000 * TAU ) {
            return ( 0 );
        }
        if( ( i - XMESH / 2 ) * ( i - XMESH / 2 ) + ( j - YMESH / 2 ) * ( j - YMESH / 2 ) < XMESH * YMESH / 32 ) {
            return ( 1E2 );
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
        for( int i = 0; i < alpha.length; i++ ) {
            alpha[i] = new Complex();
            beta[i] = new Complex();
            gamma[i] = new Complex();
        }

        Complex XAP = new Complex( 0, -0.5 * XMESH * XMESH * TAU );
        Complex XA0 = new Complex();
        Complex XA00 = new Complex( 1, XMESH * XMESH * TAU );
        Complex XA0V = new Complex( 0, 0.5 * TAU );
        Complex YAP = new Complex( 0, -0.5 * YMESH * YMESH * TAU );
        Complex YA0 = new Complex();
        Complex YA00 = new Complex( 1, YMESH * YMESH * TAU );
        Complex YA0V = new Complex( 0, 0.5 * TAU );
        Complex bi = new Complex();
        Complex bj = new Complex();

//        int i,  N;

        for( int j = 1; j < YMESH; j++ ) {

//     N = XMESH;
//     alpha[N-1] = 0;
//     beta[N-1]  = Complex(cos(K-K*K*Time),sin(K-K*K*Time));

            int N = XMESH;
            alpha[N - 1].zero();
            beta[N - 1] = new Complex( Math.cos( K - K * K * Time ), Math.sin( K - K * K * Time ) );

//                 for (i=N-1;i>=1;i--)
//       {
//	 XA0 = XA00+XA0V*V(i,j)/2.;
//	 bi = (2-XA0)*w[i][j]-XAP*(w[i-1][j]+w[i+1][j]);
//	 gamma[i] = -1/(XA0+XAP*alpha[i]);
//	 alpha[i-1] = gamma[i]*XAP;
//	 beta[i-1]  = gamma[i]*(XAP*beta[i]-bi);
//       }
            for( int i = N - 1; i >= 1; i-- ) {
                XA0 = XA00.plus( XA0V.times( getPotential( i, j ) / 2.0 ) );
                bi = ( ( TWO.minus( XA0 ) ).times( w[i][j] ) ).minus( XAP.times( w[i - 1][j].plus( w[i + 1][j] ) ) );
                gamma[i] = MINUS_ONE.divideBy( ( XA0.plus( XAP.times( alpha[i] ) ) ) );
                alpha[i - 1] = gamma[i].times( XAP );
                beta[i - 1] = gamma[i].times( ( XAP.times( beta[i] ) ).minus( bi ) );
            }

//                 w[0][j] = Complex(cos(K*K*Time),-sin(K*K*Time));
//     for (i=0;i<=N-1;i++)
//       w[i+1][j] = alpha[i]*w[i][j]+beta[i];

            w[0][j] = new Complex( Math.cos( K * K * Time ), -Math.sin( K * K * Time ) );
            for( int i = 0; i <= N - 1; i++ ) {
                w[i + 1][j] = ( alpha[i].times( w[i][j] ) ).plus( beta[i] );
            }
        }

        for( int i = 1; i < XMESH; i++ ) {

//                 N = YMESH;
//     alpha[N-1] = 0;
//     beta[N-1]  = Complex(cos(K*i/XMESH-K*K*Time),sin(K*i/XMESH-K*K*Time));
            int N = YMESH;
            alpha[N - 1].zero();
            beta[N - 1] = new Complex( Math.cos( K * i / XMESH - K * K * Time ), Math.sin( K * i / XMESH - K * K * Time ) );

//            for (j=N-1;j>=1;j--)
//              {
//            YA0 = YA00+YA0V*V(i,j)/2.;
//            bj = (2-YA0)*w[i][j]-YAP*(w[i][j-1]+w[i][j+1]);
//            gamma[j] = -1/(YA0+YAP*alpha[j]);
//            alpha[j-1] = gamma[j]*YAP;
//            beta[j-1]  = gamma[j]*(YAP*beta[j]-bj);
//              }

            for( int j = N - 1; j >= 1; j-- ) {
                YA0 = YA00.plus( YA0V.times( getPotential( i, j ) / 2.0 ) );
                bj = ( ( TWO.minus( YA0 ) ).times( w[i][j] ) ).minus( YAP.times( w[i][j - 1].plus( w[i][j + 1] ) ) );
                gamma[j] = MINUS_ONE.divideBy( YA0.plus( YAP.times( alpha[j] ) ) );
                alpha[j - 1] = gamma[j].times( YAP );
                beta[j - 1] = gamma[j].times( ( YAP.times( beta[j] ) ).minus( bj ) );
            }

//                 w[i][0] = Complex(cos(K*i/XMESH-K*K*Time),sin(K*i/XMESH-K*K*Time));
//     for (j=0;j<=N-1;j++)
//       w[i][j+1] = alpha[j]*w[i][j]+beta[j];
            w[i][0] = new Complex( Math.cos( K * i / XMESH - K * K * Time ), Math.sin( K * i / XMESH - K * K * Time ) );
            for( int j = 0; j <= N - 1; j++ ) {
                w[i][j + 1] = ( alpha[j].times( w[i][j] ) ).plus( beta[j] );
            }
        }
    }

    public void run( int Steps ) {
//        int show = 30;
        int show = 20;
        final Complex[][] wavefunction = new Complex[XMESH + 1][YMESH + 1];
        K = 10 * PI;
        LAMBDA = (int)2 * PI / K * XMESH;

        initPlaneWave( wavefunction );
        ColorGrid colorGrid = new ColorGrid( 600, 600, XMESH, YMESH );
        ColorMap colorMap = new ColorMap() {
            public Paint getPaint( int i, int k ) {
                double h = Math.abs( wavefunction[i][k].getReal() );
                double s = Math.abs( wavefunction[i][k].getImaginary() );
                double b = wavefunction[i][k].abs();
//                double h = wavefunction[i][k].abs();
//                System.out.println( "h = " + h );
                Color color = new Color( Color.HSBtoRGB( (float)h, (float)s, (float)b ) );
                return color;
            }
        };
        colorGrid.colorize( colorMap );
        SurfaceGraphic.ImageDebugFrame frame = new SurfaceGraphic.ImageDebugFrame( colorGrid.getBufferedImage() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        for( int i = 0; i <= Steps; i++ ) {
            System.out.println( "Running to step: " + i );
            if( i % show == 0 ) {
                colorGrid.colorize( colorMap );
                frame.setImage( colorGrid.getBufferedImage() );
                for( int j = 0; j <= YMESH; j += 5 ) {
                    for( int ii = 0; ii <= XMESH; ii += 5 ) {
//                        System.out.println( "wavefunction[" + ii + "][" + j + "]=" + wavefunction[ii][j] );
                    }
//                    System.out.println( "" );
                }
            }
            Time += TAU;
            Cayley( wavefunction );
        }
    }

    public static void main( String[] args ) {
        int XMESH = 500;
        int YMESH = 500;

//        int XMESH = 500;
//        int YMESH = 500;

        double TAU = 5E-6;
//        double TAU = 1E-8;
//        double TAU = 1E-3;
        new Schrodinger( XMESH, YMESH, TAU ).run( 15000 );
    }

}