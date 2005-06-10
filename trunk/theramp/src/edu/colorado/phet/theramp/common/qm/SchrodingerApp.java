/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import edu.colorado.phet.theramp.view.SurfaceGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:06:40 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApp {
    public void run( int Steps ) {
        int show = 1;
        int XMESH = 100;
        int YMESH = 100;

        double TAU = 5E-6;
        final Complex[][] wavefunction = new Complex[XMESH + 1][YMESH + 1];

//        Potential potential = new ConstantPotential( 0 );
        Potential potential = new SimpleGradientPotential( 30 );
        BoundaryCondition boundaryCondition = new ZeroBoundaryCondition();
        CNCPropagator cncPropagator = new CNCPropagator( TAU, boundaryCondition, potential );
        double K = 10 * Math.PI;
        double LAMBDA = (int)2 * Math.PI / K * XMESH;

        new SquareWave().initialize( wavefunction );
//        initGaussian( wavefunction );
        ColorGrid colorGrid = new ColorGrid( 600, 600, XMESH, YMESH );
        ColorMap colorMap = new ColorMap() {
            public Paint getPaint( int i, int k ) {
                double h = Math.abs( wavefunction[i][k].getReal() );
                double s = Math.abs( wavefunction[i][k].getImaginary() );
                double b = wavefunction[i][k].abs();
                Color color = new Color( Color.HSBtoRGB( (float)h, (float)s, (float)b ) );
                return color;
            }
        };
        colorGrid.colorize( colorMap );
        SurfaceGraphic.ImageDebugFrame frame = new SurfaceGraphic.ImageDebugFrame( colorGrid.getBufferedImage() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        for( int i = 0; i <= Steps; i++ ) {
            System.out.println( "Running to step: " + i );
            if( i % show == 0 ) {
                colorGrid.colorize( colorMap );
                frame.setImage( colorGrid.getBufferedImage() );
            }
            cncPropagator.propagate( wavefunction );
        }
    }

    public static void main( String[] args ) {
        new SchrodingerApp().run( 15000 );
    }
}
