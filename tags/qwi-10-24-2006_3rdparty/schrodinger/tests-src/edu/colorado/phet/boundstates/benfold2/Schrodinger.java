/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * Numerically integrates the Schrodinger equation.
 */
public class Schrodinger {

    /**
     * The energy parameter
     */
    protected double energy = Schrodinger.DEFAULT_ENERGY;

    /**
     * The initial value of the function (arbitrary)
     */
    protected double firstValue = 1;

    /**
     * The potential function
     */
    protected Function pot;


    public static final double DEFAULT_ENERGY = -0.1;
    public static final double H = 6.63e-34;
    public static final double E = 1.60e-19;
    public static final double M = 9.11e-31;
    public static final double _2m_over_h_bar_sq = 8 * Math.PI * Math.PI * Schrodinger.M / ( Schrodinger.H * Schrodinger.H );

    /**
     * Creates a new Schrodinger solver
     *
     * @param fn The potential function
     */
    public Schrodinger( Function fn ) {
        this.pot = fn;
    }

    public void solve( double x, double h, double[] vals ) {
        double y, w;
        w = firstValue;
        y = 0;

        double a = -1;//-_2m_over_h_bar_sq;

        for( int i = 1; i < vals.length; i++ ) {
            double k1, k2, k3, k4;
            double j1, j2, j3, j4;

            k1 = a * ( energy - pot.evaluate( x ) ) * w;
            j1 = y;

            k2 = a * ( energy - pot.evaluate( x + h / 2 ) ) * ( w + j1 * h / 2 );
            j2 = y + k1 * h / 2;

            k3 = a * ( energy - pot.evaluate( x + h / 2 ) ) * ( w + j2 * h / 2 );
            j3 = y + k2 * h / 2;

            k4 = a * ( energy - pot.evaluate( x + h ) ) * ( w + j3 * h );
            j4 = y + k3 * h;

            x += h;
            y += ( h / 6 ) * ( k1 + 2 * k2 + 2 * k3 + k4 );
            w += ( h / 6 ) * ( j1 + 2 * j2 + 2 * j3 + j4 );
            vals[i] = w;
        }

    }


    /**
     * Returns the value of the energy parameter
     */
    public double getEnergy() {
        return energy;
    }


    /**
     * Sets the value of the energy parameter
     */
    public void setEnergy( double d ) {
        energy = d;
    }


    public void setPotentialFunction( Function f ) {
        this.pot = f;
    }


}
