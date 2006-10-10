/*
*   Class RungeKutta
*       requires interfaces DerivFunction and DerivnFunction
*
*   Contains the methods for the Runge-Kutta procedures
*   for solving single or solving sets of ordinary
*   differential equations (ODEs).
*
*   A single ODE is supplied by means of an interface,
*       DerivFunction
*   A set of ODEs is supplied by means of an interface,
*       DerivnFunction
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:	 February 2002
*   UPDATE:  22 June 2003
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   RungeKutta.html
*
*   Copyright (c) April 2004
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package edu.colorado.phet.energyskatepark.model;

// Interface for RungeKutta class (single ODE)

interface DerivFunction {
    double deriv( double x, double y );
}

// Interface for RungeKutta class (n ODEs)

interface DerivnFunction {
    double[] derivn( double x, double[] y );
}


class Deriv implements DerivFunction {

    public double a = 0.0D;

    public double deriv( double x, double y ) {
        double dydx = a * x * Math.sqrt( 1.0 - y * y );
        return dydx;
    }
}

// Class for Runge-Kutta solution of ordinary differential equations

public class FlanaganRK4th {

    public FlanaganRK4th() {
    }

    private static double SAFETY = 0.9D; // safety scaling factor for Runge Kutta Fehlberg tolerance check

    // Fourth order Runge-Kutta for a single ordinary differential equation
    public static double fourthOrder( DerivFunction g, double x0, double y0, double xn, double h ) {
        double k1 = 0.0D, k2 = 0.0D, k3 = 0.0D, k4 = 0.0D;
        double x = 0.0D, y = y0;

        // Calculate nsteps
        double ns = ( xn - x0 ) / h;
        ns = Math.rint( ns );
        int nsteps = (int)ns;  // number of steps
        h = ( xn - x0 ) / ns;

        for( int i = 0; i < nsteps; i++ ) {
            x = x0 + i * h;

            k1 = h * g.deriv( x, y );
            k2 = h * g.deriv( x + h / 2, y + k1 / 2 );
            k3 = h * g.deriv( x + h / 2, y + k2 / 2 );
            k4 = h * g.deriv( x + h, y + k3 );

            y += k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
        }
        return y;
    }

    // Fourth order Runge-Kutta for n (nequ) ordinary differential equations (ODE)
    public static double[] fourthOrder( DerivnFunction g, double x0, double[] y0, double xn, double h ) {
        int nequ = y0.length;
        double[] k1 = new double[nequ];
        double[] k2 = new double[nequ];
        double[] k3 = new double[nequ];
        double[] k4 = new double[nequ];
        double[] y = new double[nequ];
        double[] yd = new double[nequ];
        double[] dydx = new double[nequ];
        double x = 0.0D;

        // Calculate nsteps
        double ns = ( xn - x0 ) / h;
        ns = Math.rint( ns );
        int nsteps = (int)ns;
        h = ( xn - x0 ) / ns;

        // initialise
        for( int i = 0; i < nequ; i++ ) {
            y[i] = y0[i];
        }

        // iteration over allowed steps
        for( int j = 0; j < nsteps; j++ ) {
            x = x0 + j * h;
            dydx = g.derivn( x, y );
            for( int i = 0; i < nequ; i++ ) {
                k1[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + k1[i] / 2;
            }
            dydx = g.derivn( x + h / 2, yd );
            for( int i = 0; i < nequ; i++ ) {
                k2[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + k2[i] / 2;
            }
            dydx = g.derivn( x + h / 2, yd );
            for( int i = 0; i < nequ; i++ ) {
                k3[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + k3[i];
            }
            dydx = g.derivn( x + h, yd );
            for( int i = 0; i < nequ; i++ ) {
                k4[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                y[i] += k1[i] / 6 + k2[i] / 3 + k3[i] / 3 + k4[i] / 6;
            }

        }
        return y;
    }

    // Runge-Kutta-Cash-Karp for a single ordinary differential equation (ODE)
    public static double cashKarp( DerivFunction g, double x0, double y0, double xn, double h, double abstol, double reltol, int maxiter ) {
        double k1 = 0.0D, k2 = 0.0D, k3 = 0.0D, k4 = 0.0D, k5 = 0.0D, k6 = 0.0D;
        double y = y0, y6 = 0.0D, y5 = 0.0D, yd = 0.0D, dydx = 0.0D;
        double x = x0, err = 0.0D, delta = 0.0D, tol = 0.0D;
        int i = 0;

        while( x < xn ) {
            i++;
            if( i > maxiter ) {
                throw new ArithmeticException( "Maximum number of iterations exceeded" );
            }
            dydx = g.deriv( x, y );
            k1 = h * dydx;

            yd = y + k1 / 5.0;
            dydx = g.deriv( x + h / 5.0, yd );
            k2 = h * dydx;

            yd = y + ( 3.0 * k1 + 9.0 * k2 ) / 40.0;
            dydx = g.deriv( x + 3.0 * h / 10.0, yd );
            k3 = h * dydx;

            yd = y + ( 3.0 * k1 - 9.0 * k2 + 12.0 * k3 ) / 10.0;
            dydx = g.deriv( x + 3.0 * h / 5.0, yd );
            k4 = h * dydx;

            yd = y - 11.0 * k1 / 55.0 + 5.0 * k2 / 2.0 - 70.0 * k3 / 27.0 + 35.0 * k4 / 27.0;
            dydx = g.deriv( x + h, yd );
            k5 = h * dydx;

            yd = y + 1631.0 * k1 / 55296.0 + 175.0 * k2 / 512.0 + 575.0 * k3 / 13824.0 + 44275.0 * k4 / 110592.0 + 253.0 * k5 / 4096.0;
            dydx = g.deriv( x + 7.0 * h / 8.0, yd );
            k6 = h * dydx;

            y5 = y + 2825.0 * k1 / 27648.0 + 18575.0 * k3 / 48384.0 + 13525.0 * k4 / 55296.0 + 277.0 * k5 / 14336.0 + k6 / 4.0;
            y6 = y + 37 * k1 / 378.0 + 250.0 * k3 / 621.0 + 125.0 * k4 / 594.0 + 512.0 * k6 / 1771.0;
            err = Math.abs( y6 - y5 );
            tol = err / ( y5 * reltol + abstol );
            if( tol <= 1.0 ) {
                x += h;
                delta = SAFETY * Math.pow( tol, -0.2 );
                if( delta > 4.0 ) {
                    h *= 4.0;
                }
                else {
                    h *= delta;
                }
                if( x + h > xn ) {
                    h = xn - x;
                }
                y = y5;
            }
            else {
                delta = SAFETY * Math.pow( tol, -0.25 );
                if( delta < 0.1 ) {
                    h *= 0.1;
                }
                else {
                    h *= delta;
                }
            }
        }
        return y;
    }

    // maximum iteration default option
    public static double cashKarp( DerivFunction g, double x0, double y0, double xn, double h, double abstol, double reltol ) {

        double nsteps = ( xn - x0 ) / h;
        int maxiter = (int)nsteps * 100;

        return cashKarp( g, x0, y0, xn, h, abstol, reltol, maxiter );
    }


    // Runge-Kutta-Cash-Karp for n (nequ) ordinary differential equations (ODEs
    public static double[] cashKarp( DerivnFunction g, double x0, double[] y0, double xn, double h, double abstol, double reltol, int maxiter ) {
        int nequ = y0.length;
        double[] k1 = new double[nequ];
        double[] k2 = new double[nequ];
        double[] k3 = new double[nequ];
        double[] k4 = new double[nequ];
        double[] k5 = new double[nequ];
        double[] k6 = new double[nequ];
        double[] y = new double[nequ];
        double[] y6 = new double[nequ];
        double[] y5 = new double[nequ];
        double[] yd = new double[nequ];
        double[] dydx = new double[nequ];

        double x = 0.0D, err = 0.0D, maxerr = 0.0D, delta = 0.0D, tol = 1.0D;
        int ii = 0;

        // initialise
        for( int i = 0; i < nequ; i++ ) {
            y[i] = y0[i];
        }
        x = x0;

        while( x < xn ) {
            ii++;
            if( ii > maxiter ) {
                throw new ArithmeticException( "Maximum number of iterations exceeded" );
            }

            dydx = g.derivn( x, y );
            for( int i = 0; i < nequ; i++ ) {
                k1[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + k1[i] / 5.0;
            }
            dydx = g.derivn( x + h / 5.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k2[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + ( 3.0 * k1[i] + 9.0 * k2[i] ) / 40.0;
            }
            dydx = g.derivn( x + 3.0 * h / 10.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k3[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + ( 3.0 * k1[i] - 9.0 * k2[i] + 12.0 * k3[i] ) / 10.0;
            }
            dydx = g.derivn( x + 3.0 * h / 5.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k4[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] - 11.0 * k1[i] / 55.0 + 5.0 * k2[i] / 2.0 - 70.0 * k3[i] / 27.0 + 35.0 * k4[i] / 27.0;
            }
            dydx = g.derivn( x + h, yd );
            for( int i = 0; i < nequ; i++ ) {
                k5[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + 1631.0 * k1[i] / 55296.0 + 175.0 * k2[i] / 512.0 + 575.0 * k3[i] / 13824.0 + 44275.0 * k4[i] / 110592.0 + 253.0 * k5[i] / 4096.0;
            }
            dydx = g.derivn( x + 7.0 * h / 8.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k6[i] = h * dydx[i];
            }

            maxerr = 0.0D;
            for( int i = 0; i < nequ; i++ ) {
                y5[i] = y[i] + 2825.0 * k1[i] / 27648.0 + 18575.0 * k3[i] / 48384.0 + 13525.0 * k4[i] / 55296.0 + 277.0 * k5[i] / 14336.0 + k6[i] / 4.0;
                y6[i] = y[i] + 37 * k1[i] / 378.0 + 250.0 * k3[i] / 621.0 + 125.0 * k4[i] / 594.0 + 512.0 * k6[i] / 1771.0;
                err = Math.abs( y6[i] - y5[i] );
                tol = y5[i] * reltol + abstol;
                maxerr = Math.max( maxerr, err / tol );
            }
            if( tol <= 1.0D ) {
                x += h;
                delta = SAFETY * Math.pow( maxerr, -0.2 );
                if( delta > 4.0 ) {
                    h *= 4.0;
                }
                else {
                    h *= delta;
                }
                if( x + h > xn ) {
                    h = xn - x;
                }
                y = y5;
            }
            else {
                delta = SAFETY * Math.pow( maxerr, -0.25 );
                if( delta < 0.1D ) {
                    h *= 0.1;
                }
                else {
                    h *= delta;
                }
            }
        }
        return y;
    }

    public static double[] cashKarp( DerivnFunction g, double x0, double[] y0, double xn, double h, double abstol, double reltol ) {

        double nsteps = ( xn - x0 ) / h;
        int maxiter = (int)nsteps * 100;

        return cashKarp( g, x0, y0, xn, h, abstol, reltol, maxiter );
    }

    // Runge-Kutta-Fehlberg for a single ordinary differential equation (ODE)
    public static double fehlberg( DerivFunction g, double x0, double y0, double xn, double h, double abstol, double reltol, int maxiter ) {
        double k1 = 0.0D, k2 = 0.0D, k3 = 0.0D, k4 = 0.0D, k5 = 0.0D, k6 = 0.0D;
        double x = x0, y = y0, y5 = 0.0D, y6 = 0.0D, err = 0.0D, delta = 0.0D, tol = 0.0D;
        int i = 0;

        while( x < xn ) {
            i++;
            if( i > maxiter ) {
                throw new ArithmeticException( "Maximum number of iterations exceeded" );
            }
            k1 = h * g.deriv( x, y );
            k2 = h * g.deriv( x + h / 4.0, y + k1 / 4.0 );
            k3 = h * g.deriv( x + 3.0 * h / 8.0, y + ( 3.0 * k1 + 9.0 * k2 ) / 32.0 );
            k4 = h * g.deriv( x + 12.0 * h / 13.0, y + ( 1932.0 * k1 - 7200.0 * k2 + 7296.0 * k3 ) / 2197.0 );
            k5 = h * g.deriv( x + h, y + 439.0 * k1 / 216.0 - 8.0 * k2 + 3680.0 * k3 / 513.0 - 845 * k4 / 4104.0 );
            k6 = h * g.deriv( x + 0.5 * h, y - 8.0 * k1 / 27.0 + 2.0 * k2 - 3544.0 * k3 / 2565.0 + 1859.0 * k4 / 4104.0 - 11.0 * k5 / 40.0 );

            y5 = y + 25.0 * k1 / 216.0 + 1408.0 * k3 / 2565.0 + 2197.0 * k4 / 4104.0 - k5 / 5.0;
            y6 = y + 16.0 * k1 / 135.0 + 6656.0 * k3 / 12825.0 + 28561.0 * k4 / 56430.0 - 9.0 * k5 / 50.0 + 2.0 * k6 / 55.0;
            err = Math.abs( y6 - y5 );
            tol = err / ( y5 * reltol + abstol );
            if( tol <= 1.0 ) {
                x += h;
                delta = SAFETY * Math.pow( tol, -0.2 );
                if( delta > 4.0 ) {
                    h *= 4.0;
                }
                else {
                    h *= delta;
                }
                if( x + h > xn ) {
                    h = xn - x;
                }
                y = y5;
            }
            else {
                delta = SAFETY * Math.pow( tol, -0.25 );
                if( delta < 0.1 ) {
                    h *= 0.1;
                }
                else {
                    h *= delta;
                }
            }
        }
        return y;
    }

    // maximum iteration default option
    public static double fehlberg( DerivFunction g, double x0, double y0, double xn, double h, double abstol, double reltol ) {

        double nsteps = ( xn - x0 ) / h;
        int maxiter = (int)nsteps * 100;

        return fehlberg( g, x0, y0, xn, h, abstol, reltol, maxiter );
    }

    // Runge-Kutta-Fehlberg for n (nequ) ordinary differential equations (ODEs)
    public static double[] fehlberg( DerivnFunction g, double x0, double[] y0, double xn, double h, double abstol, double reltol, int maxiter ) {
        int nequ = y0.length;
        double[] k1 = new double[nequ];
        double[] k2 = new double[nequ];
        double[] k3 = new double[nequ];
        double[] k4 = new double[nequ];
        double[] k5 = new double[nequ];
        double[] k6 = new double[nequ];
        double[] y = new double[nequ];
        double[] y6 = new double[nequ];
        double[] y5 = new double[nequ];
        double[] yd = new double[nequ];
        double[] dydx = new double[nequ];

        double x = x0, err = 0.0D, maxerr = 0.0D, delta = 0.0D, tol = 1.0D;
        int ii = 0;

        // initialise
        for( int i = 0; i < nequ; i++ ) {
            y[i] = y0[i];
        }

        while( x < xn ) {
            ii++;
            if( ii > maxiter ) {
                throw new ArithmeticException( "Maximum number of iterations exceeded" );
            }
            dydx = g.derivn( x, y );
            for( int i = 0; i < nequ; i++ ) {
                k1[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + k1[i] / 4.0;
            }
            dydx = g.derivn( x + h / 4.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k2[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + ( 3.0 * k1[i] + 9.0 * k2[i] ) / 32.0;
            }
            dydx = g.derivn( x + 3.0 * h / 8.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k3[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + ( 1932.0 * k1[i] - 7200.0 * k2[i] + 7296.0 * k3[i] ) / 2197.0;
            }
            dydx = g.derivn( x + 12.0 * h / 13.0, yd );
            for( int i = 0; i < nequ; i++ ) {
                k4[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] + 439.0 * k1[i] / 216.0 - 8.0 * k2[i] + 3680.0 * k3[i] / 513.0 - 845 * k4[i] / 4104.0;
            }
            dydx = g.derivn( x + h, yd );
            for( int i = 0; i < nequ; i++ ) {
                k5[i] = h * dydx[i];
            }

            for( int i = 0; i < nequ; i++ ) {
                yd[i] = y[i] - 8.0 * k1[i] / 27.0 + 2.0 * k2[i] - 3544.0 * k3[i] / 2565.0 + 1859.0 * k4[i] / 4104.0 - 11.0 * k5[i] / 40.0;
            }
            dydx = g.derivn( x + 0.5 * h, yd );
            for( int i = 0; i < nequ; i++ ) {
                k6[i] = h * dydx[i];
            }

            maxerr = 0.0D;
            for( int i = 0; i < nequ; i++ ) {
                y5[i] = y[i] + 25.0 * k1[i] / 216.0 + 1408.0 * k3[i] / 2565.0 + 2197.0 * k4[i] / 4104.0 - k5[i] / 5.0;
                y6[i] = y[i] + 16.0 * k1[i] / 135.0 + 6656.0 * k3[i] / 12825.0 + 28561.0 * k4[i] / 56430.0 - 9.0 * k5[i] / 50.0 + 2.0 * k6[i] / 55.0;
                err = Math.abs( y6[i] - y5[i] );
                tol = y5[i] * reltol + abstol;
                maxerr = Math.max( maxerr, err / tol );
            }

            if( tol <= 1.0D ) {
                x += h;
                delta = SAFETY * Math.pow( maxerr, -0.2 );
                if( delta > 4.0 ) {
                    h *= 4.0;
                }
                else {
                    h *= delta;
                }
                if( x + h > xn ) {
                    h = xn - x;
                }
                y = y5;
            }
            else {
                delta = SAFETY * Math.pow( maxerr, -0.25 );
                if( delta < 0.1 ) {
                    h *= 0.1;
                }
                else {
                    h *= delta;
                }
            }
        }
        return y;
    }

    // maximum iteration default option
    public static double[] fehlberg( DerivnFunction g, double x0, double[] y0, double xn, double h, double abstol, double reltol ) {

        double nsteps = ( xn - x0 ) / h;
        int maxiter = (int)nsteps * 100;

        return fehlberg( g, x0, y0, xn, h, abstol, reltol, maxiter );
    }
}






