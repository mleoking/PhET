// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.view.colorgrid.ColorMap;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.WavefunctionGraphic;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 12:18:40 PM
 */

public class DGWavefunctionGraphic extends WavefunctionGraphic {
    public DGWavefunctionGraphic( QWIModel discreteModel, Wavefunction wavefunction ) {
        super( discreteModel, wavefunction );
    }

    public void setColorMap( ColorMap colorMap ) {
        super.setColorMapIgnorePotential( colorMap );
    }
}
