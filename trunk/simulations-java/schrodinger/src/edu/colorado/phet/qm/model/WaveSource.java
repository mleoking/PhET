/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 9:33:23 AM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */

public class WaveSource extends QWIModel.Adapter {
    private Rectangle region;
    private Wave wave;

    public WaveSource( Rectangle region, Wave wave ) {
        this.region = region;
        this.wave = wave;
    }

    public void beforeTimeStep( QWIModel model ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int k = region.y; k < region.y + region.height; k++ ) {
                if( model.getWavefunction().containsLocation( i, k ) ) {
                    Complex value = wave.getValue( i, k, model.getSimulationTime() );
                    model.getWavefunction().setValue( i, k, value );
                }
            }
        }
    }
}
