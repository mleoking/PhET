/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 9:05:13 AM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class Damping implements DiscreteModel.Listener {

    private double[] damp = new double[]{0.98, 0.95, 0.8, 0.5, 0.2, 0.001};
//    private int dw = 10;
//    private int dh = 4;

    public void finishedTimeStep( DiscreteModel model ) {
        damp( model.getWavefunction() );
    }

    private void damp( Wavefunction wavefunction ) {
        leftWall( wavefunction );
        rightWall( wavefunction );
    }

    private void rightWall( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int i = wavefunction.getWidth() - 1 - depth;
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
//        int depth = 1;
//        for( int i = wavefunction.getWidth() - 1 - damp.length; i < wavefunction.getWidth(); i++ ) {
////            double penetrationDepthFraction = depth / dw;
//            double scale = getScaleFactor( depth );
//            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
//                wavefunction.valueAt( i, j ).scale( scale );
//            }
//            depth++;
//        }
    }

    private void leftWall( Wavefunction wavefunction ) {
        int depth = 1;
        for( int i = damp.length - 1; i >= 0; i-- ) {
//            double penetrationDepthFraction = depth / dw;
            double scale = getScaleFactor( depth );
            System.out.println( "depth = " + depth + ", scale=" + scale );
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
            depth++;
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

    public void sizeChanged() {
    }

    public void potentialChanged() {
    }

    public void beforeTimeStep( DiscreteModel discreteModel ) {
    }
}
