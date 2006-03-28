/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.numerov;

import edu.colorado.phet.boundstates.test.benfold.Function;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 7:16:09 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class Numerov {

    Function potential;
    double dx;
    double hbarSquared;
    double mass;
    double energy;

    public Numerov( Function potential, double mass, double energy, double dx, double hbarSquared ) {
        this.potential = potential;
        this.mass = mass;
        this.energy = energy;
        this.dx = dx;
        this.hbarSquared = hbarSquared;
    }

    void integrate( double[]psi, double psi0, double psi1, double x0 ) {
        psi[0] = psi0;
        psi[1] = psi1;
        double h2 = hbarSquared;

        for( int n = 1; n < psi.length - 1; n++ ) {//todo save k values in an array for performance
            double a = 2 * ( 1 - 5.0 / 12.0 * h2 * k2( x0 + n * dx ) );
            double b = 1 + 1.0 / 12.0 * h2 * k2( x0 + ( n - 1 ) * dx );
            double c = 1 + 1.0 / 12.0 * h2 * k2( x0 + ( n + 1 ) * dx );
            psi[n + 1] = ( a * psi[n] - b * psi[n - 1] ) / c;
        }
    }

    private double k2( double x ) {
        return 2 * mass / hbarSquared * ( energy - potential.evaluate( x ) );
    }
}
