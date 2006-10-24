/* Copyright 2004, Sam Reid */
package org.falstad;

/**
 * User: Sam Reid
 * Date: Jan 17, 2006
 * Time: 8:01:47 PM
 * Copyright (c) Jan 17, 2006 by Sam Reid
 */
public class FFT {
    double wtab[];
    int size;

    FFT( int sz ) {
        size = sz;
        if( ( size & ( size - 1 ) ) != 0 ) {
            System.out.println( "size must be power of two!" );
        }
        calcWTable();
    }

    void calcWTable() {
        // calculate table of powers of w
        wtab = new double[size];
        int i;
        for( i = 0; i != size; i += 2 ) {
            double th = QuantumCircFrame.pi * i / size;
            wtab[i] = Math.cos( th );
            wtab[i + 1] = Math.sin( th );
        }
    }

    void transform( double data[] ) {
        int i;
        int j = 0;
        int size2 = size * 2;

        // bit-reversal
        for( i = 0; i != size2; i += 2 ) {
            if( i > j ) {
                double q = data[i];
                data[i] = data[j];
                data[j] = q;
                q = data[i + 1];
                data[i + 1] = data[j + 1];
                data[j + 1] = q;
            }
            // increment j by one, from the left side (bit-reversed)
            int bit = size;
            while( ( bit & j ) != 0 ) {
                j &= ~bit;
                bit >>= 1;
            }
            j |= bit;
        }

        // amount to skip through w table
        int tabskip = size2;

        int skip1;
        for( skip1 = 4; skip1 <= size2; skip1 <<= 1 ) {
            // skip2 = length of subarrays we are combining
            // skip1 = length of subarray after combination
            int skip2 = skip1 >> 1;
            tabskip >>= 1;
            // for each subarray
            for( i = 0; i < size2; i += skip1 ) {
                int ix = 0;
                // for each pair of complex numbers (one in each subarray)
                for( j = i; j != i + skip2; j += 2, ix += tabskip ) {
                    double wr = wtab[ix];
                    double wi = wtab[ix + 1];
                    double d1r = data[j];
                    double d1i = data[j + 1];
                    int j2 = j + skip2;
                    double d2r = data[j2];
                    double d2i = data[j2 + 1];
                    double d2wr = d2r * wr - d2i * wi;
                    double d2wi = d2r * wi + d2i * wr;
                    data[j] = d1r + d2wr;
                    data[j + 1] = d1i + d2wi;
                    data[j2] = d1r - d2wr;
                    data[j2 + 1] = d1i - d2wi;
                }
            }
        }
    }
}
