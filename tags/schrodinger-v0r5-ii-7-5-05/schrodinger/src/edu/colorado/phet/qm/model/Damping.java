/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 9:05:13 AM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class Damping extends DiscreteModel.Adapter {

//    private double[] damp = new double[]{0.99, 0.98, 0.95, 0.92, 0.85, 0.8,0.5,0.1};
//    private double[] damp = new double[]{0.999, 0.99, 0.98, 0.97, 0.95, 0.9, 0.5, 0.1};
    private double[] damp = new double[]{0.999, 0.995, 0.99, 0.975, 0.95, 0.925, 0.9, 0.85, 0.7, 0.3};
//    private double[] damp = new double[]{0.999, 0.99, 0.95, 0.9, 0.85, 0.8, 0.7,0.6,0.5};
//    private double[] damp = new double[]{0.99, 0.5};//, 0.96, 0.9, 0.8, 0.7, 0.6, 0.5,0.3,0.1};

    public void finishedTimeStep( DiscreteModel model ) {
        damp( model.getWavefunction() );
    }

    public void damp( Wavefunction wavefunction ) {
        leftWall( wavefunction );
        rightWall( wavefunction );
        topWall( wavefunction );
        bottomWall( wavefunction );
    }

    private void bottomWall( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int j = wavefunction.getHeight() - damp.length + depth;
            for( int i = 0; i < wavefunction.getWidth(); i++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }

    private void topWall( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int j = damp.length - depth - 1;
            for( int i = 0; i < wavefunction.getWidth(); i++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }

    private void rightWall( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int i = wavefunction.getWidth() - damp.length + depth;
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }


    private void leftWall( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int i = damp.length - depth - 1;
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }

    /**
     * Should go to zero at fraction =1
     */
    private double getScaleFactor( int depth ) {
        return damp[depth];
//        double scale = 1.0 - penetrationFraction;
//        return scale;
    }

    public double getDamping( Wavefunction wavefunction, int i, int k ) {
        if( i < damp.length || k < damp.length || i > wavefunction.getWidth() - damp.length || k > wavefunction.getHeight() - damp.length ) {
            return 1.0;
        }
        return 0.0;
    }

    public int getDepth() {
        return damp.length;
    }
}
