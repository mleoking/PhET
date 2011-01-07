// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import java.awt.geom.Point2D;

public class LinearRegression {
    public static interface Input {

        boolean isEmpty();

        Point2D readPoint();
    }

    public static class Result {
        double slope;
        double intercept;

        public Result( double slope, double intercept ) {
            this.slope = slope;
            this.intercept = intercept;
        }

        public String toString() {
            return "slope=" + slope + ", intercept=" + intercept;
        }

        public double evaluate( double x ) {
            return slope * x + intercept;
        }

        public double getSlope() {
            return slope;
        }
    }

    public static Result main( Input StdIn ) {
        int MAXN = 1000;
        int n = 0;
        double[] x = new double[MAXN];
        double[] y = new double[MAXN];

        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        while( !StdIn.isEmpty() ) {
            Point2D pt = StdIn.readPoint();
            x[n] = pt.getX();
            y[n] = pt.getY();
            sumx += x[n];
            sumx2 += x[n] * x[n];
            sumy += y[n];
            n++;
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for( int i = 0; i < n; i++ ) {
            xxbar += ( x[i] - xbar ) * ( x[i] - xbar );
            yybar += ( y[i] - ybar ) * ( y[i] - ybar );
            xybar += ( x[i] - xbar ) * ( y[i] - ybar );
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        System.out.println( "y   = " + beta1 + " * x + " + beta0 );

        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for( int i = 0; i < n; i++ ) {
            double fit = beta1 * x[i] + beta0;
            rss += ( fit - y[i] ) * ( fit - y[i] );
            ssr += ( fit - ybar ) * ( fit - ybar );
        }
        double R2 = ssr / yybar;
        double svar = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar / n + xbar * xbar * svar1;
        System.out.println( "R^2                 = " + R2 );
        System.out.println( "std error of beta_1 = " + Math.sqrt( svar1 ) );
        System.out.println( "std error of beta_0 = " + Math.sqrt( svar0 ) );
        svar0 = svar * sumx2 / ( n * xxbar );
        System.out.println( "std error of beta_0 = " + Math.sqrt( svar0 ) );

        System.out.println( "SSTO = " + yybar );
        System.out.println( "SSE  = " + rss );
        System.out.println( "SSR  = " + ssr );
        System.out.println( "http://www.cs.princeton.edu/introcs/97data/LinearRegression.java.html" );
        return new Result( beta1, beta0 );
    }
}
