package edu.colorado.phet.ec3.model;

/**
 * Program using the second order Runge-Kutta method
 * to solve for the landing point of a projectile.*
 */
public class JavaTechTestRK4th implements Derivable {
    // Constants
    double g = 9.80;// meter per sec**2
    double x0 = 0.0;
    double y0 = 0.0;
    double v0 = 100.0;// initial vel, m/s
    double angle = Math.PI / 4.0;

    // Instance variables
    double[] pos = new double[2];
    double[] vel = new double[2];

    /**
     * Set up for the algorithm. *
     */
    public void init() {

        double t;
        double totalT;

        int n;

        double dt = 0.01;
        int nSteps = 100000;

        pos[0] = x0;
        pos[1] = y0;
        vel[0] = v0 * Math.cos( angle );
        vel[1] = v0 * Math.sin( angle );
        t = 0.0;

        for( n = 0; n < nSteps; n++ ) {
            JavaTechRK4th.step( t, dt, pos, vel, this );
            if( pos[1] <= 0.0 ) {
                break;
            }
            System.out.println( "n=" + n + "\t" + pos[0] + "\t" + pos[1] );
        }

        System.out.println( "After steps = " + n );
        System.out.println( " x = " + pos[0] );

    } // init

    /**
     * Implement the interface method for the derivative
     * of the variables. *
     */
    public double deriv( int i, double var, double vel, double t ) {
        if( i == 0 ) { // x variable
            return 0.0;
        }
        else {// y variable
            return -g;
        }
    }

    /**
     * Run optionally as a application. *
     */
    public static void main( String[] args ) {
        JavaTechTestRK4th obj = new JavaTechTestRK4th();
        obj.init();
    }

}