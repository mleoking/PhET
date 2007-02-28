/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Dec 19, 2005
 * Time: 9:56:01 PM
 * Copyright (c) Dec 19, 2005 by Sam Reid
 */

public class DebugIntensityReader extends QWIModel.Adapter {
    public void finishedTimeStep( QWIModel model ) {
        super.finishedTimeStep( model );
        Wavefunction wavefunction = model.getWavefunction();
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                double val = ( i + k ) % 2 == 0 ? 1 : 0;
                wavefunction.setValue( i, k, val, val );
            }
        }
    }
}
