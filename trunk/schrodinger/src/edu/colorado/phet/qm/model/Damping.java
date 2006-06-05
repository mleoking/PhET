/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 9:05:13 AM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class Damping extends QWIModel.Adapter {

//    private double[] damp = new double[]{0.99, 0.98, 0.95, 0.92, 0.85, 0.8,0.5,0.1};
//    private double[] damp = new double[]{0.999, 0.99, 0.98, 0.97, 0.95, 0.9, 0.5, 0.1};
    private double[] damp = new double[]{0.999, 0.995, 0.99, 0.975, 0.95, 0.925, 0.9, 0.85, 0.7, 0.3};
//    private double[] wallDamp = new double[]{0.999, 0.995, 0.99, 0.975, 0.95, 0.925, 0.9, 0.85, 0.7, 0.3};
//    private double[] damp = new double[]{0.999, 0.99, 0.95, 0.9, 0.85, 0.8, 0.7,0.6,0.5};
//    private double[] damp = new double[]{0.99, 0.5};//, 0.96, 0.9, 0.8, 0.7, 0.6, 0.5,0.3,0.1};

    public void finishedTimeStep( QWIModel model ) {
        damp( model.getWavefunction() );
//        dampBarrier( model );
    }

    private void dampBarrier( QWIModel model ) {
        model.getWavefunction();
        HorizontalDoubleSlit horizontalDoubleSlit = model.getDoubleSlitPotential();
        Rectangle[] blockAreas = horizontalDoubleSlit.getBlockAreas();
        for( int i = 0; i < blockAreas.length; i++ ) {
            Rectangle blockArea = blockAreas[i];
            dampArea( model, blockArea );
        }
    }

    private void dampArea( QWIModel model, Rectangle blockArea ) {
        Wavefunction wavefunction = model.getWavefunction();
        for( int x = blockArea.x; x < blockArea.x + blockArea.width; x++ ) {
            for( int j = 0; j < damp.length && j < blockArea.height; j++ ) {
                int y = blockArea.y + j;
//                System.out.println( "x=" + x + ", y = " + y + ", damp=" + damp[j] );
                wavefunction.valueAt( x, y ).scale( damp[damp.length - j - 1] );
            }
        }
    }

    public void damp( Wavefunction wavefunction ) {
        dampLeft( wavefunction );
        dampRight( wavefunction );
        dampTop( wavefunction );
        dampBottom( wavefunction );
    }

    private void dampBottom( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int j = wavefunction.getHeight() - damp.length + depth;
            for( int i = 0; i < wavefunction.getWidth(); i++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }

    private void dampTop( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int j = damp.length - depth - 1;
            for( int i = 0; i < wavefunction.getWidth(); i++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }

    private void dampRight( Wavefunction wavefunction ) {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int i = wavefunction.getWidth() - damp.length + depth;
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                wavefunction.valueAt( i, j ).scale( scale );
            }
        }
    }


    private void dampLeft( Wavefunction wavefunction ) {
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
    }


    public int getDepth() {
        return damp.length;
    }
}
