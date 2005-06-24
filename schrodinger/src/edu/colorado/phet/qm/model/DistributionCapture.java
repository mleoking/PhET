/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 10:54:53 AM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DistributionCapture implements VerticalETA.Listener {
    private DiscreteModel discreteModel;

    public DistributionCapture( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
    }

    public void arrived() {
        Wavefunction w = discreteModel.getWavefunction().copy();
        System.out.println( "w = " + w );
    }
}
