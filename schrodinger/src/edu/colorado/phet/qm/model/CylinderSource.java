/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 9:33:23 AM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */

public class CylinderSource {
    private Rectangle region;
    private Wave wave;
    private Rectangle2D.Double ellipse;

    public CylinderSource( Rectangle region, Wave wave ) {
        setRegion( region );
        this.wave = wave;
    }

    public void initializeEntrantWave( WaveModel waveModel, double simulationTime ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int k = region.y; k < region.y + region.height; k++ ) {
                if( ellipse.contains( i, k ) && waveModel.containsLocation( i, k ) ) {
                    Complex value = wave.getValue( i, k, simulationTime );
                    waveModel.setValue( i, k, value.real, value.imag );
                }
            }
        }
    }

    public void setRegion( Rectangle rectangle ) {
        this.region = rectangle;
        ellipse = new Rectangle2D.Double( region.getX(), region.getY(), region.getWidth(), region.getHeight() );
    }

    public void setWave( Wave wave ) {
        this.wave = wave;
    }
}
