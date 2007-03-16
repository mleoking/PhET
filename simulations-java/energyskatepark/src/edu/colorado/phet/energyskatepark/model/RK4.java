package edu.colorado.phet.energyskatepark.model;

//http://www.physics.orst.edu/~rubin/CPbook/PROGS/J_PROGS/For_Students/RK4.java
//Ordinary Differential Equations Solver (ODES)
//Solve the differential equation  using 4th order Runge-Kutta Method
//copywrite 2002 R Landau, Oregon State U

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class RK4 {
    public static void main( String[] argv ) throws IOException,
                                                    FileNotFoundException {// open  file RK4.dat for output data
        PrintWriter w =
                new PrintWriter( new FileOutputStream( "RK4.dat" ), true );

        double h, t;
        double y[] = new double[2];
        double Tmin = 0.0;
        double Tmax = 10.0;         //endpoints
        int Ntimes = 100;

        y[0] = 3.0;
        y[1] = -5.0;                   //initialize
        h = ( Tmax - Tmin ) / Ntimes;
        t = Tmin;
        Diff diff = new DampedSpring();
        for( t = Tmin; t <= Tmax; t += h ) {
            System.out.println( "RK4 t=" + t + " , x= " + y[0] + ", v= " + y[1] );//printout
            w.println( t + " " + y[0] + " " + y[1] );//output to file
            rk4( t, y, h, diff );
        }
        System.out.println( "Data stored in RK4.dat" );
    }

// ====== rk4 method below is *NOT TO BE MODIFIED. Instead, modify f method
// this method advances the N-vector solution ahead by one step.

    public static void rk4( double t, double y[], double h, Diff diff ) {
        int Neqs = y.length;
        int i;
        double F[] = new double[Neqs];
        double ydumb[] = new double[Neqs];
        double k1[] = new double[Neqs];
        double k2[] = new double[Neqs];
        double k3[] = new double[Neqs];
        double k4[] = new double[Neqs];

        f( t, y, F, diff );
        for( i = 0; i < Neqs; i++ ) {
            k1[i] = h * F[i];
            ydumb[i] = y[i] + k1[i] / 2;
        }

        f( t + h / 2, ydumb, F, diff );
        for( i = 0; i < Neqs; i++ ) {
            k2[i] = h * F[i];
            ydumb[i] = y[i] + k2[i] / 2;
        }

        f( t + h / 2, ydumb, F, diff );
        for( i = 0; i < Neqs; i++ ) {
            k3[i] = h * F[i];
            ydumb[i] = y[i] + k3[i];
        }

        f( t + h, ydumb, F, diff );
        for( i = 0; i < Neqs; i++ ) {
            k4[i] = h * F[i];
            y[i] = y[i] + ( k1[i] + 2 * ( k2[i] + k3[i] ) + k4[i] ) / 6;
        }
    }

    private static void f( double t, double[] y, double[] f, Diff diff ) {
        diff.f( t, y, f );
    }
// FUNCTION of your choice below

    public static interface Diff {

        public void f( double t, double y[], double F[] );
    }

    public static class DampedSpring implements Diff {

        public void f( double t, double y[], double F[] ) {

//definition of equation-example
//damped harmonic oscillator with harmonic driver
// x" + 100*x + 2*x'= 10*sin 3*t
// we define y[0] = x, y[1] = x'
//  You need to transform the second order differential equation
// in a system of two differential equatios of first order:
// F[0] = x'= y[1]
// F[1] = x" = -100*x-2*x' = -100 *y[0] -2*y[1] + 10*sin 3*t
// You may enter your own equation here!

            {
                F[0] = y[1];  // RHS of first equation
                F[1] = -100 * y[0] - 2 * y[1] + 100 * Math.sin( t ); // RHS of second equation
            }
        }
    }
}