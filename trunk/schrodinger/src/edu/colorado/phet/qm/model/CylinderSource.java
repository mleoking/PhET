/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 9:33:23 AM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */

public class CylinderSource extends DiscreteModel.Adapter {
    private Rectangle region;
    private Wave wave;
    private Ellipse2D.Double ellipse;

    public CylinderSource( Rectangle region, Wave wave ) {
        setRegion( region );
        this.wave = wave;
    }

    public void beforeTimeStep( DiscreteModel model ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int k = region.y; k < region.y + region.height; k++ ) {
                if( model.getWavefunction().containsLocation( i, k ) && ellipse.contains( i, k ) ) {
                    Complex value = wave.getValue( i, k, model.getSimulationTime() );
                    model.getWavefunction().setValue( i, k, value );
                }
            }
        }
    }

    public void setRegion( Rectangle rectangle ) {
        this.region = rectangle;
        ellipse = new Ellipse2D.Double( region.getX(), region.getY(), region.getWidth(), region.getHeight() );
//        GaussianWave1D xwave = new GaussianWave1D();
    }


    public void setWave( Wave wave ) {
        this.wave = wave;
    }
}
