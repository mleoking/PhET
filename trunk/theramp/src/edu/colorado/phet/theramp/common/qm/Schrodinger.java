package edu.colorado.phet.theramp.common.qm;

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
        for( int i = 0; i < alpha.length; i++ ) {
            alpha[i] = new Complex();
        }

        Complex[] beta = new Complex[XMESH + YMESH];
        for( int i = 0; i < beta.length; i++ ) {
            beta[i] = new Complex();
        }
        Complex[] gamma = new Complex[XMESH + YMESH];
        for( int i = 0; i < gamma.length; i++ ) {
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

        int i, j, N;

        for( j = 1; j < YMESH; j++ ) {
            N = XMESH;
            if( alpha[N - 1] == null ) {
                throw new RuntimeException( "Null array" );
            }
            alpha[N - 1].zero();
            beta[N - 1] = new Complex( cos( K - K * K * Time ), sin( K - K * K * Time ) );

            for( i = N - 1; i >= 1; i-- ) {
                XA0 = XA00.plus( XA0V.times( V( i, j ) / 2.0 ) );
                bi = ( TWO.minus( XA0 ) ).times( w[i][j].minus( XAP.times( w[i - 1][j].plus( w[i + 1][j] ) ) ) );
                gamma[i] = MINUS_ONE.divideBy( ( XA0.plus( XAP.times( alpha[i] ) ) ) );
                alpha[i - 1] = gamma[i].times( XAP );
                beta[i - 1] = gamma[i].times( XAP.times( beta[i].minus( bi ) ) );
            }

            w[0][j] = new Complex( cos( K * K * Time ), -sin( K * K * Time ) );
            for( i = 0; i <= N - 1; i++ ) {
                w[i + 1][j] = ( alpha[i].times( w[i][j] ) ).plus( beta[i] );
            }
        }

        for( i = 1; i < XMESH; i++ ) {
            N = YMESH;
            alpha[N - 1].zero();
            beta[N - 1] = new Complex( cos( K * i / XMESH - K * K * Time ), sin( K * i / XMESH - K * K * Time ) );

            for( j = N - 1; j >= 1; j-- ) {
                YA0 = YA00.plus( YA0V.times( V( i, j ) / 2.0 ) );
                bj = ( TWO.minus( YA0 ) ).times( w[i][j].minus( YAP.times( w[i][j - 1].plus( w[i][j + 1] ) ) ) );
                gamma[j] = MINUS_ONE.divideBy( YA0.plus( YAP.times( alpha[j] ) ) );
                alpha[j - 1] = gamma[j].times( YAP );
                beta[j - 1] = gamma[j].times( ( YAP.times( beta[j] ) ).minus( bj ) );
            }

            w[i][0] = new Complex( cos( K * i / XMESH - K * K * Time ), sin( K * i / XMESH - K * K * Time ) );
            for( j = 0; j <= N - 1; j++ ) {
                w[i][j + 1] = ( alpha[j].times( w[i][j] ) ).plus( beta[j] );
            }
        }
    }

    public void main() {
        long Steps = 1500;
        int Shot = 30;
        Complex[][] wavefunction = new Complex[XMESH + 1][YMESH + 1];
        K = 10 * PI;
        LAMBDA = (int)2 * PI / K * XMESH;

        InitWave( wavefunction );

        for( int i = 0; i <= Steps; i++ ) {
            System.out.println( "Running to step: " + i );
            if( i % Shot == 0 ) {
                for( int j = 0; j <= YMESH; j += 5 ) {
                    for( int ii = 0; ii <= XMESH; ii += 5 ) {
                        System.out.println( "wavefunction[" + ii + "][" + j + "]=" + wavefunction[ii][j] );
                    }
                    System.out.println( "" );
                }
            }
            Time += TAU;
            Cayley( wavefunction );
        }
    }

    public static void main( String[] args ) {
        int XMESH = 500;
        int YMESH = 500;
        double TAU = 2E-6;
        new Schrodinger( XMESH, YMESH, TAU ).main();
    }

}