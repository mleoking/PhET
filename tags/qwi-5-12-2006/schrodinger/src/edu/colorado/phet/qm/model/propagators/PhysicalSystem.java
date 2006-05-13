/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 11:56:56 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */

public class PhysicalSystem {
    private Rectangle2D.Double modelArea;
    private int width;
    private int height;
    private double hbar;
    private Lattice2D spaceLattice;
    private Lattice2D momentumLattice;

    public PhysicalSystem( int width, int height, double hbar, Rectangle2D.Double modelArea ) {
        this.width = width;
        this.height = height;
        this.hbar = hbar;
        this.modelArea = modelArea;
        updateLattices();
    }

    public void updateLattices() {
        spaceLattice = new Lattice2D( width, height );
        double dx = modelArea.width / width;//todo minus one in denominator?
        double dy = modelArea.height / height;
        double x = modelArea.x;
        double y = modelArea.y;
        for( int i = 0; i < spaceLattice.getWidth(); i++ ) {
            for( int k = 0; k < spaceLattice.getHeight(); k++ ) {
                spaceLattice.setValue( i, k, x, y );
                y += dy;
            }
            x += dx;
        }

        momentumLattice = new Lattice2D( width, height );
        double dpx = 2.0 * Math.PI * hbar / dx / width;
        double dpy = 2.0 * Math.PI * hbar / dy / height;

        double px = 0;
        double py = 0;
        for( int i = 0; i < momentumLattice.getWidth(); i++ ) {
            for( int k = 0; k < momentumLattice.getHeight(); k++ ) {
                momentumLattice.setValue( i, k, px, py );
                py += dpy;                         //todo update py correctly
            }
            px += dpx;
        }
    }

    void setupGridsTLDP( double hbar, int nr_points, double xmax, double xmin ) {
        /* Coordinate grid */
        double dx = ( xmax - xmin ) / (double)( nr_points - 1 );
        double x = xmin;
        double[]xLattice = new double[nr_points];
        for( int i = 0; i < nr_points; i++ ) {
            xLattice[i] = x;
            x += dx;
        }

        double[]pLattice = new double[nr_points];
        /* Momentum grid */
        double dp = 2.0 * Math.PI * hbar / dx / nr_points;
        double p = 0.0;
        for( int i = 0; i < nr_points / 2; i++ ) {
            pLattice[i] = p;
            p += dp;
            pLattice[nr_points - i - 1] = -p;
        }
    }

    /* Setup Kinetic part of the propagator
    http://www.tldp.org/linuxfocus/common/March1998/example2.c
    */
    void K_setupTLDP( long nx, double hbar, double tau, double mass, double[]p, double[]expK ) {
        double tmp = tau / ( 4.0 * hbar * mass );
        for( int i = 0; i < nx; i++ ) {
            double theta = tmp * Math.pow( p[i], 2 );
            expK[2 * i] = Math.cos( theta );
            expK[2 * i + 1] = -Math.sin( theta );
        }
    }
}
