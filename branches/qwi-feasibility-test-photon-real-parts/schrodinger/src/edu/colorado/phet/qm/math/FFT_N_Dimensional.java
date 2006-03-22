/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.math;

/**
 * User: Sam Reid
 * Date: Feb 10, 2006
 * Time: 1:10:51 AM
 * Copyright (c) Feb 10, 2006 by Sam Reid
 */

public class FFT_N_Dimensional {
// n-dimensional discrete FFT from Numerical Recipes

    //by Falstad


    /**
     * Comments from
     * http://64.233.179.104/search?q=cache:pwK4K0gwb7UJ:jas2.eng.buffalo.edu/semiconductor/apiWIE/crw.math.FFT.html+n-dimensional+fft+recipes&hl=en&gl=us&ct=clnk&cd=1
     * <p/>
     * Replaces data by its ndim-dimensional discrete Fourier transform, if isign is input as +1.
     * nn[1..ndim] is an integer array containing the lengths of each dimension
     * (number of complex values), which MUST all be powers of 2.
     * <p/>
     * data is a real array of length
     * twice the product of these lengths, in which the data are stored as in a
     * multidimensional complex array; real and imaginary parts of each
     * element are in cosecutive locations, and the rightmost index of the array
     * increases most rapidly as one proceeds along data.
     * For a two dimensional array, this is equivalent to storing the array by rows.
     * If isign is input as -1, data is replaced by its inverse transform times the product of the lengths of all dimensions.
     * <p/>
     * Parameters:
     * data - a real array twice the product of lengths of each dimension.
     * nn - an integer array containg the array of each dimension, MUST be powers of 2.
     * ndim - the dimension.
     * isign - +1 for discrete Foruier transform, -1 for inverse transform times the product of each dimensions.
     */


    public static void ndfft( double data[], int nn[], int ndim, int isign ) {
        int ntot = 1;
        int nprev = 1;
        int idim;
        float i2pi = (float)( isign * 2 * Math.PI );

        for( idim = 0; idim < ndim; idim++ ) {
            ntot *= nn[idim];
        }
        //int steps = 0;

        for( idim = 0; idim < ndim; idim++ ) {

            int n = nn[idim];
            int nrem = ntot / ( n * nprev );
            int ip1 = 2 * nprev;
            int ip2 = ip1 * n;
            int ip3 = ip2 * nrem;
            int i2rev = 0;
            int i2;
            int ifp1;

            /*
            * Bit reversal stuff.
            */

            for( i2 = 0; i2 < ip2; i2 += ip1 ) {

                int ibit;

                if( i2 < i2rev ) {

                    int i1;

                    for( i1 = i2; i1 < i2 + ip1; i1 += 2 ) {

                        int i3;

                        for( i3 = i1; i3 < ip3; i3 += ip2 ) {

                            int i3rev = i2rev + i3 - i2;
                            double tempr = data[i3];
                            double tempi = data[i3 + 1];

                            data[i3] = data[i3rev];
                            data[i3 + 1] = data[i3rev + 1];
                            data[i3rev] = tempr;
                            data[i3rev + 1] = tempi;
                            //steps++;
                        }

                    }

                }

                ibit = ip2 / 2;
                while( ( ibit > ip1 ) && ( i2rev > ibit - 1 ) ) {

                    i2rev -= ibit;
                    ibit /= 2;

                }

                i2rev += ibit;

            }

            /*
            * Danielson-Lanczos stuff.
            */

            ifp1 = ip1;
            while( ifp1 < ip2 ) {

                int ifp2 = 2 * ifp1;
                float theta = i2pi / ( (float)ifp2 / ip1 );
                float wpr;
                float wpi;
                float wr = 1.0f;
                float wi = 0.0f;
                int i3;

                wpr = (float)Math.sin( 0.5 * theta );
                wpr *= wpr * -2.0;
                wpi = (float)Math.sin( theta );

                for( i3 = 0; i3 < ifp1; i3 += ip1 ) {

                    int i1;
                    float wtemp;

                    for( i1 = i3; i1 < i3 + ip1; i1 += 2 ) {

                        for( i2 = i1; i2 < ip3; i2 += ifp2 ) {

                            int i21 = i2 + 1;
                            int k2 = i2 + ifp1;
                            int k21 = k2 + 1;
                            double tempr = ( wr * data[k2] ) -
                                           ( wi * data[k21] );
                            double tempi = ( wr * data[k21] ) +
                                           ( wi * data[k2] );

                            data[k2] = data[i2] - tempr;
                            data[k21] = data[i21] - tempi;

                            data[i2] += tempr;
                            data[i21] += tempi;
                            //steps++;

                        }

                    }

                    wtemp = wr;
                    wr += ( wr * wpr ) - ( wi * wpi );
                    wi += ( wi * wpr ) + ( wtemp * wpi );

                }
                ifp1 = ifp2;

            }
            nprev *= n;

        }
        //System.out.print(steps + "\n");
    }

}
