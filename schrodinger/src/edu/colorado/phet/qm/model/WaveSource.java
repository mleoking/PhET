/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 9:33:23 AM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */

public class WaveSource implements DiscreteModel.Listener {
    private Rectangle region;
    private BoundaryCondition boundaryCondition;
    private double norm = 1.0;

    public WaveSource( Rectangle region, BoundaryCondition boundaryCondition ) {
        this.region = region;
        this.boundaryCondition = boundaryCondition;
    }

    public double getNorm() {
        return norm;
    }

    public void setNorm( double norm ) {
        this.norm = norm;
    }

    public void finishedTimeStep( DiscreteModel model ) {
    }

    public void sizeChanged() {
    }

    public void potentialChanged() {
    }

    public void beforeTimeStep( DiscreteModel model ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int k = region.y; k < region.y + region.height; k++ ) {
                if( Wavefunction.containsLocation( model.getWavefunction(), i, k ) ) {
                    Complex value = boundaryCondition.getValue( i, k, model.getSimulationTime() );
//                    System.out.println( "i="+i+", k="+k+", t="+model.getSimulationTime()+", , value = " + value );
                    model.getWavefunction()[i][k].setValue( value );
                }
            }
        }
        Wavefunction.setNorm( model.getWavefunction(), norm );
    }
}
