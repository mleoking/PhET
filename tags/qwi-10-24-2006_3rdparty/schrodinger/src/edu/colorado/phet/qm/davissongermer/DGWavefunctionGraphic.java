package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 12:18:40 PM
 * Copyright (c) Jul 14, 2006 by Sam Reid
 */

public class DGWavefunctionGraphic extends WavefunctionGraphic {
    public DGWavefunctionGraphic( QWIModel discreteModel, Wavefunction wavefunction ) {
        super( discreteModel, wavefunction );
    }

    public void setColorMap( ColorMap colorMap ) {
        super.setColorMapIgnorePotential( colorMap );
    }
}
