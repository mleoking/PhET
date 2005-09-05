package edu.colorado.phet.qm.util;

import edu.colorado.phet.qm.model.Complex;

/**
 * **********************************************************************
 * Compilation:  javac FFT.java
 * Execution:    java FFT N
 * Dependencies: Complex.java
 * <p/>
 * Compute the FFT and inverse FFT of a length N complex sequence.
 * Bare bones implementation that runs in O(N log N) time.
 * <p/>
 * Limitations
 * -----------
 * * assumes N is a power of 2
 * * not the most memory efficient algorithm
 * <p/>
 * ***********************************************************************
 * <p/>
 * Taken from:
 * www.cs.princeton.edu/introcs/97data/FFT.java.html
 * <p/>
 * Numerical Recipes is in C but I know Java and C and I have no problem converting between them.
 * <p/>
 * You can make a 2D FFT by applyiong a 1 D FFT to the columns
 * and then applying a 1D FFT to the rows of the result.
 * <p/>
 * Or you can do it the other way around! Rows first, then columns.
 * <p/>
 * Theoretically this is because the 2D FFT iof a matrix A is a matrix
 * multiplication:
 * T
 * F = T A T
 * <p/>
 * where T is  matrix of Fourier coefficients. There are three terms.
 * Doing the transform with columns first is multiplying two terms on
 * the left -- then multiply by the last.
 * Doing rows first goes from right to left.
 */

public class FFT {

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft( Complex[] x ) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // base case
        if( N == 1 ) {
            y[0] = x[0];
            return y;
        }

        // radix 2 Cooley-Tukey FFT
        if( N % 2 != 0 ) {
            throw new RuntimeException( "N is not a power of 2" );
        }
        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
        for( int k = 0; k < N / 2; k++ ) {
            even[k] = x[2 * k];
        }
        for( int k = 0; k < N / 2; k++ ) {
            odd[k] = x[2 * k + 1];
        }

        Complex[] q = fft( even );
        Complex[] r = fft( odd );

        for( int k = 0; k < N / 2; k++ ) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex( Math.cos( kth ), Math.sin( kth ) );
            y[k] = q[k].plus( wk.times( r[k] ) );
            y[k + N / 2] = q[k].minus( wk.times( r[k] ) );
        }
        return y;
    }


    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft( Complex[] x ) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for( int i = 0; i < N; i++ ) {
            y[i] = x[i].complexConjugate();
        }

        // compute forward FFT
        y = fft( y );

        // take conjugate again
        for( int i = 0; i < N; i++ ) {
            y[i] = y[i].complexConjugate();
        }

        // divide by N
        for( int i = 0; i < N; i++ ) {
            y[i] = y[i].times( 1.0 / N );
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static Complex[] cconvolve( Complex[] x, Complex[] y ) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if( x.length != y.length ) {
            throw new RuntimeException( "Dimensions don't agree" );
        }

        int N = x.length;

        // compute FFT of each sequence
        Complex[] a = fft( x );
        Complex[] b = fft( y );

        // point-wise multiply
        Complex[] c = new Complex[N];
        for( int i = 0; i < N; i++ ) {
            c[i] = a[i].times( b[i] );
        }

        // compute inverse FFT
        return ifft( c );
    }


    // compute the linear convolution of x and y
    public static Complex[] convolve( Complex[] x, Complex[] y ) {
        Complex ZERO = new Complex( 0, 0 );

        Complex[] a = new Complex[2 * x.length];
        for( int i = 0; i < x.length; i++ ) {
            a[i] = x[i];
        }
        for( int i = x.length; i < 2 * x.length; i++ ) {
            a[i] = ZERO;
        }

        Complex[] b = new Complex[2 * y.length];
        for( int i = 0; i < y.length; i++ ) {
            b[i] = y[i];
        }
        for( int i = y.length; i < 2 * y.length; i++ ) {
            b[i] = ZERO;
        }

        return cconvolve( a, b );
    }


    // test client
    public static void main( String[] args ) {
        if( args.length == 0 ) {
            args = new String[]{"16"};
        }
        int N = Integer.parseInt( args[0] );
//        N=(int)Math.pow(2,16);
        System.out.println( "N = " + N );

        Complex[] x = new Complex[N];

        for( int i = 0; i < N; i++ ) {
            System.out.print( Math.sin( i / 30.0 ) + " " );
        }
        System.out.println( "FFT.main" );
        // original data
        for( int i = 0; i < N; i++ ) {
            x[i] = new Complex( Math.sin( i / 30.0 ), 0 );
        }
        System.out.println( "Original Data:" );
        for( int i = 0; i < N; i++ ) {
            System.out.println( x[i] );
        }
        System.out.println();

        // FFT of original data
        Complex[] y = fft( x );
        System.out.println( "FFT of original data:" );
        for( int i = 0; i < N; i++ ) {
            System.out.println( y[i] );
        }
        System.out.println();

        // take inverse FFT
        Complex[] z = ifft( y );
        System.out.println( "Inverse FFT" );
        for( int i = 0; i < N; i++ ) {
            System.out.println( z[i] );
        }
        System.out.println();

        // circular convolution of x with itself
        Complex[] c = convolve( x, x );
        System.out.println( "convolve (X,X)" );
        for( int i = 0; i < N; i++ ) {
            System.out.println( c[i] );
        }
        System.out.println();

        // linear convolution of x with itself
        Complex[] d = convolve( x, x );
        for( int i = 0; i < d.length; i++ ) {
            System.out.println( d[i] );
        }
        System.out.println();

    }

}