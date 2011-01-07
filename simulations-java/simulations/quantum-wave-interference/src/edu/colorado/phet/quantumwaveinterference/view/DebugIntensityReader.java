// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view;

import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Dec 19, 2005
 * Time: 9:56:01 PM
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
